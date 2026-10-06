package com.neowallet.identity.service;

import com.neowallet.config.AuthProperties;
import com.neowallet.identity.dto.*;
import com.neowallet.identity.entity.*;
import com.neowallet.identity.repository.*;
import com.neowallet.identity.security.Hashing;
import com.neowallet.identity.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthProperties authProperties;
    private final UserRepository userRepository;
    private final UserSessionRepository sessionRepository;
    private final UserDeviceRepository deviceRepository;
    private final OtpService otpService;
    private final Hashing hashing;
    private final JwtService jwtService;
    private final AuditService auditService;
    private final RateLimitService rateLimitService;
    private final AccountLockoutService accountLockoutService;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailAndDeletedAtIsNull(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPasswordHash(hashing.hashPassword(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setAccountStatus("PENDING");

        user = userRepository.save(user);

        auditService.recordAuthentication(user.getUserId(), "REGISTRATION", user.getUserId(), true, "");

        OtpRequest otpRequest = new OtpRequest();
        otpRequest.setEmail(request.getEmail());
        otpRequest.setPurpose("REGISTRATION");
        otpService.requestOtp(otpRequest);

        return RegisterResponse.builder()
            .userId(user.getUserId())
            .email(user.getEmail())
            .phoneNumber(user.getPhoneNumber())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .createdAt(user.getCreatedAt())
            .requiresVerification(true)
            .build();
    }

    @Transactional
    public LoginResponse login(LoginRequest request, String ipAddress, String userAgent) {
        if (!rateLimitService.isAllowed("login:" + request.getEmail(), 10, 60)) {
            throw new IllegalStateException("Too many login attempts");
        }

        User user = userRepository.findByEmailAndDeletedAtIsNull(request.getEmail())
            .orElse(null);

        if (user == null || !user.isActive()) {
            auditService.recordAuthentication(
                user != null ? user.getUserId() : null,
                "LOGIN_FAILURE",
                user != null ? user.getUserId() : null,
                false,
                "{\"reason\": \"invalid_or_inactive\"}"
            );
            throw new IllegalArgumentException("Invalid credentials");
        }

        if (user.isLocked()) {
            auditService.recordAuthentication(user.getUserId(), "LOGIN_FAILURE", user.getUserId(), false, "{\"reason\": \"locked\"}");
            throw new IllegalStateException("Account locked");
        }

        if (!hashing.checkPassword(request.getPassword(), user.getPasswordHash())) {
            int attempts = user.getFailedLoginAttempts() + 1;
            boolean lock = attempts >= authProperties.getMaxFailedLogins();
            accountLockoutService.recordFailedLogin(user.getUserId(), attempts, lock);
            auditService.recordAuthentication(user.getUserId(), "LOGIN_FAILURE", user.getUserId(), false, "{\"reason\": \"bad_password\", \"attempt\": " + attempts + "}");
            throw new IllegalArgumentException("Invalid credentials");
        }

        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        user.setAccountStatus("ACTIVE");
        userRepository.save(user);

        UserDevice device = registerOrUpdateDevice(user, request);

        String refreshToken = hashing.generateToken();
        String refreshTokenHash = hashing.hashRefreshToken(refreshToken);

        UserSession session = new UserSession();
        session.setUser(user);
        session.setDevice(device);
        session.setRefreshTokenHash(refreshTokenHash);
        session.setIpAddress(ipAddress);
        session.setUserAgent(userAgent);
        session.setExpiresAt(Instant.now().plus(authProperties.getRefreshTokenTtl()));
        sessionRepository.save(session);

        String accessToken = jwtService.generateAccessToken(user.getUserId(), null);

        auditService.recordAuthentication(user.getUserId(), "LOGIN_SUCCESS", user.getUserId(), true, "{\"session\": \"" + session.getSessionId() + "\"}");

        return LoginResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(authProperties.getAccessTokenTtl().getSeconds())
            .user(LoginResponse.UserSummary.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build())
            .build();
    }

    @Transactional
    public RefreshTokenResponse refresh(RefreshTokenRequest request) {
        String hash = hashing.hashRefreshToken(request.getRefreshToken());
        UserSession session = sessionRepository.findByRefreshTokenHash(hash)
            .orElse(null);

        if (session == null) {
            session = sessionRepository.findByPreviousRefreshTokenHash(hash).orElse(null);
            if (session != null) {
                session.setRevokedAt(Instant.now());
                sessionRepository.save(session);
                auditService.recordAuthentication(session.getUser().getUserId(), "REFRESH_FAILURE", session.getSessionId(), false, "{\"reason\": \"token_replay\"}");
                throw new IllegalStateException("Token replay detected");
            }
            auditService.recordAuthentication(null, "REFRESH_FAILURE", null, false, "{\"reason\": \"token_not_found\"}");
            throw new IllegalArgumentException("Invalid refresh token");
        }

        if (session.getRevokedAt() != null || session.getExpiresAt().isBefore(Instant.now())) {
            auditService.recordAuthentication(session.getUser().getUserId(), "REFRESH_FAILURE", session.getSessionId(), false, "{\"reason\": \"revoked_or_expired\"}");
            throw new IllegalArgumentException("Refresh token revoked or expired");
        }

        String newRefreshToken = hashing.generateToken();
        String newRefreshTokenHash = hashing.hashRefreshToken(newRefreshToken);

        session.setPreviousRefreshTokenHash(session.getRefreshTokenHash());
        session.setRefreshTokenHash(newRefreshTokenHash);
        session.setExpiresAt(Instant.now().plus(authProperties.getRefreshTokenTtl()));
        session.setLastActiveAt(Instant.now());
        sessionRepository.save(session);

        String accessToken = jwtService.generateAccessToken(session.getUser().getUserId(), null);

        auditService.recordAuthentication(session.getUser().getUserId(), "REFRESH_SUCCESS", session.getSessionId(), true, "");

        return RefreshTokenResponse.builder()
            .accessToken(accessToken)
            .refreshToken(newRefreshToken)
            .tokenType("Bearer")
            .expiresIn(authProperties.getAccessTokenTtl().getSeconds())
            .build();
    }

    @Transactional
    public void logout(UUID userId, String refreshToken) {
        UserSession session = sessionRepository.findByRefreshTokenHash(hashing.hashRefreshToken(refreshToken))
            .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (!session.getUser().getUserId().equals(userId)) {
            throw new AuthorizationServiceException("Session not owned by user");
        }

        session.setRevokedAt(Instant.now());
        sessionRepository.save(session);

        auditService.recordAuthentication(userId, "LOGOUT", session.getSessionId(), true, "");
    }

    @Transactional
    public void logoutAll(UUID userId) {
        int revoked = sessionRepository.revokeAllByUserId(userId, Instant.now());
        deviceRepository.findByUserUserIdAndIsActiveTrue(userId).forEach(d -> {
            d.setIsActive(false);
            deviceRepository.save(d);
        });

        auditService.recordAuthentication(userId, "LOGOUT_ALL", userId, true, "{\"revoked_sessions\": " + revoked + "}");
    }

    @Transactional(readOnly = true)
    public List<SessionResponse> listSessions(UUID userId) {
        return sessionRepository.findByUserUserIdAndRevokedAtIsNullAndExpiresAtAfter(userId, Instant.now()).stream()
            .map(s -> SessionResponse.builder()
                .sessionId(s.getSessionId())
                .deviceId(s.getDevice() != null ? s.getDevice().getDeviceId() : null)
                .deviceName(s.getDevice() != null ? s.getDevice().getDeviceName() : null)
                .lastActiveAt(s.getLastActiveAt())
                .createdAt(s.getCreatedAt())
                .build())
            .collect(Collectors.toList());
    }

    @Transactional
    public void revokeSession(UUID userId, UUID sessionId) {
        UserSession session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (!session.getUser().getUserId().equals(userId)) {
            throw new AuthorizationServiceException("Session not owned by user");
        }

        session.setRevokedAt(Instant.now());
        sessionRepository.save(session);

        auditService.recordAuthentication(userId, "SESSION_REVOKED", session.getSessionId(), true, "");
    }

    private UserDevice registerOrUpdateDevice(User user, LoginRequest request) {
        if (request.getDeviceType() == null) {
            return null;
        }

        UserDevice device = new UserDevice();
        device.setUser(user);
        device.setDeviceName(request.getDeviceName());
        device.setDeviceType(request.getDeviceType().toUpperCase());
        device.setPlatform(request.getPlatform());
        device.setOsVersion(request.getOsVersion());
        device.setAppVersion(request.getAppVersion());
        device.setLastActiveAt(Instant.now());

        int activeCount = deviceRepository.countByUserUserIdAndIsActiveTrue(user.getUserId());
        if (activeCount >= authProperties.getMaxDevices()) {
            throw new IllegalStateException("Maximum device limit reached");
        }

        return deviceRepository.save(device);
    }

}

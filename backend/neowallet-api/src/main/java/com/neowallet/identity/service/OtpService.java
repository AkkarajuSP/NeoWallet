package com.neowallet.identity.service;

import com.neowallet.config.AuthProperties;
import com.neowallet.identity.dto.OtpRequest;
import com.neowallet.identity.dto.OtpResponse;
import com.neowallet.identity.dto.OtpVerifyRequest;
import com.neowallet.identity.entity.OtpChallenge;
import com.neowallet.identity.entity.User;
import com.neowallet.identity.repository.OtpChallengeRepository;
import com.neowallet.identity.repository.UserRepository;
import com.neowallet.identity.security.Hashing;
import com.neowallet.provider.OtpProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {

    private final AuthProperties authProperties;
    private final OtpChallengeRepository otpChallengeRepository;
    private final UserRepository userRepository;
    private final OtpProvider otpProvider;
    private final Hashing hashing;
    private final AuditService auditService;
    private final RateLimitService rateLimitService;

    @Transactional
    public OtpResponse requestOtp(OtpRequest request) {
        Instant now = Instant.now();
        Instant expiry = now.plus(authProperties.getOtpTtl());

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String rateKey = "otp:" + request.getEmail() + ":" + request.getPurpose();
            if (!rateLimitService.isAllowed(rateKey, authProperties.getOtpResendLimit(),
                authProperties.getOtpResendWindow().getSeconds())) {
                throw new IllegalStateException("OTP resend limit exceeded");
            }
        }

        User user = null;
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user = userRepository.findByEmailAndDeletedAtIsNull(request.getEmail()).orElse(null);
        }

        String plainOtp = generateOtp();

        OtpChallenge challenge = new OtpChallenge();
        challenge.setUser(user);
        challenge.setEmail(request.getEmail());
        challenge.setPhoneNumber(request.getPhoneNumber());
        challenge.setOtpHash(hashing.hashOtp(plainOtp));
        challenge.setPurpose(request.getPurpose());
        challenge.setExpiresAt(expiry);
        challenge.setAttempts(0);
        challenge.setResendCount(0);

        otpChallengeRepository.save(challenge);

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            otpProvider.sendEmail(request.getEmail(), request.getPurpose(), plainOtp, challenge.getOtpId().toString());
        }
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            otpProvider.sendSms(request.getPhoneNumber(), request.getPurpose(), plainOtp, challenge.getOtpId().toString());
        }

        auditService.recordAuthentication(
            user != null ? user.getUserId() : null,
            "OTP_REQUESTED",
            challenge.getOtpId(),
            true,
            "{\"purpose\": \"" + request.getPurpose() + "\"}"
        );

        return OtpResponse.builder()
            .otpId(challenge.getOtpId().toString())
            .verified(false)
            .expiresAt(expiry)
            .build();
    }

    @Transactional
    public OtpResponse verifyOtp(OtpVerifyRequest request) {
        UUID otpId = UUID.fromString(request.getOtpId());
        OtpChallenge challenge = otpChallengeRepository.findByOtpId(otpId)
            .orElseThrow(() -> new IllegalArgumentException("OTP not found"));

        if (challenge.isExpired()) {
            auditService.recordAuthentication(
                challenge.getUser() != null ? challenge.getUser().getUserId() : null,
                "OTP_FAILED",
                challenge.getOtpId(),
                false,
                "{\"reason\": \"expired\"}"
            );
            throw new IllegalArgumentException("OTP expired");
        }

        if (challenge.isVerified()) {
            auditService.recordAuthentication(
                challenge.getUser() != null ? challenge.getUser().getUserId() : null,
                "OTP_FAILED",
                challenge.getOtpId(),
                false,
                "{\"reason\": \"already_used\"}"
            );
            throw new IllegalArgumentException("OTP already used");
        }

        if (challenge.getAttempts() >= authProperties.getOtpMaxAttempts()) {
            auditService.recordAuthentication(
                challenge.getUser() != null ? challenge.getUser().getUserId() : null,
                "OTP_FAILED",
                challenge.getOtpId(),
                false,
                "{\"reason\": \"max_attempts\"}"
            );
            throw new IllegalArgumentException("Maximum attempts exceeded");
        }

        challenge.setAttempts(challenge.getAttempts() + 1);
        otpChallengeRepository.save(challenge);

        if (!hashing.checkOtp(request.getCode(), challenge.getOtpHash())) {
            auditService.recordAuthentication(
                challenge.getUser() != null ? challenge.getUser().getUserId() : null,
                "OTP_FAILED",
                challenge.getOtpId(),
                false,
                "{\"reason\": \"invalid_code\", \"attempt\": " + challenge.getAttempts() + "}"
            );
            throw new IllegalArgumentException("Invalid OTP");
        }

        challenge.setVerifiedAt(Instant.now());
        otpChallengeRepository.save(challenge);

        if ("REGISTRATION".equals(challenge.getPurpose()) && challenge.getUser() != null) {
            User user = challenge.getUser();
            user.setEmailVerified(true);
            user.setAccountStatus("ACTIVE");
            userRepository.save(user);
        }

        auditService.recordAuthentication(
            challenge.getUser() != null ? challenge.getUser().getUserId() : null,
            "OTP_VERIFIED",
            challenge.getOtpId(),
            true,
            "{\"purpose\": \"" + challenge.getPurpose() + "\"}"
        );

        return OtpResponse.builder()
            .otpId(challenge.getOtpId().toString())
            .verified(true)
            .build();
    }

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private String generateOtp() {
        int code = SECURE_RANDOM.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    public Optional<OtpChallenge> findLatestPending(String email, String purpose) {
        List<OtpChallenge> list = otpChallengeRepository.findByEmailAndPurposeAndVerifiedAtIsNullOrderByCreatedAtDesc(email, purpose);
        if (list.isEmpty()) return Optional.empty();
        return Optional.of(list.get(0));
    }

}

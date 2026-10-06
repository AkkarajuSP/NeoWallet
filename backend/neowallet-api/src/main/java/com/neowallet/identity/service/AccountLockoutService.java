package com.neowallet.identity.service;

import com.neowallet.config.AuthProperties;
import com.neowallet.identity.entity.User;
import com.neowallet.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountLockoutService {

    private final AuthProperties authProperties;
    private final UserRepository userRepository;
    private final AuditService auditService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFailedLogin(UUID userId, int attempts, boolean lock) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setFailedLoginAttempts(attempts);
        if (lock) {
            user.setLockedUntil(Instant.now().plus(authProperties.getLockoutDuration()));
            user.setAccountStatus("LOCKED");
            auditService.recordAuthentication(user.getUserId(), "ACCOUNT_LOCKED", user.getUserId(), true, "{\"reason\": \"failed_logins\"}");
        }
        userRepository.save(user);
    }

}

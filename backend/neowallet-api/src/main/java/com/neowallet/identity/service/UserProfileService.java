package com.neowallet.identity.service;

import com.neowallet.identity.dto.*;
import com.neowallet.identity.entity.User;
import com.neowallet.identity.entity.UserPreference;
import com.neowallet.identity.entity.UserProfile;
import com.neowallet.identity.repository.UserPreferenceRepository;
import com.neowallet.identity.repository.UserProfileRepository;
import com.neowallet.identity.repository.UserRepository;
import com.neowallet.identity.repository.UserSessionRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private static final Set<String> VALID_RESPONSE_STYLES = Set.of("CONCISE", "DETAILED");
    private static final Set<String> VALID_FREQUENCIES = Set.of("DAILY", "WEEKLY", "MONTHLY");
    private static final String PHONE_E164_REGEX = "^\\+[1-9]\\d{1,14}$";
    private static final String NAME_REGEX = "^[\\p{L} '-]+$";
    private static final String LOCALE_REGEX = "^[a-zA-Z]{2}(-[a-zA-Z]{2})?$";
    private static final String CURRENCY_REGEX = "^[A-Z]{3}$";

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    private final UserSessionRepository userSessionRepository;
    private final AuditService auditService;

    @Transactional
    public UserResponse getUserProfile(UUID userId) {
        User user = findUser(userId);
        UserProfile profile = findOrCreateProfile(user);
        return mapToUserResponse(user, profile);
    }

    @Transactional
    public UserResponse updateUserProfile(UUID userId, UpdateUserRequest request) {
        User user = findUser(userId);
        UserProfile profile = findOrCreateProfile(user);

        if (request.getFirstName() != null) {
            String firstName = request.getFirstName().trim();
            validateName(firstName, "firstName");
            user.setFirstName(firstName);
        }

        if (request.getLastName() != null) {
            String lastName = request.getLastName().trim();
            validateName(lastName, "lastName");
            user.setLastName(lastName);
        }

        if (request.getPhoneNumber() != null) {
            String phone = request.getPhoneNumber().trim();
            if (phone.isEmpty()) {
                user.setPhoneNumber(null);
            } else {
                if (!phone.matches(PHONE_E164_REGEX)) {
                    throw new ValidationException("phoneNumber must be in E.164 format");
                }
                if (!phone.equals(user.getPhoneNumber())) {
                    Optional<User> existing = userRepository.findByPhoneNumberAndDeletedAtIsNull(phone);
                    if (existing.isPresent() && !existing.get().getUserId().equals(userId)) {
                        throw new IllegalStateException("Phone number already exists");
                    }
                }
                user.setPhoneNumber(phone);
            }
        }

        userRepository.save(user);

        auditService.record(userId, "USER", "PROFILE_UPDATE", "PROFILE", userId, "SUCCESS",
            "{\"fields\": [\"firstName\", \"lastName\", \"phoneNumber\"]}");

        return mapToUserResponse(user, profile);
    }

    @Transactional
    public void deleteUser(UUID userId, DeleteUserRequest request) {
        if (!"DELETE".equals(request.getConfirmation())) {
            throw new ValidationException("Confirmation must be 'DELETE'");
        }

        User user = findUser(userId);
        UserProfile profile = findOrCreateProfile(user);

        if ("OWNER".equals(profile.getFamilyRole())) {
            throw new AccessDeniedException("Cannot delete family owner account");
        }

        user.setDeletedAt(Instant.now());
        user.setAccountStatus("DELETED");
        userRepository.save(user);

        userSessionRepository.revokeAllByUserId(userId, Instant.now());

        auditService.record(userId, "USER", "ACCOUNT_DELETED", "USER", userId, "SUCCESS", "{}");
    }

    @Transactional
    public UserPreferencesResponse getUserPreferences(UUID userId) {
        User user = findUser(userId);
        UserProfile profile = findOrCreateProfile(user);
        UserPreference preference = findOrCreatePreferences(user);
        return mapToPreferencesResponse(user, profile, preference);
    }

    @Transactional
    public UserPreferencesResponse updateUserPreferences(UUID userId, UpdateUserPreferencesRequest request) {
        User user = findUser(userId);
        UserProfile profile = findOrCreateProfile(user);
        UserPreference preference = findOrCreatePreferences(user);

        if (request.getLocale() != null) {
            String locale = request.getLocale().trim();
            if (!locale.matches(LOCALE_REGEX)) {
                throw new ValidationException("locale must match language or language-region format");
            }
            profile.setLocale(locale);
        }

        if (request.getCurrency() != null) {
            String currency = request.getCurrency().trim().toUpperCase();
            if (!currency.matches(CURRENCY_REGEX)) {
                throw new ValidationException("currency must be a 3-letter ISO code");
            }
            profile.setCurrency(currency);
        }

        if (request.getTimezone() != null) {
            String timezone = request.getTimezone().trim();
            if (timezone.isEmpty() || timezone.length() > 50) {
                throw new ValidationException("timezone must be between 1 and 50 characters");
            }
            profile.setTimezone(timezone);
        }

        if (request.getNotificationPreferences() != null) {
            NotificationPreferencesDto n = request.getNotificationPreferences();
            if (n.getBudgetAlerts() == null || n.getBillReminders() == null ||
                n.getSavingsUpdates() == null || n.getFinancialHealthUpdates() == null) {
                throw new ValidationException("All notification preference booleans must be provided");
            }
            preference.setBudgetAlertsEnabled(n.getBudgetAlerts());
            preference.setBillRemindersEnabled(n.getBillReminders());
            preference.setSavingsUpdatesEnabled(n.getSavingsUpdates());
            preference.setFinancialHealthUpdatesEnabled(n.getFinancialHealthUpdates());
        }

        if (request.getAiPreferences() != null) {
            AiPreferencesDto a = request.getAiPreferences();
            if (a.getResponseStyle() == null || !VALID_RESPONSE_STYLES.contains(a.getResponseStyle().toUpperCase())) {
                throw new ValidationException("aiPreferences.responseStyle must be CONCISE or DETAILED");
            }
            if (a.getLanguage() == null || a.getLanguage().trim().isEmpty() || a.getLanguage().length() > 10) {
                throw new ValidationException("aiPreferences.language is required and must be <= 10 characters");
            }
            preference.setAiResponseStyle(a.getResponseStyle().toUpperCase());
            preference.setAiLanguage(a.getLanguage().trim());
        }

        userProfileRepository.save(profile);
        userPreferenceRepository.save(preference);

        auditService.record(userId, "USER", "PREFERENCES_UPDATE", "PREFERENCES", userId, "SUCCESS",
            "{\"fields\": [\"locale\", \"currency\", \"timezone\", \"notificationPreferences\", \"aiPreferences\"]}");

        return mapToPreferencesResponse(user, profile, preference);
    }

    private User findUser(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (user.getDeletedAt() != null) {
            throw new IllegalArgumentException("User account is deleted");
        }
        return user;
    }

    private UserProfile findOrCreateProfile(User user) {
        return userProfileRepository.findByUser_UserId(user.getUserId()).orElseGet(() -> {
            UserProfile p = new UserProfile();
            p.setUser(user);
            return userProfileRepository.save(p);
        });
    }

    private UserPreference findOrCreatePreferences(User user) {
        return userPreferenceRepository.findByUser_UserId(user.getUserId()).orElseGet(() -> {
            UserPreference p = new UserPreference();
            p.setUser(user);
            return userPreferenceRepository.save(p);
        });
    }

    private void validateName(String value, String field) {
        if (value.isEmpty() || value.length() > 100) {
            throw new ValidationException(field + " must be between 1 and 100 characters");
        }
        if (!value.matches(NAME_REGEX)) {
            throw new ValidationException(field + " contains invalid characters");
        }
    }

    private UserResponse mapToUserResponse(User user, UserProfile profile) {
        return UserResponse.builder()
            .userId(user.getUserId())
            .email(user.getEmail())
            .phoneNumber(user.getPhoneNumber())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .familyId(profile.getFamilyId())
            .familyRole(profile.getFamilyRole())
            .createdAt(user.getCreatedAt().toString())
            .updatedAt(user.getUpdatedAt().toString())
            .build();
    }

    private UserPreferencesResponse mapToPreferencesResponse(User user, UserProfile profile, UserPreference preference) {
        return UserPreferencesResponse.builder()
            .userId(user.getUserId())
            .locale(profile.getLocale())
            .currency(profile.getCurrency())
            .timezone(profile.getTimezone())
            .notificationPreferences(new NotificationPreferencesDto(
                preference.getBudgetAlertsEnabled(),
                preference.getBillRemindersEnabled(),
                preference.getSavingsUpdatesEnabled(),
                preference.getFinancialHealthUpdatesEnabled()))
            .aiPreferences(new AiPreferencesDto(
                preference.getAiResponseStyle(),
                preference.getAiLanguage()))
            .build();
    }

}

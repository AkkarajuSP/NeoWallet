package com.neowallet.identity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neowallet.config.AuthProperties;
import com.neowallet.identity.dto.DeleteUserRequest;
import com.neowallet.identity.dto.UpdateUserPreferencesRequest;
import com.neowallet.identity.dto.UpdateUserRequest;
import com.neowallet.identity.entity.User;
import com.neowallet.identity.entity.UserPreference;
import com.neowallet.identity.entity.UserProfile;
import com.neowallet.identity.repository.UserPreferenceRepository;
import com.neowallet.identity.repository.UserProfileRepository;
import com.neowallet.identity.repository.UserRepository;
import com.neowallet.identity.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE neowallet.audit_logs, neowallet.user_preferences, neowallet.user_profiles, neowallet.families, neowallet.users CASCADE");
    }

    private User createUser(String email, String phone) {
        User user = new User();
        user.setEmail(email);
        user.setPhoneNumber(phone);
        user.setPasswordHash("$2a$10$hashed");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setAccountStatus("ACTIVE");
        user.setEmailVerified(true);
        user = userRepository.save(user);

        UserProfile profile = new UserProfile();
        profile.setUser(user);
        userProfileRepository.save(profile);

        UserPreference pref = new UserPreference();
        pref.setUser(user);
        userPreferenceRepository.save(pref);

        return user;
    }

    private String tokenFor(User user) {
        return jwtService.generateAccessToken(user.getUserId(), null);
    }

    @Test
    void getCurrentUserReturnsOwnProfile() throws Exception {
        User user = createUser("alice@example.com", "+15551234567");
        String token = tokenFor(user);

        mockMvc.perform(get("/api/v1/users/me")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(user.getUserId().toString()))
            .andExpect(jsonPath("$.email").value("alice@example.com"))
            .andExpect(jsonPath("$.firstName").value("Test"))
            .andExpect(jsonPath("$.lastName").value("User"));
    }

    @Test
    void updateCurrentUser() throws Exception {
        User user = createUser("bob@example.com", "+15551234568");
        String token = tokenFor(user);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setFirstName("Robert");
        request.setLastName("Builder");
        request.setPhoneNumber("+15551234569");

        mockMvc.perform(put("/api/v1/users/me")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Robert"))
            .andExpect(jsonPath("$.lastName").value("Builder"))
            .andExpect(jsonPath("$.phoneNumber").value("+15551234569"));
    }

    @Test
    void updateCurrentUserWithInvalidPhoneReturns422() throws Exception {
        User user = createUser("carol@example.com", null);
        String token = tokenFor(user);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setPhoneNumber("123");

        mockMvc.perform(put("/api/v1/users/me")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void updateCurrentUserWithDuplicatePhoneReturns409() throws Exception {
        User alice = createUser("alice2@example.com", "+15551234570");
        User bob = createUser("bob2@example.com", "+15551234571");

        UpdateUserRequest request = new UpdateUserRequest();
        request.setPhoneNumber("+15551234570");

        mockMvc.perform(put("/api/v1/users/me")
                .header("Authorization", "Bearer " + tokenFor(bob))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict());
    }

    @Test
    void getUserPreferences() throws Exception {
        User user = createUser("dave@example.com", null);
        String token = tokenFor(user);

        mockMvc.perform(get("/api/v1/users/me/preferences")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(user.getUserId().toString()))
            .andExpect(jsonPath("$.locale").value("en-US"))
            .andExpect(jsonPath("$.currency").value("USD"))
            .andExpect(jsonPath("$.notificationPreferences.budgetAlerts").value(true));
    }

    @Test
    void updateUserPreferences() throws Exception {
        User user = createUser("eve@example.com", null);
        String token = tokenFor(user);

        UpdateUserPreferencesRequest request = new UpdateUserPreferencesRequest();
        request.setLocale("fr-FR");
        request.setCurrency("EUR");
        request.setTimezone("Europe/Paris");
        request.setNotificationPreferences(new com.neowallet.identity.dto.NotificationPreferencesDto(
            false, true, false, true));
        request.setAiPreferences(new com.neowallet.identity.dto.AiPreferencesDto("DETAILED", "fr"));

        mockMvc.perform(put("/api/v1/users/me/preferences")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.locale").value("fr-FR"))
            .andExpect(jsonPath("$.currency").value("EUR"))
            .andExpect(jsonPath("$.timezone").value("Europe/Paris"))
            .andExpect(jsonPath("$.notificationPreferences.budgetAlerts").value(false))
            .andExpect(jsonPath("$.aiPreferences.responseStyle").value("DETAILED"))
            .andExpect(jsonPath("$.aiPreferences.language").value("fr"));
    }

    @Test
    void updateUserPreferencesInvalidResponseStyleReturns422() throws Exception {
        User user = createUser("frank@example.com", null);
        String token = tokenFor(user);

        UpdateUserPreferencesRequest request = new UpdateUserPreferencesRequest();
        request.setAiPreferences(new com.neowallet.identity.dto.AiPreferencesDto("VERBOSE", "en"));

        mockMvc.perform(put("/api/v1/users/me/preferences")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deleteCurrentUser() throws Exception {
        User user = createUser("grace@example.com", null);
        String token = tokenFor(user);

        mockMvc.perform(delete("/api/v1/users/me")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new DeleteUserRequest("DELETE"))))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/users/me")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isBadRequest());
    }

    @Test
    void unauthenticatedRequestIsDenied() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))
            .andExpect(status().isForbidden());
    }

    @Test
    void userCanOnlyAccessOwnProfile() throws Exception {
        User alice = createUser("alice3@example.com", null);
        User bob = createUser("bob3@example.com", null);

        MvcResult result = mockMvc.perform(get("/api/v1/users/me")
                .header("Authorization", "Bearer " + tokenFor(alice)))
            .andExpect(status().isOk())
            .andReturn();

        String body = result.getResponse().getContentAsString();
        org.junit.jupiter.api.Assertions.assertTrue(body.contains(alice.getUserId().toString()));
        org.junit.jupiter.api.Assertions.assertFalse(body.contains(bob.getUserId().toString()));
    }

}

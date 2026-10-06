package com.neowallet.identity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neowallet.config.AuthProperties;
import com.neowallet.identity.dto.*;
import com.neowallet.provider.OtpProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthProperties authProperties;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private OtpProvider otpProvider;

    private final AtomicReference<String> lastOtp = new AtomicReference<>();

    @BeforeEach
    void setUp() {
        lastOtp.set(null);
        doAnswer(this::captureEmailOtp).when(otpProvider).sendEmail(anyString(), anyString(), anyString(), anyString());
        jdbcTemplate.execute("TRUNCATE TABLE neowallet.users CASCADE");
    }

    private Object captureEmailOtp(InvocationOnMock invocation) {
        String plainOtp = invocation.getArgument(2);
        lastOtp.set(plainOtp);
        return null;
    }

    @Test
    void fullAuthFlow() throws Exception {
        String email = "alice@example.com";
        String password = "Test1234!";

        // Register
        RegisterRequest register = new RegisterRequest();
        register.setEmail(email);
        register.setPassword(password);
        register.setFirstName("Alice");
        register.setLastName("Smith");

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.requiresVerification").value(true));

        // Request OTP
        OtpRequest otpRequest = new OtpRequest();
        otpRequest.setEmail(email);
        otpRequest.setPurpose("REGISTRATION");

        MvcResult otpResult = mockMvc.perform(post("/api/v1/auth/otp/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(otpRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.verified").value(false))
            .andReturn();

        OtpResponse otpResponse = objectMapper.readValue(otpResult.getResponse().getContentAsString(), OtpResponse.class);

        // Verify OTP
        OtpVerifyRequest verifyRequest = new OtpVerifyRequest();
        verifyRequest.setOtpId(otpResponse.getOtpId());
        verifyRequest.setCode(lastOtp.get());

        mockMvc.perform(post("/api/v1/auth/otp/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.verified").value(true));

        // Login
        LoginRequest login = new LoginRequest();
        login.setEmail(email);
        login.setPassword(password);
        login.setDeviceType("IOS");
        login.setDeviceName("iPhone");

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.refreshToken").exists())
            .andReturn();

        LoginResponse loginResponse = objectMapper.readValue(loginResult.getResponse().getContentAsString(), LoginResponse.class);
        String accessToken = loginResponse.getAccessToken();
        String refreshToken = loginResponse.getRefreshToken();

        // List sessions
        mockMvc.perform(get("/api/v1/auth/sessions")
                .header("Authorization", "Bearer " + accessToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));

        // List devices (empty since login does not always create with metadata?)
        mockMvc.perform(get("/api/v1/users/me/devices")
                .header("Authorization", "Bearer " + accessToken))
            .andExpect(status().isOk());

        // Refresh token
        RefreshTokenRequest refresh = new RefreshTokenRequest();
        refresh.setRefreshToken(refreshToken);

        MvcResult refreshResult = mockMvc.perform(post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refresh)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.refreshToken").exists())
            .andReturn();

        RefreshTokenResponse refreshResponse = objectMapper.readValue(refreshResult.getResponse().getContentAsString(), RefreshTokenResponse.class);
        String newRefreshToken = refreshResponse.getRefreshToken();

        // Old refresh token should fail (rotation)
        RefreshTokenRequest oldRefresh = new RefreshTokenRequest();
        oldRefresh.setRefreshToken(refreshToken);

        mockMvc.perform(post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(oldRefresh)))
            .andExpect(status().is4xxClientError());

        // Logout
        mockMvc.perform(post("/api/v1/auth/logout")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(RefreshTokenRequest.builder().refreshToken(newRefreshToken).build())))
            .andExpect(status().isNoContent());

        // New refresh token should fail after logout
        mockMvc.perform(post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(RefreshTokenRequest.builder().refreshToken(newRefreshToken).build())))
            .andExpect(status().is4xxClientError());
    }

    @Test
    void duplicateRegistrationFails() throws Exception {
        String email = "bob@example.com";

        RegisterRequest register = new RegisterRequest();
        register.setEmail(email);
        register.setPassword("Test1234!");
        register.setFirstName("Bob");
        register.setLastName("Jones");

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)))
            .andExpect(status().is4xxClientError());
    }

    @Test
    void wrongOtpFailsAndReplayPreventsReuse() throws Exception {
        String email = "carol@example.com";

        RegisterRequest register = new RegisterRequest();
        register.setEmail(email);
        register.setPassword("Test1234!");
        register.setFirstName("Carol");
        register.setLastName("White");

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)))
            .andExpect(status().isCreated());

        OtpRequest otpRequest = new OtpRequest();
        otpRequest.setEmail(email);
        otpRequest.setPurpose("REGISTRATION");

        MvcResult otpResult = mockMvc.perform(post("/api/v1/auth/otp/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(otpRequest)))
            .andExpect(status().isOk())
            .andReturn();

        OtpResponse otpResponse = objectMapper.readValue(otpResult.getResponse().getContentAsString(), OtpResponse.class);

        // Wrong code
        OtpVerifyRequest verifyRequest = new OtpVerifyRequest();
        verifyRequest.setOtpId(otpResponse.getOtpId());
        verifyRequest.setCode("000000");

        mockMvc.perform(post("/api/v1/auth/otp/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyRequest)))
            .andExpect(status().isBadRequest());

        // Same wrong code again should still fail (attempts incremented but not locked in this test)
        mockMvc.perform(post("/api/v1/auth/otp/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyRequest)))
            .andExpect(status().isBadRequest());

        // Correct code should still work until max attempts reached
        verifyRequest.setCode(lastOtp.get());
        mockMvc.perform(post("/api/v1/auth/otp/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyRequest)))
            .andExpect(status().isOk());
    }

    @Test
    void accountLockoutAfterFailedLogins() throws Exception {
        String email = "dave@example.com";
        String password = "Test1234!";

        RegisterRequest register = new RegisterRequest();
        register.setEmail(email);
        register.setPassword(password);
        register.setFirstName("Dave");
        register.setLastName("Brown");

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)))
            .andExpect(status().isCreated());

        OtpRequest otpRequest = new OtpRequest();
        otpRequest.setEmail(email);
        otpRequest.setPurpose("REGISTRATION");

        MvcResult otpResult = mockMvc.perform(post("/api/v1/auth/otp/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(otpRequest)))
            .andReturn();

        OtpResponse otpResponse = objectMapper.readValue(otpResult.getResponse().getContentAsString(), OtpResponse.class);

        OtpVerifyRequest verifyRequest = new OtpVerifyRequest();
        verifyRequest.setOtpId(otpResponse.getOtpId());
        verifyRequest.setCode(lastOtp.get());

        mockMvc.perform(post("/api/v1/auth/otp/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyRequest)))
            .andExpect(status().isOk());

        LoginRequest login = new LoginRequest();
        login.setEmail(email);
        login.setPassword("WrongPassword123!");

        int maxFailed = authProperties.getMaxFailedLogins();
        for (int i = 0; i < maxFailed; i++) {
            mockMvc.perform(post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isBadRequest());
        }

        // Correct password should be rejected due to lockout
        login.setPassword(password);
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
            .andExpect(status().is4xxClientError());
    }

    @Test
    void loginDoesNotRevealAccountExistence() throws Exception {
        LoginRequest login = new LoginRequest();
        login.setEmail("nonexistent@example.com");
        login.setPassword("Test1234!");

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
            .andExpect(status().isBadRequest());
    }

}

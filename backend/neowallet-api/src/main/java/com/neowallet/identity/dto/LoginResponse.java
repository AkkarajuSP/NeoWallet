package com.neowallet.identity.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private UserSummary user;

    @Data
    @Builder
    public static class UserSummary {
        private UUID userId;
        private String email;
        private String firstName;
        private String lastName;
    }

}

package com.neowallet.identity.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class RegisterResponse {

    private UUID userId;
    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private Instant createdAt;
    private boolean requiresVerification;

}

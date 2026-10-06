package com.neowallet.identity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamilyInvitationResponse {

    private UUID invitationId;
    private String token;
    private String email;
    private String role;
    private String expiresAt;
    private String createdAt;

}

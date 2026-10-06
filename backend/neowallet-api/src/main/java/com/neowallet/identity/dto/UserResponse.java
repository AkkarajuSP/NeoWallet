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
public class UserResponse {

    private UUID userId;
    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private UUID familyId;
    private String familyRole;
    private String createdAt;
    private String updatedAt;

}

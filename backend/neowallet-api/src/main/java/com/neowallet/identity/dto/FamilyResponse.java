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
public class FamilyResponse {

    private UUID familyId;
    private String name;
    private String currency;
    private UUID ownerId;
    private Integer memberCount;
    private String createdAt;
    private String updatedAt;

}

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
public class UserPreferencesResponse {

    private UUID userId;
    private String locale;
    private String currency;
    private String timezone;
    private NotificationPreferencesDto notificationPreferences;
    private AiPreferencesDto aiPreferences;

}

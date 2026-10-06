package com.neowallet.identity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserPreferencesRequest {

    private String locale;
    private String currency;
    private String timezone;
    private NotificationPreferencesDto notificationPreferences;
    private AiPreferencesDto aiPreferences;

}

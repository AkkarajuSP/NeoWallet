package com.neowallet.identity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferencesDto {

    private Boolean budgetAlerts;
    private Boolean billReminders;
    private Boolean savingsUpdates;
    private Boolean financialHealthUpdates;

}

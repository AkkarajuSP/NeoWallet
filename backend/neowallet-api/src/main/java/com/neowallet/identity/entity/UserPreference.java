package com.neowallet.identity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "user_preferences", schema = "neowallet")
@Getter
@Setter
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "preference_id")
    private UUID preferenceId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "budget_alerts_enabled", nullable = false)
    private Boolean budgetAlertsEnabled = true;

    @Column(name = "budget_alert_threshold_percentage", nullable = false)
    private Integer budgetAlertThresholdPercentage = 80;

    @Column(name = "bill_reminders_enabled", nullable = false)
    private Boolean billRemindersEnabled = true;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "bill_reminder_days", columnDefinition = "INTEGER[]", nullable = false)
    private List<Integer> billReminderDays = new ArrayList<>(List.of(3, 7));

    @Column(name = "savings_updates_enabled", nullable = false)
    private Boolean savingsUpdatesEnabled = true;

    @Column(name = "savings_updates_frequency", nullable = false, length = 10)
    private String savingsUpdatesFrequency = "WEEKLY";

    @Column(name = "financial_health_updates_enabled", nullable = false)
    private Boolean financialHealthUpdatesEnabled = true;

    @Column(name = "financial_health_updates_frequency", nullable = false, length = 10)
    private String financialHealthUpdatesFrequency = "MONTHLY";

    @Column(name = "ai_response_style", nullable = false, length = 10)
    private String aiResponseStyle = "CONCISE";

    @Column(name = "ai_language", nullable = false, length = 10)
    private String aiLanguage = "en";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

}

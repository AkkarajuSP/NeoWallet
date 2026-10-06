package com.neowallet.finance.entity;

import com.neowallet.identity.entity.Family;
import com.neowallet.identity.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "budget_recommendations", schema = "neowallet")
@Getter
@Setter
public class BudgetRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "recommendation_id")
    private UUID recommendationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id")
    private Budget budget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id")
    private Family family;

    @Column(nullable = false, length = 7)
    private String period;

    @Column(nullable = false, length = 3)
    private String currency = "USD";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "recommended_allocation", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> recommendedAllocation;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "user_modification", columnDefinition = "jsonb")
    private Map<String, Object> userModification;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "final_approved_budget", columnDefinition = "jsonb")
    private Map<String, Object> finalApprovedBudget;

    @Column(nullable = false, length = 20)
    private String status = "PENDING";

    @Column(nullable = false, length = 10)
    private String confidence = "MEDIUM";

    @Column(name = "historical_months_used", nullable = false)
    private Integer historicalMonthsUsed = 0;

    @CreationTimestamp
    @Column(name = "generated_at", nullable = false, updatable = false)
    private Instant generatedAt;

    @Column(name = "approved_at")
    private Instant approvedAt;
}

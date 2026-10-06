package com.neowallet.finance.entity;

import com.neowallet.identity.entity.Family;
import com.neowallet.identity.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "financial_overviews", schema = "neowallet")
@Getter
@Setter
public class FinancialOverview {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "overview_id")
    private UUID overviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id")
    private Family family;

    @Column(name = "planning_income", nullable = false, precision = 15, scale = 2)
    private BigDecimal planningIncome = BigDecimal.ZERO;

    @Column(name = "mandatory_commitments", nullable = false, precision = 15, scale = 2)
    private BigDecimal mandatoryCommitments = BigDecimal.ZERO;

    @Column(name = "essential_allocation", nullable = false, precision = 15, scale = 2)
    private BigDecimal essentialAllocation = BigDecimal.ZERO;

    @Column(name = "variable_allocation", nullable = false, precision = 15, scale = 2)
    private BigDecimal variableAllocation = BigDecimal.ZERO;

    @Column(name = "savings_allocation", nullable = false, precision = 15, scale = 2)
    private BigDecimal savingsAllocation = BigDecimal.ZERO;

    @Column(name = "emergency_allocation", nullable = false, precision = 15, scale = 2)
    private BigDecimal emergencyAllocation = BigDecimal.ZERO;

    @Column(name = "discretionary_planning", nullable = false, precision = 15, scale = 2)
    private BigDecimal discretionaryPlanning = BigDecimal.ZERO;

    @Column(name = "committed_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal committedAmount = BigDecimal.ZERO;

    @Column(name = "pending_payments", nullable = false, precision = 15, scale = 2)
    private BigDecimal pendingPayments = BigDecimal.ZERO;

    @Column(name = "actual_transactions", nullable = false, precision = 15, scale = 2)
    private BigDecimal actualTransactions = BigDecimal.ZERO;

    @Column(name = "available_financial_capacity", nullable = false, precision = 15, scale = 2)
    private BigDecimal availableFinancialCapacity = BigDecimal.ZERO;

    @Column(nullable = false, length = 3)
    private String currency = "USD";

    @Column(nullable = false, length = 7)
    private String period;

    @Column(name = "calculated_at", nullable = false)
    private Instant calculatedAt = Instant.now();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

}

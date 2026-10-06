package com.neowallet.finance.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "financial_periods", schema = "neowallet")
@Getter
@Setter
public class FinancialPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "period_id")
    private UUID periodId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "overview_id", nullable = false)
    private FinancialOverview overview;

    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    @Column(name = "period_type", nullable = false, length = 20)
    private String periodType;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

}

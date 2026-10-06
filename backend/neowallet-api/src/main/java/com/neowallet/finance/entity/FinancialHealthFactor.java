package com.neowallet.finance.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "financial_health_factors", schema = "neowallet")
@Getter
@Setter
public class FinancialHealthFactor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "factor_id")
    private UUID factorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "score_id", nullable = false)
    private FinancialHealthScore score;

    @Column(name = "factor_name", nullable = false, length = 50)
    private String factorName;

    @Column(name = "factor_score", nullable = false)
    private Integer factorScore;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal weight;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal contribution;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}

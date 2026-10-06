package com.neowallet.finance.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "financial_health_calculations", schema = "neowallet")
@Getter
@Setter
public class FinancialHealthCalculation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "calculation_id")
    private UUID calculationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "score_id", nullable = false)
    private FinancialHealthScore score;

    @Column(name = "calculation_version", nullable = false, length = 20)
    private String calculationVersion;

    @Column(name = "input_snapshot", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private String inputSnapshot;

    @Column(name = "factor_scores", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private String factorScores;

    @Column(name = "weights", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private String weights;

    @Column(name = "calculated_at", nullable = false)
    private Instant calculatedAt = Instant.now();
}

package com.neowallet.finance.entity;

import com.neowallet.identity.entity.Family;
import com.neowallet.identity.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "financial_health_scores", schema = "neowallet")
@Getter
@Setter
public class FinancialHealthScore {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "score_id")
    private UUID scoreId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id")
    private Family family;

    @Column(name = "overall_score", nullable = false)
    private Integer overallScore;

    @Column(name = "score_label", nullable = false, length = 20)
    private String scoreLabel;

    @Column(nullable = false, length = 10)
    private String confidence;

    @Column(nullable = false, length = 20)
    private String status = "FINAL";

    @Column(name = "calculated_at", nullable = false)
    private Instant calculatedAt = Instant.now();
}

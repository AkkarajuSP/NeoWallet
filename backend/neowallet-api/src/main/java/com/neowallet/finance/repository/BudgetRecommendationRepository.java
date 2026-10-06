package com.neowallet.finance.repository;

import com.neowallet.finance.entity.BudgetRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BudgetRecommendationRepository extends JpaRepository<BudgetRecommendation, UUID> {
    Optional<BudgetRecommendation> findTopByUser_UserIdAndFamily_FamilyIdAndPeriodOrderByGeneratedAtDesc(UUID userId, UUID familyId, String period);
}

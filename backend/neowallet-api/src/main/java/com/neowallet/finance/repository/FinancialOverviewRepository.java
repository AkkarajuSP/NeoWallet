package com.neowallet.finance.repository;

import com.neowallet.finance.entity.FinancialOverview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FinancialOverviewRepository extends JpaRepository<FinancialOverview, UUID> {
    Optional<FinancialOverview> findByUser_UserIdAndPeriod(UUID userId, String period);
    Optional<FinancialOverview> findByFamily_FamilyIdAndPeriod(UUID familyId, String period);

    Optional<FinancialOverview> findFirstByUser_UserIdOrderByPeriodDesc(UUID userId);
    Optional<FinancialOverview> findFirstByFamily_FamilyIdOrderByPeriodDesc(UUID familyId);
}

package com.neowallet.finance.repository;

import com.neowallet.finance.entity.FinancialHealthScore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FinancialHealthScoreRepository extends JpaRepository<FinancialHealthScore, UUID> {

    Optional<FinancialHealthScore> findTopByUser_UserIdAndFamilyIsNullOrderByCalculatedAtDesc(UUID userId);

    Optional<FinancialHealthScore> findTopByFamily_FamilyIdOrderByCalculatedAtDesc(UUID familyId);

    Page<FinancialHealthScore> findByUser_UserIdAndFamilyIsNullOrderByCalculatedAtDesc(UUID userId, Pageable pageable);

    Page<FinancialHealthScore> findByFamily_FamilyIdOrderByCalculatedAtDesc(UUID familyId, Pageable pageable);
}

package com.neowallet.finance.repository;

import com.neowallet.finance.entity.FinancialHealthCalculation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FinancialHealthCalculationRepository extends JpaRepository<FinancialHealthCalculation, UUID> {

    Optional<FinancialHealthCalculation> findByScore_ScoreId(UUID scoreId);
}

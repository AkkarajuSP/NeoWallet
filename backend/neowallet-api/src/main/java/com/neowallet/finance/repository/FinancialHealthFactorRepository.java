package com.neowallet.finance.repository;

import com.neowallet.finance.entity.FinancialHealthFactor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FinancialHealthFactorRepository extends JpaRepository<FinancialHealthFactor, UUID> {

    List<FinancialHealthFactor> findByScore_ScoreIdOrderByFactorNameAsc(UUID scoreId);
}

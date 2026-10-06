package com.neowallet.finance.repository;

import com.neowallet.finance.entity.BudgetPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BudgetPeriodRepository extends JpaRepository<BudgetPeriod, UUID> {
}

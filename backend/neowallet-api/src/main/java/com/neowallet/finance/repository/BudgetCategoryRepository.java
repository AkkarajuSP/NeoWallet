package com.neowallet.finance.repository;

import com.neowallet.finance.entity.BudgetCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BudgetCategoryRepository extends JpaRepository<BudgetCategory, UUID> {
    List<BudgetCategory> findByBudget_BudgetId(UUID budgetId);
    void deleteByBudget_BudgetId(UUID budgetId);
}

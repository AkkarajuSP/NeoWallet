package com.neowallet.finance.repository;

import com.neowallet.finance.entity.Budget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, UUID> {

    Optional<Budget> findByBudgetIdAndStatusNot(UUID budgetId, String status);

    @Query("""
            SELECT b FROM Budget b
            LEFT JOIN FETCH b.categories
            WHERE b.budgetId = :budgetId AND b.status != 'DELETED'
            """)
    Optional<Budget> findByBudgetIdWithCategories(@Param("budgetId") UUID budgetId);

    @Query("""
            SELECT b FROM Budget b
            WHERE (b.user.userId = :userId OR b.family.familyId = :familyId)
            AND b.status != 'DELETED'
            AND (:period IS NULL OR b.period = :period)
            """)
    Page<Budget> findBudgets(
            @Param("userId") UUID userId,
            @Param("familyId") UUID familyId,
            @Param("period") String period,
            Pageable pageable);

    boolean existsByUser_UserIdAndPeriodAndNameAndStatusNot(UUID userId, String period, String name, String status);

    boolean existsByFamily_FamilyIdAndPeriodAndNameAndStatusNot(UUID familyId, String period, String name, String status);
}

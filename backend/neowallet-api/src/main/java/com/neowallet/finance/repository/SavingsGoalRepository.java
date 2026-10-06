package com.neowallet.finance.repository;

import com.neowallet.finance.entity.SavingsGoal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, UUID> {

    Optional<SavingsGoal> findByGoalIdAndStatusNot(UUID goalId, String status);

    @Query("""
            SELECT sg FROM SavingsGoal sg
            WHERE (sg.user.userId = :userId OR sg.family.familyId = :familyId)
            AND sg.status != 'CANCELLED'
            AND (:status IS NULL OR sg.status = :status)
            AND (:priority IS NULL OR sg.priority = :priority)
            """)
    Page<SavingsGoal> findSavingsGoals(
            @Param("userId") UUID userId,
            @Param("familyId") UUID familyId,
            @Param("status") String status,
            @Param("priority") String priority,
            Pageable pageable);

    boolean existsByUser_UserIdAndNameAndStatusNot(UUID userId, String name, String status);

    boolean existsByFamily_FamilyIdAndNameAndStatusNot(UUID familyId, String name, String status);
}

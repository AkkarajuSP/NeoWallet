package com.neowallet.finance.repository;

import com.neowallet.finance.entity.Bill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BillRepository extends JpaRepository<Bill, UUID> {

    Optional<Bill> findByBillIdAndDeletedAtIsNull(UUID billId);

    @Query("""
            SELECT b FROM Bill b
            WHERE b.deletedAt IS NULL
            AND ((:userId IS NOT NULL AND b.user.userId = :userId) OR (:familyId IS NOT NULL AND b.family.familyId = :familyId))
            AND (:status IS NULL OR b.status = :status)
            AND (:category IS NULL OR b.category = :category)
            AND (:dueDateFrom IS NULL OR b.dueDate >= :dueDateFrom)
            AND (:dueDateTo IS NULL OR b.dueDate <= :dueDateTo)
            """)
    Page<Bill> findBills(
            @Param("userId") UUID userId,
            @Param("familyId") UUID familyId,
            @Param("status") String status,
            @Param("category") String category,
            @Param("dueDateFrom") java.time.LocalDate dueDateFrom,
            @Param("dueDateTo") java.time.LocalDate dueDateTo,
            Pageable pageable);

    @Query("""
            SELECT b FROM Bill b
            WHERE b.deletedAt IS NULL
            AND ((:userId IS NOT NULL AND b.user.userId = :userId) OR (:familyId IS NOT NULL AND b.family.familyId = :familyId))
            AND b.dueDate >= :dueDateFrom
            AND b.dueDate <= :dueDateTo
            """)
    List<Bill> findBillsDueBetween(
            @Param("userId") UUID userId,
            @Param("familyId") UUID familyId,
            @Param("dueDateFrom") java.time.LocalDate dueDateFrom,
            @Param("dueDateTo") java.time.LocalDate dueDateTo);

    boolean existsByUser_UserIdAndNameAndDeletedAtIsNull(UUID userId, String name);

    boolean existsByFamily_FamilyIdAndNameAndDeletedAtIsNull(UUID familyId, String name);
}

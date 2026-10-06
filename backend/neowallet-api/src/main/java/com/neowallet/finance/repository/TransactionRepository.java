package com.neowallet.finance.repository;

import com.neowallet.finance.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID>, JpaSpecificationExecutor<Transaction> {
    Optional<Transaction> findByTransactionIdAndDeletedAtIsNull(UUID transactionId);

    Page<Transaction> findAll(Specification<Transaction> spec, Pageable pageable);

    @Query("""
            SELECT t FROM Transaction t
            WHERE t.deletedAt IS NULL
            AND ((:userId IS NOT NULL AND t.user.userId = :userId) OR (:familyId IS NOT NULL AND t.family.familyId = :familyId))
            AND t.transactionDate >= :fromDate
            AND t.transactionDate <= :toDate
            """)
    List<Transaction> findTransactionsForDateRange(
            @Param("userId") UUID userId,
            @Param("familyId") UUID familyId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);
}

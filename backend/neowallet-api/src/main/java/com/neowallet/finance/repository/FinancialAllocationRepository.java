package com.neowallet.finance.repository;

import com.neowallet.finance.entity.FinancialAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FinancialAllocationRepository extends JpaRepository<FinancialAllocation, UUID> {
    List<FinancialAllocation> findByOverview_OverviewId(UUID overviewId);
}

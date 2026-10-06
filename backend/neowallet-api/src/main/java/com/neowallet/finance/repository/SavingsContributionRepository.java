package com.neowallet.finance.repository;

import com.neowallet.finance.entity.SavingsContribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SavingsContributionRepository extends JpaRepository<SavingsContribution, UUID> {

    List<SavingsContribution> findByGoal_GoalIdOrderByContributionDateDesc(UUID goalId);
}

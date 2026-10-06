package com.neowallet.finance.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neowallet.finance.dto.*;
import com.neowallet.finance.engine.FinancialHealthCalculationEngine;
import com.neowallet.finance.engine.FinancialHealthInput;
import com.neowallet.finance.engine.FinancialHealthResult;
import com.neowallet.finance.entity.*;
import com.neowallet.finance.repository.*;
import com.neowallet.identity.entity.Family;
import com.neowallet.identity.entity.FamilyMember;
import com.neowallet.identity.entity.User;
import com.neowallet.identity.repository.FamilyMemberRepository;
import com.neowallet.identity.repository.FamilyRepository;
import com.neowallet.identity.repository.UserRepository;
import com.neowallet.identity.service.AuditService;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinancialHealthService {

    private final FinancialHealthScoreRepository scoreRepository;
    private final FinancialHealthFactorRepository factorRepository;
    private final FinancialHealthCalculationRepository calculationRepository;
    private final FinancialOverviewRepository overviewRepository;
    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final SavingsGoalRepository savingsGoalRepository;
    private final BillRepository billRepository;
    private final UserRepository userRepository;
    private final FamilyRepository familyRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    private static final String ALGORITHM_VERSION = FinancialHealthCalculationEngine.ALGORITHM_VERSION;

    @Transactional
    public FinancialHealthResponse getFinancialHealth(UUID userId, Optional<UUID> familyId) {
        authorize(userId, familyId);

        FinancialHealthResult result = calculate(userId, familyId);
        FinancialHealthScore score = persistScore(userId, familyId, result);

        String actorType = familyId.isPresent() ? "FAMILY_MEMBER" : "USER";
        auditService.record(userId, actorType, "FINANCIAL_HEALTH_CALCULATED", "FINANCIAL_HEALTH", score.getScoreId(), "SUCCESS",
                toJson(Map.of("overallScore", String.valueOf(result.overallScore()), "label", result.scoreLabel(), "confidence", result.confidence())));

        return toResponse(score);
    }

    @Transactional
    public FinancialHealthFactorsResponse getFactors(UUID userId, Optional<UUID> familyId) {
        authorize(userId, familyId);
        FinancialHealthScore latest = findLatest(userId, familyId)
                .orElseGet(() -> persistScore(userId, familyId, calculate(userId, familyId)));
        return toFactorsResponse(latest);
    }

    @Transactional(readOnly = true)
    public FinancialHealthHistoryResponse getHistory(UUID userId, Optional<UUID> familyId, int page, int limit, String sort) {
        authorize(userId, familyId);
        Sort s = parseSort(sort);
        Pageable pageable = PageRequest.of(page - 1, limit, s);
        Page<FinancialHealthScore> scores;
        if (familyId.isPresent()) {
            scores = scoreRepository.findByFamily_FamilyIdOrderByCalculatedAtDesc(familyId.get(), pageable);
        } else {
            scores = scoreRepository.findByUser_UserIdAndFamilyIsNullOrderByCalculatedAtDesc(userId, pageable);
        }
        return new FinancialHealthHistoryResponse(
                scores.getContent().stream().map(this::toResponse).toList(),
                new FinancialHealthHistoryResponse.PaginationDto(page, limit, scores.getTotalElements(), scores.getTotalPages())
        );
    }

    @Transactional
    public FinancialHealthExplanationResponse getExplanation(UUID userId, Optional<UUID> familyId) {
        authorize(userId, familyId);
        FinancialHealthScore latest = findLatest(userId, familyId)
                .orElseGet(() -> persistScore(userId, familyId, calculate(userId, familyId)));
        List<FinancialHealthFactor> factors = factorRepository.findByScore_ScoreIdOrderByFactorNameAsc(latest.getScoreId());
        return buildExplanationResponse(latest, factors);
    }

    private FinancialHealthResult calculate(UUID userId, Optional<UUID> familyId) {
        YearMonth current = YearMonth.now();
        String period = current.format(DateTimeFormatter.ofPattern("yyyy-MM"));

        FinancialOverview overview = familyId
                .map(fid -> overviewRepository.findByFamily_FamilyIdAndPeriod(fid, period))
                .orElseGet(() -> overviewRepository.findByUser_UserIdAndPeriod(userId, period))
                .orElse(null);

        if (overview == null) {
            overview = new FinancialOverview();
            overview.setPlanningIncome(BigDecimal.ZERO);
            overview.setSavingsAllocation(BigDecimal.ZERO);
            overview.setEmergencyAllocation(BigDecimal.ZERO);
            overview.setEssentialAllocation(BigDecimal.ZERO);
        }

        if (overview.getPlanningIncome() == null || overview.getPlanningIncome().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Income is required for score calculation");
        }

        List<Budget> budgets = familyId
                .map(fid -> budgetRepository.findBudgets(null, fid, period, Pageable.unpaged()))
                .orElseGet(() -> budgetRepository.findBudgets(userId, null, period, Pageable.unpaged()))
                .getContent();

        List<FinancialHealthInput.BudgetCategoryInput> budgetCategories = budgets.stream()
                .flatMap(b -> b.getCategories().stream())
                .map(c -> new FinancialHealthInput.BudgetCategoryInput(c.getCategoryName(), c.getLimitAmount(), c.getSpentAmount()))
                .toList();

        LocalDate startDate = current.minusMonths(3).atDay(1);
        LocalDate endDate = current.atEndOfMonth();

        List<Transaction> transactions = transactionRepository.findTransactionsForDateRange(
                familyId.isEmpty() ? userId : null,
                familyId.orElse(null),
                startDate,
                endDate);

        Map<String, BigDecimal> monthlyTotals = transactions.stream()
                .filter(t -> "EXPENSE".equals(t.getType()) || "ADJUSTMENT".equals(t.getType()))
                .collect(Collectors.groupingBy(
                        t -> t.getTransactionDate().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                        Collectors.mapping(Transaction::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        List<FinancialHealthInput.MonthlySpendingInput> monthlySpendings = new ArrayList<>();
        for (int i = -3; i <= 0; i++) {
            YearMonth ym = current.plusMonths(i);
            String key = ym.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            BigDecimal total = monthlyTotals.getOrDefault(key, BigDecimal.ZERO);
            if (i == 0) {
                total = total.add(monthlyTotals.getOrDefault("REFUND".equals(key) ? null : null, BigDecimal.ZERO)); // no-op
            }
            monthlySpendings.add(new FinancialHealthInput.MonthlySpendingInput(key, total));
        }

        BigDecimal plannedSavings = overview.getSavingsAllocation().add(overview.getEmergencyAllocation());
        BigDecimal actualSavings = transactions.stream()
                .filter(t -> t.getTransactionDate().getMonthValue() == current.getMonthValue() && t.getTransactionDate().getYear() == current.getYear())
                .filter(t -> "EXPENSE".equals(t.getType()) || "ADJUSTMENT".equals(t.getType()))
                .filter(t -> isSavingsCategory(t.getCategoryName()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Bill> bills = billRepository.findBillsDueBetween(
                familyId.isEmpty() ? userId : null,
                familyId.orElse(null),
                current.atDay(1), current.atEndOfMonth());

        List<FinancialHealthInput.BillInput> billInputs = bills.stream()
                .map(b -> new FinancialHealthInput.BillInput(b.getDueDate(), b.getPaidDate(), b.getStatus()))
                .toList();

        BigDecimal emergencyTarget = overview.getEssentialAllocation().multiply(new BigDecimal("3"));
        BigDecimal emergencyCurrent = overview.getEmergencyAllocation();

        Page<SavingsGoal> goals = familyId
                .map(fid -> savingsGoalRepository.findSavingsGoals(null, fid, null, null, Pageable.unpaged()))
                .orElseGet(() -> savingsGoalRepository.findSavingsGoals(userId, null, null, null, Pageable.unpaged()));

        List<FinancialHealthInput.SavingsGoalInput> goalInputs = goals.getContent().stream()
                .map(g -> new FinancialHealthInput.SavingsGoalInput(g.getCurrentAmount(), g.getTargetAmount(), g.getTargetDate() == null ? null : g.getTargetDate()))
                .toList();

        Integer previousScore = findLatest(userId, familyId)
                .map(FinancialHealthScore::getOverallScore)
                .orElse(null);

        int historicalMonths = (int) transactions.stream()
                .map(t -> t.getTransactionDate().format(DateTimeFormatter.ofPattern("yyyy-MM")))
                .distinct()
                .count();

        FinancialHealthInput input = new FinancialHealthInput(
                overview.getPlanningIncome(),
                historicalMonths,
                budgetCategories,
                plannedSavings,
                actualSavings,
                monthlySpendings,
                billInputs,
                emergencyTarget,
                emergencyCurrent,
                goalInputs,
                previousScore,
                familyId.isPresent()
        );

        return new FinancialHealthCalculationEngine().calculate(input);
    }

    private FinancialHealthScore persistScore(UUID userId, Optional<UUID> familyId, FinancialHealthResult result) {
        FinancialHealthScore score = new FinancialHealthScore();
        if (familyId.isEmpty()) {
            score.setUser(userRepository.getReferenceById(userId));
        } else {
            score.setFamily(familyRepository.getReferenceById(familyId.get()));
        }
        score.setOverallScore(result.overallScore());
        score.setScoreLabel(result.scoreLabel());
        score.setConfidence(result.confidence());
        score.setStatus(result.status());
        score.setCalculatedAt(result.calculatedAt());
        score = scoreRepository.saveAndFlush(score);

        for (FinancialHealthResult.FactorResult f : result.factors()) {
            FinancialHealthFactor factor = new FinancialHealthFactor();
            factor.setScore(score);
            factor.setFactorName(f.name());
            factor.setFactorScore(f.score());
            factor.setWeight(f.weight());
            factor.setContribution(f.contribution());
            factorRepository.save(factor);
        }

        FinancialHealthCalculation calc = new FinancialHealthCalculation();
        calc.setScore(score);
        calc.setCalculationVersion(result.algorithmVersion());
        try {
            calc.setInputSnapshot(objectMapper.writeValueAsString(result.notes()));
            calc.setFactorScores(objectMapper.writeValueAsString(result.factors()));
            calc.setWeights(objectMapper.writeValueAsString(result.factors().stream().map(FinancialHealthResult.FactorResult::weight).toList()));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize calculation snapshot", e);
        }
        calc.setCalculatedAt(result.calculatedAt());
        calculationRepository.save(calc);

        return score;
    }

    private Optional<FinancialHealthScore> findLatest(UUID userId, Optional<UUID> familyId) {
        if (familyId.isPresent()) {
            return scoreRepository.findTopByFamily_FamilyIdOrderByCalculatedAtDesc(familyId.get());
        }
        return scoreRepository.findTopByUser_UserIdAndFamilyIsNullOrderByCalculatedAtDesc(userId);
    }

    private void authorize(UUID userId, Optional<UUID> familyId) {
        if (familyId.isEmpty()) {
            userRepository.findById(userId).orElseThrow(() -> new AccessDeniedException("User not found"));
            return;
        }
        familyMemberRepository
                .findByFamily_FamilyIdAndUser_UserIdAndLeftAtIsNull(familyId.get(), userId)
                .orElseThrow(() -> new AccessDeniedException("Not a family member"));
    }

    private boolean isSavingsCategory(String categoryName) {
        if (categoryName == null) return false;
        String c = categoryName.toLowerCase();
        return c.contains("savings") || c.contains("emergency") || c.contains("goal");
    }

    private Sort parseSort(String sort) {
        String[] parts = sort.split(":");
        Sort.Direction direction = parts.length > 1 && parts[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, parts[0]);
    }

    private FinancialHealthResponse toResponse(FinancialHealthScore score) {
        return new FinancialHealthResponse(
                score.getScoreId().toString(),
                score.getUser() != null ? score.getUser().getUserId().toString() : null,
                score.getFamily() != null ? score.getFamily().getFamilyId().toString() : null,
                score.getOverallScore(),
                score.getScoreLabel(),
                score.getConfidence(),
                score.getStatus(),
                score.getCalculatedAt(),
                "This is an educational indicator, not a regulated financial assessment"
        );
    }

    private FinancialHealthFactorsResponse toFactorsResponse(FinancialHealthScore score) {
        List<FinancialHealthFactor> factors = factorRepository.findByScore_ScoreIdOrderByFactorNameAsc(score.getScoreId());
        Map<String, FactorScoreDto> map = new HashMap<>();
        for (FinancialHealthFactor f : factors) {
            map.put(f.getFactorName(), new FactorScoreDto(f.getFactorScore(), f.getWeight(), f.getContribution()));
        }
        return new FinancialHealthFactorsResponse(score.getScoreId().toString(), map, score.getCalculatedAt());
    }

    private FinancialHealthExplanationResponse buildExplanationResponse(FinancialHealthScore score, List<FinancialHealthFactor> factors) {
        List<FinancialHealthExplanationResponse.ContributorDto> positive = factors.stream()
                .filter(f -> f.getFactorScore() >= 75)
                .map(f -> new FinancialHealthExplanationResponse.ContributorDto(f.getFactorName(), f.getFactorScore(), getPraise(f.getFactorName())))
                .toList();
        List<FinancialHealthExplanationResponse.ContributorDto> negative = factors.stream()
                .filter(f -> f.getFactorScore() < 60)
                .map(f -> new FinancialHealthExplanationResponse.ContributorDto(f.getFactorName(), f.getFactorScore(), getConcern(f.getFactorName())))
                .toList();
        List<FinancialHealthExplanationResponse.RecommendedActionDto> recommendations = factors.stream()
                .filter(f -> f.getFactorScore() < getThreshold(f.getFactorName()))
                .map(f -> new FinancialHealthExplanationResponse.RecommendedActionDto(getActionText(f.getFactorName()), "HIGH"))
                .toList();
        FinancialHealthExplanationResponse.ScoreChangeDto scoreChange = new FinancialHealthExplanationResponse.ScoreChangeDto(null, score.getOverallScore(), null, "STABLE", "MINOR", List.of());
        return new FinancialHealthExplanationResponse(
                score.getScoreId().toString(),
                score.getOverallScore(),
                score.getScoreLabel(),
                "Financial health score calculated deterministically using the approved v" + ALGORITHM_VERSION + " algorithm",
                positive,
                negative,
                recommendations,
                scoreChange,
                score.getCalculatedAt()
        );
    }

    private String getPraise(String factor) {
        return switch (factor) {
            case "budgetAdherence" -> "Budget adherence is strong";
            case "savingsBehavior" -> "Savings behavior is good";
            case "expenseTrend" -> "Expense trend is stable";
            case "billDiscipline" -> "Bill payment discipline is excellent";
            case "emergencyPreparedness" -> "Emergency fund is adequate";
            case "goalProgress" -> "Savings goals are on track";
            default -> factor + " is positive";
        };
    }

    private String getConcern(String factor) {
        return switch (factor) {
            case "budgetAdherence" -> "Budget adherence needs improvement";
            case "savingsBehavior" -> "Savings below target";
            case "expenseTrend" -> "Expenses are trending up";
            case "billDiscipline" -> "Bills need attention";
            case "emergencyPreparedness" -> "Emergency fund needs improvement";
            case "goalProgress" -> "Savings goals behind target";
            default -> factor + " needs attention";
        };
    }

    private int getThreshold(String factor) {
        return switch (factor) {
            case "budgetAdherence" -> 70;
            case "savingsBehavior" -> 60;
            case "expenseTrend" -> 70;
            case "emergencyPreparedness" -> 50;
            case "goalProgress" -> 50;
            default -> 50;
        };
    }

    private String getActionText(String factor) {
        return switch (factor) {
            case "budgetAdherence" -> "Review high-spending categories";
            case "savingsBehavior" -> "Review savings allocation";
            case "expenseTrend" -> "Review variable expenses";
            case "emergencyPreparedness" -> "Consider emergency allocation";
            case "goalProgress" -> "Review savings goals";
            default -> "Review " + factor;
        };
    }

    private String toJson(Map<String, String> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}

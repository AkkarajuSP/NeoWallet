package com.neowallet.finance.service;

import com.neowallet.finance.dto.*;
import com.neowallet.finance.entity.*;
import com.neowallet.finance.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neowallet.identity.entity.Family;
import com.neowallet.identity.entity.FamilyMember;
import com.neowallet.identity.entity.User;
import com.neowallet.identity.repository.FamilyMemberRepository;
import com.neowallet.identity.repository.FamilyRepository;
import com.neowallet.identity.repository.UserPreferencesRepository;
import com.neowallet.identity.repository.UserRepository;
import com.neowallet.identity.service.AuditService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final BudgetCategoryRepository budgetCategoryRepository;
    private final BudgetRecommendationRepository budgetRecommendationRepository;
    private final BudgetCalculationEngine calculationEngine;
    private final TransactionRepository transactionRepository;
    private final FinancialOverviewRepository financialOverviewRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final FamilyRepository familyRepository;
    private final UserRepository userRepository;
    private final UserPreferencesRepository userPreferencesRepository;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    private static final String DISCLAIMER = "This is a deterministic recommendation based on historical data";
    private static final DateTimeFormatter PERIOD_FMT = DateTimeFormatter.ofPattern("yyyy-MM");

    @Transactional
    public BudgetResponse createBudget(UUID userId, Optional<UUID> familyId, CreateBudgetRequest request) {
        familyId.ifPresent(fid -> ensureFamilyMember(userId, fid));

        validateCreate(request);
        if (existsBudget(userId, familyId, request.period(), request.name())) {
            throw new IllegalStateException("Budget already exists for period");
        }

        User user = userRepository.getReferenceById(userId);
        Budget budget = new Budget();
        budget.setUser(user);
        budget.setName(request.name());
        budget.setPeriod(request.period());
        budget.setCurrency(request.currency() != null ? request.currency() : "USD");

        if (familyId.isPresent()) {
            budget.setFamily(familyRepository.getReferenceById(familyId.get()));
        }

        List<BudgetCategory> cats = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (CreateBudgetRequest.CreateBudgetCategoryRequest c : request.categories()) {
            BudgetCategory bc = new BudgetCategory();
            bc.setBudget(budget);
            bc.setCategoryName(c.category());
            bc.setLimitAmount(c.limit());
            bc.setPriority(c.priority() != null ? c.priority().toUpperCase() : "VARIABLE");
            cats.add(bc);
            total = total.add(c.limit());
        }
        budget.setCategories(cats);
        budget.setTotalLimit(total);

        budget = budgetRepository.saveAndFlush(budget);
        saveBudgetPeriod(budget);

        auditService.record(userId, "USER", "BUDGET_CREATED", "BUDGET", budget.getBudgetId(), "SUCCESS",
            toJson(Map.of("name", budget.getName(), "period", budget.getPeriod(), "totalLimit", budget.getTotalLimit().toString())));

        return getBudget(userId, budget.getBudgetId(), familyId);
    }

    @Transactional(readOnly = true)
    public BudgetResponse getBudget(UUID userId, UUID budgetId, Optional<UUID> familyId) {
        Budget budget = budgetRepository.findByBudgetIdWithCategories(budgetId)
            .orElseThrow(() -> new EntityNotFoundException("Budget not found"));
        ensureAccess(userId, familyId, budget);
        return mapToResponse(budget, computeSpent(budget));
    }

    @Transactional(readOnly = true)
    public BudgetListResponse listBudgets(UUID userId, Optional<UUID> familyId, Optional<String> period, int page, int limit, String sort) {
        familyId.ifPresent(fid -> ensureFamilyMember(userId, fid));

        Pageable pageable = buildPageable(page, limit, sort);
        Page<Budget> budgets = budgetRepository.findBudgets(
            userId, familyId.orElse(null), period.orElse(null), pageable);

        List<BudgetResponse> items = budgets.getContent().stream()
            .map(b -> mapToResponse(b, computeSpent(b)))
            .toList();

        return BudgetListResponse.builder()
            .budgets(items)
            .pagination(BudgetListResponse.PaginationDto.builder()
                .page(page)
                .limit(limit)
                .totalCount((int) budgets.getTotalElements())
                .pageCount(budgets.getTotalPages())
                .build())
            .build();
    }

    @Transactional
    public BudgetResponse updateBudget(UUID userId, UUID budgetId, Optional<UUID> familyId, UpdateBudgetRequest request) {
        Budget budget = budgetRepository.findByBudgetIdWithCategories(budgetId)
            .orElseThrow(() -> new EntityNotFoundException("Budget not found"));
        ensureWriteAccess(userId, familyId, budget);

        if (request.name() != null && !request.name().isBlank()) {
            budget.setName(request.name());
        }

        if (request.categories() != null && !request.categories().isEmpty()) {
            budget.getCategories().clear();
            BigDecimal total = BigDecimal.ZERO;
            for (UpdateBudgetRequest.UpdateBudgetCategoryRequest c : request.categories()) {
                BudgetCategory bc = new BudgetCategory();
                bc.setBudget(budget);
                bc.setCategoryName(c.category());
                bc.setLimitAmount(c.limit());
                bc.setPriority(c.priority() != null ? c.priority().toUpperCase() : "VARIABLE");
                budget.getCategories().add(bc);
                total = total.add(c.limit());
            }
            budget.setTotalLimit(total);
            budget.setVersion(budget.getVersion() + 1);
        }

        budget = budgetRepository.saveAndFlush(budget);

        auditService.record(userId, "USER", "BUDGET_UPDATED", "BUDGET", budget.getBudgetId(), "SUCCESS",
            toJson(Map.of("name", budget.getName(), "period", budget.getPeriod(), "totalLimit", budget.getTotalLimit().toString())));

        return getBudget(userId, budgetId, familyId);
    }

    @Transactional
    public void deleteBudget(UUID userId, UUID budgetId, Optional<UUID> familyId) {
        Budget budget = budgetRepository.findByBudgetIdWithCategories(budgetId)
            .orElseThrow(() -> new EntityNotFoundException("Budget not found"));
        ensureWriteAccess(userId, familyId, budget);

        Map<String, BigDecimal> spent = computeSpent(budget);
        BigDecimal totalSpent = spent.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalSpent.compareTo(BigDecimal.ZERO) > 0) {
            throw new ValidationException("Cannot delete budget with committed transactions");
        }

        budget.setStatus("DELETED");
        budgetRepository.saveAndFlush(budget);

        auditService.record(userId, "USER", "BUDGET_DELETED", "BUDGET", budget.getBudgetId(), "SUCCESS",
            toJson(Map.of("period", budget.getPeriod(), "name", budget.getName())));
    }

    @Transactional(readOnly = true)
    public BudgetUtilizationResponse getUtilization(UUID userId, UUID budgetId, Optional<UUID> familyId) {
        Budget budget = budgetRepository.findByBudgetIdWithCategories(budgetId)
            .orElseThrow(() -> new EntityNotFoundException("Budget not found"));
        ensureAccess(userId, familyId, budget);

        Map<String, BigDecimal> spent = computeSpent(budget);
        var thresholds = getThresholds(userId);

        List<BudgetUtilizationResponse.CategoryUtilization> cats = new ArrayList<>();
        BigDecimal totalSpent = BigDecimal.ZERO;
        for (BudgetCategory bc : budget.getCategories()) {
            BigDecimal catSpent = spent.getOrDefault(bc.getCategoryName(), BigDecimal.ZERO);
            BigDecimal remaining = bc.getLimitAmount().subtract(catSpent);
            BigDecimal util = bc.getLimitAmount().compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : catSpent.multiply(BigDecimal.valueOf(100)).divide(bc.getLimitAmount(), 2, RoundingMode.HALF_UP);
            String status = calculationEngine.determineStatus(util, thresholds.warning(), thresholds.exceeded());
            cats.add(BudgetUtilizationResponse.CategoryUtilization.builder()
                .category(bc.getCategoryName())
                .limit(bc.getLimitAmount())
                .spent(catSpent)
                .remaining(remaining)
                .utilizationPercentage(util)
                .status(status)
                .build());
            totalSpent = totalSpent.add(catSpent);
        }

        BigDecimal totalRemaining = budget.getTotalLimit().subtract(totalSpent);
        BigDecimal overallUtil = budget.getTotalLimit().compareTo(BigDecimal.ZERO) == 0
            ? BigDecimal.ZERO
            : totalSpent.multiply(BigDecimal.valueOf(100)).divide(budget.getTotalLimit(), 2, RoundingMode.HALF_UP);

        return BudgetUtilizationResponse.builder()
            .budgetId(budget.getBudgetId().toString())
            .period(budget.getPeriod())
            .currency(budget.getCurrency())
            .categories(cats)
            .totalLimit(budget.getTotalLimit())
            .totalSpent(totalSpent)
            .totalRemaining(totalRemaining)
            .overallUtilizationPercentage(overallUtil)
            .overallStatus(calculationEngine.determineStatus(overallUtil, thresholds.warning(), thresholds.exceeded()))
            .calculatedAt(Instant.now().toString())
            .build();
    }

    @Transactional(readOnly = true)
    public BudgetForecastResponse getForecast(UUID userId, UUID budgetId, Optional<UUID> familyId) {
        Budget budget = budgetRepository.findByBudgetIdWithCategories(budgetId)
            .orElseThrow(() -> new EntityNotFoundException("Budget not found"));
        ensureAccess(userId, familyId, budget);

        BudgetResponse response = mapToResponse(budget, computeSpent(budget));
        List<BudgetCalculationEngine.CategoryHistory> histories = buildCategoryHistories(userId, familyId, budget.getPeriod());
        return calculationEngine.forecast(response, histories);
    }

    @Transactional
    public BudgetRecommendationResponse getRecommendation(UUID userId, Optional<UUID> familyId, String period) {
        familyId.ifPresent(fid -> ensureFamilyMember(userId, fid));

        FinancialOverview overview = resolveOverview(userId, familyId, period);
        if (overview == null) {
            throw new EntityNotFoundException("No financial data available for period");
        }

        List<BudgetCalculationEngine.CategoryHistory> histories = buildCategoryHistories(userId, familyId, period);
        BudgetCalculationEngine.CalculationInput input = new BudgetCalculationEngine.CalculationInput(
            overview.getPlanningIncome(),
            histories,
            overview.getEmergencyAllocation().divide(overview.getPlanningIncome(), 4, RoundingMode.HALF_UP),
            overview.getSavingsAllocation().divide(overview.getPlanningIncome(), 4, RoundingMode.HALF_UP)
        );

        var allocation = calculationEngine.calculateAllocation(input);
        var categories = calculationEngine.calculateCategories(input);
        int historicalMonths = histories.stream().mapToInt(h -> h.monthlyTotals.size()).max().orElse(0);

        BudgetRecommendation recommendation = new BudgetRecommendation();
        recommendation.setUser(userRepository.getReferenceById(userId));
        recommendation.setPeriod(period);
        recommendation.setCurrency(overview.getCurrency());
        recommendation.setRecommendedAllocation(new java.util.LinkedHashMap<>());
        recommendation.setHistoricalMonthsUsed(historicalMonths);
        recommendation.setConfidence(calculationEngine.determineConfidence(historicalMonths));
        if (familyId.isPresent()) {
            recommendation.setFamily(familyRepository.getReferenceById(familyId.get()));
        }
        recommendation = budgetRecommendationRepository.saveAndFlush(recommendation);

        return BudgetRecommendationResponse.builder()
            .recommendationId(recommendation.getRecommendationId().toString())
            .userId(userId.toString())
            .familyId(familyId.map(UUID::toString).orElse(null))
            .period(period)
            .currency(overview.getCurrency())
            .recommendedAllocation(allocation)
            .categories(categories)
            .confidence(recommendation.getConfidence())
            .historicalMonthsUsed(historicalMonths)
            .generatedAt(recommendation.getGeneratedAt().toString())
            .disclaimer(DISCLAIMER)
            .build();
    }

    private FinancialOverview resolveOverview(UUID userId, Optional<UUID> familyId, String period) {
        return familyId
            .flatMap(fid -> financialOverviewRepository.findByFamily_FamilyIdAndPeriod(fid, period))
            .or(() -> financialOverviewRepository.findByUser_UserIdAndPeriod(userId, period))
            .orElse(null);
    }

    private Map<String, BigDecimal> computeSpent(Budget budget) {
        YearMonth ym = YearMonth.parse(budget.getPeriod(), PERIOD_FMT);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        UUID userId = budget.getUser() != null ? budget.getUser().getUserId() : null;
        UUID familyId = budget.getFamily() != null ? budget.getFamily().getFamilyId() : null;

        List<Transaction> txns = fetchTransactions(userId, familyId, start, end);
        Map<String, BigDecimal> spent = new HashMap<>();
        for (Transaction t : txns) {
            if ("EXPENSE".equalsIgnoreCase(t.getType()) && t.getDeletedAt() == null) {
                String cat = t.getCategoryName();
                spent.merge(cat, t.getAmount(), BigDecimal::add);
            }
        }

        BigDecimal total = spent.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        budget.setTotalSpent(total);
        budget.setUtilizationPercentage(budget.getTotalLimit().compareTo(BigDecimal.ZERO) == 0
            ? BigDecimal.ZERO
            : total.multiply(BigDecimal.valueOf(100)).divide(budget.getTotalLimit(), 2, RoundingMode.HALF_UP));

        return spent;
    }

    private List<Transaction> fetchTransactions(UUID userId, UUID familyId, LocalDate start, LocalDate end) {
        Specification<Transaction> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (familyId != null) {
                predicates.add(cb.equal(root.get("family").get("familyId"), familyId));
            } else {
                predicates.add(cb.equal(root.get("user").get("userId"), userId));
            }
            predicates.add(cb.between(root.get("transactionDate"), start, end));
            predicates.add(cb.isNull(root.get("deletedAt")));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return transactionRepository.findAll(spec);
    }

    private List<BudgetCalculationEngine.CategoryHistory> buildCategoryHistories(UUID userId, Optional<UUID> familyId, String period) {
        YearMonth current = YearMonth.parse(period, PERIOD_FMT);
        YearMonth start = current.minusMonths(6);
        YearMonth end = current.minusMonths(1);

        LocalDate startDate = start.atDay(1);
        LocalDate endDate = end.atEndOfMonth();

        UUID fid = familyId.orElse(null);
        UUID uid = fid == null ? userId : null;
        List<Transaction> txns = fetchTransactions(uid, fid, startDate, endDate);

        Map<String, Map<YearMonth, BigDecimal>> grouped = new HashMap<>();
        for (Transaction t : txns) {
            if ("EXPENSE".equalsIgnoreCase(t.getType()) && t.getDeletedAt() == null) {
                YearMonth ym = YearMonth.from(t.getTransactionDate());
                grouped.computeIfAbsent(t.getCategoryName(), k -> new HashMap<>())
                    .merge(ym, t.getAmount(), BigDecimal::add);
            }
        }

        List<BudgetCalculationEngine.CategoryHistory> histories = new ArrayList<>();
        for (String category : grouped.keySet()) {
            List<BigDecimal> totals = grouped.get(category).entrySet().stream()
                .filter(e -> e.getValue().compareTo(BigDecimal.ZERO) > 0)
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .toList();
            histories.add(new BudgetCalculationEngine.CategoryHistory(category, totals));
        }
        return histories;
    }

    private BudgetResponse mapToResponse(Budget budget, Map<String, BigDecimal> spent) {
        List<BudgetCategoryDto> cats = budget.getCategories().stream().map(bc -> {
            BigDecimal catSpent = spent.getOrDefault(bc.getCategoryName(), BigDecimal.ZERO);
            BigDecimal util = bc.getLimitAmount().compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : catSpent.multiply(BigDecimal.valueOf(100)).divide(bc.getLimitAmount(), 2, RoundingMode.HALF_UP);
            return BudgetCategoryDto.builder()
                .category(bc.getCategoryName())
                .limit(bc.getLimitAmount())
                .spent(catSpent)
                .utilizationPercentage(util)
                .priority(bc.getPriority())
                .build();
        }).toList();

        BigDecimal totalSpent = spent.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        return BudgetResponse.builder()
            .budgetId(budget.getBudgetId().toString())
            .userId(budget.getUser() != null ? budget.getUser().getUserId().toString() : null)
            .familyId(budget.getFamily() != null ? budget.getFamily().getFamilyId().toString() : null)
            .name(budget.getName())
            .period(budget.getPeriod())
            .currency(budget.getCurrency())
            .categories(cats)
            .totalLimit(budget.getTotalLimit())
            .totalSpent(totalSpent)
            .utilizationPercentage(budget.getTotalLimit().compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : totalSpent.multiply(BigDecimal.valueOf(100)).divide(budget.getTotalLimit(), 2, RoundingMode.HALF_UP))
            .createdAt(budget.getCreatedAt().toString())
            .updatedAt(budget.getUpdatedAt().toString())
            .build();
    }

    private void validateCreate(CreateBudgetRequest request) {
        if (request.name() == null || request.name().isBlank()) throw new ValidationException("Name is required");
        if (request.period() == null || !request.period().matches("\\d{4}-\\d{2}")) throw new ValidationException("Period must be YYYY-MM");
        if (request.categories() == null || request.categories().isEmpty()) throw new ValidationException("At least one category is required");
        for (CreateBudgetRequest.CreateBudgetCategoryRequest c : request.categories()) {
            if (c.category() == null || c.category().isBlank()) throw new ValidationException("Category name is required");
            if (c.limit() == null || c.limit().compareTo(BigDecimal.ZERO) <= 0) throw new ValidationException("Limit must be positive");
        }
    }

    private boolean existsBudget(UUID userId, Optional<UUID> familyId, String period, String name) {
        if (familyId.isPresent()) {
            return budgetRepository.existsByFamily_FamilyIdAndPeriodAndNameAndStatusNot(familyId.get(), period, name, "DELETED");
        }
        return budgetRepository.existsByUser_UserIdAndPeriodAndNameAndStatusNot(userId, period, name, "DELETED");
    }

    private void saveBudgetPeriod(Budget budget) {
        YearMonth ym = YearMonth.parse(budget.getPeriod(), PERIOD_FMT);
        BudgetPeriod p = new BudgetPeriod();
        p.setBudget(budget);
        p.setPeriodStart(ym.atDay(1));
        p.setPeriodEnd(ym.atEndOfMonth());
        p.setPeriodType("MONTHLY");
        p.setIsActive(true);
    }

    private Pageable buildPageable(int page, int limit, String sort) {
        Sort s = Sort.by("period").descending();
        if (sort != null && sort.contains(":")) {
            String[] parts = sort.split(":");
            s = Sort.by(parts[0]);
            if ("desc".equalsIgnoreCase(parts[1])) s = s.descending();
        }
        return PageRequest.of(page - 1, Math.min(limit, 100), s);
    }

    private void ensureAccess(UUID userId, Optional<UUID> familyId, Budget budget) {
        if (budget.getFamily() != null) {
            ensureFamilyMember(userId, budget.getFamily().getFamilyId());
        } else if (budget.getUser() == null || !budget.getUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("Not authorized");
        }
    }

    private void ensureWriteAccess(UUID userId, Optional<UUID> familyId, Budget budget) {
        if (budget.getFamily() != null) {
            FamilyMember member = familyMemberRepository
                .findByFamily_FamilyIdAndUser_UserIdAndLeftAtIsNull(budget.getFamily().getFamilyId(), userId)
                .orElseThrow(() -> new AccessDeniedException("Not authorized"));
            if (!List.of("OWNER").contains(member.getRole())) {
                throw new AccessDeniedException("Not authorized");
            }
        } else if (budget.getUser() == null || !budget.getUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("Not authorized");
        }
    }

    private void ensureFamilyMember(UUID userId, UUID familyId) {
        if (!familyMemberRepository.existsByFamily_FamilyIdAndUser_UserIdAndLeftAtIsNull(familyId, userId)) {
            throw new AccessDeniedException("Not a family member");
        }
    }

    private Thresholds getThresholds(UUID userId) {
        int warning = userPreferencesRepository.findByUser_UserId(userId)
            .map(p -> p.getBudgetAlertThresholdPercentage())
            .orElse(80);
        return new Thresholds(warning, 100);
    }

    private String toJson(Map<String, String> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    private record Thresholds(int warning, int exceeded) {}
}

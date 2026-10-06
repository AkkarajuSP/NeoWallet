package com.neowallet.finance.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neowallet.finance.dto.*;
import com.neowallet.finance.entity.FinancialOverview;
import com.neowallet.finance.entity.SavingsContribution;
import com.neowallet.finance.entity.SavingsGoal;
import com.neowallet.finance.repository.FinancialOverviewRepository;
import com.neowallet.finance.repository.SavingsContributionRepository;
import com.neowallet.finance.repository.SavingsGoalRepository;
import com.neowallet.identity.entity.Family;
import com.neowallet.identity.entity.FamilyMember;
import com.neowallet.identity.entity.User;
import com.neowallet.identity.repository.FamilyMemberRepository;
import com.neowallet.identity.repository.FamilyRepository;
import com.neowallet.identity.repository.UserRepository;
import com.neowallet.identity.service.AuditService;
import jakarta.persistence.EntityNotFoundException;
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
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SavingsGoalService {

    private final SavingsGoalRepository savingsGoalRepository;
    private final FinancialOverviewRepository financialOverviewRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final FamilyRepository familyRepository;
    private final UserRepository userRepository;
    private final SavingsContributionRepository savingsContributionRepository;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    @Transactional
    public SavingsGoalResponse createSavingsGoal(UUID userId, Optional<UUID> familyId, CreateSavingsGoalRequest request) {
        familyId.ifPresent(fid -> ensureFamilyMemberCanCreate(userId, fid));
        validateCreate(request);

        if (existsGoalName(userId, familyId, request.name())) {
            throw new IllegalStateException("Savings goal name already exists");
        }

        User user = userRepository.getReferenceById(userId);
        SavingsGoal goal = new SavingsGoal();
        goal.setUser(user);
        goal.setName(request.name());
        goal.setTargetAmount(request.targetAmount().setScale(2, RoundingMode.HALF_UP));
        goal.setTargetDate(request.targetDate());
        goal.setCurrency(request.currency() != null ? request.currency() : "USD");
        goal.setPriority(request.priority() != null ? request.priority().toUpperCase() : "MEDIUM");
        goal.setCategory(request.category());

        BigDecimal current = request.currentAmount() != null ? request.currentAmount() : BigDecimal.ZERO;
        goal.setCurrentAmount(current.setScale(2, RoundingMode.HALF_UP));
        goal.setProgressPercentage(calculateProgressPercentage(goal.getCurrentAmount(), goal.getTargetAmount()));

        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus("COMPLETED");
        }

        if (familyId.isPresent()) {
            goal.setFamily(familyRepository.getReferenceById(familyId.get()));
        }

        goal = savingsGoalRepository.saveAndFlush(goal);

        auditService.record(userId, "USER", "SAVINGS_GOAL_CREATED", "SAVINGS_GOAL", goal.getGoalId(), "SUCCESS",
            toJson(Map.of("name", goal.getName(), "targetAmount", goal.getTargetAmount().toString(),
                "targetDate", goal.getTargetDate().toString())));

        return toResponse(goal);
    }

    @Transactional(readOnly = true)
    public SavingsGoalListResponse listSavingsGoals(UUID userId, Optional<UUID> familyId, Optional<String> status,
                                                     Optional<String> priority, int page, int limit, String sort) {
        familyId.ifPresent(fid -> ensureFamilyMember(userId, fid));

        UUID queryUserId = familyId.isEmpty() ? userId : null;
        UUID queryFamilyId = familyId.orElse(null);

        Pageable pageable = buildPageable(page, limit, sort);
        Page<SavingsGoal> pageResult = savingsGoalRepository.findSavingsGoals(
            queryUserId, queryFamilyId, status.orElse(null), priority.orElse(null), pageable);

        List<SavingsGoalResponse> goals = pageResult.getContent().stream()
            .map(this::toResponse)
            .toList();

        return SavingsGoalListResponse.builder()
            .goals(goals)
            .pagination(SavingsGoalListResponse.PaginationDto.builder()
                .page(page)
                .limit(limit)
                .totalCount((int) pageResult.getTotalElements())
                .pageCount(pageResult.getTotalPages())
                .build())
            .build();
    }

    @Transactional(readOnly = true)
    public SavingsGoalResponse getSavingsGoal(UUID userId, UUID goalId, Optional<UUID> familyId) {
        SavingsGoal goal = findAndEnsureAccess(userId, goalId, familyId);
        return toResponse(goal);
    }

    @Transactional
    public SavingsGoalResponse updateSavingsGoal(UUID userId, UUID goalId, Optional<UUID> familyId,
                                                  UpdateSavingsGoalRequest request) {
        SavingsGoal goal = findAndEnsureWriteAccess(userId, goalId, familyId);

        if (request.name() != null && !request.name().isBlank()) {
            goal.setName(request.name());
        }
        if (request.targetAmount() != null) {
            if (request.targetAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValidationException("Target amount must be positive");
            }
            goal.setTargetAmount(request.targetAmount().setScale(2, RoundingMode.HALF_UP));
        }
        if (request.targetDate() != null) {
            goal.setTargetDate(request.targetDate());
        }
        if (request.currentAmount() != null) {
            if (request.currentAmount().compareTo(BigDecimal.ZERO) < 0) {
                throw new ValidationException("Current amount cannot be negative");
            }
            goal.setCurrentAmount(request.currentAmount().setScale(2, RoundingMode.HALF_UP));
        }
        if (request.priority() != null && !request.priority().isBlank()) {
            goal.setPriority(request.priority().toUpperCase());
        }
        if (request.category() != null) {
            goal.setCategory(request.category());
        }
        if (request.status() != null && !request.status().isBlank()) {
            goal.setStatus(request.status().toUpperCase());
        }
        if (request.currency() != null && !request.currency().isBlank()) {
            goal.setCurrency(request.currency());
        }

        goal.setProgressPercentage(calculateProgressPercentage(goal.getCurrentAmount(), goal.getTargetAmount()));

        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0
            && !"CANCELLED".equals(goal.getStatus())) {
            goal.setStatus("COMPLETED");
        }

        goal = savingsGoalRepository.saveAndFlush(goal);

        auditService.record(userId, "USER", "SAVINGS_GOAL_UPDATED", "SAVINGS_GOAL", goal.getGoalId(), "SUCCESS",
            toJson(Map.of("name", goal.getName(), "currentAmount", goal.getCurrentAmount().toString(),
                "progressPercentage", goal.getProgressPercentage().toString())));

        return toResponse(goal);
    }

    @Transactional
    public void deleteSavingsGoal(UUID userId, UUID goalId, Optional<UUID> familyId) {
        SavingsGoal goal = findAndEnsureWriteAccess(userId, goalId, familyId);
        goal.setStatus("CANCELLED");
        savingsGoalRepository.saveAndFlush(goal);

        auditService.record(userId, "USER", "SAVINGS_GOAL_CANCELLED", "SAVINGS_GOAL", goal.getGoalId(), "SUCCESS",
            toJson(Map.of("name", goal.getName())));
    }

    @Transactional
    public SavingsContributionResult addContribution(UUID userId, UUID goalId, Optional<UUID> familyId,
                                                      CreateSavingsContributionRequest request) {
        SavingsGoal goal = findAndEnsureWriteAccess(userId, goalId, familyId);

        if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Contribution amount must be positive");
        }
        if (request.contributionDate() == null) {
            throw new ValidationException("Contribution date is required");
        }
        if (request.contributionDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Contribution date cannot be in the future");
        }

        User user = userRepository.getReferenceById(userId);

        SavingsContribution contribution = new SavingsContribution();
        contribution.setGoal(goal);
        contribution.setUser(user);
        contribution.setAmount(request.amount().setScale(2, RoundingMode.HALF_UP));
        contribution.setCurrency(goal.getCurrency());
        contribution.setContributionDate(request.contributionDate());
        contribution.setNotes(request.notes());

        savingsContributionRepository.save(contribution);

        goal.setCurrentAmount(goal.getCurrentAmount().add(contribution.getAmount())
            .setScale(2, RoundingMode.HALF_UP));
        goal.setProgressPercentage(calculateProgressPercentage(goal.getCurrentAmount(), goal.getTargetAmount()));
        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0
            && !"CANCELLED".equals(goal.getStatus())) {
            goal.setStatus("COMPLETED");
        }
        goal = savingsGoalRepository.saveAndFlush(goal);

        auditService.record(userId, "USER", "SAVINGS_GOAL_CONTRIBUTION_CREATED", "SAVINGS_GOAL", goal.getGoalId(), "SUCCESS",
            toJson(Map.of("contributionId", contribution.getContributionId().toString(),
                "amount", contribution.getAmount().toString())));

        return SavingsContributionResult.builder()
            .contribution(toContributionResponse(contribution))
            .goal(toResponse(goal))
            .build();
    }

    @Transactional(readOnly = true)
    public SavingsGoalProgressResponse getProgress(UUID userId, UUID goalId, Optional<UUID> familyId) {
        SavingsGoal goal = findAndEnsureAccess(userId, goalId, familyId);
        return calculateProgress(goal);
    }

    @Transactional(readOnly = true)
    public SavingsGoalForecastResponse getForecast(UUID userId, UUID goalId, Optional<UUID> familyId) {
        SavingsGoal goal = findAndEnsureAccess(userId, goalId, familyId);
        return calculateForecast(goal);
    }

    private SavingsGoal findAndEnsureAccess(UUID userId, UUID goalId, Optional<UUID> familyId) {
        SavingsGoal goal = savingsGoalRepository.findByGoalIdAndStatusNot(goalId, "CANCELLED")
            .orElseThrow(() -> new EntityNotFoundException("Savings goal not found"));

        if (goal.getFamily() != null) {
            ensureFamilyMember(userId, goal.getFamily().getFamilyId());
        } else if (goal.getUser() == null || !goal.getUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("Not authorized");
        }
        return goal;
    }

    private SavingsGoal findAndEnsureWriteAccess(UUID userId, UUID goalId, Optional<UUID> familyId) {
        SavingsGoal goal = findAndEnsureAccess(userId, goalId, familyId);

        if (goal.getFamily() != null) {
            FamilyMember member = familyMemberRepository
                .findByFamily_FamilyIdAndUser_UserIdAndLeftAtIsNull(goal.getFamily().getFamilyId(), userId)
                .orElseThrow(() -> new AccessDeniedException("Not authorized"));
            if (!List.of("OWNER").contains(member.getRole())) {
                throw new AccessDeniedException("Not authorized");
            }
        } else if (goal.getUser() == null || !goal.getUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("Not authorized");
        }
        return goal;
    }

    private void ensureFamilyMember(UUID userId, UUID familyId) {
        if (!familyMemberRepository.existsByFamily_FamilyIdAndUser_UserIdAndLeftAtIsNull(familyId, userId)) {
            throw new AccessDeniedException("Not a family member");
        }
    }

    private void ensureFamilyMemberCanCreate(UUID userId, UUID familyId) {
        FamilyMember member = familyMemberRepository
            .findByFamily_FamilyIdAndUser_UserIdAndLeftAtIsNull(familyId, userId)
            .orElseThrow(() -> new AccessDeniedException("Not a family member"));
        if ("RESTRICTED".equals(member.getRole())) {
            throw new AccessDeniedException("Not authorized");
        }
    }

    private boolean existsGoalName(UUID userId, Optional<UUID> familyId, String name) {
        if (familyId.isPresent()) {
            return savingsGoalRepository.existsByFamily_FamilyIdAndNameAndStatusNot(familyId.get(), name, "CANCELLED");
        }
        return savingsGoalRepository.existsByUser_UserIdAndNameAndStatusNot(userId, name, "CANCELLED");
    }

    private void validateCreate(CreateSavingsGoalRequest request) {
        if (request.name() == null || request.name().isBlank()) throw new ValidationException("Name is required");
        if (request.targetAmount() == null || request.targetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Target amount must be positive");
        }
        if (request.targetDate() == null) throw new ValidationException("Target date is required");
        if (request.targetDate().isBefore(LocalDate.now())) {
            throw new ValidationException("Target date must be in the future");
        }
    }

    private BigDecimal calculateProgressPercentage(BigDecimal current, BigDecimal target) {
        if (target == null || target.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return current.multiply(HUNDRED).divide(target, 2, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
    }

    private SavingsGoalProgressResponse calculateProgress(SavingsGoal goal) {
        BigDecimal current = goal.getCurrentAmount();
        BigDecimal target = goal.getTargetAmount();
        BigDecimal remaining = target.subtract(current).max(BigDecimal.ZERO);

        LocalDate now = LocalDate.now();
        long monthsRemaining = Math.max(0, ChronoUnit.MONTHS.between(now, goal.getTargetDate()));

        BigDecimal requiredMonthly = monthsRemaining > 0
            ? remaining.divide(BigDecimal.valueOf(monthsRemaining), 2, RoundingMode.HALF_UP)
            : remaining;

        long monthsSinceCreation = Math.max(1, ChronoUnit.MONTHS.between(
            Instant.ofEpochMilli(goal.getCreatedAt().toEpochMilli()).atZone(java.time.ZoneId.systemDefault()).toLocalDate(), now));
        BigDecimal actualMonthly = monthsSinceCreation > 0
            ? current.divide(BigDecimal.valueOf(monthsSinceCreation), 2, RoundingMode.HALF_UP)
            : current;

        boolean affordable = isAffordable(goal, requiredMonthly);
        boolean onTrack;
        String status;

        if (current.compareTo(target) >= 0) {
            onTrack = true;
            status = "AHEAD";
        } else if (goal.getTargetDate().isBefore(now)) {
            onTrack = false;
            status = "BEHIND";
        } else if (!affordable) {
            onTrack = false;
            status = "BEHIND";
        } else if (actualMonthly.compareTo(requiredMonthly) >= 0) {
            onTrack = true;
            status = actualMonthly.compareTo(requiredMonthly) > 0 ? "AHEAD" : "ON_TRACK";
        } else {
            onTrack = false;
            status = "BEHIND";
        }

        return SavingsGoalProgressResponse.builder()
            .goalId(goal.getGoalId().toString())
            .currentAmount(current)
            .targetAmount(target)
            .progressPercentage(calculateProgressPercentage(current, target))
            .remainingAmount(remaining)
            .monthsRemaining((int) monthsRemaining)
            .requiredMonthlyContribution(requiredMonthly)
            .actualMonthlyContribution(actualMonthly)
            .onTrack(onTrack)
            .status(status)
            .calculatedAt(Instant.now().toString())
            .build();
    }

    private SavingsGoalForecastResponse calculateForecast(SavingsGoal goal) {
        BigDecimal current = goal.getCurrentAmount();
        BigDecimal target = goal.getTargetAmount();
        BigDecimal remaining = target.subtract(current).max(BigDecimal.ZERO);

        LocalDate now = LocalDate.now();
        long monthsSinceCreation = Math.max(0, ChronoUnit.MONTHS.between(
            Instant.ofEpochMilli(goal.getCreatedAt().toEpochMilli()).atZone(java.time.ZoneId.systemDefault()).toLocalDate(), now));
        String confidence = monthsSinceCreation >= 3 ? "HIGH" : (monthsSinceCreation >= 1 ? "MEDIUM" : "LOW");

        long monthsRemainingToTarget = Math.max(0, ChronoUnit.MONTHS.between(now, goal.getTargetDate()));
        BigDecimal requiredMonthly = monthsRemainingToTarget > 0
            ? remaining.divide(BigDecimal.valueOf(monthsRemainingToTarget), 2, RoundingMode.HALF_UP)
            : remaining;

        long monthsActive = Math.max(1, monthsSinceCreation);
        BigDecimal actualMonthly = current.divide(BigDecimal.valueOf(monthsActive), 2, RoundingMode.HALF_UP);

        List<SavingsGoalForecastResponse.ScenarioDto> scenarios = new ArrayList<>();
        scenarios.add(buildScenario(remaining, now, requiredMonthly));
        if (actualMonthly.compareTo(BigDecimal.ZERO) > 0 && actualMonthly.compareTo(requiredMonthly) != 0) {
            scenarios.add(buildScenario(remaining, now, actualMonthly));
        }

        Integer monthsToCompletion = null;
        LocalDate projectedCompletionDate = null;
        if (actualMonthly.compareTo(BigDecimal.ZERO) > 0) {
            monthsToCompletion = (int) ceilMonths(remaining, actualMonthly);
            projectedCompletionDate = now.plusMonths(monthsToCompletion);
        }

        BigDecimal projectedCompletionAmount = current.add(actualMonthly.multiply(BigDecimal.valueOf(monthsRemainingToTarget)));
        if (projectedCompletionAmount.compareTo(target) > 0) {
            projectedCompletionAmount = target;
        }

        if (current.compareTo(target) >= 0) {
            projectedCompletionDate = now;
            monthsToCompletion = 0;
            projectedCompletionAmount = target;
        }

        SavingsGoalForecastResponse.ForecastDto forecast = SavingsGoalForecastResponse.ForecastDto.builder()
            .projectedCompletionDate(projectedCompletionDate)
            .projectedCompletionAmount(projectedCompletionAmount)
            .monthsToCompletion(monthsToCompletion)
            .confidence(confidence)
            .build();

        return SavingsGoalForecastResponse.builder()
            .goalId(goal.getGoalId().toString())
            .forecast(forecast)
            .scenarios(scenarios)
            .calculatedAt(Instant.now().toString())
            .build();
    }

    private SavingsGoalForecastResponse.ScenarioDto buildScenario(BigDecimal remaining, LocalDate now, BigDecimal monthly) {
        if (monthly == null || monthly.compareTo(BigDecimal.ZERO) <= 0) {
            return SavingsGoalForecastResponse.ScenarioDto.builder()
                .monthlyContribution(monthly)
                .projectedCompletionDate(null)
                .monthsToCompletion(null)
                .build();
        }
        long months = ceilMonths(remaining, monthly);
        return SavingsGoalForecastResponse.ScenarioDto.builder()
            .monthlyContribution(monthly)
            .projectedCompletionDate(now.plusMonths(months))
            .monthsToCompletion((int) months)
            .build();
    }

    private long ceilMonths(BigDecimal remaining, BigDecimal monthly) {
        return remaining.divide(monthly, 0, RoundingMode.CEILING).longValueExact();
    }

    private boolean isAffordable(SavingsGoal goal, BigDecimal requiredMonthly) {
        Optional<FinancialOverview> overview;
        if (goal.getFamily() != null) {
            overview = financialOverviewRepository.findFirstByFamily_FamilyIdOrderByPeriodDesc(goal.getFamily().getFamilyId());
        } else {
            overview = financialOverviewRepository.findFirstByUser_UserIdOrderByPeriodDesc(goal.getUser().getUserId());
        }
        return overview.map(o -> o.getSavingsAllocation() == null
                || o.getSavingsAllocation().compareTo(requiredMonthly) >= 0)
            .orElse(true);
    }

    private Pageable buildPageable(int page, int limit, String sort) {
        Sort s = Sort.by("targetDate").ascending();
        if (sort != null && sort.contains(":")) {
            String[] parts = sort.split(":");
            s = Sort.by(parts[0]);
            if ("desc".equalsIgnoreCase(parts[1])) s = s.descending();
        }
        return PageRequest.of(page - 1, Math.min(limit, 100), s);
    }

    private SavingsGoalResponse toResponse(SavingsGoal goal) {
        return SavingsGoalResponse.builder()
            .goalId(goal.getGoalId().toString())
            .userId(goal.getUser() != null ? goal.getUser().getUserId().toString() : null)
            .familyId(goal.getFamily() != null ? goal.getFamily().getFamilyId().toString() : null)
            .name(goal.getName())
            .targetAmount(goal.getTargetAmount())
            .currentAmount(goal.getCurrentAmount())
            .progressPercentage(goal.getProgressPercentage())
            .targetDate(goal.getTargetDate())
            .currency(goal.getCurrency())
            .priority(goal.getPriority())
            .category(goal.getCategory())
            .status(goal.getStatus())
            .createdAt(goal.getCreatedAt().toString())
            .updatedAt(goal.getUpdatedAt().toString())
            .build();
    }

    private SavingsContributionResponse toContributionResponse(SavingsContribution contribution) {
        return SavingsContributionResponse.builder()
            .contributionId(contribution.getContributionId().toString())
            .goalId(contribution.getGoal().getGoalId().toString())
            .userId(contribution.getUser().getUserId().toString())
            .amount(contribution.getAmount())
            .currency(contribution.getCurrency())
            .contributionDate(contribution.getContributionDate())
            .notes(contribution.getNotes())
            .createdAt(contribution.getCreatedAt().toString())
            .build();
    }

    private String toJson(Map<String, String> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}

package com.neowallet.finance.controller;

import com.neowallet.finance.dto.*;
import com.neowallet.finance.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(
            @AuthenticationPrincipal UserDetails user,
            @RequestHeader(value = "X-Family-ID", required = false) String familyId,
            @RequestBody CreateBudgetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(budgetService.createBudget(UUID.fromString(user.getUsername()), parseFamilyId(familyId), request));
    }

    @GetMapping
    public ResponseEntity<BudgetListResponse> getBudgets(
            @AuthenticationPrincipal UserDetails user,
            @RequestHeader(value = "X-Family-ID", required = false) String familyId,
            @RequestParam(name = "filter[period]", required = false) String period,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "period:desc") String sort) {
        return ResponseEntity.ok(budgetService.listBudgets(
            UUID.fromString(user.getUsername()),
            parseFamilyId(familyId),
            Optional.ofNullable(period),
            page,
            limit,
            sort));
    }

    @GetMapping("/{budgetId}")
    public ResponseEntity<BudgetResponse> getBudget(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable UUID budgetId,
            @RequestHeader(value = "X-Family-ID", required = false) String familyId) {
        return ResponseEntity.ok(budgetService.getBudget(
            UUID.fromString(user.getUsername()), budgetId, parseFamilyId(familyId)));
    }

    @PutMapping("/{budgetId}")
    public ResponseEntity<BudgetResponse> updateBudget(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable UUID budgetId,
            @RequestHeader(value = "X-Family-ID", required = false) String familyId,
            @RequestBody UpdateBudgetRequest request) {
        return ResponseEntity.ok(budgetService.updateBudget(
            UUID.fromString(user.getUsername()), budgetId, parseFamilyId(familyId), request));
    }

    @DeleteMapping("/{budgetId}")
    public ResponseEntity<Void> deleteBudget(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable UUID budgetId,
            @RequestHeader(value = "X-Family-ID", required = false) String familyId) {
        budgetService.deleteBudget(UUID.fromString(user.getUsername()), budgetId, parseFamilyId(familyId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{budgetId}/utilization")
    public ResponseEntity<BudgetUtilizationResponse> getUtilization(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable UUID budgetId,
            @RequestHeader(value = "X-Family-ID", required = false) String familyId) {
        return ResponseEntity.ok(budgetService.getUtilization(
            UUID.fromString(user.getUsername()), budgetId, parseFamilyId(familyId)));
    }

    @GetMapping("/{budgetId}/forecast")
    public ResponseEntity<BudgetForecastResponse> getForecast(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable UUID budgetId,
            @RequestHeader(value = "X-Family-ID", required = false) String familyId) {
        return ResponseEntity.ok(budgetService.getForecast(
            UUID.fromString(user.getUsername()), budgetId, parseFamilyId(familyId)));
    }

    @GetMapping("/recommendation")
    public ResponseEntity<BudgetRecommendationResponse> getRecommendation(
            @AuthenticationPrincipal UserDetails user,
            @RequestHeader(value = "X-Family-ID", required = false) String familyId,
            @RequestParam String period) {
        return ResponseEntity.ok(budgetService.getRecommendation(
            UUID.fromString(user.getUsername()), parseFamilyId(familyId), period));
    }

    private Optional<UUID> parseFamilyId(String familyId) {
        return familyId == null || familyId.isBlank() ? Optional.empty() : Optional.of(UUID.fromString(familyId));
    }
}

package com.neowallet.finance.controller;

import com.neowallet.finance.dto.*;
import com.neowallet.finance.service.SavingsGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/savings-goals")
@RequiredArgsConstructor
public class SavingsGoalController {

    private final SavingsGoalService savingsGoalService;

    @PostMapping
    public ResponseEntity<SavingsGoalResponse> createSavingsGoal(
            @AuthenticationPrincipal UserDetails user,
            @RequestHeader(value = "X-Family-ID", required = false) String familyId,
            @RequestBody CreateSavingsGoalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(savingsGoalService.createSavingsGoal(UUID.fromString(user.getUsername()), parseFamilyId(familyId), request));
    }

    @GetMapping
    public ResponseEntity<SavingsGoalListResponse> getSavingsGoals(
            @AuthenticationPrincipal UserDetails user,
            @RequestHeader(value = "X-Family-ID", required = false) String familyId,
            @RequestParam(name = "filter[status]", required = false) String status,
            @RequestParam(name = "filter[priority]", required = false) String priority,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "targetDate:asc") String sort) {
        return ResponseEntity.ok(savingsGoalService.listSavingsGoals(
            UUID.fromString(user.getUsername()),
            parseFamilyId(familyId),
            Optional.ofNullable(status),
            Optional.ofNullable(priority),
            page,
            limit,
            sort));
    }

    @GetMapping("/{goalId}")
    public ResponseEntity<SavingsGoalResponse> getSavingsGoal(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable UUID goalId,
            @RequestHeader(value = "X-Family-ID", required = false) String familyId) {
        return ResponseEntity.ok(savingsGoalService.getSavingsGoal(
            UUID.fromString(user.getUsername()), goalId, parseFamilyId(familyId)));
    }

    @PutMapping("/{goalId}")
    public ResponseEntity<SavingsGoalResponse> updateSavingsGoal(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable UUID goalId,
            @RequestHeader(value = "X-Family-ID", required = false) String familyId,
            @RequestBody UpdateSavingsGoalRequest request) {
        return ResponseEntity.ok(savingsGoalService.updateSavingsGoal(
            UUID.fromString(user.getUsername()), goalId, parseFamilyId(familyId), request));
    }

    @PostMapping("/{goalId}/contributions")
    public ResponseEntity<SavingsContributionResult> addContribution(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable UUID goalId,
            @RequestHeader(value = "X-Family-ID", required = false) String familyId,
            @RequestBody CreateSavingsContributionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(savingsGoalService.addContribution(
                UUID.fromString(user.getUsername()), goalId, parseFamilyId(familyId), request));
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteSavingsGoal(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable UUID goalId,
            @RequestHeader(value = "X-Family-ID", required = false) String familyId) {
        savingsGoalService.deleteSavingsGoal(UUID.fromString(user.getUsername()), goalId, parseFamilyId(familyId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{goalId}/progress")
    public ResponseEntity<SavingsGoalProgressResponse> getProgress(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable UUID goalId,
            @RequestHeader(value = "X-Family-ID", required = false) String familyId) {
        return ResponseEntity.ok(savingsGoalService.getProgress(
            UUID.fromString(user.getUsername()), goalId, parseFamilyId(familyId)));
    }

    @GetMapping("/{goalId}/forecast")
    public ResponseEntity<SavingsGoalForecastResponse> getForecast(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable UUID goalId,
            @RequestHeader(value = "X-Family-ID", required = false) String familyId) {
        return ResponseEntity.ok(savingsGoalService.getForecast(
            UUID.fromString(user.getUsername()), goalId, parseFamilyId(familyId)));
    }

    private Optional<UUID> parseFamilyId(String familyId) {
        return familyId == null || familyId.isBlank() ? Optional.empty() : Optional.of(UUID.fromString(familyId));
    }
}

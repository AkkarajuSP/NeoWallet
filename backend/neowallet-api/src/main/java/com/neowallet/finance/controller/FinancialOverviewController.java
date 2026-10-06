package com.neowallet.finance.controller;

import com.neowallet.finance.dto.*;
import com.neowallet.finance.service.FinancialOverviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FinancialOverviewController {

    private final FinancialOverviewService financialOverviewService;

    @GetMapping("/financial-overview")
    public ResponseEntity<FinancialOverviewResponse> getFinancialOverview(
            @AuthenticationPrincipal UserDetails user,
            @RequestHeader(value = "X-Family-ID", required = false) String familyId) {
        return ResponseEntity.ok(financialOverviewService.getOverview(
                UUID.fromString(user.getUsername()),
                parseFamilyId(familyId)));
    }

    @GetMapping("/financial-overview/summary")
    public ResponseEntity<FinancialSummaryResponse> getFinancialOverviewSummary(
            @AuthenticationPrincipal UserDetails user,
            @RequestHeader(value = "X-Family-ID", required = false) String familyId) {
        return ResponseEntity.ok(financialOverviewService.getSummary(
                UUID.fromString(user.getUsername()),
                parseFamilyId(familyId)));
    }

    @GetMapping("/financial-overview/allocations")
    public ResponseEntity<AllocationsResponse> getFinancialOverviewAllocations(
            @AuthenticationPrincipal UserDetails user,
            @RequestHeader(value = "X-Family-ID", required = false) String familyId) {
        return ResponseEntity.ok(financialOverviewService.getAllocations(
                UUID.fromString(user.getUsername()),
                parseFamilyId(familyId)));
    }

    @GetMapping("/financial-overview/commitments")
    public ResponseEntity<CommitmentsResponse> getFinancialOverviewCommitments(
            @AuthenticationPrincipal UserDetails user,
            @RequestHeader(value = "X-Family-ID", required = false) String familyId) {
        return ResponseEntity.ok(financialOverviewService.getCommitments(
                UUID.fromString(user.getUsername()),
                parseFamilyId(familyId)));
    }

    @GetMapping("/financial-overview/pending")
    public ResponseEntity<PendingPaymentsResponse> getFinancialOverviewPending(
            @AuthenticationPrincipal UserDetails user,
            @RequestHeader(value = "X-Family-ID", required = false) String familyId) {
        return ResponseEntity.ok(financialOverviewService.getPendingPayments(
                UUID.fromString(user.getUsername()),
                parseFamilyId(familyId)));
    }

    @GetMapping("/financial-overview/capacity")
    public ResponseEntity<CapacityResponse> getFinancialOverviewCapacity(
            @AuthenticationPrincipal UserDetails user,
            @RequestHeader(value = "X-Family-ID", required = false) String familyId) {
        return ResponseEntity.ok(financialOverviewService.getCapacity(
                UUID.fromString(user.getUsername()),
                parseFamilyId(familyId)));
    }

    private Optional<UUID> parseFamilyId(String familyId) {
        return familyId == null || familyId.isBlank() ? Optional.empty() : Optional.of(UUID.fromString(familyId));
    }
}

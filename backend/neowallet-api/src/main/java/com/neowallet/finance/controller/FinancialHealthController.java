package com.neowallet.finance.controller;

import com.neowallet.finance.dto.*;
import com.neowallet.finance.service.FinancialHealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/financial-health")
@RequiredArgsConstructor
public class FinancialHealthController {

    private final FinancialHealthService financialHealthService;

    @GetMapping
    public ResponseEntity<FinancialHealthResponse> getFinancialHealth(
            @AuthenticationPrincipal UserDetails user,
            @RequestHeader(value = "X-Family-ID", required = false) String familyId) {
        return ResponseEntity.ok(financialHealthService.getFinancialHealth(UUID.fromString(user.getUsername()), parseFamilyId(familyId)));
    }

    @GetMapping("/factors")
    public ResponseEntity<FinancialHealthFactorsResponse> getFactors(
            @AuthenticationPrincipal UserDetails user,
            @RequestHeader(value = "X-Family-ID", required = false) String familyId) {
        return ResponseEntity.ok(financialHealthService.getFactors(UUID.fromString(user.getUsername()), parseFamilyId(familyId)));
    }

    @GetMapping("/history")
    public ResponseEntity<FinancialHealthHistoryResponse> getHistory(
            @AuthenticationPrincipal UserDetails user,
            @RequestHeader(value = "X-Family-ID", required = false) String familyId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int limit,
            @RequestParam(defaultValue = "calculatedAt:desc") String sort) {
        return ResponseEntity.ok(financialHealthService.getHistory(UUID.fromString(user.getUsername()), parseFamilyId(familyId), page, limit, sort));
    }

    @GetMapping("/explanation")
    public ResponseEntity<FinancialHealthExplanationResponse> getExplanation(
            @AuthenticationPrincipal UserDetails user,
            @RequestHeader(value = "X-Family-ID", required = false) String familyId) {
        return ResponseEntity.ok(financialHealthService.getExplanation(UUID.fromString(user.getUsername()), parseFamilyId(familyId)));
    }

    private Optional<UUID> parseFamilyId(String familyId) {
        return familyId == null || familyId.isBlank() ? Optional.empty() : Optional.of(UUID.fromString(familyId));
    }
}

package com.neowallet.finance.controller;

import com.neowallet.finance.dto.*;
import com.neowallet.finance.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transactions")
    public ResponseEntity<TransactionResponse> createTransaction(
            @AuthenticationPrincipal UserDetails user,
            @RequestHeader(value = "X-Family-ID", required = false) String familyId,
            @Valid @RequestBody CreateTransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(transactionService.createTransaction(UUID.fromString(user.getUsername()), parseFamilyId(familyId), request));
    }

    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransaction(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable UUID transactionId) {
        return ResponseEntity.ok(transactionService.getTransaction(UUID.fromString(user.getUsername()), transactionId));
    }

    @GetMapping("/transactions")
    public ResponseEntity<TransactionsResponse> listTransactions(
            @AuthenticationPrincipal UserDetails user,
            @RequestHeader(value = "X-Family-ID", required = false) String familyId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String filterCategory,
            @RequestParam(required = false) String filterType,
            @RequestParam(required = false) String filterStatus,
            @RequestParam(required = false) String filterFamilyMemberId,
            @RequestParam(required = false) BigDecimal filterAmountFrom,
            @RequestParam(required = false) BigDecimal filterAmountTo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate filterDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate filterDateTo) {
        TransactionFilter filter = TransactionFilter.builder()
            .category(filterCategory)
            .type(filterType)
            .status(filterStatus)
            .familyMemberId(filterFamilyMemberId)
            .amountFrom(filterAmountFrom)
            .amountTo(filterAmountTo)
            .dateFrom(filterDateFrom)
            .dateTo(filterDateTo)
            .build();
        return ResponseEntity.ok(transactionService.listTransactions(
            UUID.fromString(user.getUsername()), parseFamilyId(familyId), filter, page, limit, sort));
    }

    @PutMapping("/transactions/{transactionId}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable UUID transactionId,
            @Valid @RequestBody UpdateTransactionRequest request) {
        return ResponseEntity.ok(transactionService.updateTransaction(UUID.fromString(user.getUsername()), transactionId, request));
    }

    @DeleteMapping("/transactions/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable UUID transactionId) {
        transactionService.deleteTransaction(UUID.fromString(user.getUsername()), transactionId);
        return ResponseEntity.noContent().build();
    }

    private Optional<UUID> parseFamilyId(String familyId) {
        return familyId == null || familyId.isBlank() ? Optional.empty() : Optional.of(UUID.fromString(familyId));
    }
}

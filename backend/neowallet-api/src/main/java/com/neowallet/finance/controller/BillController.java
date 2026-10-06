package com.neowallet.finance.controller;

import com.neowallet.finance.dto.*;
import com.neowallet.finance.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    @PostMapping
    public ResponseEntity<BillResponse> createBill(
            @AuthenticationPrincipal UserDetails user,
            @RequestHeader(value = "X-Family-ID", required = false) String familyId,
            @RequestBody CreateBillRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(billService.createBill(UUID.fromString(user.getUsername()), parseFamilyId(familyId), request));
    }

    @GetMapping
    public ResponseEntity<BillListResponse> getBills(
            @AuthenticationPrincipal UserDetails user,
            @RequestHeader(value = "X-Family-ID", required = false) String familyId,
            @RequestParam(name = "filter[status]", required = false) String status,
            @RequestParam(name = "filter[category]", required = false) String category,
            @RequestParam(name = "filter[dueDateFrom]", required = false) LocalDate dueDateFrom,
            @RequestParam(name = "filter[dueDateTo]", required = false) LocalDate dueDateTo,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "dueDate:asc") String sort) {
        return ResponseEntity.ok(billService.listBills(
                UUID.fromString(user.getUsername()),
                parseFamilyId(familyId),
                Optional.ofNullable(status),
                Optional.ofNullable(category),
                Optional.ofNullable(dueDateFrom),
                Optional.ofNullable(dueDateTo),
                page,
                limit,
                sort));
    }

    @GetMapping("/{billId}")
    public ResponseEntity<BillResponse> getBill(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable UUID billId,
            @RequestHeader(value = "X-Family-ID", required = false) String familyId) {
        return ResponseEntity.ok(billService.getBill(UUID.fromString(user.getUsername()), billId, parseFamilyId(familyId)));
    }

    @PutMapping("/{billId}")
    public ResponseEntity<BillResponse> updateBill(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable UUID billId,
            @RequestHeader(value = "X-Family-ID", required = false) String familyId,
            @RequestBody UpdateBillRequest request) {
        return ResponseEntity.ok(billService.updateBill(UUID.fromString(user.getUsername()), billId, parseFamilyId(familyId), request));
    }

    @DeleteMapping("/{billId}")
    public ResponseEntity<Void> deleteBill(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable UUID billId,
            @RequestHeader(value = "X-Family-ID", required = false) String familyId) {
        billService.deleteBill(UUID.fromString(user.getUsername()), billId, parseFamilyId(familyId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{billId}/mark-paid")
    public ResponseEntity<MarkPaidResponse> markBillPaid(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable UUID billId,
            @RequestHeader(value = "X-Family-ID", required = false) String familyId,
            @RequestBody(required = false) MarkPaidRequest request) {
        return ResponseEntity.ok(billService.markBillPaid(UUID.fromString(user.getUsername()), billId, parseFamilyId(familyId), request));
    }

    private Optional<UUID> parseFamilyId(String familyId) {
        return familyId == null || familyId.isBlank() ? Optional.empty() : Optional.of(UUID.fromString(familyId));
    }
}

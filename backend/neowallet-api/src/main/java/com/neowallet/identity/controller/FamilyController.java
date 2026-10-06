package com.neowallet.identity.controller;

import com.neowallet.identity.dto.*;
import com.neowallet.identity.service.FamilyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FamilyController {

    private final FamilyService familyService;

    @PostMapping("/families")
    public ResponseEntity<FamilyResponse> createFamily(@AuthenticationPrincipal UserDetails user,
                                                       @Valid @RequestBody CreateFamilyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(familyService.createFamily(UUID.fromString(user.getUsername()), request));
    }

    @GetMapping("/families/{familyId}")
    public ResponseEntity<FamilyResponse> getFamily(@AuthenticationPrincipal UserDetails user,
                                                    @PathVariable UUID familyId) {
        return ResponseEntity.ok(familyService.getFamily(UUID.fromString(user.getUsername()), familyId));
    }

    @PutMapping("/families/{familyId}")
    public ResponseEntity<FamilyResponse> updateFamily(@AuthenticationPrincipal UserDetails user,
                                                       @PathVariable UUID familyId,
                                                       @Valid @RequestBody UpdateFamilyRequest request) {
        return ResponseEntity.ok(familyService.updateFamily(UUID.fromString(user.getUsername()), familyId, request));
    }

    @DeleteMapping("/families/{familyId}")
    public ResponseEntity<Void> deleteFamily(@AuthenticationPrincipal UserDetails user,
                                             @PathVariable UUID familyId,
                                             @Valid @RequestBody DeleteFamilyRequest request) {
        familyService.deleteFamily(UUID.fromString(user.getUsername()), familyId, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/families/{familyId}/invitations")
    public ResponseEntity<FamilyInvitationResponse> inviteFamilyMember(@AuthenticationPrincipal UserDetails user,
                                                                       @PathVariable UUID familyId,
                                                                       @Valid @RequestBody FamilyInvitationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(familyService.inviteFamilyMember(UUID.fromString(user.getUsername()), familyId, request));
    }

    @GetMapping("/families/{familyId}/members")
    public ResponseEntity<List<FamilyMemberResponse>> getFamilyMembers(@AuthenticationPrincipal UserDetails user,
                                                                       @PathVariable UUID familyId) {
        return ResponseEntity.ok(familyService.getFamilyMembers(UUID.fromString(user.getUsername()), familyId));
    }

    @PutMapping("/families/{familyId}/members/{memberId}")
    public ResponseEntity<FamilyMemberResponse> updateFamilyMember(@AuthenticationPrincipal UserDetails user,
                                                                   @PathVariable UUID familyId,
                                                                   @PathVariable UUID memberId,
                                                                   @Valid @RequestBody UpdateFamilyMemberRequest request) {
        return ResponseEntity.ok(familyService.updateFamilyMember(UUID.fromString(user.getUsername()), familyId, memberId, request));
    }

    @DeleteMapping("/families/{familyId}/members/{memberId}")
    public ResponseEntity<Void> removeFamilyMember(@AuthenticationPrincipal UserDetails user,
                                                   @PathVariable UUID familyId,
                                                   @PathVariable UUID memberId) {
        familyService.removeFamilyMember(UUID.fromString(user.getUsername()), familyId, memberId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/family-invitations/{token}/accept")
    public ResponseEntity<AcceptInvitationResponse> acceptInvitation(@AuthenticationPrincipal UserDetails user,
                                                                     @PathVariable String token) {
        return ResponseEntity.ok(familyService.acceptFamilyInvitation(UUID.fromString(user.getUsername()), token));
    }

    @PostMapping("/family-invitations/{token}/reject")
    public ResponseEntity<FamilyInvitationResponse> rejectInvitation(@AuthenticationPrincipal UserDetails user,
                                                                     @PathVariable String token) {
        return ResponseEntity.ok(familyService.rejectFamilyInvitation(UUID.fromString(user.getUsername()), token));
    }

}

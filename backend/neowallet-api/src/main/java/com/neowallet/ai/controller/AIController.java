package com.neowallet.ai.controller;

import com.neowallet.ai.dto.AIResponse;
import com.neowallet.ai.service.NeoConciergeService;
import com.neowallet.identity.entity.FamilyMember;
import com.neowallet.identity.repository.FamilyMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

/**
 * Entry point for Neo AI conversations.
 */
@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AIController {

    private final NeoConciergeService neoConciergeService;
    private final FamilyMemberRepository familyMemberRepository;

    @PostMapping("/chat")
    public ResponseEntity<AIResponse> chat(
            @AuthenticationPrincipal UUID userId,
            @RequestBody AIChatRequest request
    ) {
        String role = resolveRole(userId, request.familyId());
        AIResponse response = neoConciergeService.chat(userId, request.familyId(), request.message(), request.sessionId(), role);
        return ResponseEntity.ok(response);
    }

    private String resolveRole(UUID userId, UUID familyId) {
        if (familyId == null) {
            return "OWNER";
        }
        Optional<FamilyMember> membership = familyMemberRepository.findByFamily_FamilyIdAndUser_UserIdAndLeftAtIsNull(familyId, userId);
        return membership.map(FamilyMember::getRole).orElse("RESTRICTED");
    }

    public record AIChatRequest(UUID familyId, String message, String sessionId) {
    }
}

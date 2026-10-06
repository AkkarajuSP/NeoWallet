package com.neowallet.identity.service;

import com.neowallet.identity.dto.*;
import com.neowallet.identity.entity.*;
import com.neowallet.identity.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FamilyService {

    private static final List<String> VALID_FAMILY_ROLES = List.of("OWNER", "MEMBER", "RESTRICTED");
    private static final List<String> INVITABLE_ROLES = List.of("MEMBER", "RESTRICTED");
    private static final List<String> MUTABLE_ROLES = List.of("MEMBER", "RESTRICTED");
    private static final int TOKEN_BYTES = 24;
    private static final int INVITATION_TTL_HOURS = 24;

    private final FamilyRepository familyRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final FamilyInvitationRepository familyInvitationRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final AuditService auditService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public FamilyResponse createFamily(UUID userId, CreateFamilyRequest request) {
        User user = findUser(userId);
        ensureNotInFamily(userId);

        String name = request.getName().trim();
        validateFamilyName(name);

        String currency = request.getCurrency() != null ? request.getCurrency().trim().toUpperCase() : "USD";
        if (!currency.matches("^[A-Z]{3}$")) {
            throw new ValidationException("currency must be a 3-letter ISO code");
        }

        Family family = new Family();
        family.setName(name);
        family.setCurrency(currency);
        family.setOwner(user);
        family.setMemberCount(1);
        family = familyRepository.save(family);
        family = familyRepository.findByFamilyIdAndDeletedAtIsNull(family.getFamilyId())
            .orElseThrow(() -> new IllegalStateException("Family not found after save"));
        family.setOwner(user);

        FamilyMember owner = new FamilyMember();
        owner.setFamily(family);
        owner.setUser(user);
        owner.setRole("OWNER");
        familyMemberRepository.save(owner);

        updateUserProfile(user, family.getFamilyId(), "OWNER");

        auditService.record(userId, "USER", "FAMILY_CREATED", "FAMILY", family.getFamilyId(), "SUCCESS",
            "{\"name\": \"" + escape(name) + "\"}");

        return mapToFamilyResponse(family);
    }

    @Transactional(readOnly = true)
    public FamilyResponse getFamily(UUID userId, UUID familyId) {
        Family family = findFamily(familyId);
        ensureFamilyMember(userId, familyId);
        return mapToFamilyResponse(family);
    }

    @Transactional
    public FamilyResponse updateFamily(UUID userId, UUID familyId, UpdateFamilyRequest request) {
        Family family = findFamily(familyId);
        ensureFamilyOwner(userId, familyId);

        if (request.getName() != null) {
            String name = request.getName().trim();
            validateFamilyName(name);
            family.setName(name);
        }

        if (request.getCurrency() != null) {
            String currency = request.getCurrency().trim().toUpperCase();
            if (!currency.matches("^[A-Z]{3}$")) {
                throw new ValidationException("currency must be a 3-letter ISO code");
            }
            family.setCurrency(currency);
        }

        family = familyRepository.save(family);

        auditService.record(userId, "USER", "FAMILY_UPDATED", "FAMILY", familyId, "SUCCESS", "{}");

        return mapToFamilyResponse(family);
    }

    @Transactional
    public void deleteFamily(UUID userId, UUID familyId, DeleteFamilyRequest request) {
        if (!"DELETE".equals(request.getConfirmation())) {
            throw new ValidationException("Confirmation must be 'DELETE'");
        }

        Family family = findFamily(familyId);
        ensureFamilyOwner(userId, familyId);

        List<FamilyMember> activeMembers = familyMemberRepository.findByFamily_FamilyIdAndLeftAtIsNull(familyId);
        if (activeMembers.size() > 1) {
            throw new AccessDeniedException("Cannot delete family with other members");
        }

        family.setDeletedAt(Instant.now());
        familyRepository.save(family);

        for (FamilyMember member : activeMembers) {
            member.setLeftAt(Instant.now());
            familyMemberRepository.save(member);
            updateUserProfile(member.getUser(), null, null);
        }

        auditService.record(userId, "USER", "FAMILY_DELETED", "FAMILY", familyId, "SUCCESS", "{}");
    }

    @Transactional
    public FamilyInvitationResponse inviteFamilyMember(UUID userId, UUID familyId, FamilyInvitationRequest request) {
        Family family = findFamily(familyId);
        FamilyMember inviter = familyMemberRepository.findByFamily_FamilyIdAndUser_UserIdAndLeftAtIsNull(familyId, userId)
            .orElseThrow(() -> new AccessDeniedException("Not a family member"));

        if (!("OWNER".equals(inviter.getRole()) || "MEMBER".equals(inviter.getRole()))) {
            throw new AccessDeniedException("Only OWNER or MEMBER can invite");
        }

        String email = request.getEmail().trim().toLowerCase();
        String role = request.getRole() != null ? request.getRole().toUpperCase() : "MEMBER";
        if (!INVITABLE_ROLES.contains(role)) {
            throw new ValidationException("role must be MEMBER or RESTRICTED");
        }

        Optional<User> existing = userRepository.findByEmailAndDeletedAtIsNull(email);
        if (existing.isPresent()) {
            UUID targetId = existing.get().getUserId();
            if (familyMemberRepository.existsByFamily_FamilyIdAndUser_UserIdAndLeftAtIsNull(familyId, targetId)) {
                throw new IllegalStateException("Email already a member");
            }
        }

        if (familyInvitationRepository.existsByFamily_FamilyIdAndEmailAndAcceptedAtIsNullAndRejectedAtIsNullAndExpiresAtAfter(
            familyId, email, Instant.now())) {
            throw new IllegalStateException("Invitation already pending");
        }

        String token = generateToken();
        FamilyInvitation invitation = new FamilyInvitation();
        invitation.setFamily(family);
        invitation.setInviter(inviter.getUser());
        invitation.setEmail(email);
        invitation.setRole(role);
        invitation.setToken(token);
        invitation.setExpiresAt(Instant.now().plus(INVITATION_TTL_HOURS, ChronoUnit.HOURS));
        familyInvitationRepository.save(invitation);
        invitation = familyInvitationRepository.findByToken(token)
            .orElseThrow(() -> new IllegalStateException("Invitation not found after save"));

        auditService.record(userId, "USER", "FAMILY_INVITATION_CREATED", "INVITATION", invitation.getInvitationId(), "SUCCESS",
            "{\"email\": \"" + escape(email) + "\", \"familyId\": \"" + familyId + "\"}");

        return mapToInvitationResponse(invitation);
    }

    @Transactional(readOnly = true)
    public List<FamilyMemberResponse> getFamilyMembers(UUID userId, UUID familyId) {
        ensureFamilyMember(userId, familyId);
        return familyMemberRepository.findByFamily_FamilyIdAndLeftAtIsNull(familyId).stream()
            .map(this::mapToMemberResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public FamilyMemberResponse updateFamilyMember(UUID userId, UUID familyId, UUID memberId, UpdateFamilyMemberRequest request) {
        Family family = findFamily(familyId);
        ensureFamilyOwner(userId, familyId);

        FamilyMember member = familyMemberRepository.findByMemberIdAndLeftAtIsNull(memberId)
            .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        if (!member.getFamily().getFamilyId().equals(familyId)) {
            throw new IllegalArgumentException("Member not in this family");
        }

        if ("OWNER".equals(member.getRole())) {
            throw new ValidationException("Cannot change owner role");
        }

        String role = request.getRole().toUpperCase();
        if (!MUTABLE_ROLES.contains(role)) {
            throw new ValidationException("role must be MEMBER or RESTRICTED");
        }

        member.setRole(role);
        familyMemberRepository.save(member);

        updateUserProfile(member.getUser(), family.getFamilyId(), role);

        auditService.record(userId, "USER", "FAMILY_MEMBER_ROLE_CHANGED", "FAMILY_MEMBER", memberId, "SUCCESS",
            "{\"newRole\": \"" + role + "\"}");

        return mapToMemberResponse(member);
    }

    @Transactional
    public void removeFamilyMember(UUID userId, UUID familyId, UUID memberId) {
        Family family = findFamily(familyId);
        ensureFamilyOwner(userId, familyId);

        FamilyMember member = familyMemberRepository.findByMemberIdAndLeftAtIsNull(memberId)
            .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        if (!member.getFamily().getFamilyId().equals(familyId)) {
            throw new IllegalArgumentException("Member not in this family");
        }

        if (member.getUser().getUserId().equals(userId)) {
            throw new ValidationException("Owner cannot remove themselves");
        }

        if ("OWNER".equals(member.getRole())) {
            throw new ValidationException("Cannot remove owner");
        }

        member.setLeftAt(Instant.now());
        familyMemberRepository.save(member);

        family.setMemberCount(Math.max(0, family.getMemberCount() - 1));
        familyRepository.save(family);

        updateUserProfile(member.getUser(), null, null);

        auditService.record(userId, "USER", "FAMILY_MEMBER_REMOVED", "FAMILY_MEMBER", memberId, "SUCCESS",
            "{\"removedUserId\": \"" + member.getUser().getUserId() + "\"}");
    }

    @Transactional
    public AcceptInvitationResponse acceptFamilyInvitation(UUID userId, String token) {
        User user = findUser(userId);

        FamilyInvitation invitation = familyInvitationRepository.findByToken(token)
            .orElseThrow(() -> new EntityNotFoundException("Invitation not found"));

        if (!invitation.isPending()) {
            throw new EntityNotFoundException("Invitation not found or expired");
        }

        if (!invitation.getEmail().equalsIgnoreCase(user.getEmail())) {
            throw new AccessDeniedException("Invitation not for this user");
        }

        if (familyMemberRepository.findByUser_UserIdAndLeftAtIsNull(userId).isPresent()) {
            throw new IllegalStateException("User already belongs to a family");
        }

        Family family = invitation.getFamily();
        FamilyMember member = new FamilyMember();
        member.setFamily(family);
        member.setUser(user);
        member.setRole(invitation.getRole());
        familyMemberRepository.save(member);

        family.setMemberCount(family.getMemberCount() + 1);
        familyRepository.save(family);

        invitation.setAcceptedAt(Instant.now());
        familyInvitationRepository.save(invitation);

        updateUserProfile(user, family.getFamilyId(), invitation.getRole());

        auditService.record(userId, "USER", "FAMILY_INVITATION_ACCEPTED", "INVITATION", invitation.getInvitationId(), "SUCCESS",
            "{\"familyId\": \"" + family.getFamilyId() + "\", \"role\": \"" + invitation.getRole() + "\"}");

        return new AcceptInvitationResponse(
            family.getFamilyId(),
            member.getMemberId(),
            member.getRole(),
            member.getJoinedAt().toString()
        );
    }

    @Transactional
    public FamilyInvitationResponse rejectFamilyInvitation(UUID userId, String token) {
        User user = findUser(userId);

        FamilyInvitation invitation = familyInvitationRepository.findByToken(token)
            .orElseThrow(() -> new EntityNotFoundException("Invitation not found"));

        if (!invitation.isPending()) {
            throw new EntityNotFoundException("Invitation not found or expired");
        }

        if (!invitation.getEmail().equalsIgnoreCase(user.getEmail())) {
            throw new AccessDeniedException("Invitation not for this user");
        }

        invitation.setRejectedAt(Instant.now());
        familyInvitationRepository.save(invitation);
        invitation = familyInvitationRepository.findByToken(token)
            .orElseThrow(() -> new IllegalStateException("Invitation not found after save"));

        auditService.record(userId, "USER", "FAMILY_INVITATION_REJECTED", "INVITATION", invitation.getInvitationId(), "SUCCESS",
            "{\"familyId\": \"" + invitation.getFamily().getFamilyId() + "\"}");

        return mapToInvitationResponse(invitation);
    }

    private User findUser(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private Family findFamily(UUID familyId) {
        return familyRepository.findByFamilyIdAndDeletedAtIsNull(familyId)
            .orElseThrow(() -> new IllegalArgumentException("Family not found"));
    }

    private void ensureFamilyMember(UUID userId, UUID familyId) {
        if (!familyMemberRepository.existsByFamily_FamilyIdAndUser_UserIdAndLeftAtIsNull(familyId, userId)) {
            throw new AccessDeniedException("Not a family member");
        }
    }

    private void ensureFamilyOwner(UUID userId, UUID familyId) {
        FamilyMember member = familyMemberRepository.findByFamily_FamilyIdAndUser_UserIdAndLeftAtIsNull(familyId, userId)
            .orElseThrow(() -> new AccessDeniedException("Not a family member"));
        if (!"OWNER".equals(member.getRole())) {
            throw new AccessDeniedException("Not family owner");
        }
    }

    private void ensureNotInFamily(UUID userId) {
        if (familyMemberRepository.findByUser_UserIdAndLeftAtIsNull(userId).isPresent()) {
            throw new IllegalStateException("User already belongs to a family");
        }
    }

    private void validateFamilyName(String name) {
        if (name.isEmpty() || name.length() > 100) {
            throw new ValidationException("Family name must be 1-100 characters");
        }
    }

    private void updateUserProfile(User user, UUID familyId, String role) {
        UserProfile profile = userProfileRepository.findByUser_UserId(user.getUserId())
            .orElseGet(() -> {
                UserProfile p = new UserProfile();
                p.setUser(user);
                return p;
            });
        profile.setFamilyId(familyId);
        profile.setFamilyRole(role);
        userProfileRepository.save(profile);
    }

    private String generateToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String escape(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private FamilyResponse mapToFamilyResponse(Family family) {
        return FamilyResponse.builder()
            .familyId(family.getFamilyId())
            .name(family.getName())
            .currency(family.getCurrency())
            .ownerId(family.getOwner().getUserId())
            .memberCount(family.getMemberCount())
            .createdAt(family.getCreatedAt().toString())
            .updatedAt(family.getUpdatedAt().toString())
            .build();
    }

    private FamilyMemberResponse mapToMemberResponse(FamilyMember member) {
        User u = member.getUser();
        return FamilyMemberResponse.builder()
            .memberId(member.getMemberId())
            .userId(u.getUserId())
            .firstName(u.getFirstName())
            .lastName(u.getLastName())
            .email(u.getEmail())
            .role(member.getRole())
            .joinedAt(member.getJoinedAt().toString())
            .build();
    }

    private FamilyInvitationResponse mapToInvitationResponse(FamilyInvitation invitation) {
        return FamilyInvitationResponse.builder()
            .invitationId(invitation.getInvitationId())
            .token(invitation.getToken())
            .email(invitation.getEmail())
            .role(invitation.getRole())
            .expiresAt(invitation.getExpiresAt().toString())
            .createdAt(invitation.getCreatedAt().toString())
            .build();
    }

}

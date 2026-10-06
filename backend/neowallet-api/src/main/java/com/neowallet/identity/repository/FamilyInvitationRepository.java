package com.neowallet.identity.repository;

import com.neowallet.identity.entity.FamilyInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FamilyInvitationRepository extends JpaRepository<FamilyInvitation, UUID> {

    Optional<FamilyInvitation> findByToken(String token);

    boolean existsByFamily_FamilyIdAndEmailAndAcceptedAtIsNullAndRejectedAtIsNullAndExpiresAtAfter(
        UUID familyId, String email, java.time.Instant now);

}

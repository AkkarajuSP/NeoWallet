package com.neowallet.identity.repository;

import com.neowallet.identity.entity.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FamilyMemberRepository extends JpaRepository<FamilyMember, UUID> {

    Optional<FamilyMember> findByFamily_FamilyIdAndUser_UserIdAndLeftAtIsNull(UUID familyId, UUID userId);

    List<FamilyMember> findByFamily_FamilyIdAndLeftAtIsNull(UUID familyId);

    Optional<FamilyMember> findByMemberIdAndLeftAtIsNull(UUID memberId);

    Optional<FamilyMember> findByUser_UserIdAndLeftAtIsNull(UUID userId);

    boolean existsByFamily_FamilyIdAndUser_UserIdAndLeftAtIsNull(UUID familyId, UUID userId);

}

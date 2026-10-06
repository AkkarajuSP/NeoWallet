package com.neowallet.identity.repository;

import com.neowallet.identity.entity.OtpChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OtpChallengeRepository extends JpaRepository<OtpChallenge, UUID> {

    List<OtpChallenge> findByEmailAndPurposeAndVerifiedAtIsNullOrderByCreatedAtDesc(String email, String purpose);

    List<OtpChallenge> findByUserUserIdAndPurposeAndVerifiedAtIsNullOrderByCreatedAtDesc(UUID userId, String purpose);

    Optional<OtpChallenge> findByOtpId(UUID otpId);

}

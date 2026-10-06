package com.neowallet.identity.repository;

import com.neowallet.identity.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserCredentialRepository extends JpaRepository<UserCredential, UUID> {

    List<UserCredential> findByUserUserIdAndCredentialTypeOrderByCreatedAtDesc(UUID userId, String credentialType);

}

package com.neowallet.identity.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neowallet.identity.entity.AuditLog;
import com.neowallet.identity.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(UUID actorId, String actorType, String action, String resourceType,
                       UUID resourceId, String result, String metadata) {
        AuditLog audit = new AuditLog();
        audit.setActorId(actorId);
        audit.setActorType(actorType);
        audit.setAction(action);
        audit.setResourceType(resourceType);
        audit.setResourceId(resourceId);
        audit.setResult(result);
        audit.setMetadata(parseMetadata(metadata));
        auditLogRepository.save(audit);
    }

    public void recordAuthentication(UUID actorId, String action, UUID resourceId, boolean success, String metadata) {
        record(actorId, actorId == null ? "SYSTEM" : "USER", action, "AUTH", resourceId, success ? "SUCCESS" : "FAILURE", metadata);
    }

    private JsonNode parseMetadata(String metadata) {
        if (metadata == null || metadata.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readTree(metadata);
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse audit metadata as JSON, storing null", e);
            return null;
        }
    }

}

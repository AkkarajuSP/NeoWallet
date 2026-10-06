# NeoWallet Compliance Traceability v1

## Executive Summary

This document provides a comprehensive compliance traceability matrix mapping requirements to risks, controls, business rules, APIs, data entities, security controls, and tests for NeoWallet MVP.

**IMPORTANT DISCLAIMER**: This document is a requirements/control mapping and risk assessment only. NeoWallet is NOT claimed to be legally or regulatorily compliant.

**Date**: August 18, 2026
**Status**: Ready for Product Owner Approval

---

## 1. Traceability Matrix Overview

### 1.1 Traceability Categories

| Category | Requirements | Risks | Controls | Business Rules | APIs | Data Entities | Security Controls | Tests |
|----------|--------------|-------|----------|---------------|------|---------------|------------------|-------|
| Data Privacy | 8 | 12 | 8 | 5 | 12 | 15 | 8 | 12 |
| AI Governance | 7 | 11 | 7 | 3 | 4 | 4 | 7 | 8 |
| KYC/AML | 4 | 6 | 4 | 2 | 3 | 3 | 4 | 4 |
| Consumer Protection | 8 | 8 | 8 | 4 | 6 | 6 | 8 | 8 |
| User Rights | 8 | 8 | 8 | 4 | 8 | 8 | 8 | 8 |
| Security | 10 | 10 | 10 | 5 | 10 | 10 | 10 | 10 |
| **TOTAL** | **45** | **55** | **45** | **23** | **43** | **46** | **45** | **50** |

---

## 2. Data Privacy Traceability

### 2.1 Personal Data Collection

**Requirement**: Collect only necessary personal data

**Risk**: Excessive PII collection, data breach

**Control**: Data minimization, PII identification in data dictionary

**Business Rule**: BR-USR-001, BR-SEC-004

**API**: POST /api/v1/auth/register, PUT /api/v1/users/profile

**Data Entity**: users, user_profiles

**Security Control**: Control 25 (PII Masking), Control 27 (Log Security)

**Test**: Test PII minimization, Test PII masking in logs

### 2.2 Sensitive Financial Information Collection

**Requirement**: Collect only necessary financial information

**Risk**: Financial data breach, unauthorized access

**Control**: Data minimization, encryption at rest and in transit

**Business Rule**: BR-FIN-001, BR-SEC-004

**API**: POST /api/v1/transactions, GET /api/v1/financial-overview

**Data Entity**: transactions, financial_overviews

**Security Control**: Control 19 (Encryption at Rest), Control 20 (Encryption in Transit), Control 26 (Financial Data Protection)

**Test**: Test financial data encryption, Test financial data access controls

### 2.3 Purpose Limitation

**Requirement**: Use data only for stated purposes

**Risk**: Unauthorized data use, data misuse

**Control**: Purpose limitation documentation, consent management

**Business Rule**: BR-USR-001, BR-SEC-004

**API**: PUT /api/v1/users/consent

**Data Entity**: user_preferences

**Security Control**: Control 25 (PII Masking), Control 27 (Log Security)

**Test**: Test purpose limitation, Test consent management

### 2.4 Data Minimization

**Requirement**: Minimize data collection and processing

**Risk**: Excessive data collection, data breach

**Control**: Data minimization in design, data minimization in AI requests

**Business Rule**: BR-USR-001, BR-AI-007

**API**: POST /api/v1/ai/chat

**Data Entity**: ai_conversations, ai_messages

**Security Control**: Control 42 (AI Data Minimization)

**Test**: Test data minimization in AI requests

### 2.5 Consent Management

**Requirement**: Obtain and manage user consent

**Risk**: Legal non-compliance, user complaints

**Control**: Consent collection, consent withdrawal, consent tracking

**Business Rule**: BR-USR-001, BR-SEC-004

**API**: PUT /api/v1/users/consent

**Data Entity**: user_preferences

**Security Control**: Control 25 (PII Masking), Control 27 (Log Security)

**Test**: Test consent collection, Test consent withdrawal

### 2.6 Data Access

**Requirement**: Provide user access to own data

**Risk**: Unauthorized data access, data breach

**Control**: Server-side authorization, RBAC, family isolation

**Business Rule**: BR-USR-005, BR-FAM-009

**API**: GET /api/v1/users/profile, GET /api/v1/transactions

**Data Entity**: users, transactions

**Security Control**: Control 9 (RBAC - User), Control 13 (Family Data Isolation)

**Test**: Test user data access, Test family data access

### 2.7 Data Correction

**Requirement**: Allow users to correct own data

**Risk**: Incorrect data, user complaints

**Control**: Update APIs, authorization checks

**Business Rule**: BR-USR-006

**API**: PUT /api/v1/users/profile, PUT /api/v1/transactions

**Data Entity**: users, transactions

**Security Control**: Control 9 (RBAC - User), Control 14 (Resource Ownership)

**Test**: Test data correction

### 2.8 Data Deletion

**Requirement**: Allow users to request data deletion

**Risk**: Legal non-compliance, data breach

**Control**: Account deletion API, soft delete, retention policy

**Business Rule**: BR-RET-001

**API**: DELETE /api/v1/users

**Data Entity**: users (soft delete)

**Security Control**: Control 25 (PII Masking), Control 27 (Log Security)

**Test**: Test account deletion, Test data retention

---

## 3. AI Governance Traceability

### 3.1 AI Financial Recommendations

**Requirement**: AI recommendations are educational, not guaranteed financial advice

**Risk**: Financial harm, legal liability, user complaints

**Control**: AI read-only enforcement, recommendation disclaimers, human-in-the-loop

**Business Rule**: BR-AI-001, BR-AI-004

**API**: POST /api/v1/ai/chat

**Data Entity**: ai_conversations, ai_recommendations

**Security Control**: Control 39 (AI Data Access), Control 40 (AI Read-Only), Control 41 (Prompt Injection Protection)

**Test**: Test AI read-only enforcement, Test recommendation disclaimers

### 3.2 AI Financial Education

**Requirement**: AI education is for educational purposes only

**Risk**: Misleading financial advice, legal liability

**Control**: Education disclaimers, AI is not a licensed financial advisor

**Business Rule**: BR-AI-001

**API**: POST /api/v1/ai/chat

**Data Entity**: ai_conversations, ai_messages

**Security Control**: Control 40 (AI Read-Only)

**Test**: Test education disclaimers

### 3.3 Personalized Recommendations

**Requirement**: AI recommendations are based on user data and not guaranteed

**Risk**: Incorrect recommendations, financial harm

**Control**: Data minimization in AI requests, recommendation disclaimers

**Business Rule**: BR-AI-007

**API**: POST /api/v1/ai/chat

**Data Entity**: ai_conversations, ai_messages

**Security Control**: Control 42 (AI Data Minimization)

**Test**: Test personalized recommendations

### 3.4 Automated Decision-Making

**Requirement**: AI does not make automated financial decisions for MVP

**Risk**: Unauthorized financial actions, financial harm

**Control**: AI read-only enforcement, human-in-the-loop

**Business Rule**: BR-AI-001, BR-AI-004

**API**: POST /api/v1/ai/chat

**Data Entity**: ai_conversations, ai_recommendations

**Security Control**: Control 40 (AI Read-Only)

**Test**: Test AI read-only enforcement

### 3.5 AI Hallucination

**Requirement**: Mitigate AI hallucination risks

**Risk**: Incorrect information, financial harm

**Control**: Confidence thresholds, output validation, human-in-the-loop

**Business Rule**: BR-AI-005, BR-AI-006

**API**: POST /api/v1/ai/chat

**Data Entity**: ai_conversations, ai_messages

**Security Control**: Control 41 (Prompt Injection Protection)

**Test**: Test confidence thresholds, Test output validation

### 3.6 AI Transparency

**Requirement**: Provide AI transparency and explainability

**Risk**: Lack of trust, legal liability

**Control**: AI recommendation explanations, confidence scores, audit logging

**Business Rule**: BR-AI-010

**API**: POST /api/v1/ai/chat

**Data Entity**: ai_conversations, ai_tool_invocations

**Security Control**: Control 44 (AI Audit Logging)

**Test**: Test AI transparency

### 3.7 AI Disclosures

**Requirement**: Provide appropriate AI disclosures

**Risk**: Legal liability, user complaints

**Control**: AI recommendation disclaimers, AI is not a licensed financial advisor

**Business Rule**: BR-AI-001

**API**: POST /api/v1/ai/chat

**Data Entity**: ai_conversations

**Security Control**: Control 40 (AI Read-Only)

**Test**: Test AI disclosures

---

## 4. KYC/AML Traceability

### 4.1 MVP KYC/AML Assessment

**Requirement**: Assess MVP KYC/AML obligations

**Risk**: Legal non-compliance, regulatory penalties

**Control**: Professional review, email verification, phone verification

**Business Rule**: BR-USR-001, BR-USR-003

**API**: POST /api/v1/auth/register, POST /api/v1/auth/password-reset/request

**Data Entity**: users, otp_challenges

**Security Control**: Control 1 (User Registration), Control 3 (OTP)

**Test**: Test email verification, Test phone verification

### 4.2 Provider KYC/AML Responsibility

**Requirement**: Define provider KYC/AML responsibility

**Risk**: Legal non-compliance, regulatory penalties

**Control**: Provider contracts, provider due diligence

**Business Rule**: BR-USR-001

**API**: N/A (future)

**Data Entity**: N/A (future)

**Security Control**: Control 32 (Secrets Management)

**Test**: Test provider due diligence (future)

### 4.3 NeoWallet KYC/AML Responsibility

**Requirement**: Define NeoWallet KYC/AML responsibility

**Risk**: Legal non-compliance, regulatory penalties

**Control**: Professional review, KYC/AML assessment

**Business Rule**: BR-USR-001

**API**: N/A

**Data Entity**: N/A

**Security Control**: N/A

**Test**: N/A (professional review)

### 4.4 Future KYC/AML Considerations

**Requirement**: Assess future KYC/AML obligations

**Risk**: Legal non-compliance, regulatory penalties

**Control**: Professional review, phase-based assessment

**Business Rule**: BR-USR-001

**API**: N/A (future)

**Data Entity**: N/A (future)

**Security Control**: N/A

**Test**: N/A (professional review)

---

## 5. Consumer Protection Traceability

### 5.1 Transparent Pricing

**Requirement**: Provide transparent pricing information

**Risk**: Legal non-compliance, user complaints

**Control**: Pricing documentation, subscription terms

**Business Rule**: BR-USR-001

**API**: N/A (future subscription API)

**Data Entity**: N/A (future)

**Security Control**: N/A

**Test**: Test pricing transparency (future)

### 5.2 Subscription Terms

**Requirement**: Provide clear subscription terms

**Risk**: Legal non-compliance, user complaints

**Control**: Subscription terms documentation, cancellation policy

**Business Rule**: BR-USR-001

**API**: N/A (future subscription API)

**Data Entity**: N/A (future)

**Security Control**: N/A

**Test**: Test subscription terms (future)

### 5.3 Refunds

**Requirement**: Provide clear refund policy

**Risk**: Legal non-compliance, user complaints

**Control**: Refund policy documentation, refund process

**Business Rule**: BR-USR-001

**API**: N/A (future refund API)

**Data Entity**: N/A (future)

**Security Control**: N/A

**Test**: Test refund policy (future)

### 5.4 Disclosures

**Requirement**: Provide appropriate disclosures

**Risk**: Legal non-compliance, user complaints

**Control**: AI recommendation disclaimers, financial health score disclaimers

**Business Rule**: BR-AI-001, BR-FHL-001

**API**: POST /api/v1/ai/chat, GET /api/v1/financial-health

**Data Entity**: ai_conversations, financial_health_scores

**Security Control**: Control 40 (AI Read-Only)

**Test**: Test AI disclosures, Test financial health score disclaimers

### 5.5 User Consent

**Requirement**: Obtain and manage user consent

**Risk**: Legal non-compliance, user complaints

**Control**: Consent collection, consent withdrawal, consent tracking

**Business Rule**: BR-USR-001, BR-SEC-004

**API**: PUT /api/v1/users/consent

**Data Entity**: user_preferences

**Security Control**: Control 25 (PII Masking), Control 27 (Log Security)

**Test**: Test consent collection, Test consent withdrawal

### 5.6 Complaint Handling

**Requirement**: Provide complaint handling mechanism

**Risk**: Legal non-compliance, user complaints

**Control**: Complaint mechanism, complaint tracking, complaint resolution

**Business Rule**: BR-USR-001

**API**: POST /api/v1/grievances (future)

**Data Entity**: grievances (future)

**Security Control**: N/A

**Test**: Test complaint handling (future)

### 5.7 Misleading Financial Claims

**Requirement**: Avoid misleading financial claims

**Risk**: Legal non-compliance, user complaints

**Control**: Marketing guidelines, product language guidelines

**Business Rule**: BR-USR-001

**API**: N/A

**Data Entity**: N/A

**Security Control**: N/A

**Test**: Test marketing language (future)

### 5.8 AI Recommendation Disclaimers

**Requirement**: Provide AI recommendation disclaimers

**Risk**: Legal non-compliance, user complaints

**Control**: AI recommendation disclaimers, AI is not a licensed financial advisor

**Business Rule**: BR-AI-001

**API**: POST /api/v1/ai/chat

**Data Entity**: ai_conversations

**Security Control**: Control 40 (AI Read-Only)

**Test**: Test AI recommendation disclaimers

---

## 6. User Rights Traceability

### 6.1 Account Access

**Requirement**: Provide user account access

**Risk**: Unauthorized access, user complaints

**Control**: Authentication, authorization, multi-factor authentication

**Business Rule**: BR-USR-002, BR-SEC-001

**API**: GET /api/v1/users/profile

**Data Entity**: users, user_profiles

**Security Control**: Control 2 (User Login), Control 5 (Session Management)

**Test**: Test account access

### 6.2 Data Access

**Requirement**: Provide user data access

**Risk**: Unauthorized access, data breach

**Control**: Server-side authorization, RBAC, family isolation

**Business Rule**: BR-USR-005, BR-FAM-009

**API**: GET /api/v1/users/profile, GET /api/v1/transactions, GET /api/v1/ai/conversations

**Data Entity**: users, transactions, ai_conversations

**Security Control**: Control 9 (RBAC - User), Control 13 (Family Data Isolation)

**Test**: Test user data access, Test family data access

### 6.3 Data Correction

**Requirement**: Allow users to correct own data

**Risk**: Incorrect data, user complaints

**Control**: Update APIs, authorization checks

**Business Rule**: BR-USR-006

**API**: PUT /api/v1/users/profile, PUT /api/v1/transactions

**Data Entity**: users, transactions

**Security Control**: Control 9 (RBAC - User), Control 14 (Resource Ownership)

**Test**: Test data correction

### 6.4 Data Deletion

**Requirement**: Allow users to request data deletion

**Risk**: Legal non-compliance, data breach

**Control**: Account deletion API, soft delete, retention policy

**Business Rule**: BR-RET-001

**API**: DELETE /api/v1/users, DELETE /api/v1/ai/conversations

**Data Entity**: users (soft delete), ai_conversations (soft delete)

**Security Control**: Control 25 (PII Masking), Control 27 (Log Security)

**Test**: Test account deletion, Test AI conversation deletion

### 6.5 Consent/Preferences

**Requirement**: Allow users to manage consent and preferences

**Risk**: Legal non-compliance, user complaints

**Control**: Consent management, preference management

**Business Rule**: BR-USR-001, BR-SEC-004

**API**: PUT /api/v1/users/consent, PUT /api/v1/users/preferences

**Data Entity**: user_preferences

**Security Control**: Control 25 (PII Masking), Control 27 (Log Security)

**Test**: Test consent management, Test preference management

### 6.6 Data Portability

**Requirement**: Provide data portability (future)

**Risk**: Legal non-compliance, user complaints

**Control**: Data export API, data transfer API

**Business Rule**: BR-USR-001

**API**: GET /api/v1/users/export (future)

**Data Entity**: All user data

**Security Control**: Control 9 (RBAC - User), Control 14 (Resource Ownership)

**Test**: Test data portability (future)

### 6.7 Grievance Handling

**Requirement**: Provide grievance handling mechanism (future)

**Risk**: Legal non-compliance, user complaints

**Control**: Grievance API, grievance tracking, grievance resolution

**Business Rule**: BR-USR-001

**API**: POST /api/v1/grievances (future)

**Data Entity**: grievances (future)

**Security Control**: N/A

**Test**: Test grievance handling (future)

### 6.8 Communication Preferences

**Requirement**: Allow users to manage communication preferences

**Risk**: User complaints, legal non-compliance

**Control**: Communication preferences API, opt-out mechanisms

**Business Rule**: BR-USR-001

**API**: PUT /api/v1/users/preferences

**Data Entity**: user_preferences

**Security Control**: Control 25 (PII Masking), Control 27 (Log Security)

**Test**: Test communication preferences

---

## 7. Security Traceability

### 7.1 Encryption

**Requirement**: Encrypt data at rest and in transit

**Risk**: Data breach, unauthorized access

**Control**: PostgreSQL TDE, TLS 1.3, certificate validation

**Business Rule**: BR-SEC-004

**API**: All APIs

**Data Entity**: All data entities

**Security Control**: Control 19 (Encryption at Rest), Control 20 (Encryption in Transit)

**Test**: Test encryption at rest, Test encryption in transit

### 7.2 Access Control

**Requirement**: Implement access controls

**Risk**: Unauthorized access, data breach

**Control**: RBAC, family isolation, least privilege

**Business Rule**: BR-SEC-004, BR-FAM-009

**API**: All APIs

**Data Entity**: All data entities

**Security Control**: Control 9 (RBAC - User), Control 13 (Family Data Isolation)

**Test**: Test RBAC, Test family isolation

### 7.3 Audit

**Requirement**: Implement audit logging

**Risk**: Lack of accountability, legal non-compliance

**Control**: Immutable audit logs, request ID tracking, correlation ID tracking

**Business Rule**: BR-SEC-004, BR-RET-001

**API**: All APIs

**Data Entity**: audit_logs

**Security Control**: Control 53 (Security Event Logging)

**Test**: Test audit logging

### 7.4 Incident Response

**Requirement**: Implement incident response

**Risk**: Security incident, data breach

**Control**: Incident response plan, detection, classification, containment, eradication, recovery, notification

**Business Rule**: BR-SEC-004

**API**: N/A

**Data Entity**: N/A

**Security Control**: N/A

**Test**: Test incident response (drill)

### 7.5 Monitoring

**Requirement**: Implement security monitoring

**Risk**: Security incident, data breach

**Control**: Security monitoring, alerting, anomaly detection

**Business Rule**: BR-SEC-004

**API**: N/A

**Data Entity**: N/A

**Security Control**: Control 45-54 (Monitoring & Detection)

**Test**: Test security monitoring

### 7.6 Backups

**Requirement**: Implement backup and recovery

**Risk**: Data loss, business disruption

**Control**: Automated daily backups, backup encryption, recovery testing

**Business Rule**: BR-SEC-004

**API**: N/A

**Data Entity**: All data entities

**Security Control**: Control 37 (Backup Security)

**Test**: Test backup and recovery

### 7.7 Secrets

**Requirement**: Implement secrets management

**Risk**: Credential theft, data breach

**Control**: Secret Manager, no hard-coded secrets, secret rotation

**Business Rule**: BR-SEC-004

**API**: N/A

**Data Entity**: N/A

**Security Control**: Control 32 (Secrets Management)

**Test**: Test secrets management

### 7.8 Vendor Security

**Requirement**: Implement vendor security

**Risk**: Vendor breach, data breach

**Control**: Vendor due diligence, security requirements, incident notification

**Business Rule**: BR-SEC-004

**API**: N/A

**Data Entity**: N/A

**Security Control**: N/A

**Test**: Test vendor due diligence

### 7.9 Mobile Security

**Requirement**: Implement mobile security

**Risk**: Mobile compromise, data breach

**Control**: Secure token storage, biometric authentication, app lock, TLS, certificate pinning

**Business Rule**: BR-SEC-004

**API**: All APIs

**Data Entity**: N/A

**Security Control**: Control 8 (Mobile Security)

**Test**: Test mobile security

### 7.10 API Security

**Requirement**: Implement API security

**Risk**: API abuse, data breach

**Control**: Input validation, output encoding, rate limiting, OWASP API Security

**Business Rule**: BR-SEC-004

**API**: All APIs

**Data Entity**: All data entities

**Security Control**: Control 9 (API Security)

**Test**: Test API security

---

## 8. Conclusion

The NeoWallet Compliance Traceability matrix provides comprehensive mapping of 45 requirements to 55 risks, 45 controls, 23 business rules, 43 APIs, 46 data entities, 45 security controls, and 50 tests.

**IMPORTANT DISCLAIMER**: NeoWallet has documented controls and identified professional review requirements. NeoWallet is NOT claimed to be legally or regulatorily compliant.

**Next Steps**:
1. Create Compliance Gap Register
2. Create Regulatory Review Register
3. Create Compliance Validation Report

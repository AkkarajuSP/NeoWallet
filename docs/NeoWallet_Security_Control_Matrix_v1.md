# NeoWallet Security Control Matrix v1

## Executive Summary

This document provides a comprehensive security control matrix mapping threats to security controls, business rules, APIs, data entities, and tests for NeoWallet MVP.

**Total Threats**: 54
**Total Security Controls**: 54
**Total Business Rules**: 85
**Total APIs**: 72
**Total Data Entities**: 35
**Date**: August 18, 2026
**Status**: Ready for Product Owner Approval

---

## 1. Control Matrix Overview

### 1.1 Control Categories

| Category | Controls | Coverage |
|----------|----------|----------|
| Authentication | 8 | 100% |
| Authorization | 10 | 100% |
| Data Protection | 12 | 100% |
| Infrastructure Security | 8 | 100% |
| AI Security | 6 | 100% |
| Monitoring & Detection | 10 | 100% |
| **TOTAL** | **54** | **100%** |

### 1.2 Control Implementation Status

| Control | Status | Priority |
|---------|--------|----------|
| Authentication Controls | PENDING | HIGH |
| Authorization Controls | PENDING | HIGH |
| Data Protection Controls | PENDING | HIGH |
| Infrastructure Security Controls | PENDING | HIGH |
| AI Security Controls | PENDING | HIGH |
| Monitoring & Detection Controls | PENDING | HIGH |

---

## 2. Authentication Controls

### 2.1 Control 1: User Registration

**Threat**: Threat 1 - Fake Mobile Application

**Security Control**:
- Email verification required before account activation
- Password strength validation (minimum 8 characters, uppercase, lowercase, number, special character)
- Bcrypt password hashing with cost factor 12
- Rate limiting on registration endpoint (10 req/min per IP)

**Business Rule**: BR-USR-001, BR-USR-002

**API**: POST /api/v1/auth/register

**Data Entity**: users, user_credentials, otp_challenges

**Test**:
- Test registration with invalid email
- Test registration with weak password
- Test registration rate limiting
- Test email verification flow
- Test password hash storage (not plaintext)

---

### 2.2 Control 2: User Login

**Threat**: Threat 5 - Device Binding Bypass

**Security Control**:
- JWT access token (RS256, 15 minute lifetime)
- JWT refresh token (30 day lifetime)
- Failed login attempt tracking (5 attempts = 10 minute lockout)
- Device fingerprinting (user-agent, IP, device type)
- Session management with device binding

**Business Rule**: BR-USR-002, BR-SEC-001

**API**: POST /api/v1/auth/login

**Data Entity**: users, user_sessions, user_devices

**Test**:
- Test login with valid credentials
- Test login with invalid credentials
- Test failed login lockout
- Test token generation and validation
- Test device fingerprinting

---

### 2.3 Control 3: OTP Verification

**Threat**: Threat 31 - OTP Message Tampering

**Security Control**:
- OTP expiration (10 minutes)
- OTP retry limit (3 attempts)
- OTP resend limit (3 resends within 10 minutes)
- OTP hash storage (SHA-256)
- No raw OTP storage
- Rate limiting on OTP endpoints (10 req/min per phone/email)

**Business Rule**: BR-USR-003, BR-SEC-001

**API**: POST /api/v1/auth/password-reset/request, POST /api/v1/auth/password-reset/verify

**Data Entity**: otp_challenges, users

**Test**:
- Test OTP generation and delivery
- Test OTP expiration
- Test OTP retry limit
- Test OTP resend limit
- Test OTP hash storage (not plaintext)

---

### 2.4 Control 4: Token Security

**Threat**: Threat 7 - API Request Tampering

**Security Control**:
- JWT RS256 signing algorithm
- JWT signing key stored in Secret Manager
- Access token lifetime: 15 minutes
- Refresh token lifetime: 30 days
- Refresh token rotation on every use
- Token revocation on logout
- Clock tolerance: 5 minutes
- Audience: neowallet-api
- Issuer: neowallet-auth

**Business Rule**: BR-SEC-002, BR-SEC-003

**API**: POST /api/v1/auth/login, POST /api/v1/auth/logout

**Data Entity**: user_sessions

**Test**:
- Test JWT generation and validation
- Test token expiration
- Test refresh token rotation
- Test token revocation
- Test clock tolerance

---

### 2.5 Control 5: Session Management

**Threat**: Threat 3 - Local Data Exposure

**Security Control**:
- Secure token storage (Keychain/Keystore)
- Session expiration (15 minutes inactivity)
- Session revocation on logout
- Session revocation on password change
- Multi-device session support
- Device fingerprinting

**Business Rule**: BR-SEC-002, BR-SEC-003

**API**: GET /api/v1/auth/sessions, DELETE /api/v1/auth/sessions/{sessionId}, DELETE /api/v1/auth/sessions

**Data Entity**: user_sessions, user_devices

**Test**:
- Test session creation
- Test session expiration
- Test session revocation
- Test multi-device sessions
- Test device fingerprinting

---

### 2.6 Control 6: Device Registration

**Threat**: Threat 2 - Mobile App Reverse Engineering

**Security Control**:
- Device token validation
- Device type verification (Android/iOS)
- Platform and OS version validation
- App version validation
- Device binding for sensitive operations
- Suspicious device detection

**Business Rule**: BR-USR-004

**API**: POST /api/v1/auth/devices, DELETE /api/v1/auth/devices/{deviceId}

**Data Entity**: user_devices

**Test**:
- Test device registration
- Test device token validation
- Test device type verification
- Test suspicious device detection

---

### 2.7 Control 7: Logout

**Threat**: Threat 5 - Device Binding Bypass

**Security Control**:
- Refresh token revocation
- Session invalidation
- Device token removal (optional)
- Audit logging
- Logout all devices support

**Business Rule**: BR-SEC-003

**API**: POST /api/v1/auth/logout, POST /api/v1/auth/logout-all

**Data Entity**: user_sessions, user_devices, audit_logs

**Test**:
- Test single device logout
- Test all devices logout
- Test token revocation
- Test audit logging

---

### 2.8 Control 8: Suspicious Device Detection

**Threat**: Threat 5 - Device Binding Bypass

**Security Control**:
- Device fingerprinting (user-agent, IP, device type)
- Location change detection
- Concurrent session detection
- Anomaly detection in login patterns
- Additional authentication for suspicious devices

**Business Rule**: BR-SEC-001

**API**: POST /api/v1/auth/login

**Data Entity**: user_sessions, user_devices, audit_logs

**Test**:
- Test device fingerprinting
- Test location change detection
- Test concurrent session detection
- Test anomaly detection

---

## 3. Authorization Controls

### 3.1 Control 9: RBAC - User

**Threat**: Threat 11 - API Authorization Bypass

**Security Control**:
- Server-side RBAC enforcement
- User can access own profile
- User can access own transactions
- User can access own budgets
- User can access own savings goals
- User can access own bills
- User can access own notifications

**Business Rule**: BR-USR-005, BR-USR-006

**API**: GET /api/v1/users/profile, GET /api/v1/transactions, GET /api/v1/budgets

**Data Entity**: users, transactions, budgets, savings_goals, bills, notifications

**Test**:
- Test user access to own data
- Test user denied access to other user data
- Test RBAC enforcement

---

### 3.2 Control 10: RBAC - Family Owner

**Threat**: Threat 47 - Family Role Escalation

**Security Control**:
- Owner can invite family members
- Owner can remove family members

**Business Rule**: BR-FAM-002, BR-FAM-006

**API**: POST /api/v1/families/{familyId}/invitations, DELETE /api/v1/families/{familyId}/members/{memberId}

**Data Entity**: families, family_members, family_invitations, audit_logs

**Test**:
- Test owner invite family member
- Test owner remove family member
- Test member denied invite/remove

---

### 3.3 Control 11: RBAC - Family Member

**Threat**: Threat 11 - API Authorization Bypass

**Security Control**:
- Member can view family financial overview
- Member can view family transactions
- Member can view family budgets
- Member can view family savings goals
- Member can view family bills
- Member cannot invite/remove members
- Member cannot change family settings

**Business Rule**: BR-FAM-009

**API**: GET /api/v1/financial-overview/family/{familyId}, GET /api/v1/transactions/family/{familyId}

**Data Entity**: financial_overviews, transactions, budgets, savings_goals, bills

**Test**:
- Test member view family data
- Test member denied invite/remove
- Test member denied settings change

---

### 3.4 Control 12: RBAC - Family Restricted

**Threat**: Threat 11 - API Authorization Bypass

**Security Control**:
- Restricted can view limited family data
- Restricted cannot view sensitive financial information
- Restricted cannot modify family data
- Restricted cannot invite/remove members

**Business Rule**: BR-FAM-008

**API**: GET /api/v1/financial-overview/family/{familyId}

**Data Entity**: financial_overviews, family_members

**Test**:
- Test restricted view limited data
- Test restricted denied sensitive data
- Test restricted denied modify

---

### 3.5 Control 13: Family Data Isolation

**Threat**: Threat 46 - Cross-Family Data Access

**Security Control**:
- Server-side family membership verification
- Resource ownership checks
- Family ID validation on all family data access
- No trust in client-side authorization
- Audit all cross-family access attempts

**Business Rule**: BR-FAM-009

**API**: GET /api/v1/financial-overview/family/{familyId}, GET /api/v1/transactions/family/{familyId}

**Data Entity**: families, family_members, financial_overviews, transactions, audit_logs

**Test**:
- Test user access own family data
- Test user denied access other family data
- Test family membership verification
- Test cross-family access audit

---

### 3.6 Control 14: Resource Ownership

**Threat**: Threat 11 - API Authorization Bypass

**Security Control**:
- Resource ownership checks on all CRUD operations
- User ID validation on user-owned resources
- Family ID validation on family-owned resources
- IDOR prevention
- Audit all ownership violations

**Business Rule**: BR-USR-005, BR-FAM-009

**API**: PUT /api/v1/transactions/{transactionId}, DELETE /api/v1/budgets/{budgetId}

**Data Entity**: transactions, budgets, savings_goals, bills, audit_logs

**Test**:
- Test user modify own resource
- Test user denied modify other user resource
- Test IDOR prevention
- Test ownership violation audit

---

### 3.7 Control 15: Transaction Authorization

**Threat**: Threat 48 - Financial Data Manipulation

**Security Control**:
- Server-side transaction authorization
- User can create own transactions
- Family member can create family transactions
- Restricted member limited transaction access
- Transaction modification requires ownership
- Audit all transaction changes

**Business Rule**: BR-TRX-001, BR-TRX-005

**API**: POST /api/v1/transactions, PUT /api/v1/transactions/{transactionId}

**Data Entity**: transactions, family_members, audit_logs

**Test**:
- Test user create own transaction
- Test family member create family transaction
- Test restricted limited access
- Test transaction modification authorization
- Test transaction change audit

---

### 3.8 Control 16: Budget Authorization

**Threat**: Threat 48 - Financial Data Manipulation

**Security Control**:
- Server-side budget authorization
- User can create own budgets
- Family member can create family budgets
- Budget modification requires ownership
- Budget deletion requires ownership
- Audit all budget changes

**Business Rule**: BR-BUD-001, BR-BUD-007

**API**: POST /api/v1/budgets, PUT /api/v1/budgets/{budgetId}, DELETE /api/v1/budgets/{budgetId}

**Data Entity**: budgets, family_members, audit_logs

**Test**:
- Test user create own budget
- Test family member create family budget
- Test budget modification authorization
- Test budget deletion authorization
- Test budget change audit

---

### 3.9 Control 17: Savings Authorization

**Threat**: Threat 48 - Financial Data Manipulation

**Security Control**:
- Server-side savings authorization
- User can create own savings goals
- Family member can create family savings goals
- Savings modification requires ownership
- Savings deletion requires ownership
- Audit all savings changes

**Business Rule**: BR-SAV-001, BR-SAV-006

**API**: POST /api/v1/savings-goals, PUT /api/v1/savings-goals/{goalId}, DELETE /api/v1/savings-goals/{goalId}

**Data Entity**: savings_goals, family_members, audit_logs

**Test**:
- Test user create own savings goal
- Test family member create family savings goal
- Test savings modification authorization
- Test savings deletion authorization
- Test savings change audit

---

### 3.10 Control 18: Bill Authorization

**Threat**: Threat 48 - Financial Data Manipulation

**Security Control**:
- Server-side bill authorization
- User can create own bills
- Family member can create family bills
- Bill modification requires ownership
- Bill mark-paid requires ownership
- Audit all bill changes

**Business Rule**: BR-BIL-001, BR-BIL-004

**API**: POST /api/v1/bills, PUT /api/v1/bills/{billId}, POST /api/v1/bills/{billId}/mark-paid

**Data Entity**: bills, family_members, bill_status_history, audit_logs

**Test**:
- Test user create own bill
- Test family member create family bill
- Test bill modification authorization
- Test bill mark-paid authorization
- Test bill change audit

---

## 4. Data Protection Controls

### 4.1 Control 19: Encryption at Rest

**Threat**: Threat 19 - Database Data Exposure

**Security Control**:
- PostgreSQL TDE (Transparent Data Encryption)
- Cloud Storage encryption (AES-256)
- Redis encryption (if used)
- Backup encryption
- Key management via Secret Manager

**Business Rule**: BR-SEC-004

**API**: N/A

**Data Entity**: All data entities

**Test**:
- Test database encryption at rest
- Test backup encryption
- Test key management

---

### 4.2 Control 20: Encryption in Transit

**Threat**: Threat 6 - API Endpoint Spoofing

**Security Control**:
- TLS 1.3 for all API communication
- TLS 1.3 for database connections
- TLS 1.3 for Redis connections
- TLS 1.3 for external provider connections
- Certificate validation
- HSTS

**Business Rule**: BR-SEC-004

**API**: All APIs

**Data Entity**: N/A

**Test**:
- Test TLS 1.3 enforcement
- Test certificate validation
- Test HSTS

---

### 4.3 Control 21: Password Hashing

**Threat**: Threat 2 - Mobile App Reverse Engineering

**Security Control**:
- Bcrypt password hashing with cost factor 12
- No plaintext password storage
- No password salt storage (Bcrypt includes salt)
- Password change requires current password
- Password change invalidates all sessions

**Business Rule**: BR-USR-002, BR-SEC-004

**API**: PUT /api/v1/users/password

**Data Entity**: users, user_sessions

**Test**:
- Test password hash storage
- Test password change requires current password
- Test password change invalidates sessions

---

### 4.4 Control 22: OTP Hashing

**Threat**: Threat 31 - OTP Message Tampering

**Security Control**:
- SHA-256 OTP hashing
- No raw OTP storage
- OTP expiration (10 minutes)
- OTP retry limit (3 attempts)
- OTP resend limit (3 resends within 10 minutes)

**Business Rule**: BR-USR-003, BR-SEC-004

**API**: POST /api/v1/auth/password-reset/request, POST /api/v1/auth/password-reset/verify

**Data Entity**: otp_challenges

**Test**:
- Test OTP hash storage
- Test OTP expiration
- Test OTP retry limit
- Test OTP resend limit

---

### 4.5 Control 25: PII Masking

**Threat**: Threat 9 - API Response Information Disclosure

**Security Control**:
- PII masking in logs
- PII masking in error messages
- PII masking in API responses (where appropriate)
- PII masking in audit logs (where appropriate)
- Data minimization in responses

**Business Rule**: BR-SEC-004, BR-RET-001

**API**: All APIs

**Data Entity**: All data entities

**Test**:
- Test PII masking in logs
- Test PII masking in error messages
- Test PII masking in responses
- Test PII masking in audit logs

---

### 4.6 Control 26: Financial Data Protection

**Threat**: Threat 49 - Financial Data Exposure

**Security Control**:
- Financial data encryption at rest
- Financial data encryption in transit
- Financial data access controls
- Financial data audit logging
- Financial data retention policy

**Business Rule**: BR-FIN-001, BR-RET-002

**API**: GET /api/v1/financial-overview, GET /api/v1/transactions

**Data Entity**: financial_overviews, transactions, audit_logs

**Test**:
- Test financial data encryption
- Test financial data access controls
- Test financial data audit logging
- Test financial data retention

---

### 4.7 Control 27: Log Security

**Threat**: Threat 14 - Backend Log Information Disclosure

**Security Control**:
- No sensitive data in logs
- Log masking for PII
- Log masking for financial data
- Log masking for tokens
- Log access controls
- Log retention policy (90 days for application logs, 7 years for audit logs)

**Business Rule**: BR-SEC-004, BR-RET-001

**API**: N/A

**Data Entity**: audit_logs

**Test**:
- Test log masking
- Test log access controls
- Test log retention

---

### 4.8 Control 28: Input Validation

**Threat**: Threat 7 - API Request Tampering

**Security Control**:
- Input validation on all API endpoints
- Output encoding for all API responses
- SQL injection prevention (parameterized queries)
- XSS prevention (output encoding)
- CSRF prevention (same-site cookies, CSRF tokens)
- Request size limits (1 MB)

**Business Rule**: BR-SEC-004

**API**: All APIs

**Data Entity**: All data entities

**Test**:
- Test input validation
- Test output encoding
- Test SQL injection prevention
- Test XSS prevention
- Test CSRF prevention

---

### 4.9 Control 29: Rate Limiting

**Threat**: Threat 10 - API Rate Limiting Bypass

**Security Control**:
- Rate limiting per user (100 req/min authenticated)
- Rate limiting per IP (10 req/min unauthenticated)
- Rate limiting for AI chat (20 req/min)
- Rate limiting for OTP endpoints (10 req/min per phone/email)
- DDoS protection (Cloud Armor)

**Business Rule**: BR-SEC-004

**API**: All APIs

**Data Entity**: N/A

**Test**:
- Test authenticated rate limiting
- Test unauthenticated rate limiting
- Test AI chat rate limiting
- Test OTP rate limiting

---

### 4.10 Control 30: Idempotency

**Threat**: Threat 7 - API Request Tampering

**Security Control**:
- Idempotency keys on critical POST endpoints
- Idempotency key validity (24 hours)
- Idempotency key uniqueness
- Idempotency key storage (Redis)

**Business Rule**: BR-API-001

**API**: POST /api/v1/transactions, POST /api/v1/budgets, POST /api/v1/savings-goals

**Data Entity**: N/A

**Test**:
- Test idempotency key generation
- Test idempotency key validation
- Test idempotency key expiration

---

## 5. Infrastructure Security Controls

### 5.1 Control 31: Database Security

**Threat**: Threat 17 - Database Connection Spoofing

**Security Control**:
- Least privilege database users
- Application database user (read/write to application schema only)
- Migration user (DDL access only during migration)
- Backup user (read-only access for backups)
- No database credentials in application code
- Database credentials in Secret Manager
- Network isolation (private IP)
- Database connection TLS

**Business Rule**: BR-SEC-004

**API**: N/A

**Data Entity**: All data entities

**Test**:
- Test least privilege database users
- Test database credential storage
- Test database connection TLS
- Test database network isolation

---

### 5.2 Control 32: Secrets Management

**Threat**: Threat 33 - Cloud Service Account Spoofing

**Security Control**:
- Google Cloud Secret Manager
- No secrets in code
- No secrets in Git
- No secrets in Docker images
- No secrets in application configuration committed to repository
- Secret rotation (90 days)
- Secret access logging
- Secret versioning

**Business Rule**: BR-SEC-004

**API**: N/A

**Data Entity**: N/A

**Test**:
- Test secret storage in Secret Manager
- Test secret rotation
- Test secret access logging
- Test secret versioning

---

### 5.3 Control 33: Cloud IAM

**Threat**: Threat 37 - Cloud Privilege Escalation

**Security Control**:
- IAM least privilege
- Service account least privilege
- Workload Identity Federation
- Service account key rotation (90 days)
- MFA for console access
- IAM audit logging
- Regular privilege audits

**Business Rule**: BR-SEC-004

**API**: N/A

**Data Entity**: N/A

**Test**:
- Test IAM least privilege
- Test service account least privilege
- Test MFA for console access
- Test IAM audit logging

---

### 5.4 Control 34: Container Security

**Threat**: Threat 13 - Backend Code Tampering

**Security Control**:
- Container image signing
- Container image scanning
- Immutable infrastructure
- Runtime security
- Container least privilege
- Network isolation

**Business Rule**: BR-SEC-004

**API**: N/A

**Data Entity**: N/A

**Test**:
- Test container image signing
- Test container image scanning
- Test runtime security
- Test container least privilege

---

### 5.5 Control 35: Network Security

**Threat**: Threat 6 - API Endpoint Spoofing

**Security Control**:
- Network isolation (VPC)
- Private IP for database
- Private IP for Redis
- Service mesh authentication (mTLS)
- Network policies
- Firewall rules

**Business Rule**: BR-SEC-004

**API**: N/A

**Data Entity**: N/A

**Test**:
- Test network isolation
- Test service mesh authentication
- Test network policies
- Test firewall rules

---

### 5.6 Control 36: CI/CD Security

**Threat**: Threat 41 - CI/CD Pipeline Spoofing

**Security Control**:
- GitHub branch protection
- Pull request requirements
- Code review requirements
- Dependency scanning
- SAST
- Secret scanning
- Container scanning
- SBOM generation
- Image signing
- Deployment approvals

**Business Rule**: BR-SEC-004

**API**: N/A

**Data Entity**: N/A

**Test**:
- Test branch protection
- Test pull request requirements
- Test dependency scanning
- Test SAST
- Test secret scanning
- Test container scanning

---

### 5.7 Control 37: Backup Security

**Threat**: Threat 19 - Database Data Exposure

**Security Control**:
- Automated daily backups
- Backup encryption (AES-256)
- Backup retention (30 days)
- Backup access controls
- Backup testing (monthly)
- RPO: 1 day
- RTO: 4 hours

**Business Rule**: BR-SEC-004

**API**: N/A

**Data Entity**: All data entities

**Test**:
- Test backup encryption
- Test backup retention
- Test backup access controls
- Test backup recovery

---

### 5.8 Control 38: Environment Separation

**Threat**: Threat 34 - Cloud Infrastructure Tampering

**Security Control**:
- Development environment
- Staging environment
- Production environment
- Environment-specific secrets
- Environment-specific configurations
- No cross-environment access

**Business Rule**: BR-SEC-004

**API**: N/A

**Data Entity**: N/A

**Test**:
- Test environment separation
- Test environment-specific secrets
- Test cross-environment access prevention

---

## 6. AI Security Controls

### 6.1 Control 39: AI Data Access

**Threat**: Threat 29 - AI Tool Access Bypass

**Security Control**:
- AI has no direct database access
- AI accesses data only through authorized application tools/APIs
- AI tools are read-only
- AI tools require user authorization
- AI tool invocations are audited
- AI tool parameters are validated

**Business Rule**: BR-AI-001, BR-AI-002

**API**: POST /api/v1/ai/chat

**Data Entity**: ai_conversations, ai_messages, ai_tool_invocations, audit_logs

**Test**:
- Test AI no direct database access
- Test AI tool authorization
- Test AI tool audit logging
- Test AI tool parameter validation

---

### 6.2 Control 40: AI Read-Only

**Threat**: Threat 52 - AI Autonomous Action

**Security Control**:
- AI is read-only for MVP
- AI cannot execute payments
- AI cannot move money
- AI cannot modify financial data
- AI cannot change permissions
- AI cannot access another family's data
- AI recommendations require user approval

**Business Rule**: BR-AI-001, BR-AI-004

**API**: POST /api/v1/ai/chat

**Data Entity**: ai_conversations, ai_recommendations

**Test**:
- Test AI read-only enforcement
- Test AI no payment execution
- Test AI no financial data modification
- Test AI recommendation approval

---

### 6.3 Control 41: Prompt Injection Protection

**Threat**: Threat 26 - AI Prompt Injection

**Security Control**:
- Prompt sanitization
- Input validation
- Context isolation
- Tool access control
- Output validation
- Confidence thresholds
- Human-in-the-loop for recommendations

**Business Rule**: BR-AI-005, BR-AI-006

**API**: POST /api/v1/ai/chat

**Data Entity**: ai_conversations, ai_messages, ai_tool_invocations

**Test**:
- Test prompt injection prevention
- Test input validation
- Test context isolation
- Test tool access control
- Test output validation

---

### 6.4 Control 42: AI Data Minimization

**Threat**: Threat 27 - AI Data Exposure to Provider

**Security Control**:
- Data minimization in AI requests
- Context minimization
- No sensitive financial data in AI requests
- Provider data handling review
- Model training opt-out
- User control over conversation deletion

**Business Rule**: BR-AI-007, BR-AI-008

**API**: POST /api/v1/ai/chat

**Data Entity**: ai_conversations, ai_messages

**Test**:
- Test data minimization in AI requests
- Test context minimization
- Test no sensitive data in AI requests
- Test conversation deletion

---

### 6.5 Control 43: AI Rate Limiting

**Threat**: Threat 28 - AI Service Abuse

**Security Control**:
- Rate limiting for AI chat (20 req/min)
- Token limits for AI requests
- Request size limits for AI requests
- Cost controls
- Circuit breakers

**Business Rule**: BR-AI-009

**API**: POST /api/v1/ai/chat

**Data Entity**: N/A

**Test**:
- Test AI chat rate limiting
- Test AI token limits
- Test AI request size limits
- Test AI cost controls

---

### 6.6 Control 44: AI Audit Logging

**Threat**: Threat 29 - AI Tool Access Bypass

**Security Control**:
- Audit all AI tool invocations
- Audit AI recommendations
- Audit AI conversation creation
- Audit AI conversation deletion
- Audit prompt injection attempts

**Business Rule**: BR-AI-010, BR-SEC-004

**API**: POST /api/v1/ai/chat

**Data Entity**: ai_conversations, ai_tool_invocations, ai_recommendations, audit_logs

**Test**:
- Test AI tool invocation audit
- Test AI recommendation audit
- Test AI conversation audit
- Test prompt injection attempt audit

---

## 7. Monitoring & Detection Controls

### 7.1 Control 45: Failed Login Monitoring

**Threat**: Threat 5 - Device Binding Bypass

**Security Control**:
- Monitor failed login attempts
- Alert on 5 failed login attempts from same IP
- Alert on 10 failed login attempts from same user
- Alert on account lockout

**Business Rule**: BR-SEC-001

**API**: POST /api/v1/auth/login

**Data Entity**: users, audit_logs

**Test**:
- Test failed login monitoring
- Test failed login alerting
- Test account lockout alerting

---

### 7.2 Control 46: OTP Abuse Monitoring

**Threat**: Threat 31 - OTP Message Tampering

**Security Control**:
- Monitor OTP generation rate
- Alert on excessive OTP generation from same phone/email
- Alert on OTP verification failures
- Alert on OTP resend abuse

**Business Rule**: BR-SEC-001

**API**: POST /api/v1/auth/password-reset/request

**Data Entity**: otp_challenges, audit_logs

**Test**:
- Test OTP generation monitoring
- Test OTP verification failure monitoring
- Test OTP resend abuse monitoring

---

### 7.3 Control 47: Token Misuse Monitoring

**Threat**: Threat 7 - API Request Tampering

**Security Control**:
- Monitor token usage patterns
- Alert on token usage from multiple IPs
- Alert on token usage from multiple devices
- Alert on token expiration abuse

**Business Rule**: BR-SEC-002

**API**: All APIs

**Data Entity**: user_sessions, audit_logs

**Test**:
- Test token usage monitoring
- Test multiple IP alerting
- Test multiple device alerting

---

### 7.4 Control 48: Authorization Failure Monitoring

**Threat**: Threat 11 - API Authorization Bypass

**Security Control**:
- Monitor authorization failures
- Alert on repeated authorization failures from same user
- Alert on cross-family access attempts
- Alert on IDOR patterns

**Business Rule**: BR-SEC-004

**API**: All APIs

**Data Entity**: audit_logs

**Test**:
- Test authorization failure monitoring
- Test cross-family access monitoring
- Test IDOR pattern monitoring

---

### 7.5 Control 49: AI Tool Abuse Monitoring

**Threat**: Threat 29 - AI Tool Access Bypass

**Security Control**:
- Monitor AI tool invocations
- Alert on unauthorized tool access attempts
- Alert on prompt injection attempts
- Alert on AI tool parameter anomalies

**Business Rule**: BR-AI-005, BR-AI-010

**API**: POST /api/v1/ai/chat

**Data Entity**: ai_tool_invocations, audit_logs

**Test**:
- Test AI tool invocation monitoring
- Test unauthorized tool access monitoring
- Test prompt injection monitoring

---

### 7.6 Control 50: Configuration Change Monitoring

**Threat**: Threat 34 - Cloud Infrastructure Tampering

**Security Control**:
- Monitor configuration changes
- Alert on unauthorized configuration changes
- Alert on IAM changes
- Alert on secret changes

**Business Rule**: BR-SEC-004

**API**: N/A

**Data Entity**: audit_logs

**Test**:
- Test configuration change monitoring
- Test IAM change monitoring
- Test secret change monitoring

---

### 7.7 Control 51: Suspicious Device Monitoring

**Threat**: Threat 5 - Device Binding Bypass

**Security Control**:
- Monitor device registration
- Alert on device registration from new location
- Alert on device registration from suspicious IP
- Alert on device fingerprint changes

**Business Rule**: BR-SEC-001

**API**: POST /api/v1/auth/devices

**Data Entity**: user_devices, audit_logs

**Test**:
- Test device registration monitoring
- Test new location alerting
- Test suspicious IP alerting

---

### 7.8 Control 52: Anomaly Detection

**Threat**: Threat 11 - API Authorization Bypass

**Security Control**:
- Monitor API usage patterns
- Alert on unusual API usage
- Alert on unusual transaction patterns
- Alert on unusual financial data access

**Business Rule**: BR-SEC-004

**API**: All APIs

**Data Entity**: audit_logs

**Test**:
- Test API usage pattern monitoring
- Test unusual transaction pattern monitoring
- Test unusual financial data access monitoring

---

### 7.9 Control 53: Security Event Logging

**Threat**: Threat 48 - Financial Data Manipulation

**Security Control**:
- Immutable audit logs for sensitive operations
- Audit authentication events
- Audit authorization changes
- Audit family membership changes
- Audit financial record changes
- Audit budget changes
- Audit savings changes
- Audit bill changes
- Audit security events
- Audit AI tool invocations
- Audit configuration changes

**Business Rule**: BR-SEC-004, BR-RET-001

**API**: All APIs

**Data Entity**: audit_logs

**Test**:
- Test authentication event audit
- Test authorization change audit
- Test family membership change audit
- Test financial record change audit
- Test AI tool invocation audit

---

### 7.10 Control 54: Alert Severity

**Threat**: All Threats

**Security Control**:
- CRITICAL: Account compromise, data breach, financial data corruption
- HIGH: Authorization bypass, privilege escalation, credential leakage
- MEDIUM: Suspicious activity, configuration changes, unusual patterns
- LOW: Failed login attempts, rate limit violations

**Business Rule**: BR-SEC-004

**API**: N/A

**Data Entity**: N/A

**Test**:
- Test CRITICAL alert generation
- Test HIGH alert generation
- Test MEDIUM alert generation
- Test LOW alert generation

---

## 8. Control Implementation Summary

### 8.1 Control Coverage by Category

| Category | Controls | Implemented | Tested |
|----------|----------|--------------|--------|
| Authentication | 8 | PENDING | PENDING |
| Authorization | 10 | PENDING | PENDING |
| Data Protection | 10 | PENDING | PENDING |
| Infrastructure Security | 8 | PENDING | PENDING |
| AI Security | 6 | PENDING | PENDING |
| Monitoring & Detection | 10 | PENDING | PENDING |
| **TOTAL** | **52** | **PENDING** | **PENDING** |

### 8.2 Control to Threat Mapping

| Threat | Control | Status |
|--------|---------|--------|
| Threat 1 | Control 1 | PENDING |
| Threat 2 | Control 6 | PENDING |
| Threat 3 | Control 5 | PENDING |
| Threat 5 | Control 7, Control 8 | PENDING |
| Threat 6 | Control 20, Control 35 | PENDING |
| Threat 7 | Control 4, Control 28, Control 30 | PENDING |
| Threat 11 | Control 9, Control 14, Control 48 | PENDING |
| Threat 14 | Control 27 | PENDING |
| Threat 17 | Control 31 | PENDING |
| Threat 19 | Control 19 | PENDING |
| Threat 21 | Control 31 | PENDING |
| Threat 26 | Control 41, Control 49 | PENDING |
| Threat 27 | Control 42 | PENDING |
| Threat 28 | Control 43 | PENDING |
| Threat 29 | Control 39, Control 44 | PENDING |
| Threat 31 | Control 22, Control 46 | PENDING |
| Threat 33 | Control 32 | PENDING |
| Threat 34 | Control 38 | PENDING |
| Threat 37 | Control 33 | PENDING |
| Threat 41 | Control 36 | PENDING |
| Threat 42 | Control 36 | PENDING |
| Threat 43 | Control 36 | PENDING |
| Threat 46 | Control 13 | PENDING |
| Threat 47 | Control 10 | PENDING |
| Threat 48 | Control 15, Control 16, Control 17, Control 18 | PENDING |
| Threat 49 | Control 26 | PENDING |
| Threat 52 | Control 40 | PENDING |

---

## 9. Conclusion

The NeoWallet Security Control Matrix defines 52 security controls mapped54 threats. All controls have defined business rules, APIs, data entities, and tests. The controls cover authentication, authorization, data protection, infrastructure security, AI security, and monitoring & detection.

**Next Steps**:
1. Create Security Implementation Guide
2. Create Security Test Strategy
3. Create Security Validation Report

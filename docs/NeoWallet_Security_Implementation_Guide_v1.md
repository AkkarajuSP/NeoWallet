# NeoWallet Security Implementation Guide v1

## Executive Summary

This document provides a production-grade security implementation guide for NeoWallet MVP, covering all security aspects from authentication to incident response.

**Date**: August 18, 2026
**Status**: Ready for Product Owner Approval
**Target Platform**: Google Cloud Run
**Backend**: Spring Boot
**Mobile**: Flutter
**Database**: PostgreSQL

---

## 1. Security Objectives

### 1.1 Confidentiality

**Objective**: Protect sensitive data from unauthorized access

**Scope**:
- User PII (email, phone, name, address)
- Financial data (transactions, budgets, savings, bills)
- Authentication credentials (passwords, tokens)
- AI conversation data

**Controls**:
- Encryption at rest (PostgreSQL TDE, Cloud Storage AES-256)
- Encryption in transit (TLS 1.3)
- Access controls (RBAC, family isolation)
- Data minimization in responses
- PII masking in logs

### 1.2 Integrity

**Objective**: Ensure data accuracy and prevent unauthorized modifications

**Scope**:
- Financial records
- Budget allocations
- Savings goals
- Bill status
- Audit logs

**Controls**:
- Immutable audit logs
- Server-side validation
- Authorization checks
- Data integrity checks
- No direct database access for AI

### 1.3 Availability

**Objective**: Ensure system availability for authorized users

**Scope**:
- API endpoints
- Mobile application
- Database
- AI service

**Controls**:
- Rate limiting (100 req/min authenticated, 10 req/min unauthenticated)
- DDoS protection (Cloud Armor)
- Auto-scaling
- Circuit breakers
- Backup and recovery

### 1.4 Authentication

**Objective**: Verify user identity before granting access

**Scope**:
- User registration
- User login
- OTP verification
- Device registration

**Controls**:
- Email verification
- Password strength validation
- Bcrypt password hashing
- JWT tokens (RS256)
- OTP with expiration and retry limits
- Device fingerprinting

### 1.5 Authorization

**Objective**: Grant appropriate permissions based on user role

**Scope**:
- User data access
- Family data access
- Resource operations (CRUD)
- AI tool access

**Controls**:
- Server-side RBAC
- Family membership verification
- Resource ownership checks
- Role-based permissions (OWNER, MEMBER, RESTRICTED)
- No trust in client-side authorization

### 1.6 Privacy

**Objective**: Protect user privacy and comply with data protection requirements

**Scope**:
- PII collection and storage
- Data retention
- Data deletion
- Data sharing

**Controls**:
- PII minimization
- Data retention policy
- User-requested deletion
- Audit retention (7 years)
- Provider data handling review

### 1.7 Auditability

**Objective**: Record all sensitive operations for accountability

**Scope**:
- Authentication events
- Authorization changes
- Family membership changes
- Financial record changes
- Budget changes
- Savings changes
- Bill changes
- Security events
- AI tool invocations
- Configuration changes

**Controls**:
- Immutable audit logs
- Request ID tracking
- Correlation ID tracking
- Device fingerprinting
- Timestamps

### 1.8 Non-Repudiation

**Objective**: Prevent users from denying their actions

**Scope**:
- Financial transactions
- Budget modifications
- Savings contributions
- Bill payments
- AI recommendations

**Controls**:
- Immutable audit logs
- Request ID tracking
- Device fingerprinting
- Timestamps
- User ID tracking

---

## 2. Threat Model

**Reference**: NeoWallet_Threat_Model_v1.md

**Summary**: 54 threats identified using STRIDE methodology across all system components. All threats have defined mitigations, detection methods, and response procedures.

**Top 10 High-Risk Threats**:
1. API Authorization Bypass
2. Backend Privilege Escalation
3. Database Privilege Escalation
4. AI Tool Access Bypass
5. Cloud Service Account Spoofing
6. Cloud Privilege Escalation
7. CI/CD Pipeline Spoofing
8. CI/CD Artifact Tampering
9. CI/CD Secret Exposure
10. CI/CD Privilege Escalation

---

## 3. Authentication

### 3.1 Registration

**Flow**:
1. User submits email, password, first name, last name
2. Server validates email format and password strength
3. Server hashes password using Bcrypt (cost factor 12)
4. Server creates user account with status PENDING
5. Server generates OTP and sends to email
6. User verifies OTP
7. Server activates account

**Password Strength Requirements**:
- Minimum 8 characters
- At least 1 uppercase letter
- At least 1 lowercase letter
- At least 1 number
- At least 1 special character

**Configurable Values**:
```yaml
PASSWORD_MIN_LENGTH: 8
PASSWORD_REQUIRE_UPPERCASE: true
PASSWORD_REQUIRE_LOWERCASE: true
PASSWORD_REQUIRE_NUMBER: true
PASSWORD_REQUIRE_SPECIAL: true
```

### 3.2 Login

**Flow**:
1. User submits email and password
2. Server validates credentials
3. Server increments failed login attempt counter on failure
4. Server locks account for 10 minutes after 5 failed attempts
5. Server generates JWT access token (RS256, 15 minute lifetime)
6. Server generates JWT refresh token (30 day lifetime)
7. Server creates session record
8. Server returns tokens to client

**Configurable Values**:
```yaml
FAILED_LOGIN_ATTEMPT_LIMIT: 5
FAILED_LOGIN_LOCKOUT_MINUTES: 10
ACCESS_TOKEN_EXPIRY_MINUTES: 15
REFRESH_TOKEN_EXPIRY_DAYS: 30
```

### 3.3 OTP

**OTP Generation**:
- 6-digit numeric OTP
- SHA-256 hash storage
- 10 minute expiration
- 3 retry attempts allowed
- 3 resends allowed within 10 minutes

**OTP Delivery**:
- Email via SMTP (TLS)
- SMS via provider (future)
- WhatsApp via provider (future)

**Configurable Values**:
```yaml
OTP_LENGTH: 6
OTP_EXPIRY_MINUTES: 10
OTP_RETRY_LIMIT: 3
OTP_RESEND_LIMIT: 3
OTP_RESEND_WINDOW_MINUTES: 10
```

**Security Requirements**:
- Never store raw OTP values
- Always store OTP hash (SHA-256)
- Rate limit OTP endpoints (10 req/min per phone/email)

### 3.4 Access Token

**JWT Structure**:
```json
{
  "header": {
    "alg": "RS256",
    "typ": "JWT"
  },
  "payload": {
    "sub": "user_id",
    "aud": "neowallet-api",
    "iss": "neowallet-auth",
    "exp": 1234567890,
    "iat": 1234567890,
    "jti": "token_id"
  }
}
```

**Configurable Values**:
```yaml
JWT_SIGNING_ALGORITHM: RS256
JWT_ACCESS_TOKEN_EXPIRY_MINUTES: 15
JWT_REFRESH_TOKEN_EXPIRY_DAYS: 30
JWT_CLOCK_TOLERANCE_SECONDS: 300
JWT_AUDIENCE: neowallet-api
JWT_ISSUER: neowallet-auth
```

**Key Management**:
- RSA key pair stored in Secret Manager
- Private key used for signing
- Public key used for verification
- Key rotation every 90 days

### 3.5 Refresh Token

**Refresh Token Rotation**:
- New refresh token generated on every refresh
- Old refresh token invalidated
- Refresh token hash stored in database
- Refresh token expiration: 30 days

**Configurable Values**:
```yaml
REFRESH_TOKEN_ROTATION: true
REFRESH_TOKEN_EXPIRY_DAYS: 30
```

### 3.6 Token Revocation

**Revocation Triggers**:
- User logout
- Password change
- Account lockout
- Account deletion
- Suspicious activity detected

**Implementation**:
- Refresh token hash marked as revoked in database
- Session record marked as revoked
- Token validation checks revocation status

### 3.7 Logout

**Flow**:
1. User submits logout request
2. Server invalidates refresh token
3. Server invalidates session
4. Server clears device token (optional)
5. Server returns success

**Logout All Devices**:
- Invalidate all refresh tokens for user
- Invalidate all sessions for user
- Clear all device tokens for user

### 3.8 Session Management

**Session Record**:
- Session ID (UUID)
- User ID
- Device ID
- Refresh token hash
- IP address
- User agent
- Last active timestamp
- Expiration timestamp
- Revoked timestamp

**Session Expiration**:
- 15 minutes inactivity
- 30 days maximum lifetime
- Auto-renew on activity

### 3.9 Device Registration

**Flow**:
1. User submits device registration request
2. Server validates device type (Android/iOS)
3. Server validates platform and OS version
4. Server validates app version
5. Server stores device token
6. Server returns device ID

**Configurable Values**:
```yaml
DEVICE_TOKEN_REQUIRED: true
DEVICE_TYPE_VALIDATION: true
PLATFORM_VERSION_VALIDATION: true
APP_VERSION_VALIDATION: true
```

### 3.10 Device Removal

**Flow**:
1. User submits device removal request
2. Server validates device ownership
3. Server invalidates device token
4. Server invalidates all sessions for device
5. Server returns success

### 3.11 Suspicious Device Detection

**Detection Criteria**:
- Login from new device fingerprint
- Login from new location (IP geolocation)
- Concurrent sessions from different locations
- Rapid device registration from same IP

**Response**:
- Require additional authentication (OTP)
- Block suspicious device
- Notify user of unusual activity
- Audit suspicious activity

---

## 4. Token Security

### 4.1 JWT Structure

**Header**:
```json
{
  "alg": "RS256",
  "typ": "JWT"
}
```

**Payload**:
```json
{
  "sub": "user_id",
  "aud": "neowallet-api",
  "iss": "neowallet-auth",
  "exp": 1234567890,
  "iat": 1234567890,
  "jti": "token_id",
  "family_id": "family_id"
}
```

### 4.2 Signing Algorithm

**Algorithm**: RS256 (RSA Signature with SHA-256)

**Key Management**:
- RSA key pair (2048-bit minimum)
- Private key stored in Secret Manager
- Public key stored in Secret Manager
- Key rotation every 90 days
- Key versioning

### 4.3 Key Management

**Secret Manager**:
- Google Cloud Secret Manager
- Secret name: `jwt-signing-key`
- Secret versions maintained
- Secret access logging
- Secret rotation automation

**Key Rotation**:
- Generate new RSA key pair
- Update Secret Manager
- Grace period for old key (24 hours)
- Update application configuration
- Revoke old key after grace period

### 4.4 Access Token Lifetime

**Default**: 15 minutes

**Configurable**:
```yaml
ACCESS_TOKEN_EXPIRY_MINUTES: 15
```

**Rationale**: Short lifetime reduces risk of token theft

### 4.5 Refresh Token Lifetime

**Default**: 30 days

**Configurable**:
```yaml
REFRESH_TOKEN_EXPIRY_DAYS: 30
```

**Rationale**: Balances user convenience with security

### 4.6 Refresh Token Rotation

**Implementation**:
- New refresh token generated on every refresh
- Old refresh token invalidated immediately
- Refresh token hash stored in database
- Refresh token expiration checked on validation

**Configurable**:
```yaml
REFRESH_TOKEN_ROTATION: true
```

### 4.7 Revocation

**Revocation Storage**:
- Refresh token hash stored in database
- Revocation timestamp
- Revocation reason

**Revocation Check**:
- Token validation checks revocation status
- Revoked tokens rejected
- Revoked tokens cannot be refreshed

### 4.8 Audience

**Default**: `neowallet-api`

**Configurable**:
```yaml
JWT_AUDIENCE: neowallet-api
```

**Rationale**: Prevents token reuse across services

### 4.9 Issuer

**Default**: `neowallet-auth`

**Configurable**:
```yaml
JWT_ISSUER: neowallet-auth
```

**Rationale**: Identifies token issuer

### 4.10 Clock Tolerance

**Default**: 5 minutes (300 seconds)

**Configurable**:
```yaml
JWT_CLOCK_TOLERANCE_SECONDS: 300
```

**Rationale**: Accommodates clock skew between client and server

---

## 5. Authorization

### 5.1 RBAC Model

**Roles**:
- **OWNER**: Full family management rights
- **MEMBER**: View and modify family data
- **RESTRICTED**: Limited family data access

**Permissions**:

| Permission | OWNER | MEMBER | RESTRICTED |
|-----------|-------|--------|------------|
| Invite family members | YES | NO | NO |
| Remove family members | YES | NO | NO |
| Change member roles | YES | NO | NO |
| View family financial overview | YES | YES | LIMITED |
| View family transactions | YES | YES | LIMITED |
| Create family transactions | YES | YES | NO |
| Modify family transactions | YES | YES | NO |
| View family budgets | YES | YES | LIMITED |
| Create family budgets | YES | YES | NO |
| Modify family budgets | YES | YES | NO |
| View family savings | YES | YES | LIMITED |
| Create family savings | YES | YES | NO |
| Modify family savings | YES | YES | NO |
| View family bills | YES | YES | LIMITED |
| Create family bills | YES | YES | NO |
| Modify family bills | YES | YES | NO |
| Mark bills as paid | YES | YES | NO |

### 5.2 User Permissions

**User can**:
- Access own profile
- Access own transactions
- Access own budgets
- Access own savings goals
- Access own bills
- Access own notifications
- Create own family
- Join family via invitation

### 5.3 Family Permissions

**OWNER can**:
- Invite family members
- Remove family members
- Change member roles
- Delete family
- Access all family data
- Modify all family data

**MEMBER can**:
- View family financial overview
- View family transactions
- Create family transactions
- Modify own transactions
- View family budgets
- Create family budgets
- Modify own budgets
- View family savings
- Create family savings
- Modify own savings
- View family bills
- Create family bills
- Modify own bills
- Mark own bills as paid
- Leave family

**RESTRICTED can**:
- View limited family financial overview
- View limited family transactions
- View limited family budgets
- View limited family savings
- View limited family bills
- Leave family

### 5.4 Transaction Permissions

**User can**:
- Create own transactions
- Modify own transactions
- Delete own transactions
- View own transactions

**Family Member can**:
- Create family transactions
- Modify own transactions
- Delete own transactions
- View family transactions

**RESTRICTED can**:
- View limited family transactions

### 5.5 Budget Permissions

**User can**:
- Create own budgets
- Modify own budgets
- Delete own budgets
- View own budgets

**Family Member can**:
- Create family budgets
- Modify own budgets
- Delete own budgets
- View family budgets

**RESTRICTED can**:
- View limited family budgets

### 5.6 Allocation Permissions

**User can**:
- View own allocation recommendations
- Approve own allocation recommendations
- Modify own allocation recommendations

**Family Member can**:
- View family allocation recommendations
- Approve family allocation recommendations
- Modify family allocation recommendations

**RESTRICTED can**:
- View limited family allocation recommendations

### 5.7 Savings Permissions

**User can**:
- Create own savings goals
- Modify own savings goals
- Delete own savings goals
- Contribute to own savings goals
- View own savings goals

**Family Member can**:
- Create family savings goals
- Modify own savings goals
- Delete own savings goals
- Contribute to family savings goals
- View family savings goals

**RESTRICTED can**:
- View limited family savings goals

### 5.8 Bills Permissions

**User can**:
- Create own bills
- Modify own bills
- Delete own bills
- Mark own bills as paid
- View own bills

**Family Member can**:
- Create family bills
- Modify own bills
- Delete own bills
- Mark own bills as paid
- View family bills

**RESTRICTED can**:
- View limited family bills

### 5.9 Financial Health Permissions

**User can**:
- View own financial health score
- View own financial health factors
- View own financial health trends

**Family Member can**:
- View family financial health score
- View family financial health factors
- View family financial health trends

**RESTRICTED can**:
- View limited family financial health score

### 5.10 AI Permissions

**User can**:
- Create AI conversations
- Send AI messages
- View own AI conversations
- Delete own AI conversations

**Family Member can**:
- Create family AI conversations
- Send AI messages
- View family AI conversations
- Delete own AI conversations

**RESTRICTED can**:
- Create limited AI conversations
- Send limited AI messages
- View limited AI conversations

### 5.11 Notification Permissions

**User can**:
- View own notifications
- Mark own notifications as read
- Configure own notification preferences

**Family Member can**:
- View own notifications
- Mark own notifications as read
- Configure own notification preferences

**RESTRICTED can**:
- View own notifications
- Mark own notifications as read
- Configure own notification preferences

### 5.12 Authorization Enforcement

**Implementation**:
- Server-side authorization for all requests
- Authorization check before data access
- Authorization check before data modification
- Authorization check before data deletion
- Never trust client-side authorization

**Code Pattern**:
```java
@PreAuthorize("hasPermission(#userId, 'READ')")
public UserProfile getUserProfile(UUID userId) {
    // Authorization check
    if (!authorizationService.canAccessUser(userId, getCurrentUserId())) {
        throw new AccessDeniedException("Unauthorized");
    }
    return userProfileService.getProfile(userId);
}
```

---

## 6. Family Data Isolation

### 6.1 Resource Ownership

**User-Owned Resources**:
- User profile
- User preferences
- User addresses
- User sessions
- User devices
- User notifications
- User AI conversations

**Family-Owned Resources**:
- Family
- Family members
- Family invitations
- Family financial overview
- Family transactions
- Family budgets
- Family allocation recommendations
- Family savings goals
- Family bills

### 6.2 Family Membership

**Membership Verification**:
- Check family membership before family data access
- Check role before family data modification
- Check membership status (active vs left)

**Implementation**:
```java
public boolean isFamilyMember(UUID familyId, UUID userId) {
    return familyMemberRepository.existsByFamilyIdAndUserIdAndLeftAtIsNull(familyId, userId);
}
```

### 6.3 Role-Based Access

**Role Verification**:
- Check role before family data modification
- OWNER: Full access
- MEMBER: View and modify own data
- RESTRICTED: Limited view access

**Implementation**:
```java
public FamilyRole getFamilyRole(UUID familyId, UUID userId) {
    FamilyMember member = familyMemberRepository.findByFamilyIdAndUserId(familyId, userId);
    return member != null ? member.getRole() : null;
}
```

### 6.4 Permission

**Permission Check**:
- Check permission before operation
- Permission matrix defined in Section 5.1

**Implementation**:
```java
public boolean hasPermission(UUID familyId, UUID userId, String permission) {
    FamilyRole role = getFamilyRole(familyId, userId);
    return permissionService.hasPermission(role, permission);
}
```

### 6.5 Server-Side Authorization

**Authorization Flow**:
1. Extract user ID from JWT
2. Extract family ID from JWT or request
3. Verify family membership
4. Verify role
5. Verify permission
6. Grant or deny access

**Never trust client-side authorization**:
- Client-side authorization is for UI only
- Server-side authorization is mandatory
- Always verify on server

### 6.6 Cross-Family Access Prevention

**Prevention**:
- Family ID validation on all family data access
- Resource ownership checks on all family data modification
- Audit all cross-family access attempts
- Block suspicious cross-family access attempts

**Implementation**:
```java
public void validateFamilyAccess(UUID familyId, UUID userId) {
    if (!isFamilyMember(familyId, userId)) {
        auditService.logCrossFamilyAccessAttempt(userId, familyId);
        throw new AccessDeniedException("Not a family member");
    }
}
```

### 6.7 Restricted Member Access

**Limited Access**:
- Restricted members cannot view sensitive financial information
- Restricted members cannot modify family data
- Restricted members cannot invite/remove members

**Sensitive Information**:
- Detailed financial breakdown
- Transaction details
- Budget details
- Savings details
- Bill details

**Implementation**:
```java
public FinancialOverview getFinancialOverview(UUID familyId, UUID userId) {
    validateFamilyAccess(familyId, userId);
    FamilyRole role = getFamilyRole(familyId, userId);
    FinancialOverview overview = financialOverviewService.getOverview(familyId);
    if (role == FamilyRole.RESTRICTED) {
        overview = maskSensitiveData(overview);
    }
    return overview;
}
```

---

## 7. Financial Data Security

### 7.1 Sensitive Financial Data

**Sensitive Data**:
- Transaction amounts
- Budget limits
- Savings goals
- Bill amounts
- Financial health scores
- Allocation recommendations

**PII in Financial Data**:
- Transaction descriptions (may contain PII)
- Bill vendor names (may contain PII)
- Transaction notes (may contain PII)

### 7.2 Encryption at Rest

**PostgreSQL TDE**:
- Transparent Data Encryption enabled
- AES-256 encryption
- Key management via Cloud KMS

**Cloud Storage**:
- AES-256 encryption
- Customer-managed encryption keys (CMEK)
- Key management via Cloud KMS

**Redis**:
- TLS encryption
- AUTH password
- Network isolation

### 7.3 Encryption in Transit

**TLS 1.3**:
- All API communication uses TLS 1.3
- All database connections use TLS 1.3
- All Redis connections use TLS 1.3
- All external provider connections use TLS 1.3

**Certificate Validation**:
- Certificate validation enabled
- Certificate pinning in mobile app
- HSTS enabled

### 7.4 Application-Level Encryption

**When Required**:
- Not required for MVP (PostgreSQL TDE sufficient)
- Future: Application-level encryption for highly sensitive fields

**Implementation**:
- AES-256-GCM encryption
- Key management via Secret Manager
- Field-level encryption

### 7.5 Key Management

**Cloud KMS**:
- Customer-managed encryption keys (CMEK)
- Key rotation every 90 days
- Key access logging
- Key versioning

**Secret Manager**:
- JWT signing keys
- Database credentials
- API keys
- OTP provider credentials
- Email credentials
- SMS/WhatsApp credentials
- AI provider keys

### 7.6 Access Controls

**Database Access**:
- Least privilege database users
- Application user (read/write to application schema only)
- Migration user (DDL access only during migration)
- Backup user (read-only access for backups)
- No direct database access for AI

**API Access**:
- Authentication required for all APIs (except public endpoints)
- Authorization required for all data access
- Rate limiting enforced

### 7.7 Logging Restrictions

**No Logging**:
- Passwords
- OTPs
- Tokens
- Secrets
- Payment credentials
- Sensitive financial payloads

**Log Masking**:
- PII masking
- Financial data masking
- Token masking
- Secret masking

### 7.8 Data Masking

**When to Mask**:
- In logs
- In error messages
- In API responses (where appropriate)
- In audit logs (where appropriate)

**Masking Strategy**:
- Email: `u***@example.com`
- Phone: `***-***-1234`
- Name: `J*** D***`
- Amount: `***.**`
- Token: `***...`

---

## 8. Mobile Security

### 8.1 Secure Token Storage

**iOS**:
- Keychain Services
- kSecAttrAccessibleWhenUnlocked
- kSecAttrAccessControl with biometric

**Android**:
- Android Keystore
- KeyStore.getInstance("AndroidKeyStore")
- Biometric authentication for key access

### 8.2 Device Binding

**Device Fingerprinting**:
- User-agent
- Device type
- Platform
- OS version
- App version
- Device ID (if available)

**Binding Strategy**:
- Device binding for sensitive operations
- Biometric authentication for sensitive actions
- Device fingerprinting for anomaly detection

### 8.3 Biometric Authentication

**When Required**:
- View sensitive financial data
- Modify financial data
- Mark bills as paid
- Approve AI recommendations

**Implementation**:
- LocalAuthentication (iOS)
- BiometricPrompt (Android)
- Fallback to PIN/passcode

### 8.4 App Lock

**Implementation**:
- PIN/passcode required on app launch
- Biometric authentication option
- Auto-lock after inactivity (5 minutes)
- Secure logout after failed attempts (5 attempts)

**Configurable**:
```yaml
APP_LOCK_ENABLED: true
APP_LOCK_INACTIVITY_MINUTES: 5
APP_LOCK_FAILED_ATTEMPT_LIMIT: 5
```

### 8.5 Screenshot Considerations

**iOS**:
- `isSecureTextEntry` for sensitive fields
- Prevent screenshots in sensitive screens (iOS 13+)

**Android**:
- `FLAG_SECURE` for sensitive screens
- Prevent screenshots in sensitive screens

**Limitation**:
- Cannot prevent screenshots on all devices
- Cannot prevent screen recording on all devices

### 8.6 Root/Jailbreak Detection

**Detection Methods**:
- Check for common root/jailbreak indicators
- Check for suspicious apps
- Check for system integrity

**Response**:
- Warning message to user
- Limited functionality
- Cannot prevent app usage (best effort)

**Limitation**:
- Cannot guarantee detection on all devices
- Cannot prevent app usage on rooted/jailbroken devices

### 8.7 TLS

**Implementation**:
- TLS 1.3 for all network communication
- Certificate pinning
- HSTS

**Certificate Pinning**:
- Pin server certificate
- Pin intermediate certificate
- Update pinning on certificate rotation

### 8.8 Certificate Pinning

**Implementation**:
- Pin server certificate in mobile app
- Validate certificate on every request
- Update pinning on certificate rotation

**Limitation**:
- Certificate rotation requires app update
- Consider certificate pinning for critical APIs only

### 8.9 Clipboard Security

**Implementation**:
- Clear clipboard on app background
- Clear clipboard on logout
- No sensitive data in clipboard

### 8.10 Local Cache Protection

**Implementation**:
- No sensitive data in local cache
- Encrypted cache for non-sensitive data
- Clear cache on logout
- Clear cache on app uninstall

### 8.11 Secure Logout

**Implementation**:
- Clear all tokens from secure storage
- Clear all sensitive data from memory
- Clear all sensitive data from cache
- Clear clipboard
- Invalidate server-side session

### 8.12 Lost Device Handling

**User Action**:
- User can revoke device from web interface
- User can logout all devices
- User can delete account

**Implementation**:
- Device revocation API
- Logout all devices API
- Account deletion API

---

## 9. API Security

### 9.1 Input Validation

**Validation Rules**:
- Email format validation
- Phone number format validation
- Password strength validation
- Amount validation (positive numbers)
- Date validation (valid dates)
- Enum validation (valid values)
- UUID validation (valid UUIDs)

**Implementation**:
```java
@PostMapping("/api/v1/transactions")
public Transaction createTransaction(@Valid @RequestBody TransactionRequest request) {
    // Validation handled by @Valid
    return transactionService.create(request);
}
```

### 9.2 Output Encoding

**Implementation**:
- JSON encoding for all API responses
- HTML encoding for any HTML output
- URL encoding for any URL output

### 9.3 Authentication

**Implementation**:
- JWT authentication for all protected endpoints
- Bearer token in Authorization header
- Token validation on every request
- Token expiration check
- Token revocation check

### 9.4 Authorization

**Implementation**:
- Server-side RBAC for all endpoints
- Authorization check before data access
- Authorization check before data modification
- Never trust client-side authorization

### 9.5 Rate Limiting

**Rate Limits**:
- Authenticated: 100 req/min per user
- Unauthenticated: 10 req/min per IP
- AI chat: 20 req/min per user
- OTP endpoints: 10 req/min per phone/email

**Implementation**:
- Redis-based rate limiting
- Sliding window algorithm
- Rate limit headers in response

### 9.6 Request Size Limits

**Limits**:
- Request body: 1 MB
- Request headers: 8 KB
- Query string: 2 KB

**Implementation**:
- Spring Boot configuration
- Reject requests exceeding limits

### 9.7 Pagination Limits

**Limits**:
- Page size: 1-100
- Default page size: 20

**Implementation**:
- Validate page size on paginated endpoints
- Reject invalid page sizes

### 9.8 Idempotency

**Implementation**:
- Idempotency keys on critical POST endpoints
- Idempotency key validity: 24 hours
- Idempotency key uniqueness
- Idempotency key storage in Redis

**Endpoints with Idempotency**:
- POST /api/v1/transactions
- POST /api/v1/budgets
- POST /api/v1/savings-goals
- POST /api/v1/bills

### 9.9 CORS

**Implementation**:
- CORS configuration for web clients (if applicable)
- Allow only trusted origins
- Allow only necessary methods
- Allow only necessary headers

### 9.10 CSRF

**Implementation**:
- SameSite cookies for session cookies
- CSRF tokens for state-changing operations
- Validate CSRF tokens on POST/PUT/DELETE

### 9.11 Security Headers

**Headers**:
- `Strict-Transport-Security: max-age=31536000; includeSubDomains`
- `X-Content-Type-Options: nosniff`
- `X-Frame-Options: DENY`
- `X-XSS-Protection: 1; mode=block`
- `Content-Security-Policy: default-src 'self'`
- `Referrer-Policy: strict-origin-when-cross-origin`

### 9.12 Request IDs

**Implementation**:
- Generate unique request ID for every request
- Include request ID in response headers
- Include request ID in logs
- Include request ID in audit logs

### 9.13 Correlation IDs

**Implementation**:
- Pass correlation ID from client
- Generate correlation ID if not provided
- Include correlation ID in response headers
- Include correlation ID in logs
- Include correlation ID in audit logs

### 9.14 OWASP API Security

**Addressed Risks**:
- Broken Object Level Authorization (IDOR) - Addressed with server-side authorization
- Broken Authentication - Addressed with JWT and OTP
- Broken Object Property Level Authorization - Addressed with field-level authorization
- Unrestricted Resource Consumption - Addressed with rate limiting
- Broken Function Level Authorization - Addressed with RBAC
- Mass Assignment - Addressed with input validation
- Security Misconfiguration - Addressed with security headers and TLS
- Injection - Addressed with parameterized queries
- Improper Assets Management - Addressed with dependency scanning
- Insufficient Logging & Monitoring - Addressed with audit logging and monitoring

---

## 10. Database Security

### 10.1 Database Credentials

**Storage**:
- Database credentials stored in Secret Manager
- No credentials in code
- No credentials in Git
- No credentials in Docker images
- No credentials in application configuration committed to repository

**Rotation**:
- Rotate database credentials every 90 days
- Rotate credentials on compromise
- Rotate credentials on personnel change

### 10.2 Least Privilege

**Database Users**:
- **Application User**: Read/write to application schema only
- **Migration User**: DDL access only during migration
- **Backup User**: Read-only access for backups
- **AI User**: Not applicable (AI has no database access)

**Schema Privileges**:
- Application user cannot access system tables
- Application user cannot modify schema
- Application user cannot create/drop tables

### 10.3 Application Database User

**Privileges**:
- SELECT on all application tables
- INSERT on all application tables
- UPDATE on all application tables
- DELETE on all application tables (where applicable)
- EXECUTE on all functions/procedures

**No Privileges**:
- No DDL (CREATE, ALTER, DROP)
- No GRANT/REVOKE
- No access to system tables
- No access to other schemas

### 10.4 Migration User

**Privileges**:
- DDL (CREATE, ALTER, DROP)
- All application user privileges
- Access to system tables (for schema management)

**Usage**:
- Used only during migration
- Disabled after migration
- Credentials rotated after migration

### 10.5 Backup User

**Privileges**:
- SELECT on all application tables
- No INSERT/UPDATE/DELETE
- No DDL

**Usage**:
- Used only for backups
- Read-only access
- Credentials rotated regularly

### 10.6 Encryption

**Encryption at Rest**:
- PostgreSQL TDE (Transparent Data Encryption)
- AES-256 encryption
- Key management via Cloud KMS

**Encryption in Transit**:
- TLS 1.3 for database connections
- Certificate validation
- No plaintext connections

### 10.7 Connection Security

**Implementation**:
- TLS 1.3 for all connections
- Certificate validation
- No plaintext connections
- Connection pooling
- Connection timeouts

### 10.8 Network Isolation

**Implementation**:
- Private IP for database
- VPC network isolation
- Firewall rules
- No public IP
- No internet access (except for outbound to required services)

### 10.9 Audit Access

**Audit Logging**:
- Log all database access
- Log all DDL operations
- Log all DML operations
- Log all connection attempts
- Log all privilege changes

**Access Control**:
- Audit logs accessible only to authorized personnel
- Audit logs retained for 7 years
- Audit logs encrypted at rest

### 10.10 Application Users

**No Database Credentials**:
- Application users never receive database credentials
- Database credentials injected at runtime
- Database credentials stored in Secret Manager
- Application accesses database via connection pool

### 10.11 AI Database Access

**No Direct Database Access**:
- AI has no direct database access
- AI accesses data only through authorized application tools/APIs
- AI tools are read-only
- AI tool invocations are audited

---

## 11. AI Security

### 11.1 AI Architecture

**Flow**:
```
User
 ↓
Authentication (JWT)
 ↓
Authorization (RBAC)
 ↓
AI Orchestrator
 ↓
Policy Engine
 ↓
Approved Tool (Read-Only)
 ↓
NeoWallet API
 ↓
Database
```

### 11.2 AI Restrictions

**AI Must NOT**:
- Access database directly
- Execute SQL
- Bypass authorization
- Modify financial data in MVP
- Execute payments
- Move money
- Change permissions
- Access another family's data

### 11.3 AI Tool Access

**Tool Access Control**:
- AI can only call approved tools
- AI tools are read-only
- AI tools require user authorization
- AI tool parameters are validated
- AI tool invocations are audited

**Approved Tools**:
- Get user profile
- Get family financial overview
- Get transactions
- Get budgets
- Get savings goals
- Get bills
- Get financial health score

### 11.4 AI Data Access

**Data Access Pattern**:
- AI requests data through application tools/APIs
- Application tools/APIs enforce authorization
- AI receives only authorized data
- AI cannot access unauthorized data

### 11.5 AI Read-Only

**Read-Only Enforcement**:
- AI tools are read-only
- AI cannot modify data
- AI cannot execute write operations
- AI recommendations require user approval

### 11.6 AI Recommendations

**Recommendation Flow**:
- AI generates recommendation
- AI presents recommendation to user
- User approves or rejects recommendation
- Application executes approved recommendation
- Application logs recommendation and approval

### 11.7 AI Audit Logging

**Audit Events**:
- AI conversation creation
- AI message creation
- AI tool invocation
- AI recommendation generation
- AI recommendation approval/rejection
- Prompt injection attempts

---

## 12. Prompt Injection

### 12.1 User Prompt Injection

**Protection**:
- Prompt sanitization
- Input validation
- Context isolation
- Tool access control
- Output validation
- Confidence thresholds

**Detection**:
- Anomaly detection in prompts
- Pattern matching for injection attempts
- AI response monitoring

### 12.2 Transaction Description Injection

**Protection**:
- Treat transaction descriptions as untrusted
- Sanitize transaction descriptions before AI processing
- Limit transaction description length
- Remove special characters

### 12.3 Vendor Data Injection

**Protection**:
- Treat vendor data as untrusted
- Sanitize vendor data before AI processing
- Validate vendor data format
- Limit vendor data length

### 12.4 Retrieved Document Injection

**Protection**:
- Treat retrieved documents as untrusted
- Sanitize retrieved documents before AI processing
- Validate document format
- Limit document length

### 12.5 Tool Manipulation

**Protection**:
- Tool access control
- Tool parameter validation
- Tool result validation
- No direct database access for AI
- Policy engine enforcement

### 12.6 Instruction Override

**Protection**:
- Prompt engineering
- System prompt isolation
- User prompt isolation
- Context isolation
- Output validation

### 12.7 Untrusted Data

**Treat as Untrusted**:
- User prompts
- Transaction descriptions
- Vendor data
- Retrieved documents
- External API responses

---

## 13. AI Data Privacy

### 13.1 Data Minimization

**Minimization Strategy**:
- Send only necessary data to AI provider
- Send only aggregated data where possible
- Send only anonymized data where possible
- Send only time-limited data where possible

### 13.2 Context Minimization

**Minimization Strategy**:
- Limit conversation context length
- Limit conversation history
- Limit tool invocation history
- Limit recommendation history

### 13.3 AI Conversation Retention

**Retention Policy**:
- AI conversations retained for 1 year (Compliance Decision Required)
- AI conversations can be deleted by user
- AI conversations deleted on account deletion

### 13.4 Deletion

**User Control**:
- User can delete individual conversations
- User can delete all conversations
- User can delete account (deletes all conversations)

### 13.5 Sensitive Data Masking

**Masking Strategy**:
- Mask PII in AI requests
- Mask financial data in AI requests
- Mask sensitive details in AI requests

### 13.6 Provider Data Sharing

**Provider Review**:
- Review AI provider data handling policy
- Review AI provider data retention policy
- Review AI provider data training policy
- Review AI provider data breach notification policy

**Provider-Specific Review**:
- Required before production
- Legal/compliance review required
- Data processing agreement required

### 13.7 Model Training Opt-Out

**Opt-Out Strategy**:
- Opt-out of model training where possible
- Provider-specific opt-out configuration
- User notification of data usage

---

## 14. Secrets Management

### 14.1 Secure Storage

**Secret Manager**:
- Google Cloud Secret Manager
- All secrets stored in Secret Manager
- No secrets in code
- No secrets in Git
- No secrets in Docker images
- No secrets in application configuration committed to repository

### 14.2 Database Credentials

**Storage**:
- Secret name: `database-credentials`
- Secret versions maintained
- Secret access logging
- Secret rotation every 90 days

### 14.3 JWT Signing Keys

**Storage**:
- Secret name: `jwt-signing-key`
- Secret versions maintained
- Secret access logging
- Secret rotation every 90 days

### 14.4 OTP Provider Credentials

**Storage**:
- Secret name: `otp-provider-credentials`
- Secret versions maintained
- Secret access logging
- Secret rotation every 90 days

### 14.5 Email Credentials

**Storage**:
- Secret name: `email-credentials`
- Secret versions maintained
- Secret access logging
- Secret rotation every 90 days

### 14.6 SMS/WhatsApp Credentials

**Storage**:
- Secret name: `sms-whatsapp-credentials`
- Secret versions maintained
- Secret access logging
- Secret rotation every 90 days

### 14.7 AI Provider Keys

**Storage**:
- Secret name: `ai-provider-keys`
- Secret versions maintained
- Secret access logging
- Secret rotation every 90 days

### 14.8 Cloud Credentials

**Storage**:
- Service account keys stored in Secret Manager
- Secret versions maintained
- Secret access logging
- Secret rotation every 90 days

### 14.9 Future Payment Provider Secrets

**Storage**:
- Secret name: `payment-provider-secrets`
- Secret versions maintained
- Secret access logging
- Secret rotation every 90 days

### 14.10 Secret Rotation

**Rotation Strategy**:
- Rotate secrets every 90 days
- Rotate secrets on compromise
- Rotate secrets on personnel change
- Grace period for old secrets (24 hours)
- Update application configuration

---

## 15. Cloud Security

### 15.1 Target Platform

**Platform**: Google Cloud Run

**Rationale**:
- Managed platform
- Auto-scaling
- Built-in security
- Integration with GCP services

### 15.2 IAM

**IAM Least Privilege**:
- Service accounts with minimal permissions
- User accounts with minimal permissions
- Regular privilege audits
- Separation of duties

### 15.3 Service Accounts

**Service Account Least Privilege**:
- Cloud Run service account: Only necessary permissions
- Database service account: Only database access
- Backup service account: Only backup access
- Monitoring service account: Only monitoring access

### 15.4 Least Privilege

**Principle**:
- Grant only necessary permissions
- Grant only for necessary resources
- Grant only for necessary duration
- Revoke permissions when no longer needed

### 15.5 Secret Manager

**Secret Manager Usage**:
- All secrets stored in Secret Manager
- Secret versions maintained
- Secret access logging
- Secret rotation automation

### 15.6 Network Controls

**Network Isolation**:
- VPC network isolation
- Private IP for database
- Private IP for Redis
- Firewall rules
- No public IP for database

### 15.7 Logging

**Logging Strategy**:
- Cloud Logging for all logs
- Log retention: 90 days for application logs, 7 years for audit logs
- Log access controls
- Log masking for sensitive data

### 15.8 Monitoring

**Monitoring Strategy**:
- Cloud Monitoring for all metrics
- Alerting for critical events
- Uptime monitoring
- Performance monitoring
- Security monitoring

### 15.9 Backups

**Backup Strategy**:
- Automated daily backups
- Backup encryption (AES-256)
- Backup retention: 30 days
- Backup access controls
- Backup testing (monthly)

### 15.10 Artifact Security

**Artifact Security**:
- Container image signing
- Container image scanning
- Immutable artifacts
- Artifact Registry access controls

### 15.11 Environment Separation

**Environments**:
- Development environment
- Staging environment
- Production environment

**Separation**:
- Environment-specific secrets
- Environment-specific configurations
- No cross-environment access
- Separate projects (optional)

---

## 16. CI/CD Security

### 16.1 GitHub Security

**GitHub Security**:
- GitHub Advanced Security
- Secret scanning
- Dependency scanning
- Code scanning

### 16.2 Branch Protection

**Branch Protection**:
- Main branch protection
- Require pull request
- Require code review
- Require status checks
- Block force pushes

### 16.3 Pull Requests

**Pull Request Requirements**:
- At least 1 code review approval
- All status checks must pass
- No merge conflicts
- No sensitive data in PR

### 16.4 Code Review

**Code Review Requirements**:
- At least 1 reviewer
- Security review for sensitive changes
- No self-approval
- Review comments addressed

### 16.5 Dependency Scanning

**Dependency Scanning**:
- GitHub Dependabot
- Automated dependency updates
- Vulnerability alerts
- Security advisories

### 16.6 SAST

**Static Application Security Testing**:
- GitHub Code Scanning
- Custom security rules
- Pull request integration
- Blocking on critical vulnerabilities

### 16.7 Secret Scanning

**Secret Scanning**:
- GitHub Secret Scanning
- Custom secret patterns
- Pull request integration
- Blocking on secrets

### 16.8 Container Scanning

**Container Scanning**:
- Container Analysis API
- Vulnerability scanning
- Base image scanning
- Blocking on critical vulnerabilities

### 16.9 SBOM

**Software Bill of Materials**:
- SBOM generation for all builds
- SBOM storage in Artifact Registry
- SBOM analysis for vulnerabilities

### 16.10 Image Signing

**Image Signing**:
- Container image signing
- Keyless signing
- Binary authorization
- Signature verification

### 16.11 Deployment Approvals

**Deployment Approvals**:
- Manual approval for production deployment
- Automatic deployment for staging
- Approval requirements in GitHub Actions

---

## 17. Audit

### 17.1 Immutable Audit Events

**Audit Events**:
- Authentication (login, logout, failed login)
- Authorization changes (role changes, permission changes)
- Family membership (invite, accept, remove, leave)
- Financial record changes (transaction create/update/delete)
- Budget changes (create, update, delete)
- Savings changes (create, update, delete, contribution)
- Bill changes (create, update, delete, mark-paid)
- Security events (account lockout, suspicious activity)
- AI tool invocations
- Configuration changes

### 17.2 Audit Record Structure

**Audit Record**:
- Audit ID (UUID)
- Actor ID (user ID or system)
- Actor type (USER, SYSTEM, ADMIN)
- Action (CREATE, READ, UPDATE, DELETE, LOGIN, LOGOUT, etc.)
- Resource type (USER, FAMILY, TRANSACTION, etc.)
- Resource ID (UUID)
- Request ID (UUID)
- Correlation ID (UUID)
- Result (SUCCESS, FAILURE, PARTIAL)
- Metadata (JSONB)
- Created timestamp (TIMESTAMP WITH TIME ZONE)

### 17.3 Audit Logging

**Logging Strategy**:
- Immutable audit logs (no UPDATE, no DELETE)
- ON DELETE RESTRICT prevents deletion of users with audit logs
- Audit logs retained for 7 years
- Audit logs encrypted at rest
- Audit logs access controlled

### 17.4 Sensitive Information

**No Sensitive Information**:
- No passwords in audit logs
- No OTPs in audit logs
- No tokens in audit logs
- No secrets in audit logs
- No payment credentials in audit logs
- No sensitive financial payloads in audit logs

### 17.5 Audit Access

**Access Control**:
- Audit logs accessible only to authorized personnel
- Audit access logged
- Audit access reviewed regularly

---

## 18. Monitoring and Detection

### 18.1 Security Monitoring

**Monitoring Events**:
- Repeated login failures
- OTP abuse
- Token misuse
- Unusual API usage
- Authorization failures
- Cross-family access attempts
- AI tool abuse
- Prompt injection attempts
- Configuration changes
- Suspicious device activity

### 18.2 Alert Severity

**Severity Levels**:
- **CRITICAL**: Account compromise, data breach, financial data corruption
- **HIGH**: Authorization bypass, privilege escalation, credential leakage
- **MEDIUM**: Suspicious activity, configuration changes, unusual patterns
- **LOW**: Failed login attempts, rate limit violations

### 18.3 Repeated Login Failures

**Alerting**:
- Alert on 5 failed login attempts from same IP
- Alert on 10 failed login attempts from same user
- Alert on account lockout

### 18.4 OTP Abuse

**Alerting**:
- Alert on excessive OTP generation from same phone/email
- Alert on OTP verification failures
- Alert on OTP resend abuse

### 18.5 Token Misuse

**Alerting**:
- Alert on token usage from multiple IPs
- Alert on token usage from multiple devices
- Alert on token expiration abuse

### 18.6 Unusual API Usage

**Alerting**:
- Alert on unusual API usage patterns
- Alert on unusual transaction patterns
- Alert on unusual financial data access

### 18.7 Authorization Failures

**Alerting**:
- Alert on repeated authorization failures from same user
- Alert on cross-family access attempts
- Alert on IDOR patterns

### 18.8 Cross-Family Access Attempts

**Alerting**:
- Alert on cross-family access attempts
- Alert on IDOR patterns
- Alert on unauthorized family access

### 18.9 AI Tool Abuse

**Alerting**:
- Alert on unauthorized tool access attempts
- Alert on prompt injection attempts
- Alert on AI tool parameter anomalies

### 18.10 Prompt Injection Attempts

**Alerting**:
- Alert on prompt injection attempts
- Alert on unusual prompt patterns
- Alert on AI response anomalies

### 18.11 Configuration Changes

**Alerting**:
- Alert on unauthorized configuration changes
- Alert on IAM changes
- Alert on secret changes

### 18.12 Suspicious Device Activity

**Alerting**:
- Alert on device registration from new location
- Alert on device registration from suspicious IP
- Alert on device fingerprint changes

---

## 19. Incident Response

### 19.1 Detection

**Detection Methods**:
- Security monitoring alerts
- User reports
- Automated anomaly detection
- Third-party notifications (provider breaches)

### 19.2 Classification

**Incident Categories**:
- Authentication compromise
- Data breach
- Financial data corruption
- AI security incident
- Credential leakage
- Provider compromise

**Severity Levels**:
- **CRITICAL**: System-wide impact, data breach, financial loss
- **HIGH**: Significant impact, partial data breach
- **MEDIUM**: Limited impact, no data breach
- **LOW**: Minimal impact, no data breach

### 19.3 Containment

**Containment Actions**:
- Isolate compromised systems
- Revoke compromised credentials
- Block malicious IPs
- Disable affected accounts
- Suspend affected services

### 19.4 Eradication

**Eradication Actions**:
- Remove malware
- Patch vulnerabilities
- Remove unauthorized access
- Clean compromised systems
- Update security controls

### 19.5 Recovery

**Recovery Actions**:
- Restore from backup
- Rebuild systems
- Update credentials
- Restore services
- Monitor for recurrence

### 19.6 Notification

**Notification Requirements**:
- Notify affected users
- Notify security team
- Notify management
- Notify legal/compliance (if required)
- Notify regulators (if required)

### 19.7 Post-Incident Review

**Review Activities**:
- Incident timeline
- Root cause analysis
- Impact assessment
- Response effectiveness
- Lessons learned
- Improvement actions

---

## 20. Backup and Recovery

### 20.1 Database Backups

**Backup Strategy**:
- Automated daily backups
- Point-in-time recovery (PITR)
- Backup encryption (AES-256)
- Backup retention: 30 days
- Backup access controls

### 20.2 Backup Encryption

**Encryption**:
- AES-256 encryption
- Customer-managed encryption keys (CMEK)
- Key management via Cloud KMS

### 20.3 Recovery Testing

**Testing Strategy**:
- Monthly recovery testing
- Test backup restoration
- Test data integrity
- Test recovery time
- Document recovery procedures

### 20.4 Retention

**Retention Policy**:
- Daily backups: 30 days
- Weekly backups: 12 weeks
- Monthly backups: 12 months
- Audit logs: 7 years

### 20.5 RPO

**Recovery Point Objective**:
- **Target**: 1 day
- **Status**: Engineering target (Product/Operations decision required)

### 20.6 RTO

**Recovery Time Objective**:
- **Target**: 4 hours
- **Status**: Engineering target (Product/Operations decision required)

---

## 21. Data Retention and Deletion

### 21.1 Business Rules Mapping

**Retention Mapping**:
- User Accounts: 7 years after deletion (BR-RET-001)
- Transactions: 7 years (BR-RET-002)
- Budgets: 7 years (BR-RET-003)
- Savings Goals: 7 years after completion (BR-RET-004)
- Bills: 7 years (BR-RET-005)
- Audit Logs: 7 years (BR-RET-001)
- AI Conversations: 1 year (Compliance Decision Required)
- Notifications: 90 days (Compliance Decision Required)

### 21.2 Database Model Mapping

**Retention Implementation**:
- Soft delete for user accounts (deleted_at timestamp)
- Hard delete for transactions (no soft delete)
- Soft delete for budgets (status = DELETED)
- Soft delete for savings goals (status = CANCELLED)
- Hard delete for audit logs (immutable)

### 21.3 Privacy Requirements

**Privacy Controls**:
- PII minimization
- Data retention policy
- User-requested deletion
- Data masking
- Access controls

### 21.4 User-Requested Deletion

**Deletion Process**:
- User requests account deletion
- System confirms deletion request
- System soft-deletes user account
- System deletes user data after retention period
- System retains audit logs for 7 years

### 21.5 Required Retention

**Retention Requirements**:
- Audit logs: 7 years (business rule)
- Financial records: 7 years (business rule)
- User data: 7 years after deletion (business rule)

### 21.6 Audit Retention

**Audit Retention**:
- Audit logs retained for 7 years
- Audit logs immutable
- Audit logs encrypted at rest
- Audit logs access controlled

### 21.7 Legal Hold

**Legal Hold Process**:
- Legal hold places data on hold
- Data cannot be deleted during legal hold
- Legal hold tracked in system
- Legal hold released when authorized

### 21.8 Compliance/Legal Decision Required

**Unknown Requirements**:
- AI Conversations retention: Compliance Decision Required
- Notifications retention: Compliance Decision Required

---

## 22. Security Testing

**Reference**: NeoWallet_Security_Test_Strategy_v1.md

**Summary**: Comprehensive security testing strategy covering SAST, DAST, dependency scanning, API security testing, mobile security testing, authentication testing, authorization testing, AI security testing, prompt injection testing, penetration testing, and performance/security testing.

---

## 23. Security Acceptance Criteria

### 23.1 Production Security Acceptance Criteria

**Criteria**:
- No critical vulnerabilities (CVSS 7.0+)
- No high vulnerabilities (CVSS 4.0-6.9) in production code
- No hard-coded secrets
- No unauthorized cross-family access
- No direct AI database access
- No payment execution from MVP AI
- All sensitive operations audited
- All security controls implemented
- All security tests passing
- All monitoring and alerting configured

### 23.2 Pre-Production Checklist

**Checklist**:
- [ ] All secrets in Secret Manager
- [ ] All secrets rotated
- [ ] All TLS certificates valid
- [ ] All rate limiting configured
- [ ] All monitoring and alerting configured
- [ ] All audit logging enabled
- [ ] All security tests passing
- [ ] All penetration testing completed
- [ ] All security reviews completed
- [ ] All documentation updated

---

## 24. Security Traceability

**Reference**: NeoWallet_Security_Control_Matrix_v1.md

**Summary**: Complete traceability matrix mapping threats to security controls, business rules, APIs, data entities, and tests.

---

## 25. Conclusion

The NeoWallet Security Implementation Guide provides comprehensive security requirements for NeoWallet MVP. All security aspects are covered including authentication, authorization, data protection, infrastructure security, AI security, and monitoring & detection.

**Next Steps**:
1. Create Security Test Strategy
2. Create Security Validation Report
3. Product Owner approval

# NeoWallet API Contract Specification v1

## Executive Summary

This document defines the complete API contract specification for NeoWallet MVP. All APIs are REST-based and follow OpenAPI 3.1 standards.

**Architecture**: Flutter → API Gateway/BFF → Spring Boot Modular Backend → PostgreSQL/Redis → Neo AI Service
**API Version**: v1
**Base URL**: /api/v1
**Authentication**: JWT-based
**Authorization**: RBAC with family roles
**Date**: August 18, 2026
**Status**: Ready for Product Owner Approval

---

## 1. Architecture

### MVP Architecture

```
Flutter Android/iOS
        ↓
API Gateway / BFF
        ↓
Spring Boot Modular Backend
        ↓
PostgreSQL
Redis (where justified)
        ↓
Neo AI Service
        ↓
Approved AI Tools
        ↓
Deterministic Financial Services
```

### Modular Backend

MVP uses a modular backend architecture within a single deployable unit:
- Authentication Module
- User Module
- Family Module
- Financial Overview Module
- Transaction Module
- Budget Module
- Allocation Module
- Savings Module
- Bill Module
- Financial Health Module
- Notification Module
- AI Module

**Note**: Do not create 11 independently deployable microservices. Use a modular monolith approach.

---

## 2. API Principles

### Base URL

```
Production: https://api.neowallet.com/api/v1
Staging: https://api-staging.neowallet.com/api/v1
Development: https://api-dev.neowallet.com/api/v1
```

### API Versioning

**URL-based versioning**: `/api/v1/`

**Versioning Strategy**:
- Major version changes require URL version change
- Minor version changes are backward compatible
- Deprecated APIs are supported for 6 months

### Authentication

**JWT-based authentication**:
- Access token: 15 minutes expiry (configurable)
- Refresh token: 30 days expiry (configurable)
- Token type: Bearer
- Algorithm: RS256

**Refresh Rotation**: Refresh tokens are rotated on each refresh
**Revocation**: Refresh tokens can be revoked via logout or session revocation
**Device Association**: Sessions are tracked per device
**OTP Expiration**: OTP codes expire in 10 minutes (configurable)
**OTP Retry Limits**: Maximum 3 OTP requests per 15 minutes (configurable)

**Configurable Values**:
- ACCESS_TOKEN_EXPIRY_MINUTES: 15
- REFRESH_TOKEN_EXPIRY_DAYS: 30
- OTP_EXPIRY_MINUTES: 10
- OTP_RETRY_LIMIT: 3
- OTP_RETRY_WINDOW_MINUTES: 15

**Header**:
```
Authorization: Bearer {access_token}
```

### Authorization

**Role-Based Access Control (RBAC)**:
- System roles: ADMIN, USER
- Family roles: OWNER, MEMBER, RESTRICTED

**Header**:
```
X-Family-ID: {familyId}
```

### Content Type

**Request/Response**: `application/json`

**Header**:
```
Content-Type: application/json
Accept: application/json
```

### Request IDs

**Header**:
```
X-Request-ID: {uuid}
```

If not provided, server generates one.

### Correlation IDs

**Header**:
```
X-Correlation-ID: {uuid}
```

Used for distributed tracing across services.

### Idempotency

**Header**:
```
X-Idempotency-Key: {uuid}
```

Idempotency keys are valid for 24 hours.

**Endpoints Requiring Idempotency-Key**:
- POST /api/v1/transactions (create transaction)
- POST /api/v1/families (create family)
- POST /api/v1/families/{familyId}/invitations (create invitation)
- POST /api/v1/savings-goals (create savings goal)
- POST /api/v1/budgets (create budget)
- POST /api/v1/bills (create bill)
- POST /api/v1/allocation/recommendation/{id}/approve (approve allocation)
- POST /api/v1/allocation/recommendation/{id}/modify (modify allocation)
- POST /api/v1/bills/{billId}/mark-paid (mark bill paid)

**Rationale**: These endpoints create or modify critical financial data and must not be duplicated due to network retries.

### Pagination

**Standardized Model**:
```
page: Integer (default: 1, min: 1)
limit: Integer (default: 20, min: 1, max: 100)
sort: {field}:{direction} (optional)
```

**Sort Direction**: `asc` or `desc`

**Response Headers**:
```
X-Total-Count: {total}
X-Page: {current_page}
X-Page-Count: {total_pages}
```

**Default Page Size**: 20
**Maximum Page Size**: 100
**Total Count**: Always returned
**Next Page Behavior**: Client increments page parameter

### Sorting

**Query Parameter**:
```
sort: {field}:{direction}
```

**Example**: `sort=createdAt:desc`

### Filtering

**Standardized Model**:
```
filter[{field}]: {value}
```

**Supported Fields**:
- Transactions: category, type, status, familyMemberId, amountFrom, amountTo, dateFrom, dateTo
- Bills: status, category, dueDateFrom, dueDateTo
- Notifications: read, type
- Budgets: period
- Savings Goals: status, priority

**Example**: `filter[status]=COMPLETED&filter[dateFrom]=2026-01-01`

### Validation

**Request validation**:
- Schema validation
- Business rule validation
- Authorization validation

**Error Response**: 422 Unprocessable Entity

### Error Handling

**Standard error structure** (see Error Model section)

### Rate Limiting

**Default Limits**:
- Authenticated: 100 requests/minute
- Unauthenticated: 10 requests/minute
- AI chat: 20 requests/minute

**Headers**:
```
X-RateLimit-Limit: {limit}
X-RateLimit-Remaining: {remaining}
X-RateLimit-Reset: {timestamp}
```

### Audit Requirements

**Audit Headers**:
```
X-Audit-User: {userId}
X-Audit-Action: {action}
X-Audit-Resource: {resource}
```

All write operations are audited.

---

## 3. Authentication APIs

### POST /api/v1/auth/register

**Purpose**: Register a new user

**Authentication**: None

**Authorization**: None

**Request**:
```json
{
  "email": "string (email, required)",
  "phoneNumber": "string (phone, optional)",
  "password": "string (min 8 chars, required)",
  "firstName": "string (required)",
  "lastName": "string (required)",
  "locale": "string (default: en-US)"
}
```

**Response** (201):
```json
{
  "userId": "uuid",
  "email": "string",
  "phoneNumber": "string",
  "firstName": "string",
  "lastName": "string",
  "createdAt": "datetime",
  "requiresVerification": true
}
```

**Validation**:
- Email must be valid and unique
- Phone number must be valid format if provided
- Password must be at least 8 characters
- First name and last name required

**Errors**:
- 400: Invalid request
- 409: Email already exists
- 422: Validation error

**Idempotency**: No

**Audit**: Yes

**Related User Story**: US-AUTH-001

**Related Business Rule**: BR-USER-001

---

### POST /api/v1/auth/login

**Purpose**: Login with email/password

**Authentication**: None

**Authorization**: None

**Request**:
```json
{
  "email": "string (email, required)",
  "password": "string (required)",
  "deviceId": "string (optional)"
}
```

**Response** (200):
```json
{
  "accessToken": "string",
  "refreshToken": "string",
  "tokenType": "Bearer",
  "expiresIn": 900,
  "user": {
    "userId": "uuid",
    "email": "string",
    "firstName": "string",
    "lastName": "string",
    "familyId": "uuid (nullable)"
  }
}
```

**Validation**:
- Email must be valid
- Password required
- Account must be verified

**Errors**:
- 400: Invalid request
- 401: Invalid credentials
- 403: Account locked
- 422: Validation error

**Idempotency**: No

**Audit**: Yes

**Related User Story**: US-AUTH-002

**Related Business Rule**: BR-USER-002

---

### POST /api/v1/auth/otp/request

**Purpose**: Request OTP for verification

**Authentication**: None (if email provided) or Bearer token

**Authorization**: None

**Request**:
```json
{
  "email": "string (email, optional)",
  "phoneNumber": "string (phone, optional)",
  "purpose": "REGISTRATION | LOGIN | RESET"
}
```

**Response** (200):
```json
{
  "otpId": "uuid",
  "expiresAt": "datetime",
  "resendAfter": 60
}
```

**Validation**:
- Email or phone number required
- Purpose must be valid

**Errors**:
- 400: Invalid request
- 422: Validation error
- 429: Too many requests

**Idempotency**: No

**Audit**: Yes

**Related User Story**: US-AUTH-003

**Related Business Rule**: BR-USER-003

---

### POST /api/v1/auth/otp/verify

**Purpose**: Verify OTP code

**Authentication**: None

**Authorization**: None

**Request**:
```json
{
  "otpId": "uuid (required)",
  "code": "string (6 digits, required)",
  "email": "string (optional)",
  "phoneNumber": "string (optional)"
}
```

**Response** (200):
```json
{
  "verified": true,
  "accessToken": "string (if login purpose)",
  "refreshToken": "string (if login purpose)"
}
```

**Validation**:
- OTP ID required
- Code must be 6 digits
- Code must match

**Errors**:
- 400: Invalid request
- 401: Invalid or expired OTP
- 422: Validation error

**Idempotency**: No

**Audit**: Yes

**Related User Story**: US-AUTH-004

**Related Business Rule**: BR-USER-004

---

### POST /api/v1/auth/refresh

**Purpose**: Refresh access token

**Authentication**: None

**Authorization**: None

**Request**:
```json
{
  "refreshToken": "string (required)"
}
```

**Response** (200):
```json
{
  "accessToken": "string",
  "refreshToken": "string",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

**Validation**:
- Refresh token required
- Refresh token must be valid

**Errors**:
- 401: Invalid or expired refresh token
- 422: Validation error

**Idempotency**: Yes

**Audit**: Yes

**Related User Story**: US-AUTH-005

**Related Business Rule**: BR-USER-005

---

### POST /api/v1/auth/logout

**Purpose**: Logout current session

**Authentication**: Bearer token

**Authorization**: USER

**Request**: None

**Response** (204): No content

**Validation**: Valid access token

**Errors**:
- 401: Invalid token
- 422: Validation error

**Idempotency**: Yes

**Audit**: Yes

**Related User Story**: US-AUTH-006

**Related Business Rule**: BR-USER-006

---

### GET /api/v1/auth/sessions

**Purpose**: Get user's active sessions

**Authentication**: Bearer token

**Authorization**: USER

**Request**: None

**Response** (200):
```json
{
  "sessions": [
    {
      "sessionId": "uuid",
      "deviceId": "string",
      "deviceName": "string",
      "lastActiveAt": "datetime",
      "createdAt": "datetime",
      "current": true
    }
  ]
}
```

**Validation**: Valid access token

**Errors**:
- 401: Invalid token

**Idempotency**: Yes

**Audit**: No

**Related User Story**: US-AUTH-007

**Related Business Rule**: BR-USER-007

---

### DELETE /api/v1/auth/sessions/{id}

**Purpose**: Revoke a specific session

**Authentication**: Bearer token

**Authorization**: USER

**Request**: None

**Response** (204): No content

**Validation**:
- Valid access token
- Session must belong to user

**Errors**:
- 401: Invalid token
- 403: Session not owned by user
- 404: Session not found

**Idempotency**: Yes

**Audit**: Yes

**Related User Story**: US-AUTH-008

**Related Business Rule**: BR-USER-008

---

## 4. User APIs

### GET /api/v1/users/me

**Purpose**: Get current user profile

**Authentication**: Bearer token

**Authorization**: USER

**Request**: None

**Response** (200):
```json
{
  "userId": "uuid",
  "email": "string",
  "phoneNumber": "string",
  "firstName": "string",
  "lastName": "string",
  "familyId": "uuid (nullable)",
  "familyRole": "OWNER | MEMBER | RESTRICTED (nullable)",
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

**Validation**: Valid access token

**Errors**:
- 401: Invalid token

**Idempotency**: Yes

**Audit**: No

**Related User Story**: US-USER-001

**Related Business Rule**: BR-USER-009

---

### PUT /api/v1/users/me

**Purpose**: Update current user profile

**Authentication**: Bearer token

**Authorization**: USER

**Request**:
```json
{
  "firstName": "string (optional)",
  "lastName": "string (optional)",
  "phoneNumber": "string (optional)"
}
```

**Response** (200):
```json
{
  "userId": "uuid",
  "email": "string",
  "phoneNumber": "string",
  "firstName": "string",
  "lastName": "string",
  "familyId": "uuid (nullable)",
  "familyRole": "string (nullable)",
  "updatedAt": "datetime"
}
```

**Validation**:
- At least one field must be provided
- Phone number must be valid format if provided

**Errors**:
- 401: Invalid token
- 409: Phone number already exists
- 422: Validation error

**Idempotency**: Yes

**Audit**: Yes

**Related User Story**: US-USER-002

**Related Business Rule**: BR-USER-010

---

### DELETE /api/v1/users/me

**Purpose**: Delete current user account

**Authentication**: Bearer token

**Authorization**: USER

**Request**:
```json
{
  "confirmation": "DELETE_MY_ACCOUNT"
}
```

**Response** (204): No content

**Validation**:
- Confirmation string must match exactly
- User must not be family owner with members

**Errors**:
- 401: Invalid token
- 403: Cannot delete family owner with members
- 422: Validation error

**Idempotency**: Yes

**Audit**: Yes

**Related User Story**: US-USER-003

**Related Business Rule**: BR-USER-011

---

### GET /api/v1/users/me/preferences

**Purpose**: Get user preferences

**Authentication**: Bearer token

**Authorization**: USER

**Request**: None

**Response** (200):
```json
{
  "userId": "uuid",
  "locale": "string",
  "currency": "string",
  "timezone": "string",
  "notificationPreferences": {
    "budgetAlerts": true,
    "billReminders": true,
    "savingsUpdates": true,
    "financialHealthUpdates": true
  },
  "aiPreferences": {
    "responseStyle": "CONCISE | DETAILED",
    "language": "string"
  }
}
```

**Validation**: Valid access token

**Errors**:
- 401: Invalid token

**Idempotency**: Yes

**Audit**: No

**Related User Story**: US-USER-004

**Related Business Rule**: BR-USER-012

---

### PUT /api/v1/users/me/preferences

**Purpose**: Update user preferences

**Authentication**: Bearer token

**Authorization**: USER

**Request**:
```json
{
  "locale": "string (optional)",
  "currency": "string (optional)",
  "timezone": "string (optional)",
  "notificationPreferences": {
    "budgetAlerts": "boolean (optional)",
    "billReminders": "boolean (optional)",
    "savingsUpdates": "boolean (optional)",
    "financialHealthUpdates": "boolean (optional)"
  },
  "aiPreferences": {
    "responseStyle": "CONCISE | DETAILED (optional)",
    "language": "string (optional)"
  }
}
```

**Response** (200):
```json
{
  "userId": "uuid",
  "locale": "string",
  "currency": "string",
  "timezone": "string",
  "notificationPreferences": {},
  "aiPreferences": {},
  "updatedAt": "datetime"
}
```

**Validation**:
- At least one field must be provided
- Values must be valid

**Errors**:
- 401: Invalid token
- 422: Validation error

**Idempotency**: Yes

**Audit**: Yes

**Related User Story**: US-USER-005

**Related Business Rule**: BR-USER-013

---

### GET /api/v1/users/me/devices

**Purpose**: Get user's registered devices

**Authentication**: Bearer token

**Authorization**: USER

**Request**: None

**Response** (200):
```json
{
  "devices": [
    {
      "deviceId": "uuid",
      "deviceName": "string",
      "deviceType": "ANDROID | IOS | WEB",
      "lastActiveAt": "datetime",
      "registeredAt": "datetime"
    }
  ]
}
```

**Validation**: Valid access token

**Errors**:
- 401: Invalid token

**Idempotency**: Yes

**Audit**: No

**Related User Story**: US-USER-006

**Related Business Rule**: BR-USER-014

---

## 5. Family APIs

### POST /api/v1/families

**Purpose**: Create a new family

**Authentication**: Bearer token

**Authorization**: USER

**Request**:
```json
{
  "name": "string (required, max 100 chars)",
  "currency": "string (default: INR)"
}
```

**Response** (201):
```json
{
  "familyId": "uuid",
  "name": "string",
  "currency": "string",
  "ownerId": "uuid",
  "memberCount": 1,
  "createdAt": "datetime"
}
```

**Validation**:
- Name required
- User must not already belong to a family

**Errors**:
- 401: Invalid token
- 409: User already belongs to a family
- 422: Validation error

**Idempotency**: No

**Audit**: Yes

**Related User Story**: US-FAM-001

**Related Business Rule**: BR-FAM-001

---

### GET /api/v1/families/{familyId}

**Purpose**: Get family details

**Authentication**: Bearer token

**Authorization**: FAMILY_OWNER, FAMILY_MEMBER, RESTRICTED

**Request**: None

**Response** (200):
```json
{
  "familyId": "uuid",
  "name": "string",
  "currency": "string",
  "ownerId": "uuid",
  "memberCount": 3,
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

**Validation**:
- Valid access token
- User must be family member

**Errors**:
- 401: Invalid token
- 403: Not a family member
- 404: Family not found

**Idempotency**: Yes

**Audit**: No

**Related User Story**: US-FAM-002

**Related Business Rule**: BR-FAM-002

---

### PUT /api/v1/families/{familyId}

**Purpose**: Update family details

**Authentication**: Bearer token

**Authorization**: FAMILY_OWNER

**Request**:
```json
{
  "name": "string (optional, max 100 chars)",
  "currency": "string (optional)"
}
```

**Response** (200):
```json
{
  "familyId": "uuid",
  "name": "string",
  "currency": "string",
  "ownerId": "uuid",
  "memberCount": 3,
  "updatedAt": "datetime"
}
```

**Validation**:
- At least one field must be provided
- User must be family owner

**Errors**:
- 401: Invalid token
- 403: Not family owner
- 404: Family not found
- 422: Validation error

**Idempotency**: Yes

**Audit**: Yes

**Related User Story**: US-FAM-003

**Related Business Rule**: BR-FAM-003

---

### DELETE /api/v1/families/{familyId}

**Purpose**: Delete family

**Authentication**: Bearer token

**Authorization**: FAMILY_OWNER

**Request**:
```json
{
  "confirmation": "DELETE_FAMILY"
}
```

**Response** (204): No content

**Validation**:
- Confirmation string must match exactly
- User must be family owner
- Family must have only owner as member

**Errors**:
- 401: Invalid token
- 403: Not family owner or family has other members
- 404: Family not found
- 422: Validation error

**Idempotency**: Yes

**Audit**: Yes

**Related User Story**: US-FAM-004

**Related Business Rule**: BR-FAM-004

---

### POST /api/v1/families/{familyId}/invitations

**Purpose**: Invite member to family

**Authentication**: Bearer token

**Authorization**: FAMILY_OWNER, FAMILY_MEMBER

**Request**:
```json
{
  "email": "string (email, required)",
  "role": "MEMBER | RESTRICTED (default: MEMBER)"
}
```

**Response** (201):
```json
{
  "invitationId": "uuid",
  "token": "string",
  "email": "string",
  "role": "MEMBER | RESTRICTED",
  "expiresAt": "datetime",
  "createdAt": "datetime"
}
```

**Validation**:
- Email required
- User must be family owner or member
- Email must not already be a member

**Errors**:
- 401: Invalid token
- 403: Not family owner or member
- 404: Family not found
- 409: Email already a member
- 422: Validation error

**Idempotency**: No

**Audit**: Yes

**Related User Story**: US-FAM-005

**Related Business Rule**: BR-FAM-005

---

### GET /api/v1/families/{familyId}/members

**Purpose**: Get family members

**Authentication**: Bearer token

**Authorization**: FAMILY_OWNER, FAMILY_MEMBER, RESTRICTED

**Request**: None

**Response** (200):
```json
{
  "members": [
    {
      "memberId": "uuid",
      "userId": "uuid",
      "firstName": "string",
      "lastName": "string",
      "email": "string",
      "role": "OWNER | MEMBER | RESTRICTED",
      "joinedAt": "datetime"
    }
  ]
}
```

**Validation**:
- Valid access token
- User must be family member

**Errors**:
- 401: Invalid token
- 403: Not a family member
- 404: Family not found

**Idempotency**: Yes

**Audit**: No

**Related User Story**: US-FAM-006

**Related Business Rule**: BR-FAM-006

---

### PUT /api/v1/families/{familyId}/members/{memberId}

**Purpose**: Update family member role

**Authentication**: Bearer token

**Authorization**: FAMILY_OWNER

**Request**:
```json
{
  "role": "MEMBER | RESTRICTED (required)"
}
```

**Response** (200):
```json
{
  "memberId": "uuid",
  "userId": "uuid",
  "role": "MEMBER | RESTRICTED",
  "updatedAt": "datetime"
}
```

**Validation**:
- Role required
- User must be family owner
- Cannot change owner role

**Errors**:
- 401: Invalid token
- 403: Not family owner
- 404: Family or member not found
- 422: Validation error

**Idempotency**: Yes

**Audit**: Yes

**Related User Story**: US-FAM-007

**Related Business Rule**: BR-FAM-007

---

### DELETE /api/v1/families/{familyId}/members/{memberId}

**Purpose**: Remove family member

**Authentication**: Bearer token

**Authorization**: FAMILY_OWNER

**Request**: None

**Response** (204): No content

**Validation**:
- User must be family owner
- Cannot remove owner

**Errors**:
- 401: Invalid token
- 403: Not family owner
- 404: Family or member not found
- 422: Cannot remove owner

**Idempotency**: Yes

**Audit**: Yes

**Related User Story**: US-FAM-008

**Related Business Rule**: BR-FAM-008

---

### POST /api/v1/family-invitations/{token}/accept

**Purpose**: Accept family invitation

**Authentication**: Bearer token

**Authorization**: USER

**Request**: None

**Response** (200):
```json
{
  "familyId": "uuid",
  "memberId": "uuid",
  "role": "MEMBER | RESTRICTED",
  "joinedAt": "datetime"
}
```

**Validation**:
- Valid access token
- Token must be valid
- User must not already belong to a family

**Errors**:
- 401: Invalid token
- 404: Invitation not found or expired
- 409: User already belongs to a family
- 422: Validation error

**Idempotency**: No

**Audit**: Yes

**Related User Story**: US-FAM-009

**Related Business Rule**: BR-FAM-009

---

### POST /api/v1/family-invitations/{token}/reject

**Purpose**: Reject family invitation

**Authentication**: Bearer token

**Authorization**: USER

**Request**: None

**Response** (204): No content

**Validation**:
- Valid access token
- Token must be valid

**Errors**:
- 401: Invalid token
- 404: Invitation not found or expired
- 422: Validation error

**Idempotency**: Yes

**Audit**: Yes

**Related User Story**: US-FAM-010

**Related Business Rule**: BR-FAM-010

---

## 6. Financial Overview APIs

### GET /api/v1/financial-overview

**Purpose**: Get complete financial overview

**Authentication**: Bearer token

**Authorization**: USER

**Header**: `X-Family-ID: {familyId}` (optional)

**Request**: None

**Response** (200):
```json
{
  "overviewId": "uuid",
  "userId": "uuid",
  "familyId": "uuid (nullable)",
  "planningIncome": "decimal",
  "mandatoryCommitments": "decimal",
  "essentialAllocation": "decimal",
  "savingsAllocation": "decimal",
  "emergencyAllocation": "decimal",
  "discretionaryPlanning": "decimal",
  "committedAmount": "decimal",
  "pendingPayments": "decimal",
  "actualTransactions": "decimal",
  "availableFinancialCapacity": "decimal",
  "currency": "string",
  "period": "string (YYYY-MM)",
  "calculatedAt": "datetime",
  "disclaimer": "This is a planning/management representation, not actual funds held by NeoWallet"
}
```

**Validation**:
- Valid access token
- If family ID provided, user must be family member

**Errors**:
- 401: Invalid token
- 403: Not family member
- 404: No financial data available

**Idempotency**: Yes

**Audit**: No

**Related User Story**: US-FIN-001

**Related Business Rule**: BR-FIN-001

**Important**: These are planning/management representations and must not be presented as NeoWallet-held funds.

---

### GET /api/v1/financial-overview/summary

**Purpose**: Get financial overview summary

**Authentication**: Bearer token

**Authorization**: USER

**Header**: `X-Family-ID: {familyId}` (optional)

**Request**: None

**Response** (200):
```json
{
  "totalIncome": "decimal",
  "totalExpenses": "decimal",
  "totalSavings": "decimal",
  "availableCapacity": "decimal",
  "budgetAdherence": "decimal",
  "currency": "string",
  "period": "string (YYYY-MM)"
}
```

**Validation**: Valid access token

**Errors**:
- 401: Invalid token
- 403: Not family member
- 404: No financial data available

**Idempotency**: Yes

**Audit**: No

**Related User Story**: US-FIN-002

**Related Business Rule**: BR-FIN-002

---

### GET /api/v1/financial-overview/allocations

**Purpose**: Get allocation breakdown

**Authentication**: Bearer token

**Authorization**: USER

**Header**: `X-Family-ID: {familyId}` (optional)

**Request**: None

**Response** (200):
```json
{
  "allocations": [
    {
      "category": "string",
      "allocatedAmount": "decimal",
      "actualAmount": "decimal",
      "utilizationPercentage": "decimal",
      "currency": "string"
    }
  ],
  "currency": "string",
  "period": "string (YYYY-MM)"
}
```

**Validation**: Valid access token

**Errors**:
- 401: Invalid token
- 403: Not family member
- 404: No financial data available

**Idempotency**: Yes

**Audit**: No

**Related User Story**: US-FIN-003

**Related Business Rule**: BR-FIN-003

---

### GET /api/v1/financial-overview/commitments

**Purpose**: Get mandatory commitments

**Authentication**: Bearer token

**Authorization**: USER

**Header**: `X-Family-ID: {familyId}` (optional)

**Request**: None

**Response** (200):
```json
{
  "commitments": [
    {
      "type": "BILL | LOAN | SUBSCRIPTION",
      "name": "string",
      "amount": "decimal",
      "dueDate": "date",
      "currency": "string"
    }
  ],
  "totalCommitments": "decimal",
  "currency": "string"
}
```

**Validation**: Valid access token

**Errors**:
- 401: Invalid token
- 403: Not family member
- 404: No financial data available

**Idempotency**: Yes

**Audit**: No

**Related User Story**: US-FIN-004

**Related Business Rule**: BR-FIN-004

---

### GET /api/v1/financial-overview/pending

**Purpose**: Get pending payments

**Authentication**: Bearer token

**Authorization**: USER

**Header**: `X-Family-ID: {familyId}` (optional)

**Request**: None

**Response** (200):
```json
{
  "pendingPayments": [
    {
      "paymentId": "uuid",
      "type": "BILL | TRANSFER",
      "amount": "decimal",
      "dueDate": "date",
      "currency": "string"
    }
  ],
  "totalPending": "decimal",
  "currency": "string"
}
```

**Validation**: Valid access token

**Errors**:
- 401: Invalid token
- 403: Not family member
- 404: No financial data available

**Idempotency**: Yes

**Audit**: No

**Related User Story**: US-FIN-005

**Related Business Rule**: BR-FIN-005

---

### GET /api/v1/financial-overview/capacity

**Purpose**: Get available financial capacity

**Authentication**: Bearer token

**Authorization**: USER

**Header**: `X-Family-ID: {familyId}` (optional)

**Request**: None

**Response** (200):
```json
{
  "availableFinancialCapacity": "decimal",
  "planningIncome": "decimal",
  "committedAmount": "decimal",
  "pendingPayments": "decimal",
  "currency": "string",
  "period": "string (YYYY-MM)",
  "calculatedAt": "datetime"
}
```

**Validation**: Valid access token

**Errors**:
- 401: Invalid token
- 403: Not family member
- 404: No financial data available

**Idempotency**: Yes

**Audit**: No

**Related User Story**: US-FIN-006

**Related Business Rule**: BR-FIN-006

---

## 7. Transaction APIs

### POST /api/v1/transactions

**Purpose**: Create a transaction

**Authentication**: Bearer token

**Authorization**: USER

**Header**: `X-Family-ID: {familyId}` (optional)

**Request**:
```json
{
  "type": "INCOME | EXPENSE | REFUND | ADJUSTMENT | TRANSFER_RECORD (required)",
  "category": "string (required)",
  "amount": "decimal (required, > 0)",
  "currency": "string (default: family currency)",
  "description": "string (optional)",
  "transactionDate": "date (default: today)",
  "status": "PLANNED | COMMITTED | PENDING | COMPLETED (default: PLANNED)",
  "isRecurring": "boolean (default: false)",
  "familyMemberId": "uuid (optional)"
}
```

**Response** (201):
```json
{
  "transactionId": "uuid",
  "userId": "uuid",
  "familyId": "uuid (nullable)",
  "type": "string",
  "category": "string",
  "amount": "decimal",
  "currency": "string",
  "description": "string",
  "transactionDate": "date",
  "status": "string",
  "isRecurring": "boolean",
  "familyMemberId": "uuid (nullable)",
  "createdAt": "datetime"
}
```

**Validation**:
- Type required
- Category required
- Amount required and > 0
- If family ID provided, user must be family member

**Errors**:
- 401: Invalid token
- 403: Not family member
- 422: Validation error

**Idempotency**: Yes

**Audit**: Yes

**Related User Story**: US-TRX-001

**Related Business Rule**: BR-TRX-001

---

### GET /api/v1/transactions

**Purpose**: Get transactions with filtering

**Authentication**: Bearer token

**Authorization**: USER

**Header**: `X-Family-ID: {familyId}` (optional)

**Query Parameters**:
```
page: Integer (default: 1)
limit: Integer (default: 20, max: 100)
filter[category]: String
filter[type]: INCOME | EXPENSE | REFUND | ADJUSTMENT | TRANSFER_RECORD
filter[status]: PLANNED | COMMITTED | PENDING | COMPLETED | FAILED | REVERSED
filter[familyMemberId]: uuid
filter[amountFrom]: decimal
filter[amountTo]: decimal
filter[dateFrom]: date
filter[dateTo]: date
sort: createdAt:desc | amount:asc | amount:desc
```

**Response** (200):
```json
{
  "transactions": [
    {
      "transactionId": "uuid",
      "userId": "uuid",
      "familyId": "uuid (nullable)",
      "type": "string",
      "category": "string",
      "amount": "decimal",
      "currency": "string",
      "description": "string",
      "transactionDate": "date",
      "status": "string",
      "isRecurring": "boolean",
      "familyMemberId": "uuid (nullable)",
      "createdAt": "datetime"
    }
  ],
  "pagination": {
    "page": 1,
    "limit": 20,
    "totalCount": 100,
    "pageCount": 5
  }
}
```

**Validation**: Valid access token

**Errors**:
- 401: Invalid token
- 403: Not family member
- 422: Validation error

**Idempotency**: Yes

**Audit**: No

**Related User Story**: US-TRX-002

**Related Business Rule**: BR-TRX-002

---

### GET /api/v1/transactions/{transactionId}

**Purpose**: Get transaction by ID

**Authentication**: Bearer token

**Authorization**: USER

**Request**: None

**Response** (200):
```json
{
  "transactionId": "uuid",
  "userId": "uuid",
  "familyId": "uuid (nullable)",
  "type": "string",
  "category": "string",
  "amount": "decimal",
  "currency": "string",
  "description": "string",
  "transactionDate": "date",
  "status": "string",
  "isRecurring": "boolean",
  "familyMemberId": "uuid (nullable)",
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

**Validation**:
- Valid access token
- User must own transaction or be family member

**Errors**:
- 401: Invalid token
- 403: Not authorized
- 404: Transaction not found

**Idempotency**: Yes

**Audit**: No

**Related User Story**: US-TRX-003

**Related Business Rule**: BR-TRX-003

---

### PUT /api/v1/transactions/{transactionId}

**Purpose**: Update transaction

**Authentication**: Bearer token

**Authorization**: USER

**Request**:
```json
{
  "category": "string (optional)",
  "amount": "decimal (optional, > 0)",
  "description": "string (optional)",
  "transactionDate": "date (optional)",
  "status": "string (optional)",
  "isRecurring": "boolean (optional)"
}
```

**Response** (200):
```json
{
  "transactionId": "uuid",
  "userId": "uuid",
  "familyId": "uuid (nullable)",
  "type": "string",
  "category": "string",
  "amount": "decimal",
  "currency": "string",
  "description": "string",
  "transactionDate": "date",
  "status": "string",
  "isRecurring": "boolean",
  "familyMemberId": "uuid (nullable)",
  "updatedAt": "datetime"
}
```

**Validation**:
- At least one field must be provided
- User must own transaction or be family owner
- Cannot change transaction type

**Errors**:
- 401: Invalid token
- 403: Not authorized
- 404: Transaction not found
- 422: Validation error

**Idempotency**: Yes

**Audit**: Yes

**Related User Story**: US-TRX-004

**Related Business Rule**: BR-TRX-004

---

### DELETE /api/v1/transactions/{transactionId}

**Purpose**: Delete transaction

**Authentication**: Bearer token

**Authorization**: USER

**Request**: None

**Response** (204): No content

**Validation**:
- User must own transaction or be family owner
- Cannot delete COMPLETED transactions

**Errors**:
- 401: Invalid token
- 403: Not authorized
- 404: Transaction not found
- 422: Cannot delete completed transaction

**Idempotency**: Yes

**Audit**: Yes

**Related User Story**: US-TRX-005

**Related Business Rule**: BR-TRX-005

---

## 8. Budget APIs

### POST /api/v1/budgets

**Purpose**: Create a budget

**Authentication**: Bearer token

**Authorization**: USER

**Header**: `X-Family-ID: {familyId}` (optional)

**Request**:
```json
{
  "name": "string (required, max 100 chars)",
  "period": "string (required, YYYY-MM)",
  "currency": "string (default: family currency)",
  "categories": [
    {
      "category": "string (required)",
      "limit": "decimal (required, > 0)",
      "priority": "ESSENTIAL | VARIABLE | DISCRETIONARY (default: VARIABLE)"
    }
  ]
}
```

**Response** (201):
```json
{
  "budgetId": "uuid",
  "userId": "uuid",
  "familyId": "uuid (nullable)",
  "name": "string",
  "period": "string",
  "currency": "string",
  "categories": [],
  "totalLimit": "decimal",
  "createdAt": "datetime"
}
```

**Validation**:
- Name required
- Period required
- At least one category required
- Category limits must be > 0

**Errors**:
- 401: Invalid token
- 403: Not family member
- 409: Budget already exists for period
- 422: Validation error

**Idempotency**: No

**Audit**: Yes

**Related User Story**: US-BUD-001

**Related Business Rule**: BR-BUD-001

---

### GET /api/v1/budgets

**Purpose**: Get budgets with filtering

**Authentication**: Bearer token

**Authorization**: USER

**Header**: `X-Family-ID: {familyId}` (optional)

**Query Parameters**:
```
page: Integer (default: 1)
limit: Integer (default: 20, max: 100)
filter[period]: string (YYYY-MM)
sort: period:desc | createdAt:desc
```

**Response** (200):
```json
{
  "budgets": [
    {
      "budgetId": "uuid",
      "userId": "uuid",
      "familyId": "uuid (nullable)",
      "name": "string",
      "period": "string",
      "currency": "string",
      "totalLimit": "decimal",
      "totalSpent": "decimal",
      "utilizationPercentage": "decimal",
      "createdAt": "datetime"
    }
  ],
  "pagination": {
    "page": 1,
    "limit": 20,
    "totalCount": 50,
    "pageCount": 3
  }
}
```

**Validation**: Valid access token

**Errors**:
- 401: Invalid token
- 403: Not family member
- 422: Validation error

**Idempotency**: Yes

**Audit**: No

**Related User Story**: US-BUD-002

**Related Business Rule**: BR-BUD-002

---

### GET /api/v1/budgets/{budgetId}

**Purpose**: Get budget by ID

**Authentication**: Bearer token

**Authorization**: USER

**Request**: None

**Response** (200):
```json
{
  "budgetId": "uuid",
  "userId": "uuid",
  "familyId": "uuid (nullable)",
  "name": "string",
  "period": "string",
  "currency": "string",
  "categories": [
    {
      "category": "string",
      "limit": "decimal",
      "spent": "decimal",
      "utilizationPercentage": "decimal",
      "priority": "string"
    }
  ],
  "totalLimit": "decimal",
  "totalSpent": "decimal",
  "utilizationPercentage": "decimal",
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

**Validation**:
- Valid access token
- User must own budget or be family member

**Errors**:
- 401: Invalid token
- 403: Not authorized
- 404: Budget not found

**Idempotency**: Yes

**Audit**: No

**Related User Story**: US-BUD-003

**Related Business Rule**: BR-BUD-003

---

### PUT /api/v1/budgets/{budgetId}

**Purpose**: Update budget

**Authentication**: Bearer token

**Authorization**: USER

**Request**:
```json
{
  "name": "string (optional, max 100 chars)",
  "categories": [
    {
      "category": "string (required)",
      "limit": "decimal (required, > 0)",
      "priority": "ESSENTIAL | VARIABLE | DISCRETIONARY (optional)"
    }
  ]
}
```

**Response** (200):
```json
{
  "budgetId": "uuid",
  "userId": "uuid",
  "familyId": "uuid (nullable)",
  "name": "string",
  "period": "string",
  "currency": "string",
  "categories": [],
  "totalLimit": "decimal",
  "updatedAt": "datetime"
}
```

**Validation**:
- At least one field must be provided
- User must own budget or be family owner
- Cannot change period

**Errors**:
- 401: Invalid token
- 403: Not authorized
- 404: Budget not found
- 422: Validation error

**Idempotency**: Yes

**Audit**: Yes

**Related User Story**: US-BUD-004

**Related Business Rule**: BR-BUD-004

---

### DELETE /api/v1/budgets/{budgetId}

**Purpose**: Delete budget

**Authentication**: Bearer token

**Authorization**: USER

**Request**: None

**Response** (204): No content

**Validation**:
- User must own budget or be family owner
- Cannot delete budget with committed transactions

**Errors**:
- 401: Invalid token
- 403: Not authorized
- 404: Budget not found
- 422: Cannot delete budget with committed transactions

**Idempotency**: Yes

**Audit**: Yes

**Related User Story**: US-BUD-005

**Related Business Rule**: BR-BUD-005

---

### GET /api/v1/budgets/{budgetId}/utilization

**Purpose**: Get budget utilization

**Authentication**: Bearer token

**Authorization**: USER

**Request**: None

**Response** (200):
```json
{
  "budgetId": "uuid",
  "period": "string",
  "currency": "string",
  "categories": [
    {
      "category": "string",
      "limit": "decimal",
      "spent": "decimal",
      "remaining": "decimal",
      "utilizationPercentage": "decimal",
      "status": "WITHIN_BUDGET | NEAR_LIMIT | OVER_BUDGET"
    }
  ],
  "totalLimit": "decimal",
  "totalSpent": "decimal",
  "totalRemaining": "decimal",
  "overallUtilizationPercentage": "decimal",
  "overallStatus": "WITHIN_BUDGET | NEAR_LIMIT | OVER_BUDGET",
  "calculatedAt": "datetime"
}
```

**Validation**:
- Valid access token
- User must own budget or be family member

**Errors**:
- 401: Invalid token
- 403: Not authorized
- 404: Budget not found

**Idempotency**: Yes

**Audit**: No

**Related User Story**: US-BUD-006

**Related Business Rule**: BR-BUD-006

---

### GET /api/v1/budgets/{budgetId}/forecast

**Purpose**: Get budget forecast

**Authentication**: Bearer token

**Authorization**: USER

**Request**: None

**Response** (200):
```json
{
  "budgetId": "uuid",
  "period": "string",
  "currency": "string",
  "forecast": {
    "projectedSpending": "decimal",
    "projectedRemaining": "decimal",
    "projectedUtilizationPercentage": "decimal",
    "projectedStatus": "WITHIN_BUDGET | NEAR_LIMIT | OVER_BUDGET",
    "confidence": "HIGH | MEDIUM | LOW",
    "basedOnHistoricalMonths": 3
  },
  "categories": [
    {
      "category": "string",
      "limit": "decimal",
      "projectedSpending": "decimal",
      "projectedRemaining": "decimal",
      "projectedUtilizationPercentage": "decimal"
    }
  ],
  "calculatedAt": "datetime"
}
```

**Validation**:
- Valid access token
- User must own budget or be family member

**Errors**:
- 401: Invalid token
- 403: Not authorized
- 404: Budget not found

**Idempotency**: Yes

**Audit**: No

**Related User Story**: US-BUD-007

**Related Business Rule**: BR-BUD-007

---

### GET /api/v1/budgets/recommendation

**Purpose**: Get budget recommendation

**Authentication**: Bearer token

**Authorization**: USER

**Header**: `X-Family-ID: {familyId}` (optional)

**Query Parameters**:
```
period: string (YYYY-MM, required)
```

**Response** (200):
```json
{
  "recommendationId": "uuid",
  "userId": "uuid",
  "familyId": "uuid (nullable)",
  "period": "string",
  "currency": "string",
  "recommendedAllocation": {
    "planningIncome": "decimal",
    "essentialAllocation": "decimal",
    "variableAllocation": "decimal",
    "savingsAllocation": "decimal",
    "emergencyAllocation": "decimal",
    "discretionaryPlanning": "decimal"
  },
  "categories": [
    {
      "category": "string",
      "recommendedLimit": "decimal",
      "basedOnHistoricalAverage": "decimal",
      "priority": "string"
    }
  ],
  "confidence": "HIGH | MEDIUM | LOW",
  "historicalMonthsUsed": 3,
  "generatedAt": "datetime",
  "disclaimer": "This is a deterministic recommendation based on historical data"
}
```

**Validation**:
- Period required
- Valid access token

**Errors**:
- 401: Invalid token
- 403: Not family member
- 422: Validation error

**Idempotency**: Yes

**Audit**: No

**Related User Story**: US-BUD-008

**Related Business Rule**: BR-BUD-008

**Important**: Budget recommendation must be deterministic.

---

## 9. Allocation APIs

### GET /api/v1/allocation/recommendation

**Purpose**: Get allocation recommendation

**Authentication**: Bearer token

**Authorization**: USER

**Header**: `X-Family-ID: {familyId}` (optional)

**Query Parameters**:
```
period: string (YYYY-MM, required)
```

**Response** (200):
```json
{
  "recommendationId": "uuid",
  "userId": "uuid",
  "familyId": "uuid (nullable)",
  "period": "string",
  "currency": "string",
  "systemRecommendation": {
    "planningIncome": "decimal",
    "essentialAllocation": "decimal",
    "variableAllocation": "decimal",
    "savingsAllocation": "decimal",
    "emergencyAllocation": "decimal",
    "discretionaryPlanning": "decimal"
  },
  "userModification": null,
  "finalApprovedAllocation": null,
  "status": "PENDING_APPROVAL",
  "generatedAt": "datetime"
}
```

**Validation**:
- Period required
- Valid access token

**Errors**:
- 401: Invalid token
- 403: Not family member
- 422: Validation error

**Idempotency**: Yes

**Audit**: No

**Related User Story**: US-ALL-001

**Related Business Rule**: BR-ALL-001

---

### POST /api/v1/allocation/recommendation/{id}/approve

**Purpose**: Approve allocation recommendation

**Authentication**: Bearer token

**Authorization**: USER

**Request**: None

**Response** (200):
```json
{
  "recommendationId": "uuid",
  "status": "APPROVED",
  "finalApprovedAllocation": {
    "planningIncome": "decimal",
    "essentialAllocation": "decimal",
    "variableAllocation": "decimal",
    "savingsAllocation": "decimal",
    "emergencyAllocation": "decimal",
    "discretionaryPlanning": "decimal"
  },
  "approvedAt": "datetime"
}
```

**Validation**:
- Valid access token
- User must own recommendation
- Recommendation must be in PENDING_APPROVAL status

**Errors**:
- 401: Invalid token
- 403: Not authorized
- 404: Recommendation not found
- 422: Invalid status

**Idempotency**: No

**Audit**: Yes

**Related User Story**: US-ALL-002

**Related Business Rule**: BR-ALL-002

---

### POST /api/v1/allocation/recommendation/{id}/modify

**Purpose**: Modify allocation recommendation

**Authentication**: Bearer token

**Authorization**: USER

**Request**:
```json
{
  "modifications": {
    "essentialAllocation": "decimal (optional)",
    "variableAllocation": "decimal (optional)",
    "savingsAllocation": "decimal (optional)",
    "emergencyAllocation": "decimal (optional)",
    "discretionaryPlanning": "decimal (optional)"
  }
}
```

**Response** (200):
```json
{
  "recommendationId": "uuid",
  "systemRecommendation": {},
  "userModification": {},
  "finalApprovedAllocation": null,
  "status": "PENDING_APPROVAL",
  "modifiedAt": "datetime"
}
```

**Validation**:
- At least one modification must be provided
- User must own recommendation
- Total must not exceed planning income

**Errors**:
- 401: Invalid token
- 403: Not authorized
- 404: Recommendation not found
- 422: Validation error

**Idempotency**: Yes

**Audit**: Yes

**Related User Story**: US-ALL-003

**Related Business Rule**: BR-ALL-003

---

### GET /api/v1/allocation/history

**Purpose**: Get allocation history

**Authentication**: Bearer token

**Authorization**: USER

**Header**: `X-Family-ID: {familyId}` (optional)

**Query Parameters**:
```
page: Integer (default: 1)
limit: Integer (default: 20, max: 100)
sort: period:desc
```

**Response** (200):
```json
{
  "history": [
    {
      "recommendationId": "uuid",
      "period": "string",
      "systemRecommendation": {},
      "userModification": {},
      "finalApprovedAllocation": {},
      "status": "APPROVED | REJECTED",
      "approvedAt": "datetime (nullable)"
    }
  ],
  "pagination": {
    "page": 1,
    "limit": 20,
    "totalCount": 50,
    "pageCount": 3
  }
}
```

**Validation**: Valid access token

**Errors**:
- 401: Invalid token
- 403: Not family member
- 422: Validation error

**Idempotency**: Yes

**Audit**: No

**Related User Story**: US-ALL-004

**Related Business Rule**: BR-ALL-004

**Important**: Preserve System Recommendation, User Modification, and Final Approved Allocation.

---

## 10. Savings APIs

### POST /api/v1/savings-goals

**Purpose**: Create savings goal

**Authentication**: Bearer token

**Authorization**: USER

**Header**: `X-Family-ID: {familyId}` (optional)

**Request**:
```json
{
  "name": "string (required, max 100 chars)",
  "targetAmount": "decimal (required, > 0)",
  "targetDate": "date (required)",
  "currentAmount": "decimal (default: 0)",
  "currency": "string (default: family currency)",
  "priority": "HIGH | MEDIUM | LOW (default: MEDIUM)",
  "category": "string (optional)"
}
```

**Response** (201):
```json
{
  "goalId": "uuid",
  "userId": "uuid",
  "familyId": "uuid (nullable)",
  "name": "string",
  "targetAmount": "decimal",
  "targetDate": "date",
  "currentAmount": "decimal",
  "currency": "string",
  "priority": "string",
  "category": "string",
  "status": "ACTIVE",
  "createdAt": "datetime"
}
```

**Validation**:
- Name required
- Target amount required and > 0
- Target date required
- Target date must be in future

**Errors**:
- 401: Invalid token
- 403: Not family member
- 422: Validation error

**Idempotency**: No

**Audit**: Yes

**Related User Story**: US-SAV-001

**Related Business Rule**: BR-SAV-001

---

### GET /api/v1/savings-goals

**Purpose**: Get savings goals

**Authentication**: Bearer token

**Authorization**: USER

**Header**: `X-Family-ID: {familyId}` (optional)

**Query Parameters**:
```
page: Integer (default: 1)
limit: Integer (default: 20, max: 100)
filter[status]: ACTIVE | COMPLETED | CANCELLED
filter[priority]: HIGH | MEDIUM | LOW
sort: targetDate:asc | priority:desc
```

**Response** (200):
```json
{
  "goals": [
    {
      "goalId": "uuid",
      "userId": "uuid",
      "familyId": "uuid (nullable)",
      "name": "string",
      "targetAmount": "decimal",
      "targetDate": "date",
      "currentAmount": "decimal",
      "currency": "string",
      "priority": "string",
      "category": "string",
      "status": "string",
      "progressPercentage": "decimal",
      "createdAt": "datetime"
    }
  ],
  "pagination": {
    "page": 1,
    "limit": 20,
    "totalCount": 30,
    "pageCount": 2
  }
}
```

**Validation**: Valid access token

**Errors**:
- 401: Invalid token
- 403: Not family member
- 422: Validation error

**Idempotency**: Yes

**Audit**: No

**Related User Story**: US-SAV-002

**Related Business Rule**: BR-SAV-002

---

### GET /api/v1/savings-goals/{goalId}

**Purpose**: Get savings goal by ID

**Authentication**: Bearer token

**Authorization**: USER

**Request**: None

**Response** (200):
```json
{
  "goalId": "uuid",
  "userId": "uuid",
  "familyId": "uuid (nullable)",
  "name": "string",
  "targetAmount": "decimal",
  "targetDate": "date",
  "currentAmount": "decimal",
  "currency": "string",
  "priority": "string",
  "category": "string",
  "status": "string",
  "progressPercentage": "decimal",
  "requiredMonthlyContribution": "decimal",
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

**Validation**:
- Valid access token
- User must own goal or be family member

**Errors**:
- 401: Invalid token
- 403: Not authorized
- 404: Goal not found

**Idempotency**: Yes

**Audit**: No

**Related User Story**: US-SAV-003

**Related Business Rule**: BR-SAV-003

---

### PUT /api/v1/savings-goals/{goalId}

**Purpose**: Update savings goal

**Authentication**: Bearer token

**Authorization**: USER

**Request**:
```json
{
  "name": "string (optional, max 100 chars)",
  "targetAmount": "decimal (optional, > 0)",
  "targetDate": "date (optional)",
  "currentAmount": "decimal (optional, >= 0)",
  "priority": "HIGH | MEDIUM | LOW (optional)",
  "category": "string (optional)",
  "status": "ACTIVE | COMPLETED | CANCELLED (optional)"
}
```

**Response** (200):
```json
{
  "goalId": "uuid",
  "userId": "uuid",
  "familyId": "uuid (nullable)",
  "name": "string",
  "targetAmount": "decimal",
  "targetDate": "date",
  "currentAmount": "decimal",
  "currency": "string",
  "priority": "string",
  "category": "string",
  "status": "string",
  "progressPercentage": "decimal",
  "updatedAt": "datetime"
}
```

**Validation**:
- At least one field must be provided
- User must own goal or be family owner
- Target date must be in future if modified

**Errors**:
- 401: Invalid token
- 403: Not authorized
- 404: Goal not found
- 422: Validation error

**Idempotency**: Yes

**Audit**: Yes

**Related User Story**: US-SAV-004

**Related Business Rule**: BR-SAV-004

---

### DELETE /api/v1/savings-goals/{goalId}

**Purpose**: Delete savings goal

**Authentication**: Bearer token

**Authorization**: USER

**Request**: None

**Response** (204): No content

**Validation**:
- User must own goal or be family owner

**Errors**:
- 401: Invalid token
- 403: Not authorized
- 404: Goal not found

**Idempotency**: Yes

**Audit**: Yes

**Related User Story**: US-SAV-005

**Related Business Rule**: BR-SAV-005

---

### GET /api/v1/savings-goals/{goalId}/progress

**Purpose**: Get savings goal progress

**Authentication**: Bearer token

**Authorization**: USER

**Request**: None

**Response** (200):
```json
{
  "goalId": "uuid",
  "currentAmount": "decimal",
  "targetAmount": "decimal",
  "progressPercentage": "decimal",
  "remainingAmount": "decimal",
  "monthsRemaining": 3,
  "requiredMonthlyContribution": "decimal",
  "actualMonthlyContribution": "decimal",
  "onTrack": true,
  "status": "ON_TRACK | BEHIND | AHEAD",
  "calculatedAt": "datetime"
}
```

**Validation**:
- Valid access token
- User must own goal or be family member

**Errors**:
- 401: Invalid token
- 403: Not authorized
- 404: Goal not found

**Idempotency**: Yes

**Audit**: No

**Related User Story**: US-SAV-006

**Related Business Rule**: BR-SAV-006

---

### GET /api/v1/savings-goals/{goalId}/forecast

**Purpose**: Get savings goal forecast

**Authentication**: Bearer token

**Authorization**: USER

**Request**: None

**Response** (200):
```json
{
  "goalId": "uuid",
  "forecast": {
    "projectedCompletionDate": "date",
    "projectedCompletionAmount": "decimal",
    "monthsToCompletion": 5,
    "confidence": "HIGH | MEDIUM | LOW"
  },
  "scenarios": [
    {
      "monthlyContribution": "decimal",
      "projectedCompletionDate": "date",
      "monthsToCompletion": 4
    }
  ],
  "calculatedAt": "datetime"
}
```

**Validation**:
- Valid access token
- User must own goal or be family member

**Errors**:
- 401: Invalid token
- 403: Not authorized
- 404: Goal not found

**Idempotency**: Yes

**Audit**: No

**Related User Story**: US-SAV-007

**Related Business Rule**: BR-SAV-007

---

### POST /api/v1/savings-goals/{goalId}/contributions

**Purpose**: Record a contribution to a savings goal

**Authentication**: Bearer token

**Authorization**: USER (individual owner or family OWNER)

**Header**: `X-Family-ID: {familyId}` (optional)

**Request**:
```json
{
  "amount": "decimal (required, > 0)",
  "contributionDate": "date (required)",
  "notes": "string (optional, max 500 chars)"
}
```

**Response** (201):
```json
{
  "contribution": {
    "contributionId": "uuid",
    "goalId": "uuid",
    "userId": "uuid",
    "amount": "decimal",
    "currency": "string",
    "contributionDate": "date",
    "notes": "string",
    "createdAt": "datetime"
  },
  "goal": {
    // SavingsGoal schema with updated currentAmount and progressPercentage
  }
}
```

**Validation**:
- Valid access token
- Goal must exist
- Amount must be > 0
- Contribution date required and cannot be in the future
- User must own goal or be family OWNER

**Errors**:
- 401: Invalid token
- 403: Not authorized
- 404: Goal not found
- 422: Validation error

**Idempotency**: No

**Audit**: Yes

**Related User Story**: US-SAV-003

**Related Business Rule**: BR-SAV-003

---

## 11. Bill APIs

### POST /api/v1/bills

**Purpose**: Create bill

**Authentication**: Bearer token

**Authorization**: USER

**Header**: `X-Family-ID: {familyId}` (optional)

**Request**:
```json
{
  "name": "string (required, max 100 chars)",
  "amount": "decimal (required, > 0)",
  "currency": "string (default: family currency)",
  "dueDate": "date (required)",
  "category": "string (optional)",
  "isRecurring": "boolean (default: false)",
  "recurringPeriod": "MONTHLY | WEEKLY | YEARLY (optional)",
  "vendor": "string (optional)",
  "notes": "string (optional)"
}
```

**Response** (201):
```json
{
  "billId": "uuid",
  "userId": "uuid",
  "familyId": "uuid (nullable)",
  "name": "string",
  "amount": "decimal",
  "currency": "string",
  "dueDate": "date",
  "category": "string",
  "isRecurring": "boolean",
  "recurringPeriod": "string (nullable)",
  "vendor": "string",
  "status": "PENDING",
  "createdAt": "datetime"
}
```

**Validation**:
- Name required
- Amount required and > 0
- Due date required
- Due date must be in future

**Errors**:
- 401: Invalid token
- 403: Not family member
- 422: Validation error

**Idempotency**: No

**Audit**: Yes

**Related User Story**: US-BILL-001

**Related Business Rule**: BR-BILL-001

**Important**: MVP bills are manual-entry/planning only.

---

### GET /api/v1/bills

**Purpose**: Get bills with filtering

**Authentication**: Bearer token

**Authorization**: USER

**Header**: `X-Family-ID: {familyId}` (optional)

**Query Parameters**:
```
page: Integer (default: 1)
limit: Integer (default: 20, max: 100)
filter[status]: PENDING | PAID | OVERDUE
filter[category]: string
filter[dueDateFrom]: date
filter[dueDateTo]: date
sort: dueDate:asc | amount:desc
```

**Response** (200):
```json
{
  "bills": [
    {
      "billId": "uuid",
      "userId": "uuid",
      "familyId": "uuid (nullable)",
      "name": "string",
      "amount": "decimal",
      "currency": "string",
      "dueDate": "date",
      "category": "string",
      "isRecurring": "boolean",
      "status": "string",
      "daysUntilDue": 5,
      "createdAt": "datetime"
    }
  ],
  "pagination": {
    "page": 1,
    "limit": 20,
    "totalCount": 40,
    "pageCount": 2
  }
}
```

**Validation**: Valid access token

**Errors**:
- 401: Invalid token
- 403: Not family member
- 422: Validation error

**Idempotency**: Yes

**Audit**: No

**Related User Story**: US-BILL-002

**Related Business Rule**: BR-BILL-002

---

### GET /api/v1/bills/{billId}

**Purpose**: Get bill by ID

**Authentication**: Bearer token

**Authorization**: USER

**Request**: None

**Response** (200):
```json
{
  "billId": "uuid",
  "userId": "uuid",
  "familyId": "uuid (nullable)",
  "name": "string",
  "amount": "decimal",
  "currency": "string",
  "dueDate": "date",
  "category": "string",
  "isRecurring": "boolean",
  "recurringPeriod": "string (nullable)",
  "vendor": "string",
  "notes": "string",
  "status": "string",
  "paidDate": "date (nullable)",
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

**Validation**:
- Valid access token
- User must own bill or be family member

**Errors**:
- 401: Invalid token
- 403: Not authorized
- 404: Bill not found

**Idempotency**: Yes

**Audit**: No

**Related User Story**: US-BILL-003

**Related Business Rule**: BR-BILL-003

---

### PUT /api/v1/bills/{billId}

**Purpose**: Update bill

**Authentication**: Bearer token

**Authorization**: USER

**Request**:
```json
{
  "name": "string (optional, max 100 chars)",
  "amount": "decimal (optional, > 0)",
  "dueDate": "date (optional)",
  "category": "string (optional)",
  "isRecurring": "boolean (optional)",
  "recurringPeriod": "MONTHLY | WEEKLY | YEARLY (optional)",
  "vendor": "string (optional)",
  "notes": "string (optional)"
}
```

**Response** (200):
```json
{
  "billId": "uuid",
  "userId": "uuid",
  "familyId": "uuid (nullable)",
  "name": "string",
  "amount": "decimal",
  "currency": "string",
  "dueDate": "date",
  "category": "string",
  "isRecurring": "boolean",
  "recurringPeriod": "string (nullable)",
  "vendor": "string",
  "notes": "string",
  "status": "string",
  "updatedAt": "datetime"
}
```

**Validation**:
- At least one field must be provided
- User must own bill or be family owner
- Cannot modify status (use mark-paid endpoint)

**Errors**:
- 401: Invalid token
- 403: Not authorized
- 404: Bill not found
- 422: Validation error

**Idempotency**: Yes

**Audit**: Yes

**Related User Story**: US-BILL-004

**Related Business Rule**: BR-BILL-004

---

### DELETE /api/v1/bills/{billId}

**Purpose**: Delete bill

**Authentication**: Bearer token

**Authorization**: USER

**Request**: None

**Response** (204): No content

**Validation**:
- User must own bill or be family owner

**Errors**:
- 401: Invalid token
- 403: Not authorized
- 404: Bill not found

**Idempotency**: Yes

**Audit**: Yes

**Related User Story**: US-BILL-005

**Related Business Rule**: BR-BILL-005

---

### POST /api/v1/bills/{billId}/mark-paid

**Purpose**: Mark bill as paid

**Authentication**: Bearer token

**Authorization**: USER

**Request**:
```json
{
  "paidDate": "date (default: today)",
  "paymentMethod": "string (optional)",
  "notes": "string (optional)"
}
```

**Response** (200):
```json
{
  "billId": "uuid",
  "status": "PAID",
  "paidDate": "date",
  "paymentMethod": "string (nullable)",
  "notes": "string (nullable)",
  "updatedAt": "datetime"
}
```

**Validation**:
- User must own bill or be family member
- Bill must be in PENDING or OVERDUE status

**Errors**:
- 401: Invalid token
- 403: Not authorized
- 404: Bill not found
- 422: Invalid status

**Idempotency**: No

**Audit**: Yes

**Related User Story**: US-BILL-006

**Related Business Rule**: BR-BILL-006

**Important**: Do NOT implement actual payment execution APIs in MVP.

---

## 12. Financial Health APIs

### GET /api/v1/financial-health

**Purpose**: Get financial health score

**Authentication**: Bearer token

**Authorization**: USER

**Header**: `X-Family-ID: {familyId}` (optional)

**Request**: None

**Response** (200):
```json
{
  "healthScoreId": "uuid",
  "userId": "uuid",
  "familyId": "uuid (nullable)",
  "overallScore": 78,
  "scoreLabel": "GOOD",
  "confidence": "HIGH",
  "status": "FINAL | PROVISIONAL",
  "calculatedAt": "datetime",
  "disclaimer": "This is an educational indicator, not a regulated financial assessment"
}
```

**Validation**:
- Valid access token
- If family ID provided, user must be family member

**Errors**:
- 401: Invalid token
- 403: Not family member
- 404: No financial health data available

**Idempotency**: Yes

**Audit**: No

**Related User Story**: US-FH-001

**Related Business Rule**: BR-FH-001

**Important**: The authoritative score must come from the deterministic engine.

---

### GET /api/v1/financial-health/factors

**Purpose**: Get financial health factor scores

**Authentication**: Bearer token

**Authorization**: USER

**Header**: `X-Family-ID: {familyId}` (optional)

**Request**: None

**Response** (200):
```json
{
  "healthScoreId": "uuid",
  "factorScores": {
    "budgetAdherence": {
      "score": 84,
      "weight": 0.20,
      "contribution": 16.8
    },
    "savingsBehavior": {
      "score": 72,
      "weight": 0.20,
      "contribution": 14.4
    },
    "expenseTrend": {
      "score": 81,
      "weight": 0.15,
      "contribution": 12.15
    },
    "billDiscipline": {
      "score": 95,
      "weight": 0.15,
      "contribution": 14.25
    },
    "emergencyPreparedness": {
      "score": 60,
      "weight": 0.15,
      "contribution": 9
    },
    "goalProgress": {
      "score": 78,
      "weight": 0.15,
      "contribution": 11.7
    }
  },
  "calculatedAt": "datetime"
}
```

**Validation**: Valid access token

**Errors**:
- 401: Invalid token
- 403: Not family member
- 404: No financial health data available

**Idempotency**: Yes

**Audit**: No

**Related User Story**: US-FH-002

**Related Business Rule**: BR-FH-006

---

### GET /api/v1/financial-health/history

**Purpose**: Get financial health score history

**Authentication**: Bearer token

**Authorization**: USER

**Header**: `X-Family-ID: {familyId}` (optional)

**Query Parameters**:
```
page: Integer (default: 1)
limit: Integer (default: 12, max: 24)
sort: calculatedAt:desc
```

**Response** (200):
```json
{
  "history": [
    {
      "healthScoreId": "uuid",
      "overallScore": 78,
      "scoreLabel": "GOOD",
      "confidence": "HIGH",
      "calculatedAt": "datetime"
    }
  ],
  "pagination": {
    "page": 1,
    "limit": 12,
    "totalCount": 24,
    "pageCount": 2
  }
}
```

**Validation**: Valid access token

**Errors**:
- 401: Invalid token
- 403: Not family member
- 422: Validation error

**Idempotency**: Yes

**Audit**: No

**Related User Story**: US-FH-003

**Related Business Rule**: BR-FH-007

---

### GET /api/v1/financial-health/explanation

**Purpose**: Get financial health score explanation

**Authentication**: Bearer token

**Authorization**: USER

**Header**: `X-Family-ID: {familyId}` (optional)

**Request**: None

**Response** (200):
```json
{
  "healthScoreId": "uuid",
  "overallScore": 78,
  "scoreLabel": "GOOD",
  "explanation": {
    "positiveContributors": [
      {
        "factor": "billDiscipline",
        "score": 95,
        "reason": "Excellent bill payment discipline"
      }
    ],
    "negativeContributors": [
      {
        "factor": "emergencyPreparedness",
        "score": 60,
        "reason": "Emergency fund needs improvement"
      }
    ],
    "recommendedActions": [
      {
        "action": "Increase emergency fund allocation",
        "priority": "HIGH"
      }
    ],
    "scoreChange": {
      "previousScore": 72,
      "currentScore": 78,
      "change": 6,
      "direction": "IMPROVING",
      "reasons": [
        "Grocery spending decreased 5%"
      ]
    }
  },
  "calculatedAt": "datetime"
}
```

**Validation**: Valid access token

**Errors**:
- 401: Invalid token
- 403: Not family member
- 404: No financial health data available

**Idempotency**: Yes

**Audit**: No

**Related User Story**: US-FH-004

**Related Business Rule**: BR-FH-008

---

## 13. Notification APIs

### GET /api/v1/notifications

**Purpose**: Get user notifications

**Authentication**: Bearer token

**Authorization**: USER

**Query Parameters**:
```
page: Integer (default: 1)
limit: Integer (default: 20, max: 100)
filter[read]: boolean
filter[type]: BUDGET_ALERT | BILL_REMINDER | SAVINGS_UPDATE | FINANCIAL_HEALTH_UPDATE
sort: createdAt:desc
```

**Response** (200):
```json
{
  "notifications": [
    {
      "notificationId": "uuid",
      "userId": "uuid",
      "type": "string",
      "title": "string",
      "message": "string",
      "data": {},
      "isRead": false,
      "createdAt": "datetime"
    }
  ],
  "pagination": {
    "page": 1,
    "limit": 20,
    "totalCount": 50,
    "pageCount": 3
  }
}
```

**Validation**: Valid access token

**Errors**:
- 401: Invalid token
- 422: Validation error

**Idempotency**: Yes

**Audit**: No

**Related User Story**: US-NOT-001

**Related Business Rule**: BR-NOT-001

---

### GET /api/v1/notifications/unread-count

**Purpose**: Get unread notification count

**Authentication**: Bearer token

**Authorization**: USER

**Request**: None

**Response** (200):
```json
{
  "unreadCount": 5
}
```

**Validation**: Valid access token

**Errors**:
- 401: Invalid token

**Idempotency**: Yes

**Audit**: No

**Related User Story**: US-NOT-002

**Related Business Rule**: BR-NOT-002

---

### POST /api/v1/notifications/{id}/read

**Purpose**: Mark notification as read

**Authentication**: Bearer token

**Authorization**: USER

**Request**: None

**Response** (200):
```json
{
  "notificationId": "uuid",
  "isRead": true,
  "readAt": "datetime"
}
```

**Validation**:
- Valid access token
- Notification must belong to user

**Errors**:
- 401: Invalid token
- 403: Not authorized
- 404: Notification not found

**Idempotency**: Yes

**Audit**: Yes

**Related User Story**: US-NOT-003

**Related Business Rule**: BR-NOT-003

---

### POST /api/v1/notifications/read-all

**Purpose**: Mark all notifications as read

**Authentication**: Bearer token

**Authorization**: USER

**Request**: None

**Response** (200):
```json
{
  "markedCount": 10,
  "markedAt": "datetime"
}
```

**Validation**: Valid access token

**Errors**:
- 401: Invalid token

**Idempotency**: Yes

**Audit**: Yes

**Related User Story**: US-NOT-004

**Related Business Rule**: BR-NOT-004

---

### GET /api/v1/notification-preferences

**Purpose**: Get notification preferences

**Authentication**: Bearer token

**Authorization**: USER

**Request**: None

**Response** (200):
```json
{
  "userId": "uuid",
  "preferences": {
    "budgetAlerts": {
      "enabled": true,
      "thresholdPercentage": 80
    },
    "billReminders": {
      "enabled": true,
      "reminderDays": [7, 2, 0]
    },
    "savingsUpdates": {
      "enabled": true,
      "frequency": "WEEKLY"
    },
    "financialHealthUpdates": {
      "enabled": true,
      "frequency": "MONTHLY"
    }
  }
}
```

**Validation**: Valid access token

**Errors**:
- 401: Invalid token

**Idempotency**: Yes

**Audit**: No

**Related User Story**: US-NOT-005

**Related Business Rule**: BR-NOT-005

---

### PUT /api/v1/notification-preferences

**Purpose**: Update notification preferences

**Authentication**: Bearer token

**Authorization**: USER

**Request**:
```json
{
  "budgetAlerts": {
    "enabled": "boolean (optional)",
    "thresholdPercentage": "integer (optional, 0-100)"
  },
  "billReminders": {
    "enabled": "boolean (optional)",
    "reminderDays": "array of integers (optional)"
  },
  "savingsUpdates": {
    "enabled": "boolean (optional)",
    "frequency": "DAILY | WEEKLY | MONTHLY (optional)"
  },
  "financialHealthUpdates": {
    "enabled": "boolean (optional)",
    "frequency": "DAILY | WEEKLY | MONTHLY (optional)"
  }
}
```

**Response** (200):
```json
{
  "userId": "uuid",
  "preferences": {},
  "updatedAt": "datetime"
}
```

**Validation**:
- At least one preference must be provided
- Values must be valid

**Errors**:
- 401: Invalid token
- 422: Validation error

**Idempotency**: Yes

**Audit**: Yes

**Related User Story**: US-NOT-006

**Related Business Rule**: BR-NOT-006

---

## 14. AI APIs

### POST /api/v1/ai/chat

**Purpose**: Send chat message to AI

**Authentication**: Bearer token

**Authorization**: USER

**Header**: `X-Family-ID: {familyId}` (optional)

**Request**:
```json
{
  "message": "string (required)",
  "conversationId": "uuid (optional)",
  "context": {
    "includeFinancialData": "boolean (default: true)",
    "includeBudgetData": "boolean (default: true)"
  }
}
```

**Response** (200):
```json
{
  "conversationId": "uuid",
  "messageId": "uuid",
  "userMessage": "string",
  "aiResponse": {
    "type": "FACT | ANALYSIS | RECOMMENDATION | WARNING | INSUFFICIENT_DATA | REFUSAL",
    "content": "string",
    "confidence": "HIGH | MEDIUM | LOW",
    "data": {},
    "sources": []
  },
  "createdAt": "datetime"
}
```

**Validation**:
- Message required
- Valid access token

**Errors**:
- 401: Invalid token
- 403: Not family member
- 422: Validation error
- 429: Rate limit exceeded

**Idempotency**: No

**Audit**: Yes

**Related User Story**: US-AI-001

**Related Business Rule**: BR-AI-001

---

### GET /api/v1/ai/conversations

**Purpose**: Get AI conversations

**Authentication**: Bearer token

**Authorization**: USER

**Query Parameters**:
```
page: Integer (default: 1)
limit: Integer (default: 20, max: 100)
sort: updatedAt:desc
```

**Response** (200):
```json
{
  "conversations": [
    {
      "conversationId": "uuid",
      "userId": "uuid",
      "title": "string",
      "messageCount": 5,
      "lastMessageAt": "datetime",
      "createdAt": "datetime"
    }
  ],
  "pagination": {
    "page": 1,
    "limit": 20,
    "totalCount": 30,
    "pageCount": 2
  }
}
```

**Validation**: Valid access token

**Errors**:
- 401: Invalid token
- 422: Validation error

**Idempotency**: Yes

**Audit**: No

**Related User Story**: US-AI-002

**Related Business Rule**: BR-AI-002

---

### GET /api/v1/ai/conversations/{id}

**Purpose**: Get AI conversation by ID

**Authentication**: Bearer token

**Authorization**: USER

**Request**: None

**Response** (200):
```json
{
  "conversationId": "uuid",
  "userId": "uuid",
  "title": "string",
  "messages": [
    {
      "messageId": "uuid",
      "role": "USER | AI",
      "content": "string",
      "type": "FACT | ANALYSIS | RECOMMENDATION | WARNING | INSUFFICIENT_DATA | REFUSAL",
      "confidence": "HIGH | MEDIUM | LOW (nullable)",
      "createdAt": "datetime"
    }
  ],
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

**Validation**:
- Valid access token
- Conversation must belong to user

**Errors**:
- 401: Invalid token
- 403: Not authorized
- 404: Conversation not found

**Idempotency**: Yes

**Audit**: No

**Related User Story**: US-AI-003

**Related Business Rule**: BR-AI-003

---

### DELETE /api/v1/ai/conversations/{id}

**Purpose**: Delete AI conversation

**Authentication**: Bearer token

**Authorization**: USER

**Request**: None

**Response** (204): No content

**Validation**:
- Valid access token
- Conversation must belong to user

**Errors**:
- 401: Invalid token
- 403: Not authorized
- 404: Conversation not found

**Idempotency**: Yes

**Audit**: Yes

**Related User Story**: US-AI-004

**Related Business Rule**: BR-AI-004

---

## 15. AI Tool APIs (Internal)

### Internal Tool: getFinancialOverview

**Purpose**: Retrieve financial overview data for AI

**Authentication**: Internal service token

**Authorization**: AI_SERVICE

**Request**:
```json
{
  "userId": "uuid (required)",
  "familyId": "uuid (optional)",
  "period": "string (optional, YYYY-MM)"
}
```

**Response** (200):
```json
{
  "planningIncome": "decimal",
  "mandatoryCommitments": "decimal",
  "essentialAllocation": "decimal",
  "savingsAllocation": "decimal",
  "emergencyAllocation": "decimal",
  "discretionaryPlanning": "decimal",
  "availableFinancialCapacity": "decimal",
  "currency": "string",
  "period": "string"
}
```

**Properties**:
- Authenticated: Yes
- Authorized: Yes
- Read-only: Yes
- Audited: Yes
- Explicitly defined: Yes

---

### Internal Tool: getTransactions

**Purpose**: Retrieve transaction data for AI

**Authentication**: Internal service token

**Authorization**: AI_SERVICE

**Request**:
```json
{
  "userId": "uuid (required)",
  "familyId": "uuid (optional)",
  "category": "string (optional)",
  "dateFrom": "date (optional)",
  "dateTo": "date (optional)",
  "limit": "integer (optional, max 100)"
}
```

**Response** (200):
```json
{
  "transactions": [
    {
      "transactionId": "uuid",
      "type": "string",
      "category": "string",
      "amount": "decimal",
      "currency": "string",
      "transactionDate": "date",
      "description": "string"
    }
  ]
}
```

**Properties**:
- Authenticated: Yes
- Authorized: Yes
- Read-only: Yes
- Audited: Yes
- Explicitly defined: Yes

---

### Internal Tool: getBudget

**Purpose**: Retrieve budget data for AI

**Authentication**: Internal service token

**Authorization**: AI_SERVICE

**Request**:
```json
{
  "userId": "uuid (required)",
  "familyId": "uuid (optional)",
  "period": "string (optional, YYYY-MM)"
}
```

**Response** (200):
```json
{
  "budgetId": "uuid",
  "period": "string",
  "categories": [
    {
      "category": "string",
      "limit": "decimal",
      "spent": "decimal",
      "utilizationPercentage": "decimal"
    }
  ],
  "currency": "string"
}
```

**Properties**:
- Authenticated: Yes
- Authorized: Yes
- Read-only: Yes
- Audited: Yes
- Explicitly defined: Yes

---

### Internal Tool: getBudgetForecast

**Purpose**: Retrieve budget forecast for AI

**Authentication**: Internal service token

**Authorization**: AI_SERVICE

**Request**:
```json
{
  "budgetId": "uuid (required)"
}
```

**Response** (200):
```json
{
  "budgetId": "uuid",
  "forecast": {
    "projectedSpending": "decimal",
    "projectedUtilizationPercentage": "decimal",
    "confidence": "HIGH | MEDIUM | LOW"
  }
}
```

**Properties**:
- Authenticated: Yes
- Authorized: Yes
- Read-only: Yes
- Audited: Yes
- Explicitly defined: Yes

---

### Internal Tool: getBills

**Purpose**: Retrieve bill data for AI

**Authentication**: Internal service token

**Authorization**: AI_SERVICE

**Request**:
```json
{
  "userId": "uuid (required)",
  "familyId": "uuid (optional)",
  "status": "string (optional)",
  "dueDateFrom": "date (optional)",
  "dueDateTo": "date (optional)"
}
```

**Response** (200):
```json
{
  "bills": [
    {
      "billId": "uuid",
      "name": "string",
      "amount": "decimal",
      "dueDate": "date",
      "status": "string",
      "currency": "string"
    }
  ]
}
```

**Properties**:
- Authenticated: Yes
- Authorized: Yes
- Read-only: Yes
- Audited: Yes
- Explicitly defined: Yes

---

### Internal Tool: getSavingsGoals

**Purpose**: Retrieve savings goals for AI

**Authentication**: Internal service token

**Authorization**: AI_SERVICE

**Request**:
```json
{
  "userId": "uuid (required)",
  "familyId": "uuid (optional)"
}
```

**Response** (200):
```json
{
  "goals": [
    {
      "goalId": "uuid",
      "name": "string",
      "targetAmount": "decimal",
      "currentAmount": "decimal",
      "targetDate": "date",
      "progressPercentage": "decimal",
      "currency": "string"
    }
  ]
}
```

**Properties**:
- Authenticated: Yes
- Authorized: Yes
- Read-only: Yes
- Audited: Yes
- Explicitly defined: Yes

---

### Internal Tool: getFinancialHealth

**Purpose**: Retrieve financial health score for AI

**Authentication**: Internal service token

**Authorization**: AI_SERVICE

**Request**:
```json
{
  "userId": "uuid (required)",
  "familyId": "uuid (optional)"
}
```

**Response** (200):
```json
{
  "overallScore": 78,
  "scoreLabel": "GOOD",
  "confidence": "HIGH",
  "factorScores": {},
  "disclaimer": "This is an educational indicator, not a regulated financial assessment"
}
```

**Properties**:
- Authenticated: Yes
- Authorized: Yes
- Read-only: Yes
- Audited: Yes
- Explicitly defined: Yes

---

### Internal Tool: getFamilySummary

**Purpose**: Retrieve family summary for AI

**Authentication**: Internal service token

**Authorization**: AI_SERVICE

**Request**:
```json
{
  "familyId": "uuid (required)"
}
```

**Response** (200):
```json
{
  "familyId": "uuid",
  "name": "string",
  "memberCount": 3,
  "currency": "string",
  "summary": {
    "totalIncome": "decimal",
    "totalExpenses": "decimal",
    "totalSavings": "decimal"
  }
}
```

**Properties**:
- Authenticated: Yes
- Authorized: Yes
- Read-only: Yes
- Audited: Yes
- Explicitly defined: Yes

**Important**: AI must not access database directly. All data access through approved tools only.

---

## 16. Configuration APIs

### GET /api/v1/admin/config

**Purpose**: Get admin configuration

**Authentication**: Bearer token

**Authorization**: ADMIN

**Request**: None

**Response** (200):
```json
{
  "budgetWarningPercentage": 80,
  "budgetExceededPercentage": 100,
  "billReminderDays": [7, 2, 0],
  "aiConfidenceThresholds": {
    "high": 80,
    "medium": 60
  },
  "notificationThresholds": {},
  "updatedAt": "datetime"
}
```

**Validation**:
- Valid access token
- User must have ADMIN role

**Errors**:
- 401: Invalid token
- 403: Not authorized

**Idempotency**: Yes

**Audit**: No

**Related User Story**: US-ADM-001

**Related Business Rule**: BR-CONF-001

---

### PUT /api/v1/admin/config

**Purpose**: Update admin configuration

**Authentication**: Bearer token

**Authorization**: ADMIN

**Request**:
```json
{
  "budgetWarningPercentage": "integer (optional, 0-100)",
  "budgetExceededPercentage": "integer (optional, 0-100)",
  "billReminderDays": "array of integers (optional)",
  "aiConfidenceThresholds": {
    "high": "integer (optional, 0-100)",
    "medium": "integer (optional, 0-100)"
  }
}
```

**Response** (200):
```json
{
  "budgetWarningPercentage": 80,
  "budgetExceededPercentage": 100,
  "billReminderDays": [7, 2, 0],
  "aiConfidenceThresholds": {},
  "updatedAt": "datetime"
}
```

**Validation**:
- At least one field must be provided
- User must have ADMIN role
- Values must be valid

**Errors**:
- 401: Invalid token
- 403: Not authorized
- 422: Validation error

**Idempotency**: Yes

**Audit**: Yes

**Related User Story**: US-ADM-002

**Related Business Rule**: BR-CONF-002

---

## 17. Error Model

### Standard Error Structure

```json
{
  "code": "ERROR_CODE",
  "message": "Human-readable error message",
  "requestId": "uuid",
  "timestamp": "datetime",
  "details": [
    {
      "field": "string",
      "message": "string"
    }
  ]
}
```

### HTTP Status Codes

**400 Bad Request**: Invalid request format or parameters

**401 Unauthorized**: Authentication failed or token invalid

**403 Forbidden**: Authorization failed (insufficient permissions)

**404 Not Found**: Resource not found

**409 Conflict**: Resource conflict (e.g., duplicate)

**422 Unprocessable Entity**: Validation error

**429 Too Many Requests**: Rate limit exceeded

**500 Internal Server Error**: Unexpected server error

**503 Service Unavailable**: Service temporarily unavailable

### Error Codes

**Authentication Errors**:
- `AUTH_INVALID_TOKEN`: Invalid or expired access token
- `AUTH_INVALID_REFRESH_TOKEN`: Invalid or expired refresh token
- `AUTH_INVALID_CREDENTIALS`: Invalid email or password
- `AUTH_ACCOUNT_LOCKED`: Account locked due to security
- `AUTH_OTP_INVALID`: Invalid or expired OTP
- `AUTH_OTP_EXPIRED`: OTP has expired

**Authorization Errors**:
- `AUTHZ_INSUFFICIENT_PERMISSIONS`: Insufficient permissions
- `AUTHZ_NOT_FAMILY_MEMBER`: Not a family member
- `AUTHZ_NOT_FAMILY_OWNER`: Not family owner
- `AUTHZ_RESOURCE_NOT_OWNED`: Resource not owned by user

**Validation Errors**:
- `VALIDATION_INVALID_EMAIL`: Invalid email format
- `VALIDATION_INVALID_PHONE`: Invalid phone format
- `VALIDATION_INVALID_PASSWORD`: Invalid password format
- `VALIDATION_REQUIRED_FIELD`: Required field missing
- `VALIDATION_INVALID_VALUE`: Invalid value for field

**Resource Errors**:
- `RESOURCE_NOT_FOUND`: Resource not found
- `RESOURCE_ALREADY_EXISTS`: Resource already exists
- `RESOURCE_CONFLICT`: Resource conflict
- `RESOURCE_LOCKED`: Resource locked

**Business Errors**:
- `BUSINESS_INVALID_STATE`: Invalid business state
- `BUSINESS_RULE_VIOLATION`: Business rule violation
- `BUSINESS_INSUFFICIENT_DATA`: Insufficient data for operation

**Rate Limit Errors**:
- `RATE_LIMIT_EXCEEDED`: Rate limit exceeded

**Server Errors**:
- `SERVER_INTERNAL_ERROR`: Internal server error
- `SERVICE_UNAVAILABLE`: Service temporarily unavailable

---

## 18. Security

### JWT/Session Model

**JWT Tokens**:
- Access token: 15 minutes expiry
- Refresh token: 30 days expiry
- Token type: Bearer
- Algorithm: RS256

**Session Management**:
- Sessions tracked per device
- Multiple sessions allowed
- Session revocation supported

### RBAC

**System Roles**:
- ADMIN: Full system access
- USER: Standard user access

**Family Roles**:
- OWNER: Full family management
- MEMBER: View and edit family data
- RESTRICTED: Limited family data access

### Family Authorization

**Family ID Header**: `X-Family-ID: {familyId}`

**Authorization Check**:
1. User must be authenticated
2. User must be family member
3. User must have required family role
4. User must have required permissions

### Resource Ownership

**Ownership Rules**:
- Users own their personal resources
- Family owners own family resources
- Family members can view family resources
- RESTRICTED members have limited access

### Rate Limits

**Default Limits**:
- Authenticated: 100 requests/minute
- Unauthenticated: 10 requests/minute
- AI chat: 20 requests/minute

**Rate Limit Headers**:
```
X-RateLimit-Limit: {limit}
X-RateLimit-Remaining: {remaining}
X-RateLimit-Reset: {timestamp}
```

### Input Validation

**Validation Types**:
- Schema validation
- Business rule validation
- Authorization validation
- Sanitization

### Sensitive Fields

**Sensitive Data**:
- Passwords: Never returned in API responses
- OTP codes: Never returned after verification
- Tokens: Never logged
- Personal data: Masked in logs

### Audit

**Audit Events**:
- User registration
- Login/logout
- Resource creation
- Resource update
- Resource deletion
- Authorization failures

**Audit Fields**:
- User ID
- Action
- Resource
- Timestamp
- IP address
- User agent

### Idempotency

**Idempotency Key**: `X-Idempotency-Key: {uuid}`

**Idempotency Duration**: 24 hours

**Idempotent Operations**:
- PUT requests
- DELETE requests
- POST requests with idempotency key

### Request Signing

**Signed Requests**: Required for sensitive operations

**Signature Algorithm**: HMAC-SHA256

**Signature Header**: `X-Signature: {signature}`

---

## 19. API Traceability Matrix

### API → User Story → Business Rule → Data Entity

| API | User Story | Business Rule | Data Entity |
|-----|------------|---------------|-------------|
| POST /api/v1/auth/register | US-AUTH-001 | BR-USER-001 | User |
| POST /api/v1/auth/login | US-AUTH-002 | BR-USER-002 | User, Session |
| POST /api/v1/auth/otp/request | US-AUTH-003 | BR-USER-003 | OTP |
| POST /api/v1/auth/otp/verify | US-AUTH-004 | BR-USER-004 | OTP, User |
| POST /api/v1/auth/refresh | US-AUTH-005 | BR-USER-005 | RefreshToken |
| POST /api/v1/auth/logout | US-AUTH-006 | BR-USER-006 | Session |
| GET /api/v1/auth/sessions | US-AUTH-007 | BR-USER-007 | Session |
| DELETE /api/v1/auth/sessions/{id} | US-AUTH-008 | BR-USER-008 | Session |
| GET /api/v1/users/me | US-USER-001 | BR-USER-009 | User |
| PUT /api/v1/users/me | US-USER-002 | BR-USER-010 | User |
| DELETE /api/v1/users/me | US-USER-003 | BR-USER-011 | User |
| GET /api/v1/users/me/preferences | US-USER-004 | BR-USER-012 | UserPreferences |
| PUT /api/v1/users/me/preferences | US-USER-005 | BR-USER-013 | UserPreferences |
| GET /api/v1/users/me/devices | US-USER-006 | BR-USER-014 | Device |
| POST /api/v1/families | US-FAM-001 | BR-FAM-001 | Family |
| GET /api/v1/families/{familyId} | US-FAM-002 | BR-FAM-002 | Family |
| PUT /api/v1/families/{familyId} | US-FAM-003 | BR-FAM-003 | Family |
| DELETE /api/v1/families/{familyId} | US-FAM-004 | BR-FAM-004 | Family |
| POST /api/v1/families/{familyId}/invitations | US-FAM-005 | BR-FAM-005 | FamilyInvitation |
| GET /api/v1/families/{familyId}/members | US-FAM-006 | BR-FAM-006 | FamilyMember |
| PUT /api/v1/families/{familyId}/members/{memberId} | US-FAM-007 | BR-FAM-007 | FamilyMember |
| DELETE /api/v1/families/{familyId}/members/{memberId} | US-FAM-008 | BR-FAM-008 | FamilyMember |
| POST /api/v1/family-invitations/{token}/accept | US-FAM-009 | BR-FAM-009 | FamilyInvitation |
| POST /api/v1/family-invitations/{token}/reject | US-FAM-010 | BR-FAM-010 | FamilyInvitation |
| GET /api/v1/financial-overview | US-FIN-001 | BR-FIN-001 | FinancialOverview |
| GET /api/v1/financial-overview/summary | US-FIN-002 | BR-FIN-002 | FinancialOverview |
| GET /api/v1/financial-overview/allocations | US-FIN-003 | BR-FIN-003 | FinancialOverview |
| GET /api/v1/financial-overview/commitments | US-FIN-004 | BR-FIN-004 | FinancialOverview |
| GET /api/v1/financial-overview/pending | US-FIN-005 | BR-FIN-005 | FinancialOverview |
| GET /api/v1/financial-overview/capacity | US-FIN-006 | BR-FIN-006 | FinancialOverview |
| POST /api/v1/transactions | US-TRX-001 | BR-TRX-001 | Transaction |
| GET /api/v1/transactions | US-TRX-002 | BR-TRX-002 | Transaction |
| GET /api/v1/transactions/{transactionId} | US-TRX-003 | BR-TRX-003 | Transaction |
| PUT /api/v1/transactions/{transactionId} | US-TRX-004 | BR-TRX-004 | Transaction |
| DELETE /api/v1/transactions/{transactionId} | US-TRX-005 | BR-TRX-005 | Transaction |
| POST /api/v1/budgets | US-BUD-001 | BR-BUD-001 | Budget |
| GET /api/v1/budgets | US-BUD-002 | BR-BUD-002 | Budget |
| GET /api/v1/budgets/{budgetId} | US-BUD-003 | BR-BUD-003 | Budget |
| PUT /api/v1/budgets/{budgetId} | US-BUD-004 | BR-BUD-004 | Budget |
| DELETE /api/v1/budgets/{budgetId} | US-BUD-005 | BR-BUD-005 | Budget |
| GET /api/v1/budgets/{budgetId}/utilization | US-BUD-006 | BR-BUD-006 | Budget |
| GET /api/v1/budgets/{budgetId}/forecast | US-BUD-007 | BR-BUD-007 | Budget |
| GET /api/v1/budgets/recommendation | US-BUD-008 | BR-BUD-008 | Budget |
| GET /api/v1/allocation/recommendation | US-ALL-001 | BR-ALL-001 | Allocation |
| POST /api/v1/allocation/recommendation/{id}/approve | US-ALL-002 | BR-ALL-002 | Allocation |
| POST /api/v1/allocation/recommendation/{id}/modify | US-ALL-003 | BR-ALL-003 | Allocation |
| GET /api/v1/allocation/history | US-ALL-004 | BR-ALL-004 | Allocation |
| POST /api/v1/savings-goals | US-SAV-001 | BR-SAV-001 | SavingsGoal |
| GET /api/v1/savings-goals | US-SAV-002 | BR-SAV-002 | SavingsGoal |
| GET /api/v1/savings-goals/{goalId} | US-SAV-002 | BR-SAV-002 | SavingsGoal |
| POST /api/v1/savings-goals/{goalId}/contributions | US-SAV-003 | BR-SAV-003 | SavingsGoal |
| PUT /api/v1/savings-goals/{goalId} | US-SAV-004 | BR-SAV-004 | SavingsGoal |
| DELETE /api/v1/savings-goals/{goalId} | US-SAV-005 | BR-SAV-005 | SavingsGoal |
| GET /api/v1/savings-goals/{goalId}/progress | US-SAV-006 | BR-SAV-006 | SavingsGoal |
| GET /api/v1/savings-goals/{goalId}/forecast | US-SAV-007 | BR-SAV-007 | SavingsGoal |
| POST /api/v1/bills | US-BILL-001 | BR-BILL-001 | Bill |
| GET /api/v1/bills | US-BILL-002 | BR-BILL-002 | Bill |
| GET /api/v1/bills/{billId} | US-BILL-003 | BR-BILL-003 | Bill |
| PUT /api/v1/bills/{billId} | US-BILL-004 | BR-BILL-004 | Bill |
| DELETE /api/v1/bills/{billId} | US-BILL-005 | BR-BILL-005 | Bill |
| POST /api/v1/bills/{billId}/mark-paid | US-BILL-006 | BR-BILL-006 | Bill |
| GET /api/v1/financial-health | US-FH-001 | BR-FH-001 | FinancialHealth |
| GET /api/v1/financial-health/factors | US-FH-002 | BR-FH-006 | FinancialHealth |
| GET /api/v1/financial-health/history | US-FH-003 | BR-FH-007 | FinancialHealth |
| GET /api/v1/financial-health/explanation | US-FH-004 | BR-FH-008 | FinancialHealth |
| GET /api/v1/notifications | US-NOT-001 | BR-NOT-001 | Notification |
| GET /api/v1/notifications/unread-count | US-NOT-002 | BR-NOT-002 | Notification |
| POST /api/v1/notifications/{id}/read | US-NOT-003 | BR-NOT-003 | Notification |
| POST /api/v1/notifications/read-all | US-NOT-004 | BR-NOT-004 | Notification |
| GET /api/v1/notification-preferences | US-NOT-005 | BR-NOT-005 | NotificationPreferences |
| PUT /api/v1/notification-preferences | US-NOT-006 | BR-NOT-006 | NotificationPreferences |
| POST /api/v1/ai/chat | US-AI-001 | BR-AI-001 | AIConversation |
| GET /api/v1/ai/conversations | US-AI-002 | BR-AI-002 | AIConversation |
| GET /api/v1/ai/conversations/{id} | US-AI-003 | BR-AI-003 | AIConversation |
| DELETE /api/v1/ai/conversations/{id} | US-AI-004 | BR-AI-004 | AIConversation |
| GET /api/v1/admin/config | US-ADM-001 | BR-CONF-001 | Configuration |
| PUT /api/v1/admin/config | US-ADM-002 | BR-CONF-002 | Configuration |

---

## Conclusion

This document defines the complete API contract specification for NeoWallet MVP that:

- Provides comprehensive REST API contracts for all MVP features
- Defines clear authentication and authorization models
- Implements proper error handling and validation
- Supports family-based authorization with role-based access control
- Includes AI chat and internal tool contracts
- Provides complete traceability to user stories and business rules
- Follows OpenAPI 3.1 standards
- Maintains clear separation between planning values and actual funds
- Ensures financial safety with read-only AI tools

**Total API Endpoints**: 70+
**API Modules**: 12
**Internal AI Tools**: 8
**Error Codes**: 30+
**Traceability Mappings**: 70+

**Next Steps**: Product Owner approval, then proceed to NW-003.

---

**Document Version**: v1
**Date**: August 18, 2026
**Status**: Ready for Product Owner Approval
**Next Step**: Product Owner approval, then NW-003

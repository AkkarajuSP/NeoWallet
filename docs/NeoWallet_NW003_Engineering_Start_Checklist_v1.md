# NeoWallet NW-003 Engineering Start Checklist v1

## 1. Executive Summary

This checklist captures all tasks that Devin must perform before the first feature implementation of NeoWallet MVP. Items are grouped by domain and marked as mandatory or conditional.

**Gate Reference**: `NeoWallet_Final_PreDevelopment_Gate_v1.md`

**Status**: Inherits `MVP ENGINEERING = GO WITH CONDITIONS`

---

## 2. Critical Pre-Engineering Decisions

These decisions must be resolved before Devin starts MVP feature implementation.

| ID | Decision | Owner | Status | Blocking |
|----|----------|-------|--------|----------|
| DEC-ENG-001 | AI Provider Selection | Product/Engineering | OPEN | YES |
| DEC-ENG-002 | OTP Provider Selection | Product/Engineering | OPEN | YES |
| DEC-ENG-003 | Email Provider Selection | Product/Engineering | OPEN | YES |
| DEC-ENG-004 | SMS/WhatsApp Provider Selection | Product/Engineering | OPEN | YES |
| DEC-ENG-005 | Push Provider Selection | Product/Engineering | OPEN | YES |
| DEC-PROD-004 | Financial Health Weights / Algorithm Defaults | Product | OPEN | CONDITIONAL |
| DEC-SEC-002 | Secrets Management Tool | Security/Engineering | OPEN | CONDITIONAL |

---

## 3. Project Setup

### 3.1 Repository and Version Control

| ID | Task | Status | Notes |
|----|------|--------|-------|
| NW-003-001 | Create source code repository | TODO | Git repository for NeoWallet |
| NW-003-002 | Define branching strategy | TODO | e.g., `main`, `develop`, feature branches |
| NW-003-003 | Define commit message convention | TODO | Conventional commits recommended |
| NW-003-004 | Configure branch protection rules | TODO | Require PR review for `main` |
| NW-003-005 | Add `.gitignore` for Flutter, Java, secrets | TODO | Prevent secret leakage |

### 3.2 CI/CD

| ID | Task | Status | Notes |
|----|------|--------|-------|
| NW-003-006 | Set up CI pipeline | TODO | Build, test, lint |
| NW-003-007 | Set up security scanning (SAST) | TODO | e.g., SonarQube, CodeQL |
| NW-003-008 | Set up dependency vulnerability scanning | TODO | SCA tool |
| NW-003-009 | Set up OpenAPI validation in CI | TODO | Validate `NeoWallet_OpenAPI_v1.yaml` |
| NW-003-010 | Define build artifacts and versioning | TODO | Semantic versioning |

---

## 4. Backend Engineering

### 4.1 Spring Boot Project

| ID | Task | Status | Notes |
|----|------|--------|-------|
| NW-003-011 | Initialize Spring Boot project | TODO | Modular monolith |
| NW-003-012 | Configure package structure by domain | TODO | `identity`, `family`, `finance`, `budget`, `savings`, `bills`, `notifications`, `ai`, `audit`, `config` |
| NW-003-013 | Configure application properties per environment | TODO | `application-dev.yml`, `application-staging.yml`, `application-prod.yml` |
| NW-003-014 | Set up Gradle or Maven build | TODO | Dependency lock file |
| NW-003-015 | Configure static code analysis | TODO | SpotBugs, PMD, Checkstyle |
| NW-003-016 | Configure unit test framework (JUnit 5) | TODO | Coverage target |
| NW-003-017 | Configure integration test framework | TODO | Testcontainers |

### 4.2 Database and Migrations

| ID | Task | Status | Notes |
|----|------|--------|-------|
| NW-003-018 | Set up Flyway or Liquibase migration framework | TODO | Do NOT create migrations before schema design is finalized |
| NW-003-019 | Configure PostgreSQL driver and connection pool | TODO | HikariCP |
| NW-003-020 | Set up local PostgreSQL for development | TODO | Docker Compose or local install |
| NW-003-021 | Configure NUMERIC/DECIMAL money handling | TODO | `BigDecimal` with `NUMERIC(19,4)` |
| NW-003-022 | Configure audit columns (`created_at`, `updated_at`, `created_by`, `updated_by`) | TODO | Automatic population |
| NW-003-023 | Configure soft delete strategy | TODO | `deleted_at` columns |
| NW-003-024 | Configure JPA/Hibernate naming and constraints | TODO | Match `NeoWallet_Database_Architecture_v1.md` |

### 4.3 Redis

| ID | Task | Status | Notes |
|----|------|--------|-------|
| NW-003-025 | Set up Redis connection | TODO | Only where justified (sessions, rate limiting, cache) |
| NW-003-026 | Configure Redis for OTP throttling if required | TODO | Provider-dependent |

### 4.4 Security Baseline

| ID | Task | Status | Notes |
|----|------|--------|-------|
| NW-003-027 | Configure secrets management | TODO | External secrets manager, no hardcoded secrets |
| NW-003-028 | Set up JWT authentication | TODO | Access token / refresh token |
| NW-003-029 | Set up password hashing (Argon2 or bcrypt) | TODO | No plain text storage |
| NW-003-030 | Set up family isolation middleware | TODO | Every query scoped to family |
| NW-003-031 | Configure CORS | TODO | Restrict to known mobile origins |
| NW-003-032 | Configure rate limiting | TODO | Login, OTP, AI endpoints |
| NW-003-033 | Configure request ID / correlation ID | TODO | For observability |
| NW-003-034 | Enable HTTPS in all environments | TODO | TLS termination |
| NW-003-035 | Set up OWASP dependency check | TODO | CI/CD integration |

### 4.5 API Contract

| ID | Task | Status | Notes |
|----|------|--------|-------|
| NW-003-036 | Import API contract into Spring Boot | TODO | From `NeoWallet_API_Contract_Specification_v1.md` |
| NW-003-037 | Validate OpenAPI file in CI | TODO | `NeoWallet_OpenAPI_v1.yaml` |
| NW-003-038 | Set up API error model (RFC 7807) | TODO | Consistent error responses |
| NW-003-039 | Implement idempotency key handling | TODO | For safe retries |
| NW-003-040 | Implement pagination standard | TODO | Cursor / offset per spec |

### 4.6 Provider Abstractions

| ID | Task | Status | Notes |
|----|------|--------|-------|
| NW-003-041 | Create AI provider interface | TODO | `AIProvider` abstraction |
| NW-003-042 | Create OTP provider interface | TODO | `OTPProvider` abstraction |
| NW-003-043 | Create email provider interface | TODO | `EmailProvider` abstraction |
| NW-003-044 | Create SMS provider interface | TODO | `SMSProvider` abstraction |
| NW-003-045 | Create WhatsApp provider interface | TODO | `WhatsAppProvider` abstraction (optional) |
| NW-003-046 | Create push notification provider interface | TODO | `PushProvider` abstraction |
| NW-003-047 | Implement stub/mock providers for local dev | TODO | No vendor lock-in |

---

## 5. Mobile Engineering

### 5.1 Flutter Project

| ID | Task | Status | Notes |
|----|------|--------|-------|
| NW-003-048 | Initialize Flutter project | TODO | Clean architecture layers |
| NW-003-049 | Configure build flavors (dev, staging, prod) | TODO | Separate app IDs |
| NW-003-050 | Set up state management | TODO | e.g., Riverpod, Bloc |
| NW-003-051 | Configure HTTP client with interceptors | TODO | Auth, logging, correlation IDs |
| NW-003-052 | Configure secure local storage | TODO | flutter_secure_storage |
| NW-003-053 | Configure dependency injection | TODO | GetIt / Injectable |
| NW-003-054 | Set up unit and widget test framework | TODO | `flutter_test`, mockito |
| NW-003-055 | Set up integration test framework | TODO | `integration_test` |
| NW-003-056 | Configure code linting | TODO | `flutter_lints` / custom rules |

---

## 6. AI Service

### 6.1 AI Orchestration

| ID | Task | Status | Notes |
|----|------|--------|-------|
| NW-003-057 | Initialize AI service (Python/FastAPI or Spring Boot) | TODO | Per `NeoWallet_Implementation_Baseline_v1.md` |
| NW-003-058 | Define authorized AI tools | TODO | No direct database access |
| NW-003-059 | Implement tool authorization (server-side) | TODO | Per `NeoWallet_AI_Functional_Specification_v1.md` |
| NW-003-060 | Implement prompt injection filtering | TODO | Input/output guards |
| NW-003-061 | Implement audit logging for AI tool calls | TODO | `ai_tool_invocations` table |
| NW-003-062 | Implement context minimization | TODO | Only necessary data passed to AI |

---

## 7. Observability

### 7.1 Logging

| ID | Task | Status | Notes |
|----|------|--------|-------|
| NW-003-063 | Configure structured logging | TODO | JSON format |
| NW-003-064 | Mask sensitive PII/financial data in logs | TODO | Do NOT log full PAN, raw OTP, passwords |
| NW-003-065 | Centralize log aggregation | TODO | e.g., Cloud Logging, ELK |

### 7.2 Metrics and Tracing

| ID | Task | Status | Notes |
|----|------|--------|-------|
| NW-003-066 | Configure application metrics | TODO | Micrometer / Prometheus |
| NW-003-067 | Configure distributed tracing | TODO | OpenTelemetry |
| NW-003-068 | Configure health checks and readiness probes | TODO | `/actuator/health` |

### 7.3 Monitoring

| ID | Task | Status | Notes |
|----|------|--------|-------|
| NW-003-069 | Set up error monitoring | TODO | e.g., Sentry |
| NW-003-070 | Set up security monitoring | TODO | Failed login, anomalous AI usage |
| NW-003-071 | Set up AI monitoring | TODO | Latency, cost, hallucination flags |

---

## 8. Testing

### 8.1 Test Frameworks

| ID | Task | Status | Notes |
|----|------|--------|-------|
| NW-003-072 | Configure unit test runner (backend) | TODO | JUnit 5, Mockito |
| NW-003-073 | Configure integration test runner | TODO | Testcontainers |
| NW-003-074 | Configure API test runner | TODO | REST Assured / Karate |
| NW-003-075 | Configure mobile unit/widget test runner | TODO | `flutter_test` |
| NW-003-076 | Configure mobile integration test runner | TODO | `integration_test` |
| NW-003-077 | Configure E2E test runner | TODO | Maestro / Appium |
| NW-003-078 | Configure security test runner | TODO | OWASP ZAP, custom scripts |
| NW-003-079 | Configure performance test runner | TODO | K6 / JMeter |
| NW-003-080 | Configure AI evaluation framework | TODO | Per `NeoWallet_AI_Functional_Specification_v1.md` |

### 8.2 Test Data

| ID | Task | Status | Notes |
|----|------|--------|-------|
| NW-003-081 | Create test fixtures and seed data | TODO | Deterministic for algorithm tests |
| NW-003-082 | Define UAT scenarios | TODO | Derived from 72 user stories |

---

## 9. Data Protection

### 9.1 Privacy and Consent

| ID | Task | Status | Notes |
|----|------|--------|-------|
| NW-003-083 | Implement privacy notice (content TBD legal) | TODO | Placeholder with configurable content |
| NW-003-084 | Implement consent and preference management | TODO | `user_preferences` table + UI |
| NW-003-085 | Implement data access endpoints | TODO | Per DPDP principles |
| NW-003-086 | Implement data correction endpoints | TODO | Per DPDP principles |
| NW-003-087 | Implement data deletion (account + specific data) | TODO | Soft delete + configurable retention |
| NW-003-088 | Implement data minimization validation | TODO | Do not collect unnecessary data |

### 9.2 Retention

| ID | Task | Status | Notes |
|----|------|--------|-------|
| NW-003-089 | Implement configurable retention settings | TODO | Notifications, AI conversations, security logs |
| NW-003-090 | Implement retention purge job skeleton | TODO | Configurable schedule |
| NW-003-091 | Implement audit log retention | TODO | 7 years default, configurable |

### 9.3 Encryption

| ID | Task | Status | Notes |
|----|------|--------|-------|
| NW-003-092 | Configure encryption at rest | TODO | PostgreSQL encryption |
| NW-003-093 | Configure encryption in transit | TODO | TLS for all services |
| NW-003-094 | Configure field-level encryption for sensitive data | TODO | e.g., device tokens where needed |

---

## 10. Documentation

### 10.1 Developer Documentation

| ID | Task | Status | Notes |
|----|------|--------|-------|
| NW-003-095 | Create README for backend | TODO | Build, run, test instructions |
| NW-003-096 | Create README for mobile | TODO | Build, run, test instructions |
| NW-003-097 | Create environment setup guide | TODO | Docker Compose, local DB |
| NW-003-098 | Document branching and PR process | TODO | Team conventions |

### 10.2 Operational Documentation

| ID | Task | Status | Notes |
|----|------|--------|-------|
| NW-003-099 | Document runbook for deployment | TODO | Staging and production steps |
| NW-003-100 | Document incident response process | TODO | CERT-In reporting (TBD legal) |
| NW-003-101 | Document backup and recovery | TODO | PostgreSQL backups |

---

## 11. Environment Configuration

### 11.1 Environment-Specific Configuration

| ID | Task | Status | Notes |
|----|------|--------|-------|
| NW-003-102 | Create `dev` environment configuration | TODO | Local/Docker |
| NW-003-103 | Create `staging` environment configuration | TODO | Mirror production |
| NW-003-104 | Create `prod` environment configuration | TODO | Separate credentials and secrets |
| NW-003-105 | Configure feature flags | TODO | Optional; enable/disable features per env |
| NW-003-106 | Configure AI provider per environment | TODO | Sandbox for dev/staging |

---

## 12. Compliance and Security Checkpoints

### 12.1 Before First Feature Commit

| ID | Task | Status | Notes |
|----|------|--------|-------|
| NW-003-107 | Confirm no custodial wallet balance in schema | TODO | Re-check every migration |
| NW-003-108 | Confirm no floating-point money types | TODO | `BigDecimal` + `NUMERIC` |
| NW-003-109 | Confirm no prohibited payment/money APIs | TODO | Re-check every new endpoint |
| NW-003-110 | Confirm AI has no direct database access | TODO | AI uses authorized tools only |
| NW-003-111 | Confirm family isolation in place | TODO | Every repository query scoped |
| NW-003-112 | Confirm secrets not hardcoded | TODO | Scan with git-secrets / detect-secrets |

### 12.2 Before MVP UAT

| ID | Task | Status | Notes |
|----|------|--------|-------|
| NW-003-113 | Privacy notice implemented | TODO | Content reviewed |
| NW-003-114 | Consent/preferences UI implemented | TODO | Functional |
| NW-003-115 | All providers selected and configured | TODO | Real credentials in staging |
| NW-003-116 | Security test plan executed | TODO | SAST, dependency scan, auth tests |
| NW-003-117 | Professional legal review initiated | TODO | At minimum KYC/AML, DPDP, AI |

### 12.3 Before MVP Production

| ID | Task | Status | Notes |
|----|------|--------|-------|
| NW-003-118 | All professional legal reviews completed | TODO | See `NeoWallet_Regulatory_Review_Register_v1.md` |
| NW-003-119 | Marketing/product language reviewed | TODO | Legal sign-off |
| NW-003-120 | Retention decisions finalized | TODO | Notifications, AI conversations, security logs |
| NW-003-121 | PEN test completed | TODO | Optional but recommended |
| NW-003-122 | Incident response process finalized | TODO | CERT-In reporting |

---

## 13. Completion Sign-Off

### 13.1 Engineering Start Sign-Off

| Role | Name | Signature | Date |
|------|------|-----------|------|
| Product Owner | | | |
| Engineering Lead | | | |
| Security Lead | | | |
| Compliance Lead | | | |

### 13.2 Conditions Acknowledged

- [ ] Provider selections completed
- [ ] Privacy and consent patterns defined
- [ ] Security baseline in place
- [ ] No payment/money movement in MVP
- [ ] Professional legal review scheduled

---

## 14. Next Steps

1. Complete critical pre-engineering decisions (Section 2)
2. Complete project setup (Section 3)
3. Begin first vertical slice feature implementation per Devin backlog
4. Track progress against `NeoWallet_08_Devin_Ready_Backlog.md`

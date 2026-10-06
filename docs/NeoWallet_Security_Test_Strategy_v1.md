# NeoWallet Security Test Strategy v1

## Executive Summary

This document defines the comprehensive security testing strategy for NeoWallet MVP, covering all security aspects from SAST to penetration testing.

**Date**: August 18, 2026
**Status**: Ready for Product Owner Approval

---

## 1. Testing Overview

### 1.1 Testing Objectives

- Identify security vulnerabilities before production
- Validate security controls are effective
- Ensure compliance with security requirements
- Validate threat mitigations are effective
- Validate security acceptance criteria

### 1.2 Testing Scope

**In Scope**:
- Mobile application (Flutter)
- API (Spring Boot)
- Database (PostgreSQL)
- Infrastructure (Google Cloud Run)
- CI/CD (GitHub Actions)
- AI service integration

**Out of Scope**:
- Physical security
- Social engineering
- Third-party provider security (review only)

### 1.3 Testing Phases

| Phase | Timing | Purpose |
|-------|--------|---------|
| SAST | Continuous | Identify code vulnerabilities |
| Dependency Scanning | Continuous | Identify vulnerable dependencies |
| DAST | Pre-production | Identify runtime vulnerabilities |
| API Security Testing | Pre-production | Validate API security |
| Mobile Security Testing | Pre-production | Validate mobile security |
| Authentication Testing | Pre-production | Validate authentication |
| Authorization Testing | Pre-production | Validate authorization |
| AI Security Testing | Pre-production | Validate AI security |
| Prompt Injection Testing | Pre-production | Validate prompt injection protection |
| Penetration Testing | Pre-production | Comprehensive security assessment |
| Performance/Security Testing | Pre-production | Validate security under load |

---

## 2. SAST (Static Application Security Testing)

### 2.1 Tool Selection

**GitHub Code Scanning**:
- Built-in GitHub Advanced Security
- Supports multiple languages (Java, Kotlin, Dart)
- Custom security rules
- Pull request integration

**SonarQube** (Optional):
- Additional SAST capabilities
- Code quality metrics
- Security hotspots

### 2.2 SAST Configuration

**GitHub Code Scanning**:
- Enable for all repositories
- Configure custom security rules
- Enable automatic analysis on push
- Enable automatic analysis on pull request
- Block on critical vulnerabilities

**Custom Security Rules**:
- No hard-coded secrets
- No SQL injection patterns
- No XSS patterns
- No insecure random number generation
- No insecure cryptography
- No insecure deserialization

### 2.3 SAST Execution

**Continuous**:
- Run on every push to main branch
- Run on every pull request
- Run on every commit

**Blocking**:
- Block pull requests with critical vulnerabilities
- Block pull requests with high vulnerabilities (optional)
- Block pull requests with hard-coded secrets

### 2.4 SAST Reporting

**Reporting**:
- GitHub Security tab
- Pull request annotations
- Security alerts dashboard
- Weekly security summary

### 2.5 SAST Acceptance Criteria

**Criteria**:
- No critical vulnerabilities in production code
- No high vulnerabilities in production code
- No hard-coded secrets
- All security rules passing

---

## 3. DAST (Dynamic Application Security Testing)

### 3.1 Tool Selection

**OWASP ZAP**:
- Open source DAST tool
- API security testing
- Automated scanning
- Manual testing support

**Burp Suite** (Optional):
- Commercial DAST tool
- Advanced testing capabilities
- API security testing

### 3.2 DAST Configuration

**OWASP ZAP**:
- Configure for API testing
- Configure authentication (JWT)
- Configure rate limiting
- Configure scan scope
- Configure scan depth

**Scan Scope**:
- API endpoints only
- No authentication bypass
- No destructive operations
- No data deletion

### 3.3 DAST Execution

**Pre-Production**:
- Run against staging environment
- Run before production deployment
- Run after major changes
- Run on schedule (weekly)

### 3.4 DAST Tests

**Tests**:
- SQL injection
- XSS (Cross-Site Scripting)
- CSRF (Cross-Site Request Forgery)
- Authentication bypass
- Authorization bypass
- IDOR (Insecure Direct Object Reference)
- Rate limiting bypass
- Input validation bypass
- Output encoding bypass

### 3.5 DAST Reporting

**Reporting**:
- Vulnerability report
- Severity classification
- Remediation recommendations
- Retesting results

### 3.6 DAST Acceptance Criteria

**Criteria**:
- No critical vulnerabilities
- No high vulnerabilities
- All OWASP Top 10 addressed
- All security controls validated

---

## 4. Dependency Scanning

### 4.1 Tool Selection

**GitHub Dependabot**:
- Built-in GitHub Advanced Security
- Automated dependency updates
- Vulnerability alerts
- Security advisories

**OWASP Dependency-Check**:
- Additional dependency scanning
- Supports multiple package managers
- CVE database

### 4.2 Dependency Scanning Configuration

**GitHub Dependabot**:
- Enable for all repositories
- Configure dependency updates
- Configure vulnerability alerts
- Configure security advisories

**Package Managers**:
- Maven (Java)
- Gradle (Java)
- Pub (Dart/Flutter)
- npm (JavaScript - if applicable)
- pip (Python - if applicable)

### 4.3 Dependency Scanning Execution

**Continuous**:
- Run on every push to main branch
- Run on every pull request
- Run on dependency update

**Automated Updates**:
- Dependabot PRs for security updates
- Automatic merging for patch updates (optional)
- Manual review for minor/major updates

### 4.4 Dependency Scanning Reporting

**Reporting**:
- GitHub Security tab
- Dependabot alerts
- Security advisories
- Weekly dependency summary

### 4.5 Dependency Scanning Acceptance Criteria

**Criteria**:
- No critical vulnerabilities in dependencies
- No high vulnerabilities in dependencies (with exception)
- All security updates applied within 30 days
- All dependencies up-to-date

---

## 5. API Security Testing

### 5.1 Tool Selection

**Postman**:
- API testing
- Security testing
- Automated collections
- Newman for CI/CD

**OWASP ZAP API**:
- API security testing
- Automated scanning
- Manual testing support

### 5.2 API Security Tests

**Authentication Tests**:
- Test login with valid credentials
- Test login with invalid credentials
- Test failed login lockout
- Test token generation and validation
- Test token expiration
- Test token revocation
- Test refresh token rotation
- Test logout
- Test logout all devices

**Authorization Tests**:
- Test user access to own data
- Test user denied access to other user data
- Test family member access to family data
- Test family member denied access to other family data
- Test restricted member limited access
- Test owner full access
- Test IDOR prevention
- Test cross-family access prevention

**Input Validation Tests**:
- Test SQL injection
- Test XSS
- Test CSRF
- Test command injection
- Test path traversal
- Test LDAP injection
- Test XML injection
- Test JSON injection

**Rate Limiting Tests**:
- Test authenticated rate limiting (100 req/min)
- Test unauthenticated rate limiting (10 req/min)
- Test AI chat rate limiting (20 req/min)
- Test OTP rate limiting (10 req/min)
- Test rate limit bypass attempts

**Idempotency Tests**:
- Test idempotency key generation
- Test idempotency key validation
- Test idempotency key expiration
- Test idempotency key uniqueness

**Security Headers Tests**:
- Test HSTS header
- Test X-Content-Type-Options header
- Test X-Frame-Options header
- Test X-XSS-Protection header
- Test CSP header
- Test Referrer-Policy header

### 5.3 API Security Test Execution

**Pre-Production**:
- Run against staging environment
- Run before production deployment
- Run after major changes
- Run on schedule (weekly)

### 5.4 API Security Test Reporting

**Reporting**:
- Test results report
- Vulnerability report
- Remediation recommendations
- Retesting results

### 5.5 API Security Test Acceptance Criteria

**Criteria**:
- All authentication tests passing
- All authorization tests passing
- All input validation tests passing
- All rate limiting tests passing
- All idempotency tests passing
- All security headers present

---

## 6. Mobile Security Testing

### 6.1 Tool Selection

**MobSF (Mobile Security Framework)**:
- Open source mobile security testing
- Static analysis
- Dynamic analysis
- Supports Android and iOS

**AppScan** (Optional):
- Commercial mobile security testing
- Advanced testing capabilities

### 6.2 Mobile Security Tests

**Static Analysis Tests**:
- Test for hard-coded secrets
- Test for insecure data storage
- Test for insecure communication
- Test for insecure cryptography
- Test for component vulnerabilities

**Dynamic Analysis Tests**:
- Test for insecure data storage
- Test for insecure communication
- Test for insecure cryptography
- Test for component vulnerabilities
- Test for root/jailbreak detection

**Manual Tests**:
- Test secure token storage
- Test device binding
- Test biometric authentication
- Test app lock
- Test screenshot prevention
- Test clipboard security
- Test local cache protection
- Test secure logout

### 6.3 Mobile Security Test Execution

**Pre-Production**:
- Run against staging build
- Run before production deployment
- Run after major changes

### 6.4 Mobile Security Test Reporting

**Reporting**:
- Static analysis report
- Dynamic analysis report
- Manual test results
- Vulnerability report
- Remediation recommendations

### 6.5 Mobile Security Test Acceptance Criteria

**Criteria**:
- No critical vulnerabilities
- No high vulnerabilities
- No hard-coded secrets
- Secure token storage validated
- Device binding validated
- Biometric authentication validated

---

## 7. Authentication Testing

### 7.1 Authentication Tests

**Registration Tests**:
- Test registration with valid email
- Test registration with invalid email
- Test registration with weak password
- Test registration with strong password
- Test registration with duplicate email
- Test email verification flow
- Test password hash storage (not plaintext)

**Login Tests**:
- Test login with valid credentials
- Test login with invalid credentials
- Test failed login lockout (5 attempts)
- Test account lockout duration (10 minutes)
- Test token generation
- Test token validation
- Test token expiration (15 minutes)
- Test refresh token generation
- Test refresh token validation
- Test refresh token expiration (30 days)
- Test refresh token rotation

**OTP Tests**:
- Test OTP generation
- Test OTP delivery
- Test OTP verification
- Test OTP expiration (10 minutes)
- Test OTP retry limit (3 attempts)
- Test OTP resend limit (3 resends within 10 minutes)
- Test OTP hash storage (not plaintext)

**Session Tests**:
- Test session creation
- Test session expiration (15 minutes inactivity)
- Test session revocation
- Test multi-device sessions
- Test device fingerprinting

**Device Tests**:
- Test device registration
- Test device removal
- Test device token validation
- Test suspicious device detection

**Logout Tests**:
- Test single device logout
- Test all devices logout
- Test token revocation
- Test session invalidation

### 7.2 Authentication Test Execution

**Pre-Production**:
- Run against staging environment
- Run before production deployment
- Run after major changes

### 7.3 Authentication Test Reporting

**Reporting**:
- Test results report
- Vulnerability report
- Remediation recommendations

### 7.4 Authentication Test Acceptance Criteria

**Criteria**:
- All registration tests passing
- All login tests passing
- All OTP tests passing
- All session tests passing
- All device tests passing
- All logout tests passing

---

## 8. Authorization Testing

### 8.1 Authorization Tests

**User Authorization Tests**:
- Test user access to own profile
- Test user access to own transactions
- Test user access to own budgets
- Test user access to own savings goals
- Test user access to own bills
- Test user access to own notifications
- Test user denied access to other user data

**Family Authorization Tests**:
- Test owner invite family member
- Test owner remove family member
- Test owner change member role
- Test member denied invite/remove
- Test member view family data
- Test member modify own data
- Test member denied modify other member data
- Test restricted member limited access
- Test restricted member denied modify

**Resource Authorization Tests**:
- Test user modify own resource
- Test user denied modify other user resource
- Test family member modify family resource
- Test family member denied modify other family resource
- Test IDOR prevention
- Test cross-family access prevention

**Role Authorization Tests**:
- Test owner full access
- Test member view and modify access
- Test restricted limited access
- Test role change authorization

### 8.2 Authorization Test Execution

**Pre-Production**:
- Run against staging environment
- Run before production deployment
- Run after major changes

### 8.3 Authorization Test Reporting

**Reporting**:
- Test results report
- Authorization violation report
- Remediation recommendations

### 8.4 Authorization Test Acceptance Criteria

**Criteria**:
- All user authorization tests passing
- All family authorization tests passing
- All resource authorization tests passing
- All role authorization tests passing
- No IDOR vulnerabilities
- No cross-family access vulnerabilities

---

## 9. AI Security Testing

### 9.1 AI Security Tests

**AI Data Access Tests**:
- Test AI no direct database access
- Test AI tool authorization
- Test AI tool read-only enforcement
- Test AI tool parameter validation
- Test AI tool audit logging

**AI Read-Only Tests**:
- Test AI no payment execution
- Test AI no financial data modification
- Test AI no permission changes
- Test AI no cross-family access
- Test AI recommendation approval required

**Prompt Injection Tests**:
- Test prompt injection prevention
- Test input validation
- Test context isolation
- Test tool access control
- Test output validation
- Test confidence thresholds

**AI Data Minimization Tests**:
- Test data minimization in AI requests
- Test context minimization
- Test no sensitive data in AI requests
- Test conversation deletion

**AI Rate Limiting Tests**:
- Test AI chat rate limiting (20 req/min)
- Test AI token limits
- Test AI request size limits
- Test AI cost controls

**AI Audit Tests**:
- Test AI tool invocation audit
- Test AI recommendation audit
- Test AI conversation audit
- Test prompt injection attempt audit

### 9.2 AI Security Test Execution

**Pre-Production**:
- Run against staging environment
- Run before production deployment
- Run after major changes

### 9.3 AI Security Test Reporting

**Reporting**:
- Test results report
- Vulnerability report
- Remediation recommendations

### 9.4 AI Security Test Acceptance Criteria

**Criteria**:
- All AI data access tests passing
- All AI read-only tests passing
- All prompt injection tests passing
- All AI data minimization tests passing
- All AI rate limiting tests passing
- All AI audit tests passing

---

## 10. Prompt Injection Testing

### 10.1 Prompt Injection Tests

**User Prompt Injection Tests**:
- Test prompt injection via user input
- Test instruction override attempts
- Test system prompt bypass attempts
- Test context isolation

**Transaction Description Injection Tests**:
- Test prompt injection via transaction description
- Test special character handling
- Test length limits

**Vendor Data Injection Tests**:
- Test prompt injection via vendor data
- Test data sanitization
- Test format validation

**Retrieved Document Injection Tests**:
- Test prompt injection via retrieved documents
- Test document sanitization
- Test format validation

**Tool Manipulation Tests**:
- Test tool access bypass attempts
- Test tool parameter manipulation
- Test tool result manipulation

### 10.2. Prompt Injection Test Execution

**Pre-Production**:
- Run against staging environment
- Run before production deployment
- Run after major changes

### 10.3 Prompt Injection Test Reporting

**Reporting**:
- Test results report
- Vulnerability report
- Remediation recommendations

### 10.4 Prompt Injection Test Acceptance Criteria

**Criteria**:
- All user prompt injection tests passing
- All transaction description injection tests passing
- All vendor data injection tests passing
- All retrieved document injection tests passing
- All tool manipulation tests passing

---

## 11. Penetration Testing

### 11.1 Penetration Testing Scope

**In Scope**:
- Mobile application (Flutter)
- API (Spring Boot)
- Database (PostgreSQL)
- Infrastructure (Google Cloud Run)
- CI/CD (GitHub Actions)

**Out of Scope**:
- Physical security
- Social engineering
- Third-party provider security

### 11.2 Penetration Testing Methodology

**Methodology**:
- Reconnaissance
- Scanning
- Enumeration
- Vulnerability assessment
- Exploitation
- Post-exploitation
- Reporting

### 11.3 Penetration Testing Phases

**Phase 1: Reconnaissance**:
- Information gathering
- Open source intelligence (OSINT)
- Network mapping

**Phase 2: Scanning**:
- Port scanning
- Service scanning
- Vulnerability scanning

**Phase 3: Enumeration**:
- Service enumeration
- User enumeration
- Resource enumeration

**Phase 4: Vulnerability Assessment**:
- Manual vulnerability identification
- Automated vulnerability scanning
- Configuration review

**Phase 5: Exploitation**:
- Vulnerability exploitation
- Privilege escalation
- Lateral movement

**Phase 6: Post-Exploitation**:
- Data exfiltration
- Persistence
- Evidence collection

**Phase 7: Reporting**:
- Vulnerability report
- Remediation recommendations
- Retesting

### 11.4 Penetration Testing Rules of Engagement

**Rules**:
- No destructive testing
- No data deletion
- No data modification
- No denial of service
- No social engineering
- No physical security testing
- Testing limited to staging environment
- Testing limited to approved scope

### 11.5 Penetration Testing Execution

**Timing**:
- Pre-production
- Before production deployment
- After major changes
- Annually (optional)

### 11.6 Penetration Testing Reporting

**Report Contents**:
- Executive summary
- Methodology
- Findings
- Severity classification
- Remediation recommendations
- Retesting results

### 11.7 Penetration Testing Acceptance Criteria

**Criteria**:
- No critical vulnerabilities
- No high vulnerabilities
- All findings addressed
- All remediations implemented
- All retests passing

---

## 12. Performance/Security Testing

### 12.1 Performance/Security Tests

**Rate Limiting Under Load**:
- Test rate limiting under high load
- Test rate limiting bypass attempts under load
- Test system stability under rate limiting

**Authentication Under Load**:
- Test authentication under high load
- Test token generation under high load
- Test token validation under high load

**Authorization Under Load**:
- Test authorization under high load
- Test family membership verification under load
- Test resource ownership checks under load

**AI Under Load**:
- Test AI rate limiting under high load
- Test AI tool invocation under high load
- Test AI response time under high load

### 12.2 Performance/Security Test Execution

**Pre-Production**:
- Run against staging environment
- Run before production deployment
- Run after major changes

### 12.3 Performance/Security Test Reporting

**Reporting**:
- Test results report
- Performance metrics
- Security metrics
- Remediation recommendations

### 12.4 Performance/Security Test Acceptance Criteria

**Criteria**:
- All rate limiting tests passing under load
- All authentication tests passing under load
- All authorization tests passing under load
- All AI tests passing under load
- System stable under load

---

## 13. Security Test Schedule

### 13.1 Continuous Testing

**SAST**:
- Every push to main branch
- Every pull request
- Every commit

**Dependency Scanning**:
- Every push to main branch
- Every pull request
- Every dependency update

**Secret Scanning**:
- Every push to main branch
- Every pull request
- Every commit

### 13.2 Pre-Production Testing

**DAST**:
- Before production deployment
- After major changes
- Weekly (optional)

**API Security Testing**:
- Before production deployment
- After major changes
- Weekly (optional)

**Mobile Security Testing**:
- Before production deployment
- After major changes

**Authentication Testing**:
- Before production deployment
- After major changes

**Authorization Testing**:
- Before production deployment
- After major changes

**AI Security Testing**:
- Before production deployment
- After major changes

**Prompt Injection Testing**:
- Before production deployment
- After major changes

**Penetration Testing**:
- Before production deployment
- Annually (optional)

**Performance/Security Testing**:
- Before production deployment
- After major changes

---

## 14. Security Test Tools

### 14.1 Tool Summary

| Tool | Purpose | Category |
|------|---------|----------|
| GitHub Code Scanning | SAST | SAST |
| SonarQube | SAST | SAST |
| GitHub Dependabot | Dependency Scanning | Dependency Scanning |
| OWASP Dependency-Check | Dependency Scanning | Dependency Scanning |
| OWASP ZAP | DAST | DAST |
| Burp Suite | DAST | DAST |
| Postman | API Security Testing | API Security |
| OWASP ZAP API | API Security Testing | API Security |
| MobSF | Mobile Security Testing | Mobile Security |
| AppScan | Mobile Security Testing | Mobile Security |

### 14.2 Tool Configuration

**GitHub Code Scanning**:
- Enable for all repositories
- Configure custom security rules
- Block on critical vulnerabilities

**GitHub Dependabot**:
- Enable for all repositories
- Configure dependency updates
- Configure vulnerability alerts

**OWASP ZAP**:
- Configure for API testing
- Configure authentication
- Configure scan scope

**MobSF**:
- Configure for Android and iOS
- Configure static analysis
- Configure dynamic analysis

---

## 15. Security Test Reporting

### 15.1 Reporting Structure

**Executive Summary**:
- Test overview
- Test results summary
- Critical findings
- Recommendations

**Detailed Findings**:
- Vulnerability description
- Severity classification
- Affected components
- Reproduction steps
- Remediation recommendations

**Test Coverage**:
- Test coverage matrix
- Uncovered areas
- Recommendations

**Remediation Status**:
- Open vulnerabilities
- In-progress remediations
- Closed vulnerabilities

**Retesting Results**:
- Retested vulnerabilities
- Retest results
- Remaining issues

### 15.2 Severity Classification

**Critical**:
- System compromise
- Data breach
- Financial loss
- CVSS 7.0+

**High**:
- Significant impact
- Partial data breach
- CVSS 4.0-6.9

**Medium**:
- Limited impact
- No data breach
- CVSS 1.0-3.9

**Low**:
- Minimal impact
- No data breach
- CVSS 0.0-0.9

### 15.3 Reporting Frequency

**Continuous**:
- SAST: Every push/PR
- Dependency Scanning: Every push/PR
- Secret Scanning: Every push/PR

**Pre-Production**:
- DAST: Before deployment
- API Security: Before deployment
- Mobile Security: Before deployment
- Authentication: Before deployment
- Authorization: Before deployment
- AI Security: Before deployment
- Prompt Injection: Before deployment
- Penetration Testing: Before deployment
- Performance/Security: Before deployment

**Periodic**:
- Weekly security summary
- Monthly security review
- Quarterly penetration testing (optional)

---

## 16. Security Test Acceptance Criteria

### 16.1 Production Acceptance Criteria

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

### 16.2 Pre-Production Checklist

**Checklist**:
- [ ] SAST passing
- [ ] Dependency scanning passing
- [ ] Secret scanning passing
- [ ] DAST passing
- [ ] API security testing passing
- [ ] Mobile security testing passing
- [ ] Authentication testing passing
- [ ] Authorization testing passing
- [ ] AI security testing passing
- [ ] Prompt injection testing passing
- [ ] Penetration testing passing
- [ ] Performance/security testing passing

---

## 17. Conclusion

The NeoWallet Security Test Strategy provides comprehensive security testing requirements for NeoWallet MVP. All security aspects are covered including SAST, DAST, dependency scanning, API security testing, mobile security testing, authentication testing, authorization testing, AI security testing, prompt injection testing, penetration testing, and performance/security testing.

**Next Steps**:
1. Create Security Validation Report
2. Product Owner approval

# NeoWallet Compliance MVP Blocking Analysis v1

## Executive Summary

This document classifies the 28 identified compliance gaps according to whether they actually block NeoWallet MVP engineering or only block specific capabilities or future phases.

**IMPORTANT DISCLAIMER**: This document is a classification analysis only. No legal conclusions are provided. NeoWallet is NOT claimed to be legally or regulatorily compliant.

**Date**: August 18, 2026
**Status**: Ready for Product Owner Approval

---

## 1. MVP Definition

### 1.1 Approved MVP Definition

**NeoWallet MVP**:
- Does NOT hold customer funds
- Does NOT issue PPI/stored value
- Does NOT provide wallet-to-wallet transfers
- Does NOT provide cash-out
- Does NOT execute payments
- Does NOT provide actual bill payment
- Uses manual transaction entry
- Provides budgeting and financial planning
- Provides financial health analytics
- Provides read-only/recommendation-only Neo AI
- Does not autonomously move money

**MVP Exclusions**:
- No merchant payments
- No payment orchestration
- No UPI
- No BBPS
- No payment aggregation
- No wallet/stored value
- No P2P transfers
- No money movement

---

## 2. Gap Classification

### 2.1 All 28 Gaps Classification

| Gap ID | Description | Severity | Affected Feature | Affected Phase | MVP Blocking? | Engineering Blocking? | Production Launch Blocking? | Future Phase Blocking? | Required Professional Review? | Owner | Required Action | Target Phase |
|--------|-------------|----------|-----------------|---------------|--------------|----------------------|-------------------------|-----------------------|------------------------------|-------|----------------|--------------|
| GAP-C-001 | Payment Orchestration Model Legal Review | CRITICAL | Payment Orchestration | Phase 1 | NO | NO | NO | YES | YES | Legal/Compliance Team | Professional legal/regulatory review | Phase 1 |
| GAP-C-002 | PPI/Stored-Value Boundary Legal Review | CRITICAL | Product Model | MVP | NO | NO | NO | NO | YES | Legal/Compliance Team | Professional legal/regulatory review | MVP |
| GAP-C-003 | Payment Aggregation Legal Review | CRITICAL | Payment Aggregation | Phase 1 | NO | NO | NO | YES | YES | Legal/Compliance Team | Professional legal/regulatory review | Phase 1 |
| GAP-C-004 | UPI Integration Legal Review | CRITICAL | UPI Integration | Phase 2 | NO | NO | NO | YES | YES | Legal/Compliance Team | Professional legal/regulatory review, NPCI approval | Phase 2 |
| GAP-C-005 | BBPS Integration Legal Review | CRITICAL | BBPS Integration | Phase 2 | NO | NO | NO | YES | YES | Legal/Compliance Team | Professional legal/regulatory review, NPCI approval | Phase 2 |
| GAP-C-006 | KYC/AML Legal Review | CRITICAL | User Registration | MVP | NO | NO | CONDITIONAL | NO | YES | Legal/Compliance Team | Professional legal/regulatory review | MVP |
| GAP-C-007 | Data Protection Legal Review | CRITICAL | Data Privacy | MVP | NO | NO | CONDITIONAL | NO | YES | Legal/Compliance Team | Professional legal/regulatory review | MVP |
| GAP-C-008 | AI Financial Recommendations Legal Review | CRITICAL | AI Recommendations | MVP | NO | NO | CONDITIONAL | NO | YES | Legal/Compliance Team | Professional legal/regulatory review | MVP |
| GAP-C-009 | Data Retention Legal Review | CRITICAL | Data Retention | MVP | NO | NO | CONDITIONAL | NO | YES | Legal/Compliance Team | Professional legal/regulatory review | MVP |
| GAP-C-010 | Consumer Protection Legal Review | CRITICAL | Consumer Protection | MVP | NO | NO | CONDITIONAL | NO | YES | Legal/Compliance Team | Professional legal/regulatory review | MVP |
| GAP-C-011 | Cross-Border Data/Provider Legal Review | CRITICAL | Cross-Border Data | Future | NO | NO | NO | YES | YES | Legal/Compliance Team | Professional legal/regulatory review | Future |
| GAP-C-012 | Future Money Movement Legal Review | CRITICAL | Money Movement | Future | NO | NO | NO | YES | YES | Legal/Compliance Team | Professional legal/regulatory review | Future |
| GAP-H-001 | AI Provider Selection | HIGH | AI Service | MVP | NO | YES | YES | NO | NO | Product/Engineering Team | AI provider selection | MVP |
| GAP-H-002 | OTP Provider Selection | HIGH | Authentication | MVP | NO | YES | YES | NO | NO | Product/Engineering Team | OTP provider selection | MVP |
| GAP-H-003 | Email Provider Selection | HIGH | Communication | MVP | NO | YES | YES | NO | NO | Product/Engineering Team | Email provider selection | MVP |
| GAP-H-004 | SMS/WhatsApp Provider Selection | HIGH | Authentication | MVP | NO | YES | YES | NO | NO | Product/Engineering Team | SMS/WhatsApp provider selection | MVP |
| GAP-H-005 | Grievance Handling Mechanism | HIGH | Consumer Protection | MVP | NO | NO | CONDITIONAL | NO | NO | Product/Engineering Team | Grievance handling implementation | MVP |
| GAP-H-006 | Data Portability | HIGH | Data Privacy | Future | NO | NO | NO | YES | NO | Product/Engineering Team | Data portability implementation | Future |
| GAP-H-007 | CERT-In Reporting Process | HIGH | Security Incident Response | MVP | NO | NO | CONDITIONAL | NO | YES | Security/Compliance Team | CERT-In reporting process definition | MVP |
| GAP-H-008 | Marketing/Product Language Review | HIGH | Marketing | MVP | NO | NO | YES | NO | YES | Marketing/Legal Team | Marketing/product language legal review | MVP |
| GAP-M-001 | Notifications Retention Period | MEDIUM | Data Retention | MVP | NO | NO | NO | NO | YES | Compliance Team | Compliance/legal decision | MVP |
| GAP-M-002 | AI Conversations Retention Period | MEDIUM | Data Retention | MVP | NO | NO | NO | NO | YES | Compliance Team | Compliance/legal decision | MVP |
| GAP-M-003 | Security Logs Retention Period | MEDIUM | Data Retention | MVP | NO | NO | NO | NO | YES | Compliance Team | Compliance/legal decision | MVP |
| GAP-M-004 | Subscription Terms | MEDIUM | Subscription | Future | NO | NO | NO | YES | YES | Product/Legal Team | Subscription terms definition | Future |
| GAP-M-005 | Refund Policy | MEDIUM | Refund | Future | NO | NO | NO | YES | YES | Product/Legal Team | Refund policy definition | Future |
| GAP-L-001 | Analytics Provider Selection | LOW | Analytics | Future | NO | NO | NO | YES | NO | Product/Engineering Team | Analytics provider selection (if applicable) | Future |
| GAP-L-002 | Push Provider Selection | LOW | Notifications | MVP | NO | YES | YES | NO | NO | Product/Engineering Team | Push provider selection | MVP |
| GAP-L-003 | Tax/GST Considerations | LOW | Tax/GST | Future | NO | NO | NO | YES | YES | Finance/Legal Team | Tax/GST review (future) | Future |

---

## 3. MVP Boundary Analysis

### 3.1 MVP-Affected Gaps

**Gaps That Affect MVP**:

| Gap ID | Description | MVP Blocking? | Engineering Blocking? | Production Launch Blocking? | Reason |
|--------|-------------|--------------|----------------------|-------------------------|---------|
| GAP-C-006 | KYC/AML Legal Review | NO | NO | CONDITIONAL | MVP collects PII, but does not hold funds or execute payments. Professional review required to confirm KYC/AML obligations. |
| GAP-C-007 | Data Protection Legal Review | NO | NO | CONDITIONAL | MVP collects personal data. Professional review required to confirm DPDP Act obligations. |
| GAP-C-008 | AI Financial Recommendations Legal Review | NO | NO | CONDITIONAL | MVP provides AI financial recommendations. Professional review required to confirm regulatory obligations. |
| GAP-C-009 | Data Retention Legal Review | NO | NO | CONDITIONAL | MVP retains data. Professional review required to confirm statutory retention requirements. |
| GAP-C-010 | Consumer Protection Legal Review | NO | NO | CONDITIONAL | MVP provides financial management services. Professional review required to confirm consumer protection obligations. |
| GAP-H-001 | AI Provider Selection | NO | YES | YES | MVP requires AI provider for AI service. |
| GAP-H-002 | OTP Provider Selection | NO | YES | YES | MVP requires OTP provider for authentication. |
| GAP-H-003 | Email Provider Selection | NO | YES | YES | MVP requires email provider for communication. |
| GAP-H-004 | SMS/WhatsApp Provider Selection | NO | YES | YES | MVP requires SMS/WhatsApp provider for OTP delivery. |
| GAP-H-005 | Grievance Handling Mechanism | NO | NO | CONDITIONAL | Consumer protection may require grievance handling. |
| GAP-H-007 | CERT-In Reporting Process | NO | NO | CONDITIONAL | Cybersecurity incident reporting may be required. |
| GAP-H-008 | Marketing/Product Language Review | NO | NO | YES | Marketing materials must be reviewed before production launch. |
| GAP-M-001 | Notifications Retention Period | NO | NO | NO | Engineering proposal. Compliance decision required. |
| GAP-M-002 | AI Conversations Retention Period | NO | NO | NO | Engineering proposal. Compliance decision required. |
| GAP-M-003 | Security Logs Retention Period | NO | NO | NO | Engineering proposal. Compliance decision required. |
| GAP-L-002 | Push Provider Selection | NO | YES | YES | MVP requires push provider for notifications. |

### 3.2 MVP-Not-Affected Gaps

**Gaps That Do NOT Affect MVP**:

| Gap ID | Description | Reason |
|--------|-------------|---------|
| GAP-C-001 | Payment Orchestration Model Legal Review | Payment orchestration is a future capability (Phase 1). |
| GAP-C-002 | PPI/Stored-Value Boundary Legal Review | MVP does NOT issue PPI/stored value. Professional review to confirm non-applicability. |
| GAP-C-003 | Payment Aggregation Legal Review | Payment aggregation is a future capability (Phase 1). |
| GAP-C-004 | UPI Integration Legal Review | UPI integration is a future capability (Phase 2). |
| GAP-C-005 | BBPS Integration Legal Review | BBPS integration is a future capability (Phase 2). |
| GAP-C-011 | Cross-Border Data/Provider Legal Review | Cross-border data/provider issues are future considerations. |
| GAP-C-012 | Future Money Movement Legal Review | Money movement is a future capability. |
| GAP-H-006 | Data Portability | Data portability is a future capability. |
| GAP-M-004 | Subscription Terms | Subscription is a future capability. |
| GAP-M-005 | Refund Policy | Refund is a future capability. |
| GAP-L-001 | Analytics Provider Selection | Analytics provider is optional for MVP. |
| GAP-L-003 | Tax/GST Considerations | Tax/GST considerations are future considerations. |

---

## 4. Future Payment Features

### 4.1 Future Payment Feature Gaps

**Gaps That Become Relevant When Future Payment Features Are Added**:

| Gap ID | Description | Relevant When Feature Added |
|--------|-------------|---------------------------|
| GAP-C-001 | Payment Orchestration Model Legal Review | Payment orchestration (Phase 1) |
| GAP-C-003 | Payment Aggregation Legal Review | Payment aggregation (Phase 1) |
| GAP-C-004 | UPI Integration Legal Review | UPI integration (Phase 2) |
| GAP-C-005 | BBPS Integration Legal Review | BBPS integration (Phase 2) |
| GAP-C-011 | Cross-Border Data/Provider Legal Review | Cross-border data/provider (Future) |
| GAP-C-012 | Future Money Movement Legal Review | Money movement (Future) |

**These gaps do NOT block the financial-management MVP.**

---

## 5. AI Classification

### 5.1 AI-Related Compliance Requirements

**MVP AI Definition**:
- Read-only
- Recommendation-only
- No autonomous financial execution
- No money movement
- No payment execution

**AI Compliance Controls Classification**:

| Control | Classification | Reason |
|---------|---------------|---------|
| AI Read-Only Enforcement | MVP Mandatory | AI must be read-only for MVP. |
| AI Recommendation Disclaimers | MVP Mandatory | AI recommendations require disclaimers. |
| AI Data Minimization | MVP Mandatory | AI data minimization required for MVP. |
| AI Audit Logging | MVP Mandatory | AI tool invocations must be audited. |
| AI Provider Data Handling Review | MVP Mandatory | AI provider data handling must be reviewed. |
| AI Transparency | MVP Recommended | AI transparency is recommended but not blocking. |
| AI Hallucination Mitigation | MVP Recommended | AI hallucination mitigation is recommended but not blocking. |
| AI Human Escalation | MVP Recommended | AI human escalation is recommended but not blocking. |
| AI Financial Recommendations Legal Review | MVP Production | Professional legal review required before production. |
| AI Model Training Opt-Out | MVP Production | Model training opt-out required before production. |

---

## 6. Data Retention Review

### 6.1 Data Retention Period Classification

| Data Category | Retention Period | Classification | Reason |
|---------------|-------------------|----------------|---------|
| User Data | 7 years after deletion | Engineering Proposal | Business rule defined. Legal verification required. |
| Authentication Records | 7 years after deletion | Engineering Proposal | Business rule defined. Legal verification required. |
| Transactions | 7 years | Engineering Proposal | Business rule defined. Legal verification required. |
| Budgets | 7 years | Engineering Proposal | Business rule defined. Legal verification required. |
| Allocations | 7 years | Engineering Proposal | Business rule defined. Legal verification required. |
| Savings | 7 years after completion | Engineering Proposal | Business rule defined. Legal verification required. |
| Bills | 7 years | Engineering Proposal | Business rule defined. Legal verification required. |
| Notifications | 90 days | Engineering Proposal | Engineering proposal. Compliance decision required. |
| AI Conversations | 1 year | Engineering Proposal | Engineering proposal. Compliance decision required. |
| Audit Logs | 7 years | Engineering Proposal | Business rule defined. Legal verification required. |
| Security Logs | 90 days | Engineering Proposal | Engineering proposal. Compliance decision required. |
| Consent Records | 7 years | Engineering Proposal | Business rule defined. Legal verification required. |

**Important**: No statutory retention periods are claimed. All retention periods are engineering proposals or business rules. Legal verification required.

### 6.2 Configurable Retention Settings

**Retention Periods Requiring Configuration**:
- Notifications retention: 90 days (configurable)
- AI conversations retention: 1 year (configurable)
- Security logs retention: 90 days (configurable)

**Implementation**:
- Use configurable retention settings in application configuration
- Allow retention periods to be adjusted based on compliance/legal decisions
- Document retention configuration in deployment documentation

---

## 7. Privacy Controls

### 7.1 MVP Privacy Controls

**MVP Privacy Controls That Must Exist Before Production**:

| Control | MVP Required | Implementation Status |
|---------|--------------|----------------------|
| Privacy Notice | YES | To be implemented |
| User Consent/Preferences | YES | Partially implemented (user_preferences table) |
| Data Access | YES | Implemented (API access controls) |
| Data Correction | YES | Implemented (API update endpoints) |
| Data Deletion | YES | Implemented (API delete endpoint) |
| Data Retention | YES | Implemented (business rules) |
| Data Minimization | YES | Implemented (data dictionary) |
| Data Security | YES | Implemented (encryption, access controls) |
| Third-Party Processors | YES | Documented (provider requirements) |
| AI Provider Data Handling | YES | Documented (provider requirements) |
| User Grievance Process | CONDITIONAL | Not implemented (GAP-H-005) |

### 7.2 Privacy Control Implementation Status

**Implemented**:
- Data Access: API access controls implemented
- Data Correction: API update endpoints implemented
- Data Deletion: API delete endpoint implemented
- Data Retention: Business rules implemented
- Data Minimization: Data dictionary implemented
- Data Security: Encryption and access controls implemented
- Third-Party Processors: Provider requirements documented
- AI Provider Data Handling: Provider requirements documented

**To Be Implemented**:
- Privacy Notice: To be implemented
- User Consent/Preferences: Partially implemented (user_preferences table), UI to be implemented
- User Grievance Process: Not implemented (GAP-H-005)

---

## 8. Provider Requirements

### 8.1 Provider-Dependent Requirements

**Provider Selection Required for Engineering**:
- AI Provider (GAP-H-001): Required for AI service engineering
- OTP Provider (GAP-H-002): Required for authentication engineering
- Email Provider (GAP-H-003): Required for communication engineering
- SMS/WhatsApp Provider (GAP-H-004): Required for OTP delivery engineering
- Push Provider (GAP-L-002): Required for notifications engineering

**Provider Selection Required for Production Launch**:
- AI Provider (GAP-H-001): Required for AI service production
- OTP Provider (GAP-H-002): Required for authentication production
- Email Provider (GAP-H-003): Required for communication production
- SMS/WhatsApp Provider (GAP-H-004): Required for OTP delivery production
- Push Provider (GAP-L-002): Required for notifications production

**Provider Selection Not Required for MVP Engineering**:
- Payment Provider: Not required for MVP (no payment capabilities)
- Analytics Provider: Optional for MVP

### 8.2 Provider Selection Timeline

**Engineering Phase**:
- AI Provider: Must be selected before AI service engineering
- OTP Provider: Must be selected before authentication engineering
- Email Provider: Must be selected before communication engineering
- SMS/WhatsApp Provider: Must be selected before OTP delivery engineering
- Push Provider: Must be selected before notifications engineering

**Production Phase**:
- All providers must be selected and configured before production launch

---

## 9. Legal Review Classification

### 9.1 Professional Review Required Before MVP Production

**Professional Review Required Before MVP Production**:

| Review Area | MVP Blocking? | Engineering Blocking? | Production Launch Blocking? | Reason |
|-------------|--------------|----------------------|-------------------------|---------|
| PPI/Stored-Value Boundary | NO | NO | NO | MVP does NOT issue PPI/stored value. Professional review to confirm non-applicability. |
| KYC/AML | NO | NO | CONDITIONAL | MVP collects PII. Professional review required to confirm KYC/AML obligations. |
| Data Protection | NO | NO | CONDITIONAL | MVP collects personal data. Professional review required to confirm DPDP Act obligations. |
| AI Financial Recommendations | NO | NO | CONDITIONAL | MVP provides AI financial recommendations. Professional review required to confirm regulatory obligations. |
| Data Retention | NO | NO | CONDITIONAL | MVP retains data. Professional review required to confirm statutory retention requirements. |
| Consumer Protection | NO | NO | CONDITIONAL | MVP provides financial management services. Professional review required to confirm consumer protection obligations. |
| Marketing/Product Language | NO | NO | YES | Marketing materials must be reviewed before production launch. |
| CERT-In Reporting Process | NO | NO | CONDITIONAL | Cybersecurity incident reporting may be required. |

### 9.2 Professional Review Required Before Future Payment Features

**Professional Review Required Before Future Payment Features**:

| Review Area | Future Phase Blocking? | Reason |
|-------------|-----------------------|---------|
| Payment Orchestration Model | YES | Payment orchestration is a future capability (Phase 1). |
| Payment Aggregation | YES | Payment aggregation is a future capability (Phase 1). |
| UPI Integration | YES | UPI integration is a future capability (Phase 2). |
| BBPS Integration | YES | BBPS integration is a future capability (Phase 2). |
| Cross-Border Data/Provider Issues | YES | Cross-border data/provider issues are future considerations. |
| Future Money Movement | YES | Money movement is a future capability. |

---

## 10. Release Gates

### 10.1 MVP Engineering Gate

**Compliance Conditions**:
- ✅ MVP product model defined (does NOT hold funds, does NOT issue PPI, does NOT execute payments)
- ✅ MVP privacy controls designed (data access, data correction, data deletion, data minimization, data security)
- ✅ MVP AI controls designed (read-only, recommendation-only, no autonomous execution)
- ✅ MVP data retention designed (configurable retention settings)
- ✅ MVP security controls designed (encryption, access controls, audit logging)
- ⚠️ Provider selection required for engineering (AI, OTP, Email, SMS/WhatsApp, Push)
- ⚠️ Professional legal review recommended (not blocking engineering)

**Gate Status**: GO

**Rationale**: No compliance gaps block MVP engineering. Provider selection required for engineering can proceed in parallel. Professional legal review is recommended but not blocking engineering.

### 10.2 MVP UAT Gate

**Compliance Conditions**:
- ✅ MVP product model validated (does NOT hold funds, does NOT issue PPI, does NOT execute payments)
- ✅ MVP privacy controls implemented (data access, data correction, data deletion, data minimization, data security)
- ✅ MVP AI controls implemented (read-only, recommendation-only, no autonomous execution)
- ✅ MVP data retention implemented (configurable retention settings)
- ✅ MVP security controls implemented (encryption, access controls, audit logging)
- ✅ All providers selected and configured (AI, OTP, Email, SMS/WhatsApp, Push)
- ⚠️ Professional legal review recommended (not blocking UAT)
- ⚠️ Privacy notice to be implemented
- ⚠️ User consent/preferences UI to be implemented
- ⚠️ Grievance handling mechanism (conditional - consumer protection may require)

**Gate Status**: GO WITH CONDITIONS

**Rationale**: MVP can proceed with UAT with conditions. Professional legal review is recommended but not blocking UAT. Privacy notice and user consent/preferences UI must be implemented before production. Grievance handling mechanism is conditional based on consumer protection requirements.

### 10.3 MVP Production Gate

**Compliance Conditions**:
- ✅ MVP product model validated (does NOT hold funds, does NOT issue PPI, does NOT execute payments)
- ✅ MVP privacy controls implemented (data access, data correction, data deletion, data minimization, data security)
- ✅ MVP AI controls implemented (read-only, recommendation-only, no autonomous execution)
- ✅ MVP data retention implemented (configurable retention settings)
- ✅ MVP security controls implemented (encryption, access controls, audit logging)
- ✅ All providers selected and configured (AI, OTP, Email, SMS/WhatsApp, Push)
- ✅ Privacy notice implemented
- ✅ User consent/preferences UI implemented
- ⚠️ Professional legal review required (KYC/AML, Data Protection, AI Financial Recommendations, Data Retention, Consumer Protection, CERT-In)
- ⚠️ Marketing/product language legal review required
- ⚠️ Grievance handling mechanism (conditional - consumer protection may require)
- ⚠️ Data retention compliance decisions required (Notifications, AI Conversations, Security Logs)

**Gate Status**: GO WITH CONDITIONS

**Rationale**: MVP can proceed with production with conditions. Professional legal review required for 6 areas before production launch. Marketing/product language legal review required. Grievance handling mechanism conditional based on consumer protection requirements. Data retention compliance decisions required for 3 areas.

### 10.4 Phase 1 Payment Gate

**Compliance Conditions**:
- ✅ MVP production gate passed
- ⚠️ Payment orchestration model professional legal review required
- ⚠️ Payment aggregation professional legal review required
- ⚠️ Payment provider selection required
- ⚠️ Payment provider due diligence required
- ⚠️ Payment provider contracts required

**Gate Status**: GO WITH CONDITIONS

**Rationale**: Phase 1 payment features can proceed with conditions. Professional legal review required for payment orchestration model and payment aggregation. Payment provider selection, due diligence, and contracts required.

### 10.5 Phase 2 Money Movement Gate

**Compliance Conditions**:
- ✅ Phase 1 payment gate passed
- ⚠️ UPI integration professional legal review required
- ⚠️ BBPS integration professional legal review required
- ⚠️ NPCI approval required for UPI integration
- ⚠️ NPCI approval required for BBPS integration
- ⚠️ Payment provider selection required
- ⚠️ Payment provider due diligence required
- ⚠️ Payment provider contracts required

**Gate Status**: GO WITH CONDITIONS

**Rationale**: Phase 2 money movement features can proceed with conditions. Professional legal review required for UPI integration and BBPS integration. NPCI approval required for both UPI and BBPS integration. Payment provider selection, due diligence, and contracts required.

---

## 11. Final Recommendation

### 11.1 MVP ENGINEERING

**Recommendation**: GO

**Rationale**:
- No compliance gaps block MVP engineering
- MVP product model clearly defined (does NOT hold funds, does NOT issue PPI, does NOT execute payments)
- MVP privacy controls designed
- MVP AI controls designed
- MVP data retention designed (configurable)
- MVP security controls designed
- Provider selection required for engineering can proceed in parallel
- Professional legal review is recommended but not blocking engineering

**Conditions**:
- Provider selection must proceed in parallel with engineering (AI, OTP, Email, SMS/WhatsApp, Push)
- Professional legal review is recommended but not blocking engineering

### 11.2 MVP PRODUCTION

**Recommendation**: GO WITH CONDITIONS

**Rationale**:
- MVP product model clearly defined (does NOT hold funds, does NOT issue PPI, does NOT execute payments)
- MVP privacy controls must be implemented
- MVP AI controls must be implemented
- MVP data retention must be implemented (configurable)
- MVP security controls must be implemented
- All providers must be selected and configured
- Privacy notice must be implemented
- User consent/preferences UI must be implemented
- Professional legal review required for 6 areas before production launch
- Marketing/product language legal review required
- Grievance handling mechanism conditional
- Data retention compliance decisions required for 3 areas

**Conditions**:
- Professional legal review required before production launch (KYC/AML, Data Protection, AI Financial Recommendations, Data Retention, Consumer Protection, CERT-In)
- Marketing/product language legal review required before production launch
- Privacy notice must be implemented before production launch
- User consent/preferences UI must be implemented before production launch
- Grievance handling mechanism must be implemented if consumer protection requires
- Data retention compliance decisions required before production launch (Notifications, AI Conversations, Security Logs)

### 11.3 PHASE 1 PAYMENT

**Recommendation**: GO WITH CONDITIONS

**Rationale**:
- MVP production gate must be passed
- Payment orchestration model professional legal review required
- Payment aggregation professional legal review required
- Payment provider selection required
- Payment provider due diligence required
- Payment provider contracts required

**Conditions**:
- Professional legal review required before Phase 1 launch (Payment Orchestration Model, Payment Aggregation)
- Payment provider selection, due diligence, and contracts required before Phase 1 launch

### 11.4 PHASE 2 MONEY MOVEMENT

**Recommendation**: GO WITH CONDITIONS

**Rationale**:
- Phase 1 payment gate must be passed
- UPI integration professional legal review required
- BBPS integration professional legal review required
- NPCI approval required for UPI integration
- NPCI approval required for BBPS integration
- Payment provider selection required
- Payment provider due diligence required
- Payment provider contracts required

**Conditions**:
- Professional legal review required before Phase 2 launch (UPI Integration, BBPS Integration)
- NPCI approval required before Phase 2 launch (UPI Integration, BBPS Integration)
- Payment provider selection, due diligence, and contracts required before Phase 2 launch

---

## 12. Items That MUST Be Resolved Before Devin Starts MVP Coding

### 12.1 Engineering Prerequisites

**Provider Selection**:
- AI Provider (GAP-H-001): Must be selected before AI service engineering
- OTP Provider (GAP-H-002): Must be selected before authentication engineering
- Email Provider (GAP-H-003): Must be selected before communication engineering
- SMS/WhatsApp Provider (GAP-H-004): Must be selected before OTP delivery engineering
- Push Provider (GAP-L-002): Must be selected before notifications engineering

**Technical Decisions**:
- Configurable retention settings for Notifications, AI Conversations, Security Logs
- Privacy notice implementation approach
- User consent/preferences UI implementation approach
- Grievance handling mechanism implementation approach (conditional)

### 12.2 Recommended Actions

**Before Devin Starts MVP Coding**:
1. Select AI provider
2. Select OTP provider
3. Select email provider
4. Select SMS/WhatsApp provider
5. Select push provider
6. Define configurable retention settings
7. Design privacy notice
8. Design user consent/preferences UI
9. Design grievance handling mechanism (conditional)

**Optional Actions**:
1. Initiate professional legal review (recommended but not blocking engineering)

---

## 13. Items That May Be Deferred Until Future Phases

### 13.1 Professional Legal Review (MVP Production)

**May Be Deferred Until Before Production Launch**:
- PPI/Stored-Value Boundary Legal Review (GAP-C-002): Can be deferred until before production launch
- KYC/AML Legal Review (GAP-C-006): Can be deferred until before production launch
- Data Protection Legal Review (GAP-C-007): Can be deferred until before production launch
- AI Financial Recommendations Legal Review (GAP-C-008): Can be deferred until before production launch
- Data Retention Legal Review (GAP-C-009): Can be deferred until before production launch
- Consumer Protection Legal Review (GAP-C-010): Can be deferred until before production launch
- CERT-In Reporting Process (GAP-H-007): Can be deferred until before production launch

### 13.2 Future Payment Features

**May Be Deferred Until Future Phases**:
- Payment Orchestration Model Legal Review (GAP-C-001): Deferred until Phase 1
- Payment Aggregation Legal Review (GAP-C-003): Deferred until Phase 1
- UPI Integration Legal Review (GAP-C-004): Deferred until Phase 2
- BBPS Integration Legal Review (GAP-C-005): Deferred until Phase 2
- Cross-Border Data/Provider Legal Review (GAP-C-011): Deferred until Future
- Future Money Movement Legal Review (GAP-C-012): Deferred until Future

### 13.3 Future Capabilities

**May Be Deferred Until Future Phases**:
- Data Portability (GAP-H-006): Deferred until Future
- Subscription Terms (GAP-M-004): Deferred until Future
- Refund Policy (GAP-M-005): Deferred until Future
- Analytics Provider Selection (GAP-L-001): Deferred until Future
- Tax/GST Considerations (GAP-L-003): Deferred until Future

---

## 14. Conclusion

The NeoWallet MVP can proceed with engineering without compliance blocking. All compliance gaps related to future payment features do not block the financial-management MVP. Professional legal review is recommended but not blocking engineering. Professional legal review is required before production launch for 6 areas.

**IMPORTANT DISCLAIMER**: This document is a classification analysis only. No legal conclusions are provided. NeoWallet is NOT claimed to be legally or regulatorily compliant.

**Next Steps**:
1. Update Compliance Gap Register with MVP blocking classification
2. Product Owner approval

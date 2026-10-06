# NeoWallet Compliance Gap Register v1

## Executive Summary

This document identifies compliance gaps for NeoWallet MVP, including severity, impact, required decisions, phase, and status.

**IMPORTANT DISCLAIMER**: This document is a requirements/control mapping and risk assessment only. NeoWallet is NOT claimed to be legally or regulatorily compliant.

**Date**: August 18, 2026
**Status**: Ready for Product Owner Approval

---

## 1. Gap Register Overview

### 1.1 Gap Summary

| Severity | Count | Status |
|----------|-------|--------|
| CRITICAL | 12 | OPEN |
| HIGH | 8 | OPEN |
| MEDIUM | 5 | OPEN |
| LOW | 3 | OPEN |
| **TOTAL** | **28** | **OPEN** |

### 1.2 Gap Status Legend

- **OPEN**: Gap identified, action required
- **IN PROGRESS**: Action in progress
- **CLOSED**: Gap addressed
- **DEFERRED**: Gap deferred to future phase

---

## 2. CRITICAL Gaps

### 2.1 GAP-C-001: Payment Orchestration Model Legal Review

**Gap ID**: GAP-C-001

**Description**: Payment orchestration model requires qualified Indian legal/regulatory review to determine regulatory obligations

**Severity**: CRITICAL

**Impact**: Legal non-compliance, regulatory penalties, inability to launch payment orchestration

**Owner**: Legal/Compliance Team

**Required Decision**: Professional legal/regulatory review required

**Phase**: Phase 1

**Status**: OPEN

**MVP Blocking?**: NO

**Engineering Blocking?**: NO

**Production Launch Blocking?**: NO

**Future Phase Blocking?**: YES

**Required Professional Review?**: YES

**Mitigation**: Document payment orchestration model, engage legal counsel for review

---

### 2.2 GAP-C-002: PPI/Stored-Value Boundary Legal Review

**Gap ID**: GAP-C-002

**Description**: PPI/stored-value boundary requires qualified Indian legal/regulatory review to confirm non-applicability

**Severity**: CRITICAL

**Impact**: Legal non-compliance, regulatory penalties, potential reclassification as PPI issuer

**Owner**: Legal/Compliance Team

**Required Decision**: Professional legal/regulatory review required

**Phase**: MVP

**Status**: OPEN

**MVP Blocking?**: NO

**Engineering Blocking?**: NO

**Production Launch Blocking?**: NO

**Future Phase Blocking?**: NO

**Required Professional Review?**: YES

**Mitigation**: Document MVP payment model, engage legal counsel for review

---

### 2.3 GAP-C-003: Payment Aggregation Legal Review

**Gap ID**: GAP-C-003

**Description**: Payment aggregation requires qualified Indian legal/regulatory review to determine regulatory obligations

**Severity**: CRITICAL

**Impact**: Legal non-compliance, regulatory penalties, inability to launch payment aggregation

**Owner**: Legal/Compliance Team

**Required Decision**: Professional legal/regulatory review required

**Phase**: Phase 1

**Status**: OPEN

**MVP Blocking?**: NO

**Engineering Blocking?**: NO

**Production Launch Blocking?**: NO

**Future Phase Blocking?**: YES

**Required Professional Review?**: YES

**Mitigation**: Document payment aggregation model, engage legal counsel for review

---

### 2.4 GAP-C-004: UPI Integration Legal Review

**Gap ID**: GAP-C-004

**Description**: UPI integration requires NPCI approval and qualified Indian legal/regulatory review

**Severity**: CRITICAL

**Impact**: Legal non-compliance, regulatory penalties, inability to integrate with UPI

**Owner**: Legal/Compliance Team

**Required Decision**: Professional legal/regulatory review required, NPCI approval required

**Phase**: Phase 2

**Status**: OPEN

**MVP Blocking?**: NO

**Engineering Blocking?**: NO

**Production Launch Blocking?**: NO

**Future Phase Blocking?**: YES

**Required Professional Review?**: YES

**Mitigation**: Document UPI integration model, engage legal counsel for review, initiate NPCI approval process

---

### 2.5 GAP-C-005: BBPS Integration Legal Review

**Gap ID**: GAP-C-005

**Description**: BBPS integration requires NPCI approval and qualified Indian legal/regulatory review

**Severity**: CRITICAL

**Impact**: Legal non-compliance, regulatory penalties, inability to integrate with BBPS

**Owner**: Legal/Compliance Team

**Required Decision**: Professional legal/regulatory review required, NPCI approval required

**Phase**: Phase 2

**Status**: OPEN

**MVP Blocking?**: NO

**Engineering Blocking?**: NO

**Production Launch Blocking?**: NO

**Future Phase Blocking?**: YES

**Required Professional Review?**: YES

**Mitigation**: Document BBPS integration model, engage legal counsel for review, initiate NPCI approval process

---

### 2.6 GAP-C-006: KYC/AML Legal Review

**Gap ID**: GAP-C-006

**Description**: KYC/AML obligations require qualified Indian legal/regulatory review to determine NeoWallet obligations

**Severity**: CRITICAL

**Impact**: Legal non-compliance, regulatory penalties, potential KYC/AML requirements

**Owner**: Legal/Compliance Team

**Required Decision**: Professional legal/regulatory review required

**Phase**: MVP

**Status**: OPEN

**MVP Blocking?**: NO

**Engineering Blocking?**: NO

**Production Launch Blocking?**: CONDITIONAL

**Future Phase Blocking?**: NO

**Required Professional Review?**: YES

**Mitigation**: Document MVP data collection, engage legal counsel for KYC/AML review

---

### 2.7 GAP-C-007: Data Protection Legal Review

**Gap ID**: GAP-C-007

**Description**: Data protection obligations under DPDP Act require qualified Indian legal/regulatory review

**Severity**: CRITICAL

**Impact**: Legal non-compliance, regulatory penalties, data breach liability

**Owner**: Legal/Compliance Team

**Required Decision**: Professional legal/regulatory review required

**Phase**: MVP

**Status**: OPEN

**MVP Blocking?**: NO

**Engineering Blocking?**: NO

**Production Launch Blocking?**: CONDITIONAL

**Future Phase Blocking?**: NO

**Required Professional Review?**: YES

**Mitigation**: Document data handling practices, engage legal counsel for DPDP Act review

---

### 2.8 GAP-C-008: AI Financial Recommendations Legal Review

**Gap ID**: GAP-C-008

**Description**: AI financial recommendations require qualified Indian legal/regulatory review to determine regulatory obligations

**Severity**: CRITICAL

**Impact**: Legal non-compliance, regulatory penalties, potential financial advisor licensing requirements

**Owner**: Legal/Compliance Team

**Required Decision**: Professional legal/regulatory review required

**Phase**: MVP

**Status**: OPEN

**MVP Blocking?**: NO

**Engineering Blocking?**: NO

**Production Launch Blocking?**: CONDITIONAL

**Future Phase Blocking?**: NO

**Required Professional Review?**: YES

**Mitigation**: Document AI recommendation model, engage legal counsel for review

---

### 2.9 GAP-C-009: Data Retention Legal Review

**Gap ID**: GAP-C-009

**Description**: Data retention periods require qualified Indian legal/regulatory review to determine statutory requirements

**Severity**: CRITICAL

**Impact**: Legal non-compliance, regulatory penalties, data retention non-compliance

**Owner**: Legal/Compliance Team

**Required Decision**: Professional legal/regulatory review required

**Phase**: MVP

**Status**: OPEN

**MVP Blocking?**: NO

**Engineering Blocking?**: NO

**Production Launch Blocking?**: CONDITIONAL

**Future Phase Blocking?**: NO

**Required Professional Review?**: YES

**Mitigation**: Document data retention matrix, engage legal counsel for retention review

---

### 2.10 GAP-C-010: Consumer Protection Legal Review

**Gap ID**: GAP-C-010

**Description**: Consumer protection obligations require qualified Indian legal/regulatory review

**Severity**: CRITICAL

**Impact**: Legal non-compliance, regulatory penalties, consumer complaints

**Owner**: Legal/Compliance Team

**Required Decision**: Professional legal/regulatory review required

**Phase**: MVP

**Status**: OPEN

**MVP Blocking?**: NO

**Engineering Blocking?**: NO

**Production Launch Blocking?**: CONDITIONAL

**Future Phase Blocking?**: NO

**Required Professional Review?**: YES

**Mitigation**: Document consumer protection practices, engage legal counsel for review

---

### 2.11 GAP-C-011: Cross-Border Data/Provider Legal Review

**Gap ID**: GAP-C-011

**Description**: Cross-border data/provider issues require qualified Indian legal/regulatory review

**Severity**: CRITICAL

**Impact**: Legal non-compliance, regulatory penalties, data transfer non-compliance

**Owner**: Legal/Compliance Team

**Required Decision**: Professional legal/regulatory review required

**Phase**: Future

**Status**: OPEN

**MVP Blocking?**: NO

**Engineering Blocking?**: NO

**Production Launch Blocking?**: NO

**Future Phase Blocking?**: YES

**Required Professional Review?**: YES

**Mitigation**: Document cross-border data flows, engage legal counsel for review

---

### 2.12 GAP-C-012: Future Money Movement Legal Review

**Gap ID**: GAP-C-012

**Description**: Future money movement capabilities require qualified Indian legal/regulatory review

**Severity**: CRITICAL

**Impact**: Legal non-compliance, regulatory penalties, inability to launch money movement

**Owner**: Legal/Compliance Team

**Required Decision**: Professional legal/regulatory review required

**Phase**: Future

**Status**: OPEN

**MVP Blocking?**: NO

**Engineering Blocking?**: NO

**Production Launch Blocking?**: NO

**Future Phase Blocking?**: YES

**Required Professional Review?**: YES

**Mitigation**: Document future money movement model, engage legal counsel for review

---

## 3. HIGH Gaps

### 3.1 GAP-H-001: AI Provider Selection

**Gap ID**: GAP-H-001

**Description**: AI provider not selected, provider data handling policy not reviewed

**Severity**: HIGH

**Impact**: AI service unavailability, data protection non-compliance, AI data breach risk

**Owner**: Product/Engineering Team

**Required Decision**: AI provider selection required

**Phase**: MVP

**Status**: OPEN

**MVP Blocking?**: NO

**Engineering Blocking?**: YES

**Production Launch Blocking?**: YES

**Future Phase Blocking?**: NO

**Required Professional Review?**: NO

**Mitigation**: Define AI provider selection criteria, evaluate AI providers, conduct provider due diligence

---

### 3.2 GAP-H-002: OTP Provider Selection

**Gap ID**: GAP-H-002

**Description**: OTP provider not selected, provider security not reviewed

**Severity**: HIGH

**Impact**: OTP service unavailability, authentication failure

**Owner**: Product/Engineering Team

**Required Decision**: OTP provider selection required

**Phase**: MVP

**Status**: OPEN

**MVP Blocking?**: NO

**Engineering Blocking?**: YES

**Production Launch Blocking?**: YES

**Future Phase Blocking?**: NO

**Required Professional Review?**: NO

**Mitigation**: Define OTP provider selection criteria, evaluate OTP providers, conduct provider due diligence

---

### 3.3 GAP-H-003: Email Provider Selection

**Gap ID**: GAP-H-003

**Description**: Email provider not selected, provider security not reviewed

**Severity**: HIGH

**Impact**: Email service unavailability, communication failure

**Owner**: Product/Engineering Team

**Required Decision**: Email provider selection required

**Phase**: MVP

**Status**: OPEN

**MVP Blocking?**: NO

**Engineering Blocking?**: YES

**Production Launch Blocking?**: YES

**Future Phase Blocking?**: NO

**Required Professional Review?**: NO

**Mitigation**: Define email provider selection criteria, evaluate email providers, conduct provider due diligence

---

### 3.4 GAP-H-004: SMS/WhatsApp Provider Selection

**Gap ID**: GAP-H-004

**Description**: SMS/WhatsApp provider not selected, provider security not reviewed

**Severity**: HIGH

**Impact**: SMS/WhatsApp service unavailability, OTP delivery failure

**Owner**: Product/Engineering Team

**Required Decision**: SMS/WhatsApp provider selection required

**Phase**: MVP

**Status**: OPEN

**MVP Blocking?**: NO

**Engineering Blocking?**: YES

**Production Launch Blocking?**: YES

**Future Phase Blocking?**: NO

**Required Professional Review?**: NO

**Mitigation**: Define SMS/WhatsApp provider selection criteria, evaluate providers, conduct provider due diligence

---

### 3.5 GAP-H-005: Grievance Handling Mechanism

**Gap ID**: GAP-H-005

**Description**: Grievance handling mechanism not implemented

**Severity**: HIGH

**Impact**: Consumer protection non-compliance, user complaints

**Owner**: Product/Engineering Team

**Required Decision**: Grievance handling implementation required

**Phase**: MVP

**Status**: OPEN

**MVP Blocking?**: NO

**Engineering Blocking?**: NO

**Production Launch Blocking?**: CONDITIONAL

**Future Phase Blocking?**: NO

**Required Professional Review?**: NO

**Mitigation**: Design grievance handling mechanism, implement grievance API, define grievance process

---

### 3.6 GAP-H-006: Data Portability

**Gap ID**: GAP-H-006

**Description**: Data portability not implemented

**Severity**: HIGH

**Impact**: Data protection non-compliance, user complaints

**Owner**: Product/Engineering Team

**Required Decision**: Data portability implementation required

**Phase**: Future

**Status**: OPEN

**MVP Blocking?**: NO

**Engineering Blocking?**: NO

**Production Launch Blocking?**: NO

**Future Phase Blocking?**: YES

**Required Professional Review?**: NO

**Mitigation**: Design data portability mechanism, implement data export API, define data portability process

---

### 3.7 GAP-H-007: CERT-In Reporting Process

**Gap ID**: GAP-H-007

**Description**: CERT-In reporting process not defined

**Severity**: HIGH

**Impact**: Cybersecurity incident non-compliance, regulatory penalties

**Owner**: Security/Compliance Team

**Required Decision**: CERT-In reporting process definition required

**Phase**: MVP

**Status**: OPEN

**MVP Blocking?**: NO

**Engineering Blocking?**: NO

**Production Launch Blocking?**: CONDITIONAL

**Future Phase Blocking?**: NO

**Required Professional Review?**: YES

**Mitigation**: Define CERT-In reporting process, define incident notification timeline, define incident notification details

---

### 3.8 GAP-H-008: Marketing/Product Language Review

**Gap ID**: GAP-H-008

**Description**: Marketing and product language not reviewed by legal counsel

**Severity**: HIGH

**Impact**: Legal non-compliance, misleading claims, regulatory penalties

**Owner**: Marketing/Legal Team

**Required Decision**: Marketing/product language legal review required

**Phase**: MVP

**Status**: OPEN

**MVP Blocking?**: NO

**Engineering Blocking?**: NO

**Production Launch Blocking?**: YES

**Future Phase Blocking?**: NO

**Required Professional Review?**: YES

**Mitigation**: Define marketing guidelines, review all marketing materials, review all product descriptions

---

## 4. MEDIUM Gaps

### 4.1 GAP-M-001: Notifications Retention Period

**Gap ID**: GAP-M-001

**Description**: Notifications retention period (90 days) requires compliance/legal decision

**Severity**: MEDIUM

**Impact**: Potential data retention non-compliance

**Owner**: Compliance Team

**Required Decision**: Compliance/legal decision required

**Phase**: MVP

**Status**: OPEN

**MVP Blocking?**: NO

**Engineering Blocking?**: NO

**Production Launch Blocking?**: NO

**Future Phase Blocking?**: NO

**Required Professional Review?**: YES

**Mitigation**: Document notifications retention rationale, engage legal counsel for retention review

---

### 4.2 GAP-M-002: AI Conversations Retention Period

**Gap ID**: GAP-M-002

**Description**: AI conversations retention period (1 year) requires compliance/legal decision

**Severity**: MEDIUM

**Impact**: Potential data retention non-compliance

**Owner**: Compliance Team

**Required Decision**: Compliance/legal decision required

**Phase**: MVP

**Status**: OPEN

**MVP Blocking?**: NO

**Engineering Blocking?**: NO

**Production Launch Blocking?**: NO

**Future Phase Blocking?**: NO

**Required Professional Review?**: YES

**Mitigation**: Document AI conversations retention rationale, engage legal counsel for retention review

---

### 4.3 GAP-M-003: Security Logs Retention Period

**Gap ID**: GAP-M-003

**Description**: Security logs retention period (90 days) requires compliance/legal decision

**Severity**: MEDIUM

**Impact**: Potential data retention non-compliance

**Owner**: Compliance Team

**Required Decision**: Compliance/legal decision required

**Phase**: MVP

**Status**: OPEN

**MVP Blocking?**: NO

**Engineering Blocking?**: NO

**Production Launch Blocking?**: NO

**Future Phase Blocking?**: NO

**Required Professional Review?**: YES

**Mitigation**: Document security logs retention rationale, engage legal counsel for retention review

---

### 4.4 GAP-M-004: Subscription Terms

**Gap ID**: GAP-M-004

**Description**: Subscription terms not defined

**Severity**: MEDIUM

**Impact**: Consumer protection non-compliance, user complaints

**Owner**: Product/Legal Team

**Required Decision**: Subscription terms definition required

**Phase**: Future

**Status**: OPEN

**MVP Blocking?**: NO

**Engineering Blocking?**: NO

**Production Launch Blocking?**: NO

**Future Phase Blocking?**: YES

**Required Professional Review?**: YES

**Mitigation**: Define subscription terms, define cancellation policy, define refund policy

---

### 4.5 GAP-M-005: Refund Policy

**Gap ID**: GAP-M-005

**Description**: Refund policy not defined

**Severity**: MEDIUM

**Impact**: Consumer protection non-compliance, user complaints

**Owner**: Product/Legal Team

**Required Decision**: Refund policy definition required

**Phase**: Future

**Status**: OPEN

**MVP Blocking?**: NO

**Engineering Blocking?**: NO

**Production Launch Blocking?**: NO

**Future Phase Blocking?**: YES

**Required Professional Review?**: YES

**Mitigation**: Define refund policy, define refund process, define refund timeline

---

## 5. LOW Gaps

### 5.1 GAP-L-001: Analytics Provider Selection

**Gap ID**: GAP-L-001

**Description**: Analytics provider not selected (if applicable)

**Severity**: LOW

**Impact**: Analytics unavailability (if applicable)

**Owner**: Product/Engineering Team

**Required Decision**: Analytics provider selection required (if applicable)

**Phase**: Future

**Status**: OPEN

**MVP Blocking?**: NO

**Engineering Blocking?**: NO

**Production Launch Blocking?**: NO

**Future Phase Blocking?**: YES

**Required Professional Review?**: NO

**Mitigation**: Evaluate analytics provider need, select analytics provider (if applicable), conduct provider due diligence

---

### 5.2 GAP-L-002: Push Provider Selection

**Gap ID**: GAP-L-002

**Description**: Push provider not selected

**Severity**: LOW

**Impact**: Push notifications unavailability

**Owner**: Product/Engineering Team

**Required Decision**: Push provider selection required

**Phase**: MVP

**Status**: OPEN

**MVP Blocking?**: NO

**Engineering Blocking?**: YES

**Production Launch Blocking?**: YES

**Future Phase Blocking?**: NO

**Required Professional Review?**: NO

**Mitigation**: Define push provider selection criteria, evaluate push providers, conduct provider due diligence

---

### 5.3 GAP-L-003: Tax/GST Considerations

**Gap ID**: GAP-L-003

**Description**: Tax/GST considerations not currently identified

**Severity**: LOW

**Impact**: Potential tax/GST non-compliance (future)

**Owner**: Finance/Legal Team

**Required Decision**: Tax/GST review required (future)

**Phase**: Future

**Status**: OPEN

**MVP Blocking?**: NO

**Engineering Blocking?**: NO

**Production Launch Blocking?**: NO

**Future Phase Blocking?**: YES

**Required Professional Review?**: YES

**Mitigation**: Monitor tax/GST requirements, engage tax/legal counsel for future review

---

## 6. Gap Analysis by Phase

### 6.1 MVP Gaps

| Gap ID | Description | Severity | Status |
|--------|-------------|----------|--------|
| GAP-C-002 | PPI/Stored-Value Boundary Legal Review | CRITICAL | OPEN |
| GAP-C-006 | KYC/AML Legal Review | CRITICAL | OPEN |
| GAP-C-007 | Data Protection Legal Review | CRITICAL | OPEN |
| GAP-C-008 | AI Financial Recommendations Legal Review | CRITICAL | OPEN |
| GAP-C-009 | Data Retention Legal Review | CRITICAL | OPEN |
| GAP-C-010 | Consumer Protection Legal Review | CRITICAL | OPEN |
| GAP-H-001 | AI Provider Selection | HIGH | OPEN |
| GAP-H-002 | OTP Provider Selection | HIGH | OPEN |
| GAP-H-003 | Email Provider Selection | HIGH | OPEN |
| GAP-H-004 | SMS/WhatsApp Provider Selection | HIGH | OPEN |
| GAP-H-005 | Grievance Handling Mechanism | HIGH | OPEN |
| GAP-H-007 | CERT-In Reporting Process | HIGH | OPEN |
| GAP-H-008 | Marketing/Product Language Review | HIGH | OPEN |
| GAP-M-001 | Notifications Retention Period | MEDIUM | OPEN |
| GAP-M-002 | AI Conversations Retention Period | MEDIUM | OPEN |
| GAP-M-003 | Security Logs Retention Period | MEDIUM | OPEN |
| GAP-L-002 | Push Provider Selection | LOW | OPEN |

**Total MVP Gaps**: 18 (6 CRITICAL, 8 HIGH, 3 MEDIUM, 1 LOW)

### 6.2 Phase 1 Gaps

| Gap ID | Description | Severity | Status |
|--------|-------------|----------|--------|
| GAP-C-001 | Payment Orchestration Model Legal Review | CRITICAL | OPEN |
| GAP-C-003 | Payment Aggregation Legal Review | CRITICAL | OPEN |

**Total Phase 1 Gaps**: 2 (2 CRITICAL)

### 6.3 Phase 2 Gaps

| Gap ID | Description | Severity | Status |
|--------|-------------|----------|--------|
| GAP-C-004 | UPI Integration Legal Review | CRITICAL | OPEN |
| GAP-C-005 | BBPS Integration Legal Review | CRITICAL | OPEN |

**Total Phase 2 Gaps**: 2 (2 CRITICAL)

### 6.4 Future Gaps

| Gap ID | Description | Severity | Status |
|--------|-------------|----------|--------|
| GAP-C-011 | Cross-Border Data/Provider Legal Review | CRITICAL | OPEN |
| GAP-C-012 | Future Money Movement Legal Review | CRITICAL | OPEN |
| GAP-H-006 | Data Portability | HIGH | OPEN |
| GAP-M-004 | Subscription Terms | MEDIUM | OPEN |
| GAP-M-005 | Refund Policy | MEDIUM | OPEN |
| GAP-L-001 | Analytics Provider Selection | LOW | OPEN |
| GAP-L-003 | Tax/GST Considerations | LOW | OPEN |

**Total Future Gaps**: 7 (2 CRITICAL, 1 HIGH, 2 MEDIUM, 2 LOW)

---

## 7. Gap Analysis by Owner

### 7.1 Legal/Compliance Team

| Gap ID | Description | Severity | Phase |
|--------|-------------|----------|-------|
| GAP-C-001 | Payment Orchestration Model Legal Review | CRITICAL | Phase 1 |
| GAP-C-002 | PPI/Stored-Value Boundary Legal Review | CRITICAL | MVP |
| GAP-C-003 | Payment Aggregation Legal Review | CRITICAL | Phase 1 |
| GAP-C-004 | UPI Integration Legal Review | CRITICAL | Phase 2 |
| GAP-C-005 | BBPS Integration Legal Review | CRITICAL | Phase 2 |
| GAP-C-006 | KYC/AML Legal Review | CRITICAL | MVP |
| GAP-C-007 | Data Protection Legal Review | CRITICAL | MVP |
| GAP-C-008 | AI Financial Recommendations Legal Review | CRITICAL | MVP |
| GAP-C-009 | Data Retention Legal Review | CRITICAL | MVP |
| GAP-C-010 | Consumer Protection Legal Review | CRITICAL | MVP |
| GAP-C-011 | Cross-Border Data/Provider Legal Review | CRITICAL | Future |
| GAP-C-012 | Future Money Movement Legal Review | CRITICAL | Future |

**Total Legal/Compliance Team Gaps**: 12 (12 CRITICAL)

### 7.2 Product/Engineering Team

| Gap ID | Description | Severity | Phase |
|--------|-------------|----------|-------|
| GAP-H-001 | AI Provider Selection | HIGH | MVP |
| GAP-H-002 | OTP Provider Selection | HIGH | MVP |
| GAP-H-003 | Email Provider Selection | HIGH | MVP |
| GAP-H-004 | SMS/WhatsApp Provider Selection | HIGH | MVP |
| GAP-H-005 | Grievance Handling Mechanism | HIGH | MVP |
| GAP-H-006 | Data Portability | HIGH | Future |
| GAP-L-001 | Analytics Provider Selection | LOW | Future |
| GAP-L-002 | Push Provider Selection | LOW | MVP |

**Total Product/Engineering Team Gaps**: 8 (6 HIGH, 2 LOW)

### 7.3 Security/Compliance Team

| Gap ID | Description | Severity | Phase |
|--------|-------------|----------|-------|
| GAP-H-007 | CERT-In Reporting Process | HIGH | MVP |

**Total Security/Compliance Team Gaps**: 1 (1 HIGH)

### 7.4 Marketing/Legal Team

| Gap ID | Description | Severity | Phase |
|--------|-------------|----------|-------|
| GAP-H-008 | Marketing/Product Language Review | HIGH | MVP |

**Total Marketing/Legal Team Gaps**: 1 (1 HIGH)

### 7.5 Compliance Team

| Gap ID | Description | Severity | Phase |
|--------|-------------|----------|-------|
| GAP-M-001 | Notifications Retention Period | MEDIUM | MVP |
| GAP-M-002 | AI Conversations Retention Period | MEDIUM | MVP |
| GAP-M-003 | Security Logs Retention Period | MEDIUM | MVP |

**Total Compliance Team Gaps**: 3 (3 MEDIUM)

### 7.6 Product/Legal Team

| Gap ID | Description | Severity | Phase |
|--------|-------------|----------|-------|
| GAP-M-004 | Subscription Terms | MEDIUM | Future |
| GAP-M-005 | Refund Policy | MEDIUM | Future |

**Total Product/Legal Team Gaps**: 2 (2 MEDIUM)

### 7.7 Finance/Legal Team

| Gap ID | Description | Severity | Phase |
|--------|-------------|----------|-------|
| GAP-L-003 | Tax/GST Considerations | LOW | Future |

**Total Finance/Legal Team Gaps**: 1 (1 LOW)

---

## 8. Conclusion

The NeoWallet Compliance Gap Register identifies 28 compliance gaps across MVP, Phase 1, Phase 2, and Future phases. All gaps require action before the respective phase can proceed.

**IMPORTANT DISCLAIMER**: NeoWallet has documented gaps and identified professional review requirements. NeoWallet is NOT claimed to be legally or regulatorily compliant.

---

## 9. Items That MUST Be Resolved Before Devin Starts MVP Coding

### 9.1 Engineering Prerequisites (Required Before Devin Starts MVP Coding)

The following items must be resolved before Devin starts MVP coding:

| Gap ID | Description | Required Action | Reason |
|--------|-------------|-----------------|---------|
| GAP-H-001 | AI Provider Selection | Select AI provider | AI provider is required for AI service implementation |
| GAP-H-002 | OTP Provider Selection | Select OTP provider | OTP provider is required for authentication implementation |
| GAP-H-003 | Email Provider Selection | Select email provider | Email provider is required for communication implementation |
| GAP-H-004 | SMS/WhatsApp Provider Selection | Select SMS/WhatsApp provider | SMS/WhatsApp provider is required for OTP delivery implementation |
| GAP-L-002 | Push Provider Selection | Select push provider | Push provider is required for notifications implementation |

**Total Items**: 5

### 9.2 Recommended Engineering Decisions

The following items are recommended to be defined before Devin starts MVP coding but are not strictly blocking:

| Gap ID | Description | Recommended Action | Reason |
|--------|-------------|-------------------|---------|
| GAP-M-001 | Notifications Retention Period | Define configurable retention setting | Engineering proposal. Configurable retention required. |
| GAP-M-002 | AI Conversations Retention Period | Define configurable retention setting | Engineering proposal. Configurable retention required. |
| GAP-M-003 | Security Logs Retention Period | Define configurable retention setting | Engineering proposal. Configurable retention required. |

**Total Recommended Items**: 3

### 9.3 Not Required Before Devin Starts MVP Coding

The following items are NOT required before Devin starts MVP coding:

- All CRITICAL legal/regulatory reviews (GAP-C-001 to GAP-C-012) - Professional review is recommended but not blocking engineering
- All future capability gaps (GAP-H-006, GAP-M-004, GAP-M-005, GAP-L-001, GAP-L-003) - These are future phase items
- Grievance handling (GAP-H-005) - Conditional on consumer protection requirements
- Marketing/product language (GAP-H-008) - Required before production, not before coding
- CERT-In reporting (GAP-H-007) - Required before production, not before coding

---

## 10. Items That May Be Deferred Until Future Phases

### 10.1 Future Payment Features

The following gaps may be deferred until future payment features are planned:

| Gap ID | Description | Target Phase |
|--------|-------------|--------------|
| GAP-C-001 | Payment Orchestration Model Legal Review | Phase 1 |
| GAP-C-003 | Payment Aggregation Legal Review | Phase 1 |
| GAP-C-004 | UPI Integration Legal Review | Phase 2 |
| GAP-C-005 | BBPS Integration Legal Review | Phase 2 |

### 10.2 Future Capabilities

The following gaps may be deferred until future capabilities are planned:

| Gap ID | Description | Target Phase |
|--------|-------------|--------------|
| GAP-C-011 | Cross-Border Data/Provider Legal Review | Future |
| GAP-C-012 | Future Money Movement Legal Review | Future |
| GAP-H-006 | Data Portability | Future |
| GAP-M-004 | Subscription Terms | Future |
| GAP-M-005 | Refund Policy | Future |
| GAP-L-001 | Analytics Provider Selection | Future |
| GAP-L-003 | Tax/GST Considerations | Future |

### 10.3 MVP Production (Not Engineering) Items

The following items may be deferred until before MVP production launch, but are not required for engineering start:

| Gap ID | Description | Target Timing |
|--------|-------------|---------------|
| GAP-C-002 | PPI/Stored-Value Boundary Legal Review | Before MVP Production |
| GAP-C-006 | KYC/AML Legal Review | Before MVP Production |
| GAP-C-007 | Data Protection Legal Review | Before MVP Production |
| GAP-C-008 | AI Financial Recommendations Legal Review | Before MVP Production |
| GAP-C-009 | Data Retention Legal Review | Before MVP Production |
| GAP-C-010 | Consumer Protection Legal Review | Before MVP Production |
| GAP-H-005 | Grievance Handling Mechanism | Before MVP Production (Conditional) |
| GAP-H-007 | CERT-In Reporting Process | Before MVP Production |
| GAP-H-008 | Marketing/Product Language Review | Before MVP Production |
| GAP-M-001 | Notifications Retention Period | Before MVP Production |
| GAP-M-002 | AI Conversations Retention Period | Before MVP Production |
| GAP-M-003 | Security Logs Retention Period | Before MVP Production |

---

## 11. Related Documents

- **MVP Blocking Analysis**: `NeoWallet_Compliance_MVP_Blocking_Analysis_v1.md`
- **Regulatory Review Register**: `NeoWallet_Regulatory_Review_Register_v1.md`
- **Compliance Validation Report**: `NeoWallet_Compliance_Validation_v1.md`

---

## 12. Next Steps

1. Product Owner review and approval of MVP blocking analysis
2. Resolve engineering prerequisites before Devin starts MVP coding
3. Initiate professional legal/regulatory review before MVP production launch
4. Address future phase gaps before respective phase launch

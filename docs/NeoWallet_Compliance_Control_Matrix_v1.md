# NeoWallet Compliance Control Matrix v1

## Executive Summary

This document provides a comprehensive compliance requirements and control matrix for NeoWallet MVP, mapping regulatory requirements to controls, risks, business rules, APIs, data entities, and security controls.

**IMPORTANT DISCLAIMER**: This document is a requirements/control mapping and risk assessment only. NeoWallet is NOT claimed to be legally or regulatorily compliant. All areas requiring qualified Indian legal, regulatory, privacy, security, or financial-services professional review are identified.

**Date**: August 18, 2026
**Status**: Ready for Product Owner Approval

---

## 1. Product Model

### 1.1 Approved MVP Model

**NeoWallet Definition**: Mobile-first AI-powered family financial management and payment orchestration platform.

**Core Value Proposition**: PLAN → OPTIMIZE → DECIDE → PAY

**Fundamental Principle**: NeoWallet does NOT hold customer funds. It maintains financial planning, allocation, budgeting, transaction records, and financial intelligence. Actual payment execution occurs through authorized/regulated payment providers.

### 1.2 MVP Scope

**MVP Capabilities**:
- ✅ Financial planning and budgeting
- ✅ Transaction tracking (manual entry and import)
- ✅ Savings goal tracking
- ✅ Bill tracking (manual entry/planning)
- ✅ Family financial collaboration
- ✅ AI-powered financial insights (read-only)
- ✅ Financial health scoring (educational)

**MVP Exclusions**:
- ❌ NeoWallet does NOT hold customer funds
- ❌ NeoWallet does NOT issue stored-value/PPI instruments
- ❌ NeoWallet does NOT provide wallet-to-wallet transfers
- ❌ NeoWallet does NOT provide cash-out
- ❌ Neo AI does NOT autonomously execute financial transactions
- ❌ MVP bills are manual-entry/planning functionality only
- ❌ Payment orchestration is a future capability through appropriate authorized/regulated providers

### 1.3 Product Language Restrictions

**Prohibited Claims**:
- "Bank account"
- "Digital bank"
- "Stored-value wallet"
- "Guaranteed savings"
- "Guaranteed financial improvement"
- "Credit score"
- "Investment advisor"
- "Guaranteed financial advice"

**Allowed Claims** (with appropriate disclaimers):
- "Financial management platform"
- "Budgeting tool"
- "Financial planning assistant"
- "Financial health score (educational)"
- "AI-powered financial insights"

---

## 2. India-Specific Regulatory Areas

### 2.1 Regulatory Area Classification

| Regulatory Area | Classification | Rationale |
|-----------------|----------------|-----------|
| Reserve Bank of India (RBI) Requirements | Future Phase Review | MVP does not hold funds or execute payments. Future payment orchestration requires RBI review. |
| Payment and Settlement Systems Framework | Future Phase Review | MVP does not process payments. Future payment orchestration requires review. |
| Prepaid Payment Instrument (PPI) | Potentially Not Applicable | MVP does not issue stored-value instruments. Professional review required to confirm. |
| Payment Aggregator | Future Phase Review | MVP does not aggregate payments. Future payment orchestration requires review. |
| UPI Ecosystem | Future Phase Review | MVP does not integrate with UPI. Future integration requires NPCI review. |
| BBPS (Bharat Bill Payment System) | Future Phase Review | MVP does not process bill payments. Future integration requires NPCI review. |
| KYC/AML | Professional Review Required | MVP collects user PII. Professional review required to determine KYC/AML obligations. |
| Consumer Protection | Potentially Applicable | MVP provides financial management services. Consumer protection laws may apply. |
| Digital Personal Data Protection Act | Potentially Applicable | MVP collects personal data. DPDP Act may apply. Professional review required. |
| CERT-In Requirements | Potentially Applicable | MVP is a digital service. CERT-In requirements may apply for cybersecurity incidents. |
| IT/Cybersecurity Requirements | Potentially Applicable | MVP is a digital service. IT Act and cybersecurity requirements may apply. |
| Electronic Records Requirements | Potentially Applicable | MVP maintains electronic records. Electronic records requirements may apply. |
| Tax/GST Considerations | Not Currently Identified | MVP does not process payments. Tax/GST considerations not currently identified. |

### 2.2 RBI Requirements

**MVP Status**: Future Phase Review

**Potential Applicability**:
- Payment and Settlement Systems Act, 2007
- RBI Master Directions on Payment Aggregators
- RBI Master Directions on PPI

**MVP Assessment**:
- MVP does not hold customer funds
- MVP does not execute payments
- MVP does not issue stored-value instruments
- MVP does not aggregate payments

**Future Considerations**:
- Payment orchestration through authorized/regulated providers
- Integration with UPI ecosystem
- Integration with BBPS
- Professional review required before any payment capabilities

**Professional Review Required**: Yes

### 2.3 Payment and Settlement Systems Framework

**MVP Status**: Future Phase Review

**Potential Applicability**:
- Payment and Settlement Systems Act, 2007
- RBI regulations on payment systems

**MVP Assessment**:
- MVP does not operate a payment system
- MVP does not settle transactions
- MVP does not hold customer funds

**Future Considerations**:
- Payment orchestration through authorized/regulated providers
- Professional review required before any payment capabilities

**Professional Review Required**: Yes

### 2.4 PPI Considerations

**MVP Status**: Potentially Not Applicable

**Potential Applicability**:
- RBI Master Directions on Prepaid Payment Instruments

**MVP Assessment**:
- MVP does NOT issue stored-value instruments
- MVP does NOT hold customer funds
- MVP does NOT provide wallet-to-wallet transfers
- MVP does NOT provide cash-out

**Professional Review Required**: Yes (to confirm non-applicability)

### 2.5 Payment Aggregator Considerations

**MVP Status**: Future Phase Review

**Potential Applicability**:
- RBI Master Directions on Payment Aggregators

**MVP Assessment**:
- MVP does NOT aggregate payments
- MVP does NOT settle transactions
- MVP does NOT hold customer funds

**Future Considerations**:
- Payment orchestration through authorized/regulated providers
- Professional review required before any payment aggregation capabilities

**Professional Review Required**: Yes

### 2.6 UPI Ecosystem Considerations

**MVP Status**: Future Phase Review

**Potential Applicability**:
- NPCI UPI guidelines
- NPCI regulations for third-party apps

**MVP Assessment**:
- MVP does NOT integrate with UPI
- MVP does NOT process UPI transactions

**Future Considerations**:
- UPI integration for payment orchestration
- NPCI approval required
- Professional review required before UPI integration

**Professional Review Required**: Yes

### 2.7 BBPS Considerations

**MVP Status**: Future Phase Review

**Potential Applicability**:
- NPCI BBPS guidelines
- NPCI regulations for bill payment aggregators

**MVP Assessment**:
- MVP does NOT process bill payments
- MVP bills are manual-entry/planning functionality only
- MVP does NOT execute bill payments

**Future Considerations**:
- BBPS integration for bill payment orchestration
- NPCI approval required
- Professional review required before BBPS integration

**Professional Review Required**: Yes

### 2.8 KYC/AML Considerations

**MVP Status**: Professional Review Required

**Potential Applicability**:
- Prevention of Money Laundering Act, 2002 (PMLA)
- RBI KYC guidelines
- Financial Intelligence Unit-India (FIU-IND) requirements

**MVP Assessment**:
- MVP collects user PII (name, email, phone)
- MVP does NOT hold customer funds
- MVP does NOT execute payments
- MVP does NOT provide financial services that typically trigger KYC

**Professional Review Required**: Yes (to determine KYC/AML obligations)

**Current Controls**:
- Email verification required
- Phone verification (optional, for OTP)
- No financial services that typically trigger KYC

**Provider Responsibility**:
- Future payment providers may handle KYC
- Professional review required to determine NeoWallet KYC obligations

### 2.9 Consumer Protection Requirements

**MVP Status**: Potentially Applicable

**Potential Applicability**:
- Consumer Protection Act, 2019
- CCPA regulations
- RBI consumer protection guidelines

**MVP Assessment**:
- MVP provides financial management services
- MVP provides AI-powered financial insights
- MVP provides financial health scoring

**Professional Review Required**: Yes

**Current Controls**:
- Transparent pricing (subscription terms)
- User consent for data processing
- Complaint handling mechanism
- AI recommendation disclaimers
- Financial health score disclaimers

### 2.10 Digital Personal Data Protection Act

**MVP Status**: Potentially Applicable

**Potential Applicability**:
- Digital Personal Data Protection Act, 2023 (DPDP Act)
- DPDP rules and regulations

**MVP Assessment**:
- MVP collects personal data (name, email, phone)
- MVP collects sensitive financial information
- MVP collects device information
- MVP collects AI conversation information

**Professional Review Required**: Yes

**Current Controls**:
- Data minimization
- Purpose limitation
- User consent
- Data access controls
- Data deletion
- Data retention policy
- Security safeguards
- Data breach response plan

### 2.11 CERT-In Requirements

**MVP Status**: Potentially Applicable

**Potential Applicability**:
- CERT-In guidelines for cybersecurity incidents
- CERT-In reporting requirements

**MVP Assessment**:
- MVP is a digital service
- MVP processes personal data
- MVP may be subject to cybersecurity incident reporting

**Professional Review Required**: Yes

**Current Controls**:
- Incident response plan
- Security monitoring
- Data breach response plan
- CERT-In reporting process (to be defined)

### 2.12 IT/Cybersecurity Requirements

**MVP Status**: Potentially Applicable

**Potential Applicability**:
- Information Technology Act, 2000
- IT Rules, 2021
- Cybersecurity requirements

**MVP Assessment**:
- MVP is a digital service
- MVP processes personal data
- MVP is subject to IT Act requirements

**Professional Review Required**: Yes

**Current Controls**:
- Encryption at rest and in transit
- Access controls
- Audit logging
- Security monitoring
- Incident response

### 2.13 Electronic Records Requirements

**MVP Status**: Potentially Applicable

**Potential Applicability**:
- Information Technology Act, 2000
- Electronic records requirements

**MVP Assessment**:
- MVP maintains electronic records
- MVP maintains transaction records
- MVP maintains audit logs

**Professional Review Required**: Yes

**Current Controls**:
- Record retention policy
- Audit logging
- Data integrity controls
- Electronic record preservation

### 2.14 Tax/GST Considerations

**MVP Status**: Not Currently Identified

**Potential Applicability**:
- GST laws
- Income Tax Act

**MVP Assessment**:
- MVP does NOT process payments
- MVP does NOT charge transaction fees
- Tax/GST considerations not currently identified

**Future Considerations**:
- Payment orchestration may introduce tax/GST considerations
- Professional review required before any payment capabilities

---

## 3. Payment Model Review

### 3.1 MVP Payment Model

**MVP Activities**:
- Financial planning and budgeting
- Transaction tracking (manual entry and import)
- Savings goal tracking
- Bill tracking (manual entry/planning)
- Family financial collaboration
- AI-powered financial insights (read-only)
- Financial health scoring (educational)

**MVP Exclusions**:
- ❌ Payment aggregation
- ❌ Payment initiation
- ❌ Money movement
- ❌ Stored value
- ❌ KYC obligations (provider responsibility)
- ❌ AML obligations (provider responsibility)
- ❌ Provider licensing requirements
- ❌ Data-sharing requirements (beyond standard privacy)

**MVP Assessment**:
- MVP does NOT hold customer funds
- MVP does NOT execute payments
- MVP does NOT issue stored-value instruments
- MVP does NOT aggregate payments
- MVP bills are manual-entry/planning functionality only

**Professional Review Required**: Yes (to confirm MVP payment model)

### 3.2 Phase 1 Payment Model

**Phase 1 Activities** (Future):
- Payment orchestration through authorized/regulated providers
- Bill payment execution through authorized/regulated providers
- Transaction import from bank accounts (read-only)

**Phase 1 Potential Introductions**:
- ⚠️ Payment aggregation (through providers)
- ⚠️ Payment initiation (through providers)
- ⚠️ Money movement (through providers)
- ❌ Stored value (NOT introduced)
- ⚠️ KYC obligations (provider responsibility, potential NeoWallet obligations)
- ⚠️ AML obligations (provider responsibility, potential NeoWallet obligations)
- ⚠️ Provider licensing requirements (provider responsibility)
- ⚠️ Data-sharing requirements (provider contracts)

**Professional Review Required**: Yes (before Phase 1 implementation)

### 3.3 Phase 2 Payment Model

**Phase 2 Activities** (Future):
- UPI integration for payment orchestration
- BBPS integration for bill payment
- Enhanced payment orchestration capabilities

**Phase 2 Potential Introductions**:
- ⚠️ Payment aggregation (through providers)
- ⚠️ Payment initiation (through providers)
- ⚠️ Money movement (through providers)
- ❌ Stored value (NOT introduced)
- ⚠️ KYC obligations (provider responsibility, potential NeoWallet obligations)
- ⚠️ AML obligations (provider responsibility, potential NeoWallet obligations)
- ⚠️ Provider licensing requirements (NPCI approval required)
- ⚠️ Data-sharing requirements (provider contracts)

**Professional Review Required**: Yes (before Phase 2 implementation)

### 3.4 Future Payment Model

**Future Activities** (Future):
- Enhanced payment orchestration
- Additional payment provider integrations
- Cross-border payment capabilities (if applicable)

**Future Potential Introductions**:
- ⚠️ Payment aggregation (through providers)
- ⚠️ Payment initiation (through providers)
- ⚠️ Money movement (through providers)
- ❌ Stored value (NOT introduced)
- ⚠️ KYC obligations (provider responsibility, potential NeoWallet obligations)
- ⚠️ AML obligations (provider responsibility, potential NeoWallet obligations)
- ⚠️ Provider licensing requirements (provider responsibility)
- ⚠️ Data-sharing requirements (provider contracts)
- ⚠️ Cross-border data/provider issues (if applicable)

**Professional Review Required**: Yes (before future implementation)

---

## 4. Data Privacy

### 4.1 Data Classification

| Data Type | Classification | Regulatory Considerations |
|-----------|----------------|-------------------------|
| Personal Data | Personal Data | DPDP Act potentially applicable |
| Sensitive Financial Information | Sensitive Personal Data | DPDP Act potentially applicable |
| Family Information | Personal Data | DPDP Act potentially applicable |
| Authentication Information | Sensitive Personal Data | DPDP Act potentially applicable |
| Device Information | Personal Data | DPDP Act potentially applicable |
| AI Conversation Information | Personal Data | DPDP Act potentially applicable |
| Analytics Information | Personal Data | DPDP Act potentially applicable |

### 4.2 Personal Data

**Personal Data Collected**:
- Name (first name, last name)
- Email address
- Phone number (optional)
- Date of birth (optional)
- Address (optional, reserved for future payment integration)

**Purpose Limitation**:
- Account management
- Authentication
- Communication
- Financial planning
- Family collaboration

**Data Minimization**:
- Only essential PII collected
- Addresses optional (reserved for future payment integration)
- Date of birth optional

**Consent/Legal Basis**:
- User consent for account creation
- User consent for data processing
- User consent for communication

**Access**:
- User can access own personal data via API
- User can access own personal data via mobile app

**Correction**:
- User can correct own personal data via API
- User can correct own personal data via mobile app

**Deletion**:
- User can request account deletion
- User data deleted after retention period
- Audit logs retained for 7 years

**Retention**:
- User data: 7 years after deletion
- Audit logs: 7 years

**Security Safeguards**:
- Encryption at rest (PostgreSQL TDE)
- Encryption in transit (TLS 1.3)
- Access controls (RBAC)
- Audit logging

**Data Breach Response**:
- Incident response plan
- Data breach notification process
- CERT-In reporting (if applicable)

**Third-Party Processors**:
- Cloud provider (Google Cloud)
- AI provider (to be selected)
- OTP provider (to be selected)
- Email provider (to be selected)
- SMS/WhatsApp provider (to be selected)

### 4.3 Sensitive Financial Information

**Sensitive Financial Information Collected**:
- Transaction amounts
- Budget limits
- Savings goals
- Bill amounts
- Financial health scores
- Allocation recommendations

**Purpose Limitation**:
- Financial planning
- Budgeting
- Savings tracking
- Bill tracking
- Financial health assessment

**Data Minimization**:
- Only necessary financial data collected
- Aggregated data used for AI where possible

**Consent/Legal Basis**:
- User consent for financial data processing
- User consent for AI insights

**Access**:
- User can access own financial data via API
- User can access own financial data via mobile app
- Family members can access family financial data (based on role)

**Correction**:
- User can correct own financial data via API
- User can correct own financial data via mobile app

**Deletion**:
- User can request account deletion
- Financial data deleted after retention period
- Audit logs retained for 7 years

**Retention**:
- Transactions: 7 years
- Budgets: 7 years
- Savings goals: 7 years after completion
- Bills: 7 years
- Financial health scores: 7 years

**Security Safeguards**:
- Encryption at rest (PostgreSQL TDE)
- Encryption in transit (TLS 1.3)
- Access controls (RBAC)
- Family isolation
- Audit logging

**Data Breach Response**:
- Incident response plan
- Data breach notification process
- CERT-In reporting (if applicable)

**Third-Party Processors**:
- Cloud provider (Google Cloud)
- AI provider (to be selected)

### 4.4 Family Information

**Family Information Collected**:
- Family name
- Family member roles
- Family member relationships

**Purpose Limitation**:
- Family collaboration
- Shared financial planning

**Data Minimization**:
- Only necessary family data collected

**Consent/Legal Basis**:
- User consent for family data processing
- Family member consent for family membership

**Access**:
- Family members can access family data (based on role)
- Owner can manage family members

**Correction**:
- Owner can correct family information
- Members can correct own family information

**Deletion**:
- User can leave family
- Owner can delete family
- Family data deleted after retention period

**Retention**:
- Family data: 7 years after deletion

**Security Safeguards**:
- Encryption at rest (PostgreSQL TDE)
- Encryption in transit (TLS 1.3)
- Access controls (RBAC)
- Family isolation
- Audit logging

**Data Breach Response**:
- Incident response plan
- Data breach notification process
- CERT-In reporting (if applicable)

### 4.5 Authentication Information

**Authentication Information Collected**:
- Password hash (Bcrypt)
- OTP hash (SHA-256)
- Session tokens
- Device tokens

**Purpose Limitation**:
- Authentication
- Session management
- Device management

**Data Minimization**:
- Only necessary authentication data collected
- No raw passwords stored
- No raw OTPs stored

**Consent/Legal Basis**:
- User consent for authentication

**Access**:
- System access only (not user-accessible)

**Correction**:
- User can change password
- User can revoke devices

**Deletion**:
- Authentication data deleted on account deletion
- Session tokens deleted on logout

**Retention**:
- Authentication records: 7 years after deletion
- Session data: 30 days

**Security Safeguards**:
- Password hashing (Bcrypt)
- OTP hashing (SHA-256)
- Token security (JWT)
- Encryption at rest (PostgreSQL TDE)
- Encryption in transit (TLS 1.3)
- Audit logging

**Data Breach Response**:
- Incident response plan
- Data breach notification process
- CERT-In reporting (if applicable)

### 4.6 Device Information

**Device Information Collected**:
- Device type
- Platform
- OS version
- App version
- Device fingerprint

**Purpose Limitation**:
- Device management
- Security monitoring
- Suspicious device detection

**Data Minimization**:
- Only necessary device data collected

**Consent/Legal Basis**:
- User consent for device data processing

**Access**:
- User can view own devices
- User can revoke own devices

**Correction**:
- User can update device information

**Deletion**:
- Device data deleted on device removal
- Device data deleted on account deletion

**Retention**:
- Device data: 7 years after deletion

**Security Safeguards**:
- Encryption at rest (PostgreSQL TDE)
- Encryption in transit (TLS 1.3)
- Access controls (RBAC)
- Audit logging

**Data Breach Response**:
- Incident response plan
- Data breach notification process
- CERT-In reporting (if applicable)

### 4.7 AI Conversation Information

**AI Conversation Information Collected**:
- User prompts
- AI responses
- AI tool invocations
- AI recommendations

**Purpose Limitation**:
- AI-powered financial insights
- Financial education
- Personalized recommendations

**Data Minimization**:
- Only necessary AI conversation data collected
- Data minimization in AI requests
- Context minimization

**Consent/Legal Basis**:
- User consent for AI data processing
- User consent for AI insights

**Access**:
- User can access own AI conversations
- User can delete own AI conversations

**Correction**:
- User can edit AI conversations (if applicable)

**Deletion**:
- User can delete individual conversations
- User can delete all conversations
- AI conversations deleted on account deletion

**Retention**:
- AI conversations: 1 year (Compliance Decision Required)

**Security Safeguards**:
- Encryption at rest (PostgreSQL TDE)
- Encryption in transit (TLS 1.3)
- Access controls (RBAC)
- AI data minimization
- Audit logging

**Data Breach Response**:
- Incident response plan
- Data breach notification process
- CERT-In reporting (if applicable)

**Third-Party Processors**:
- AI provider (to be selected)
- Provider data handling review required

### 4.8 Analytics Information

**Analytics Information Collected**:
- Usage patterns
- Feature usage
- Performance metrics
- Error rates

**Purpose Limitation**:
- Product improvement
- Performance optimization
- Bug fixing

**Data Minimization**:
- Only necessary analytics data collected
- Aggregated data where possible

**Consent/Legal Basis**:
- User consent for analytics (optional)

**Access**:
- Internal access only

**Correction**:
- Not applicable (aggregated data)

**Deletion**:
- Analytics data deleted after retention period

**Retention**:
- Analytics data: 90 days (Compliance Decision Required)

**Security Safeguards**:
- Encryption at rest (PostgreSQL TDE)
- Encryption in transit (TLS 1.3)
- Access controls (RBAC)
- Audit logging

**Data Breach Response**:
- Incident response plan
- Data breach notification process
- CERT-In reporting (if applicable)

**Third-Party Processors**:
- Analytics provider (to be selected, if applicable)

---

## 5. AI Governance

### 5.1 AI-Generated Financial Recommendations

**Risk Assessment**:
- AI recommendations are educational and planning-oriented
- AI recommendations are NOT guaranteed financial advice
- AI recommendations require user approval
- AI recommendations are read-only for MVP

**Controls**:
- AI is read-only for MVP
- AI cannot autonomously execute financial transactions
- AI recommendations require user approval
- AI recommendation disclaimers
- Human-in-the-loop for recommendations
- Confidence thresholds
- Output validation

**Professional Review Required**: Yes (AI financial recommendations)

### 5.2 Financial Education

**Risk Assessment**:
- AI provides financial education
- AI education is NOT guaranteed financial advice
- AI education is for educational purposes only

**Controls**:
- AI education disclaimers
- AI education is for educational purposes only
- AI education is NOT guaranteed financial advice
- AI education is NOT a substitute for professional financial advice

**Professional Review Required**: Yes (AI financial education)

### 5.3 Personalized Recommendations

**Risk Assessment**:
- AI provides personalized recommendations
- AI recommendations are based on user data
- AI recommendations are NOT guaranteed

**Controls**:
- AI recommendation disclaimers
- AI recommendations are NOT guaranteed
- AI recommendations require user approval
- AI recommendations are based on user data
- User consent for personalized recommendations

**Professional Review Required**: Yes (AI personalized recommendations)

### 5.4 Automated Decision-Making

**Risk Assessment**:
- AI does NOT make automated financial decisions for MVP
- AI recommendations require user approval
- AI is read-only for MVP

**Controls**:
- AI is read-only for MVP
- AI cannot autonomously execute financial transactions
- AI recommendations require user approval
- Human-in-the-loop for recommendations

**Professional Review Required**: Yes (AI automated decision-making)

### 5.5 AI Hallucination

**Risk Assessment**:
- AI may generate incorrect or misleading information
- AI hallucination could lead to incorrect financial decisions

**Controls**:
- Confidence thresholds
- Output validation
- Human-in-the-loop for recommendations
- AI recommendation disclaimers
- User feedback mechanism
- Prompt engineering

**Professional Review Required**: Yes (AI hallucination risks)

### 5.6 AI Transparency

**Risk Assessment**:
- AI decision-making may not be transparent
- AI recommendations may not be explainable

**Controls**:
- AI recommendation disclaimers
- AI recommendation explanations
- AI confidence scores
- AI tool invocation logging
- AI audit logging

**Professional Review Required**: Yes (AI transparency)

### 5.7 AI Disclosures

**Required Disclosures**:
- AI is NOT a licensed financial advisor
- AI recommendations are for educational purposes only
- AI recommendations are NOT guaranteed
- AI recommendations require user approval
- AI is NOT a substitute for professional financial advice
- AI may generate incorrect information
- User should consult professional financial advisor for financial decisions

**Product Language**:
- "AI-powered financial insights"
- "AI-powered financial education"
- "AI recommendations are for educational purposes only"
- "AI is NOT a licensed financial advisor"
- "AI recommendations are NOT guaranteed"

**Professional Review Required**: Yes (AI disclosures)

### 5.8 Human Escalation

**Risk Assessment**:
- AI may not handle all user queries
- AI may provide incorrect information
- User may need human assistance

**Controls**:
- Human escalation mechanism
- User feedback mechanism
- Support channel
- AI limitation disclaimers

**Professional Review Required**: Yes (AI human escalation)

### 5.9 Third-Party AI Providers

**Risk Assessment**:
- Third-party AI providers may have different data handling policies
- Third-party AI providers may use data for model training
- Third-party AI providers may have data breaches

**Controls**:
- Provider data handling review
- Provider data retention review
- Provider data training review
- Provider data breach notification review
- Data processing agreement
- Model training opt-out
- Data minimization in AI requests

**Professional Review Required**: Yes (third-party AI provider selection)

### 5.10 AI Data Retention

**Risk Assessment**:
- AI conversation data may be retained by provider
- AI conversation data may be used for model training
- AI conversation data may be subject to data breaches

**Controls**:
- AI conversation retention: 1 year (Compliance Decision Required)
- User can delete AI conversations
- Provider data handling review
- Model training opt-out
- Data minimization in AI requests

**Professional Review Required**: Yes (AI data retention)

### 5.11 Model Training/Data Usage

**Risk Assessment**:
- AI provider may use data for model training
- AI provider may use data for model improvement
- AI provider may share data with third parties

**Controls**:
- Model training opt-out
- Provider data handling review
- Data minimization in AI requests
- Data processing agreement

**Professional Review Required**: Yes (AI model training/data usage)

---

## 6. Payment Providers

### 6.1 Future Integration Requirements

**Payment Gateways**:
- Provider due diligence
- Contractual requirements
- Security requirements
- API credentials management
- Data sharing requirements
- Transaction reconciliation
- Failure handling
- Audit requirements
- Dispute handling

**Banks**:
- Provider due diligence
- Contractual requirements
- Security requirements
- API credentials management
- Data sharing requirements
- Transaction reconciliation
- Failure handling
- Audit requirements
- Dispute handling

**UPI Ecosystem**:
- NPCI approval required
- Provider due diligence
- Contractual requirements
- Security requirements
- API credentials management
- Data sharing requirements
- Transaction reconciliation
- Failure handling
- Audit requirements
- Dispute handling

**Bill Payment Providers**:
- Provider due diligence
- Contractual requirements
- Security requirements
- API credentials management
- Data sharing requirements
- Transaction reconciliation
- Failure handling
- Audit requirements
- Dispute handling

**Utility Providers**:
- Provider due diligence
- Contractual requirements
- Security requirements
- API credentials management
- Data sharing requirements
- Transaction reconciliation
- Failure handling
- Audit requirements
- Dispute handling

### 6.2 Provider Due Diligence

**Due Diligence Checklist**:
- Regulatory compliance
- Security certifications
- Data protection compliance
- Financial stability
- Reputation
- Customer reviews
- Incident history
- Data breach history

**Professional Review Required**: Yes (provider selection)

### 6.3 Contractual Requirements

**Contract Requirements**:
- Data processing agreement
- Service level agreement
- Security requirements
- Data protection requirements
- Incident notification requirements
- Audit requirements
- Termination requirements
- Data deletion requirements

**Professional Review Required**: Yes (provider contracts)

### 6.4 Security

**Security Requirements**:
- TLS 1.3 for all communication
- Encryption at rest
- Access controls
- Audit logging
- Incident response
- Security certifications

**Professional Review Required**: Yes (provider security)

### 6.5 API Credentials

**API Credentials Management**:
- API credentials stored in Secret Manager
- API credentials rotated regularly
- API credentials access controlled
- API credentials audit logged

**Professional Review Required**: Yes (provider API credentials)

### 6.6 Data Sharing

**Data Sharing Requirements**:
- Data minimization
- Purpose limitation
- User consent
- Data processing agreement
- Data retention limits
- Data deletion requirements

**Professional Review Required**: Yes (provider data sharing)

### 6.7 Transaction Reconciliation

**Transaction Reconciliation**:
- Daily reconciliation
- Discrepancy reporting
- Discrepancy resolution
- Audit logging

**Professional Review Required**: Yes (provider transaction reconciliation)

### 6.8 Failure Handling

**Failure Handling**:
- Failure detection
- Failure notification
- Failure recovery
- User notification
- Audit logging

**Professional Review Required**: Yes (provider failure handling)

### 6.9 Audit

**Audit Requirements**:
- Transaction audit logs
- API call audit logs
- Error audit logs
- Security audit logs
- Retention requirements

**Professional Review Required**: Yes (provider audit)

### 6.10 Dispute Handling

**Dispute Handling**:
- Dispute reporting
- Dispute investigation
- Dispute resolution
- User notification
- Audit logging

**Professional Review Required**: Yes (provider dispute handling)

---

## 7. KYC / AML

### 7.1 MVP KYC/AML Analysis

**MVP Activities**:
- Financial planning and budgeting
- Transaction tracking (manual entry and import)
- Savings goal tracking
- Bill tracking (manual entry/planning)
- Family financial collaboration
- AI-powered financial insights (read-only)
- Financial health scoring (educational)

**KYC/AML Obligations Assessment**:
- MVP does NOT hold customer funds
- MVP does NOT execute payments
- MVP does NOT issue stored-value instruments
- MVP does NOT provide financial services that typically trigger KYC

**Professional Review Required**: Yes (to determine KYC/AML obligations)

**Current Controls**:
- Email verification required
- Phone verification (optional, for OTP)
- No financial services that typically trigger KYC

### 7.2 Provider Responsibility

**Provider KYC/AML Responsibility**:
- Future payment providers may handle KYC
- Future payment providers may handle AML
- Provider contracts to define KYC/AML responsibility
- Professional review required to determine provider responsibility

**Professional Review Required**: Yes (provider KYC/AML responsibility)

### 7.3 NeoWallet Responsibility

**NeoWallet KYC/AML Responsibility**:
- MVP: Unknown (professional review required)
- Phase 1: Unknown (professional review required)
- Phase 2: Unknown (professional review required)
- Future: Unknown (professional review required)

**Professional Review Required**: Yes (NeoWallet KYC/AML responsibility)

### 7.4 Unknown / Professional Review

**Unknown Areas**:
- MVP KYC/AML obligations
- Phase 1 KYC/AML obligations
- Phase 2 KYC/AML obligations
- Future KYC/AML obligations
- Provider vs NeoWallet responsibility split

**Professional Review Required**: Yes (all KYC/AML areas)

---

## 8. Financial Data

### 8.1 Transaction Records

**Controls**:
- Transaction records stored in PostgreSQL
- Transaction records encrypted at rest (TDE)
- Transaction records encrypted in transit (TLS 1.3)
- Transaction records access controlled (RBAC)
- Transaction records audited
- Transaction records retained for 7 years

**Product Language**:
- "Transaction tracking"
- "Transaction records"
- NOT "NeoWallet holds customer funds"

**Professional Review Required**: Yes (transaction records)

### 8.2 Planning Values

**Controls**:
- Planning values stored in PostgreSQL
- Planning values encrypted at rest (TDE)
- Planning values encrypted in transit (TLS 1.3)
- Planning values access controlled (RBAC)
- Planning values audited
- Planning values retained for 7 years

**Product Language**:
- "Financial planning"
- "Budget planning"
- "Allocation planning"
- NOT "NeoWallet holds customer funds"
- NOT "NeoWallet manages customer funds"

**Professional Review Required**: Yes (planning values)

### 8.3 Financial Health Score

**Controls**:
- Financial health score stored in PostgreSQL
- Financial health score encrypted at rest (TDE)
- Financial health score encrypted in transit (TLS 1.3)
- Financial health score access controlled (RBAC)
- Financial health score audited
- Financial health score retained for 7 years

**Product Language**:
- "Financial health score (educational)"
- "Financial health assessment"
- NOT "Credit score"
- NOT "Guaranteed financial improvement"

**Professional Review Required**: Yes (financial health score)

### 8.4 Budget Information

**Controls**:
- Budget information stored in PostgreSQL
- Budget information encrypted at rest (TDE)
- Budget information encrypted in transit (TLS 1.3)
- Budget information access controlled (RBAC)
- Budget information audited
- Budget information retained for 7 years

**Product Language**:
- "Budget planning"
- "Budget tracking"
- NOT "NeoWallet holds customer funds"

**Professional Review Required**: Yes (budget information)

### 8.5 Savings Information

**Controls**:
- Savings information stored in PostgreSQL
- Savings information encrypted at rest (TDE)
- Savings information encrypted in transit (TLS 1.3)
- Savings information access controlled (RBAC)
- Savings information audited
- Savings information retained for 7 years

**Product Language**:
- "Savings goal tracking"
- "Savings planning"
- NOT "NeoWallet holds customer funds"
- NOT "Guaranteed savings"

**Professional Review Required**: Yes (savings information)

### 8.6 Bill Information

**Controls**:
- Bill information stored in PostgreSQL
- Bill information encrypted at rest (TDE)
- Bill information encrypted in transit (TLS 1.3)
- Bill information access controlled (RBAC)
- Bill information audited
- Bill information retained for 7 years

**Product Language**:
- "Bill tracking"
- "Bill planning"
- NOT "NeoWallet holds customer funds"
- NOT "NeoWallet pays bills"

**Professional Review Required**: Yes (bill information)

### 8.7 Marketing/Product Language

**Prohibited Claims**:
- "NeoWallet holds customer funds"
- "NeoWallet manages customer funds"
- "NeoWallet pays bills"
- "NeoWallet executes payments"
- "NeoWallet is a bank"
- "NeoWallet is a digital bank"
- "NeoWallet is a wallet issuer"

**Allowed Claims**:
- "NeoWallet tracks transactions"
- "NeoWallet plans budgets"
- "NeoWallet tracks savings goals"
- "NeoWallet tracks bills"
- "NeoWallet provides financial insights"

**Professional Review Required**: Yes (marketing/product language)

---

## 9. User Rights

### 9.1 Account Access

**Requirements**:
- User can access own account via mobile app
- User can access own account via API
- User can access own account via web (if applicable)
- Multi-factor authentication for sensitive operations

**Mapping**:
- API: GET /api/v1/users/profile
- Database: users, user_profiles
- Audit: audit_logs
- Security Controls: Authentication, Authorization

**Professional Review Required**: Yes (user account access)

### 9.2 Data Access

**Requirements**:
- User can access own personal data
- User can access own financial data
- User can access own family data (based on role)
- User can access own AI conversations

**Mapping**:
- API: GET /api/v1/users/profile, GET /api/v1/transactions, GET /api/v1/ai/conversations
- Database: users, transactions, ai_conversations
- Audit: audit_logs
- Security Controls: Authorization, Family Isolation

**Professional Review Required**: Yes (user data access)

### 9.3 Correction

**Requirements**:
- User can correct own personal data
- User can correct own financial data
- User can correct own family data (based on role)

**Mapping**:
- API: PUT /api/v1/users/profile, PUT /api/v1/transactions
- Database: users, transactions
- Audit: audit_logs
- Security Controls: Authorization

**Professional Review Required**: Yes (user data correction)

### 9.4 Deletion

**Requirements**:
- User can request account deletion
- User can delete own AI conversations
- User can leave family
- User data deleted after retention period
- Audit logs retained for 7 years

**Mapping**:
- API: DELETE /api/v1/users, DELETE /api/v1/ai/conversations
- Database: users (soft delete), ai_conversations (soft delete)
- Audit: audit_logs
- Security Controls: Authorization, Data Retention

**Professional Review Required**: Yes (user data deletion)

### 9.5 Consent/Preferences

**Requirements**:
- User can provide consent for data processing
- User can withdraw consent for data processing
- User can configure notification preferences
- User can configure privacy preferences

**Mapping**:
- API: PUT /api/v1/users/consent, PUT /api/v1/users/preferences
- Database: user_preferences
- Audit: audit_logs
- Security Controls: Authorization

**Professional Review Required**: Yes (user consent/preferences)

### 9.6 Data Portability

**Requirements**:
- User can export own data (future capability)
- User can transfer own data (future capability)

**Mapping**:
- API: GET /api/v1/users/export (future)
- Database: All user data
- Audit: audit_logs
- Security Controls: Authorization

**Professional Review Required**: Yes (data portability - future)

### 9.7 Grievance Handling

**Requirements**:
- User can submit grievances
- User can track grievance status
- User can receive grievance resolution
- Grievance handling documented
- Grievance handling audited

**Mapping**:
- API: POST /api/v1/grievances (future)
- Database: grievances (future)
- Audit: audit_logs
- Security Controls: Authorization

**Professional Review Required**: Yes (grievance handling - future)

### 9.8 Communication Preferences

**Requirements**:
- User can configure communication preferences
- User can opt-out of marketing communications
- User can opt-out of transactional communications (with limitations)

**Mapping**:
- API: PUT /api/v1/users/preferences
- Database: user_preferences
- Audit: audit_logs
- Security Controls: Authorization

**Professional Review Required**: Yes (communication preferences)

---

## 10. Record Retention

**Reference**: NeoWallet_Data_Retention_Matrix_v1.md

**Summary**:
- User data: 7 years after deletion
- Authentication records: 7 years after deletion
- Transactions: 7 years
- Budgets: 7 years
- Bills: 7 years
- Notifications: 90 days (Compliance Decision Required)
- AI conversations: 1 year (Compliance Decision Required)
- Audit logs: 7 years
- Security logs: 90 days
- Consent records: 7 years

**Professional Review Required**: Yes (all retention periods)

---

## 11. Security

**Reference**: NeoWallet_Security_Control_Matrix_v1.md

**Summary**:
- Encryption: PostgreSQL TDE, TLS 1.3
- Access Control: RBAC, Family Isolation
- Audit: Immutable audit logs
- Incident Response: Comprehensive incident response plan
- Monitoring: Security monitoring and alerting
- Backups: Automated daily backups, 30-day retention
- Secrets: Secret Manager, no hard-coded secrets
- Vendor Security: Provider due diligence
- Mobile Security: Secure token storage, biometric authentication
- API Security: Input validation, rate limiting, OWASP API Security

**Professional Review Required**: Yes (security controls)

---

## 12. Consumer Protection

### 12.1 Transparent Pricing

**Requirements**:
- Transparent subscription terms
- Clear pricing information
- No hidden fees
- No misleading pricing

**Professional Review Required**: Yes (transparent pricing)

### 12.2 Subscription Terms

**Requirements**:
- Clear subscription terms
- Clear cancellation policy
- Clear refund policy
- Clear renewal policy

**Professional Review Required**: Yes (subscription terms)

### 12.3 Refunds

**Requirements**:
- Clear refund policy
- Refund process documented
- Refund timeline defined

**Professional Review Required**: Yes (refunds - future)

### 12.4 Disclosures

**Requirements**:
- AI recommendation disclaimers
- Financial health score disclaimers
- Product disclaimers
- Risk disclaimers

**Professional Review Required**: Yes (disclosures)

### 12.5 User Consent

**Requirements**:
- User consent for data processing
- User consent for AI insights
- User consent for communication
- User can withdraw consent

**Professional Review Required**: Yes (user consent)

### 12.6 Complaint Handling

**Requirements**:
- Complaint mechanism
- Complaint tracking
- Complaint resolution
- Complaint escalation
- Complaint audit

**Professional Review Required**: Yes (complaint handling - future)

### 12.7 Misleading Financial Claims

**Prohibited Claims**:
- "Guaranteed savings"
- "Guaranteed financial improvement"
- "Guaranteed financial advice"
- "Credit score"
- "Investment advisor"

**Professional Review Required**: Yes (misleading financial claims)

### 12.8 AI Recommendation Disclaimers

**Required Disclaimers**:
- AI is NOT a licensed financial advisor
- AI recommendations are for educational purposes only
- AI recommendations are NOT guaranteed
- AI recommendations require user approval
- AI is NOT a substitute for professional financial advice

**Professional Review Required**: Yes (AI recommendation disclaimers)

---

## 13. Marketing and Product Language

### 13.1 Prohibited Claims

**Prohibited Claims**:
- "Bank account"
- "Digital bank"
- "Stored-value wallet"
- "Guaranteed savings"
- "Guaranteed financial improvement"
- "Credit score"
- "Investment advisor"
- "Guaranteed financial advice"
- "NeoWallet holds customer funds"
- "NeoWallet manages customer funds"
- "NeoWallet pays bills"
- "NeoWallet executes payments"

### 13.2 Allowed Claims

**Allowed Claims** (with appropriate disclaimers):
- "Financial management platform"
- "Budgeting tool"
- "Financial planning assistant"
- "Financial health score (educational)"
- "AI-powered financial insights"
- "AI-powered financial education"
- "Transaction tracking"
- "Budget planning"
- "Savings goal tracking"
- "Bill tracking"

### 13.3 Professional Review Required

**Review Required**:
- All marketing materials
- All product descriptions
- All website content
- All app store descriptions
- All social media content

**Professional Review Required**: Yes (marketing and product language)

---

## 14. Vendor / Third-Party Risk

### 14.1 AI Provider

**Due Diligence**:
- Regulatory compliance
- Security certifications
- Data protection compliance
- Data handling policy
- Data retention policy
- Model training policy
- Data breach notification policy

**Professional Review Required**: Yes (AI provider selection)

### 14.2 OTP Provider

**Due Diligence**:
- Regulatory compliance
- Security certifications
- Data protection compliance
- Service reliability
- Incident history

**Professional Review Required**: Yes (OTP provider selection)

### 14.3 SMS Provider

**Due Diligence**:
- Regulatory compliance
- Security certifications
- Data protection compliance
- Service reliability
- Incident history

**Professional Review Required**: Yes (SMS provider selection)

### 14.4 WhatsApp Provider

**Due Diligence**:
- Regulatory compliance
- Security certifications
- Data protection compliance
- Service reliability
- Incident history

**Professional Review Required**: Yes (WhatsApp provider selection)

### 14.5 Push Provider

**Due Diligence**:
- Regulatory compliance
- Security certifications
- Data protection compliance
- Service reliability
- Incident history

**Professional Review Required**: Yes (push provider selection)

### 14.6 Cloud Provider

**Due Diligence**:
- Regulatory compliance
- Security certifications
- Data protection compliance
- Service reliability
- Incident history
- Data location
- Subprocessors

**Professional Review Required**: Yes (cloud provider - Google Cloud)

### 14.7 Payment Provider

**Due Diligence**:
- Regulatory compliance
- Security certifications
- Data protection compliance
- Service reliability
- Incident history
- Data location
- Subprocessors

**Professional Review Required**: Yes (payment provider selection)

### 14.8 Analytics Provider

**Due Diligence**:
- Regulatory compliance
- Security certifications
- Data protection compliance
- Data handling policy
- Data retention policy
- Model training policy

**Professional Review Required**: Yes (analytics provider selection - if applicable)

### 14.9 Data Processing

**Data Processing Requirements**:
- Data minimization
- Purpose limitation
- User consent
- Data processing agreement
- Data retention limits
- Data deletion requirements

**Professional Review Required**: Yes (vendor data processing)

### 14.10 Security

**Security Requirements**:
- TLS 1.3 for all communication
- Encryption at rest
- Access controls
- Audit logging
- Incident response
- Security certifications

**Professional Review Required**: Yes (vendor security)

### 14.11 Availability

**Availability Requirements**:
- Service level agreement
- Uptime guarantee
- Disaster recovery
- Backup and recovery

**Professional Review Required**: Yes (vendor availability)

### 14.12 Incident Notification

**Incident Notification Requirements**:
- Incident notification timeline
- Incident notification process
- Incident notification details
- Incident notification recipients

**Professional Review Required**: Yes (vendor incident notification)

### 14.13 Data Location

**Data Location Requirements**:
- Data location disclosure
- Data location compliance
- Cross-border data transfer compliance

**Professional Review Required**: Yes (vendor data location)

### 14.14 Subprocessors

**Subprocessor Requirements**:
- Subprocessor disclosure
- Subprocessor approval
- Subprocessor compliance

**Professional Review Required**: Yes (vendor subprocessors)

### 14.15 Contract Termination

**Contract Termination Requirements**:
- Termination notice period
- Termination process
- Data deletion requirements
- Data return requirements

**Professional Review Required**: Yes (vendor contract termination)

### 14.16 Data Deletion

**Data Deletion Requirements**:
- Data deletion timeline
- Data deletion process
- Data deletion confirmation
- Data deletion audit

**Professional Review Required**: Yes (vendor data deletion)

---

## 15. Compliance Phase Model

### 15.1 MVP

**Compliance Requirements**:
- Data protection (DPDP Act - professional review required)
- Consumer protection (professional review required)
- IT/cybersecurity requirements (professional review required)
- Electronic records requirements (professional review required)
- AI governance (professional review required)
- Marketing and product language (professional review required)

**Professional Review Required**: Yes (all MVP compliance areas)

### 15.2 Phase 1

**Compliance Requirements**:
- MVP compliance requirements
- Payment orchestration model (professional review required)
- PPI/stored-value boundary (professional review required)
- Payment aggregation (professional review required)
- KYC/AML (professional review required)
- Payment provider due diligence (professional review required)
- Data sharing with providers (professional review required)

**Professional Review Required**: Yes (all Phase 1 compliance areas)

### 15.3 Phase 2

**Compliance Requirements**:
- Phase 1 compliance requirements
- UPI integration (professional review required)
- BBPS integration (professional review required)
- NPCI approval (professional review required)
- Additional payment provider due diligence (professional review required)

**Professional Review Required**: Yes (all Phase 2 compliance areas)

### 15.4 Future

**Compliance Requirements**:
- Phase 2 compliance requirements
- Future money movement capabilities (professional review required)
- Cross-border data/provider issues (professional review required)
- Additional regulatory requirements (professional review required)

**Professional Review Required**: Yes (all future compliance areas)

---

## 16. Conclusion

The NeoWallet Compliance Control Matrix provides a comprehensive mapping of regulatory requirements to controls, risks, business rules, APIs, data entities, and security controls. All areas requiring qualified Indian legal, regulatory, privacy, security, or financial-services professional review are identified.

**IMPORTANT DISCLAIMER**: NeoWallet has documented controls and identified professional review requirements. NeoWallet is NOT claimed to be legally or regulatorily compliant.

**Next Steps**:
1. Create Data Retention Matrix
2. Create Compliance Traceability
3. Create Compliance Gap Register
4. Create Regulatory Review Register
5. Create Compliance Validation Report

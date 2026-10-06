# NeoWallet Product and Payment Model v1

## Executive Summary

This document defines the NeoWallet product and payment model, resolving critical ambiguities identified in the pre-development gate. NeoWallet is positioned as a financial management and payment orchestration platform, not a stored-value wallet.

**Product Definition**: NeoWallet is a mobile-first AI-powered family financial management and payment orchestration platform.

**Core Value Proposition**: PLAN → OPTIMIZE → DECIDE → PAY

**Fundamental Principle**: NeoWallet does NOT hold customer funds. It maintains financial planning, allocation, budgeting, transaction records, and financial intelligence. Actual payment execution occurs through authorized/regulated payment providers.

**Date**: August 17, 2026
**Status**: Ready for Product Owner Approval
**Next Step**: Regulatory review, then proceed to NW-003

---

## 1. Product Definition

### NeoWallet Product Definition

**NeoWallet** is a mobile-first, AI-powered family financial management and payment orchestration platform designed to help families plan, optimize, and execute their financial decisions.

**Core Purpose**: 
- Provide families with comprehensive financial visibility and intelligence
- Enable collaborative financial planning and budgeting
- Orchestrate payments through authorized payment providers
- Deliver AI-powered financial insights and recommendations

**What NeoWallet IS**:
- Financial management and planning platform
- Payment orchestration layer
- Transaction recording and tracking system
- Budgeting and savings goal management
- Financial intelligence and analytics platform
- AI-powered financial advisor (read-only recommendations)

**What NeoWallet IS NOT**:
- Stored-value wallet (does not hold customer funds)
- Prepaid Payment Instrument (PPI) issuer
- Bank or financial institution
- Money transmitter
- Custodian of customer funds
- Payment processor (uses authorized providers)

### Product Positioning

**Tagline**: "A Smarter Wallet for a Better Family."

**Value Proposition**: PLAN → OPTIMIZE → DECIDE → PAY

**Positioning Statement**:
NeoWallet empowers families to take control of their finances through intelligent planning, AI-powered optimization, informed decision-making, and seamless payment orchestration—all without holding customer funds.

**Differentiation**:
- **Family-Centric**: Designed for family financial collaboration
- **AI-Powered**: Intelligent insights and recommendations
- **Orchestration-First**: Seamless payment execution through authorized providers
- **Privacy-First**: Data protection and user control
- **Planning-Focused**: Emphasis on financial planning vs. just payment execution

---

## 2. Wallet Definition

### NeoWallet Concept

In the context of NeoWallet, "Wallet" refers to a **Financial Overview and Planning Container**, not a stored-value wallet that holds actual funds.

### Wallet Components

#### 2.1 Financial Overview
**Definition**: Aggregated view of a user's or family's financial position across all connected accounts and planned allocations.

**Components**:
- Total planned allocation (sum of all budget allocations)
- Total committed amount (sum of pending payments and scheduled transactions)
- Available financial capacity (planned allocation - committed amount)
- Financial health score
- Spending trends
- Savings progress

**Data Source**: Calculated from budget allocations, savings goals, and transaction records

**Note**: This is a calculated representation, not actual funds held by NeoWallet.

#### 2.2 Planned Allocation
**Definition**: The amount of money a user or family plans to allocate to specific categories or goals within a budget period.

**Components**:
- Budget allocations by category (e.g., groceries, utilities, entertainment)
- Savings goal allocations
- Bill payment allocations
- Reserve allocations

**Data Source**: User-defined budget and savings goal settings

**Note**: This represents planned spending, not actual funds held by NeoWallet.

#### 2.3 Budget Allocation
**Definition**: The specific amount allocated to a budget category for a defined period.

**Components**:
- Category budget limit (e.g., ₹10,000 for groceries per month)
- Budget period (monthly, weekly, custom)
- Budget rollover rules
- Budget alert thresholds

**Data Source**: User-defined budget settings

**Note**: This is a planning tool, not a fund segregation mechanism.

#### 2.4 Committed Amount
**Definition**: The total amount of planned or scheduled financial commitments that have been initiated but not yet executed.

**Components**:
- Pending bill payments
- Scheduled transfers
- Recurring payment commitments
- Savings goal contributions (scheduled)

**Data Source**: Scheduled transactions and pending payments

**Note**: These are commitments to pay, not actual funds held by NeoWallet.

#### 2.5 Pending Payment
**Definition**: A payment that has been initiated, authorized, and submitted to a payment provider but has not yet been completed.

**Components**:
- Payment intent ID
- Payment amount
- Payment provider reference
- Submission timestamp
- Expected completion timestamp
- Status (pending, processing, completed, failed)

**Data Source**: Payment provider integration

**Note**: NeoWallet tracks the status but does not hold the funds.

#### 2.6 Actual Transaction
**Definition**: A completed financial transaction that has been executed through a payment provider or recorded manually.

**Components**:
- Transaction ID
- Transaction type (credit, debit, transfer)
- Amount
- Category
- Description
- Payment provider reference (if applicable)
- Completion timestamp
- Status (completed, failed, reversed)

**Data Source**: Payment provider confirmation or manual entry

**Note**: NeoWallet records the transaction for tracking and analytics but does not execute the transaction directly.

#### 2.7 Available Financial Capacity
**Definition**: The calculated amount of financial capacity available for new spending decisions within the current planning period.

**Calculation**:
```
Available Financial Capacity = 
    Planned Allocation 
    - Committed Amount 
    - Actual Transactions (in current period)
    + Income (in current period)
```

**Components**:
- Per-category available capacity
- Overall available capacity
- Capacity utilization percentage
- Capacity alerts

**Data Source**: Calculated from planned allocation, committed amount, and actual transactions

**Note**: This is a planning metric, not an actual balance of funds held by NeoWallet.

### Wallet vs. Stored-Value Wallet

| Aspect | NeoWallet (Financial Overview) | Stored-Value Wallet |
|--------|-------------------------------|---------------------|
| Fund Custody | Does NOT hold funds | Holds customer funds |
| Balance Representation | Calculated from planning data | Actual stored value |
| Payment Execution | Orchestration through providers | Direct execution |
| Regulatory Status | Financial management platform | Regulated financial institution |
| Risk Profile | Data privacy and security | Fund custody and security |
| Licensing | Data protection compliance | Financial licensing required |

---

## 3. Payment Model

### Conceptual Payment Flow

NeoWallet operates as a payment orchestration layer, facilitating payments through authorized payment providers without holding customer funds.

#### Payment Flow Diagram

```
User Request
    ↓
NeoWallet Application (Mobile)
    ↓
Payment Intent Creation
    ↓
User Confirmation (Explicit)
    ↓
Authentication & Authorization
    ↓
NeoWallet Payment Service
    ↓
Authorized Payment Provider
    ↓
User's Bank/Payment Account
    ↓
Merchant/Biller
    ↓
Payment Result
    ↓
NeoWallet Transaction Record
    ↓
Reconciliation
    ↓
User Notification
```

### Payment Flow Components

#### 3.1 Payment Initiation
**Trigger**: User initiates payment through NeoWallet mobile app

**Steps**:
1. User selects payment type (bill payment, merchant payment)
2. User enters payment details (amount, recipient, category)
3. NeoWallet validates payment against budget and financial capacity
4. NeoWallet creates payment intent with unique ID
5. Payment intent status: PENDING

**Validation**:
- User authentication
- Wallet status (not frozen)
- Budget availability (if applicable)
- Financial capacity check
- Payment amount limits

#### 3.2 User Confirmation
**Requirement**: Explicit user confirmation before payment execution

**Steps**:
1. NeoWallet displays payment confirmation screen
2. Payment details shown (amount, recipient, category, budget impact)
3. User confirms payment
4. Confirmation logged with timestamp

**Security**: Prevents accidental payments, clear display of payment details, confirmation cannot be bypassed

#### 3.3 Authentication
**Requirement**: User must be authenticated before payment execution

**Steps**: JWT token validation, session validation, device validation, biometric authentication (future)

#### 3.4 Authorization
**Requirement**: Payment must be authorized based on user permissions and policies

**Steps**: RBAC check, policy validation, wallet status check, budget check, family permission check

**Authorization Rules**: Per-transaction limits, daily limits, category limits, family member permissions

#### 3.5 Idempotency
**Requirement**: Prevent duplicate payment execution

**Implementation**: Payment intent ID as unique identifier, idempotency key for API calls, status tracking, retry logic

#### 3.6 Provider Integration
**Requirement**: Integration with authorized payment providers

**Provider Types**: UPI, Bank transfer (NEFT/IMPS/RTGS), Card payment, Bill payment (BBPS)

**Integration**: API-based integration with payment providers, no direct fund custody

#### 3.7 Provider Response
**Requirement**: Handle payment provider responses

**Response Types**: Success, Failure, Pending, Timeout

**Handling**: Update transaction status, notify user, log response, handle retries

#### 3.8 Success
**Requirement**: Handle successful payment completion

**Steps**: Update transaction status to COMPLETED, update budget/financial capacity, notify user, record audit event

#### 3.9 Failure
**Requirement**: Handle payment failure

**Steps**: Update transaction status to FAILED, notify user with reason, log error, enable retry

**Failure Types**: Insufficient funds, invalid recipient, provider error, timeout

#### 3.10 Timeout
**Requirement**: Handle payment provider timeout

**Steps**: Mark transaction as TIMEOUT, schedule status check, notify user, enable retry

#### 3.11 Reversal
**Requirement**: Handle payment reversal (e.g., fraud, dispute)

**Steps**: High-level authorization required, call provider reversal API, update transaction status, record audit event

#### 3.12 Refund
**Requirement**: Handle refund requests

**Steps**: User or merchant-initiated refund, authorization check, call provider refund API, track refund status, notify user

#### 3.13 Reconciliation
**Requirement**: Reconcile internal records with provider records

**Steps**: Daily reconciliation run, compare internal vs provider records, identify discrepancies, resolve disputes, generate reports

#### 3.14 Audit
**Requirement**: Complete audit trail for all payment operations

**Components**: All payment events logged, immutable records, user context (device, IP, timestamp), compliance reporting

---

## 4. Financial Data Model Concepts

### Data Model Hierarchy

```
Financial Overview (Wallet)
    ↓
Planned Allocation
    ↓
Budget Allocation (by category)
    ↓
Committed Amount
    ↓
Pending Payments
    ↓
Actual Transactions
    ↓
Available Financial Capacity (calculated)
```

### Key Distinctions

**Planned vs. Actual**:
- Planned Allocation: User's intended spending
- Actual Transaction: Completed spending
- Available Capacity: Remaining planned capacity

**Committed vs. Executed**:
- Committed Amount: Scheduled/pending payments
- Executed Transaction: Completed payments

**Budget vs. Transaction**:
- Budget: Planning tool for spending limits
- Transaction: Record of actual spending

**Financial Capacity vs. Balance**:
- Financial Capacity: Planning metric (planned - committed - actual)
- Balance: Actual funds in bank account (not held by NeoWallet)

---

## 5. MVP Capabilities

### MVP Feature List (Updated)

1. **Authentication** ✓
   - User registration and login
   - Email/password authentication
   - JWT token-based authentication
   - Password reset functionality
   - Session management
   - Device registration

2. **User Profile** ✓
   - User profile CRUD
   - Profile image management
   - User preferences
   - Profile validation

3. **Family Management** ✓
   - Family creation and management
   - Family member roles (OWNER, MEMBER, RESTRICTED)
   - Family member permissions
   - Family wallet sharing (financial overview sharing)
   - Family spending limits

4. **Financial Overview / Wallet** ✓ (Updated)
   - Financial overview creation (individual and family)
   - Planned allocation tracking
   - Budget allocation tracking
   - Committed amount tracking
   - Available financial capacity calculation
   - Financial overview types (INDIVIDUAL, FAMILY)
   - Financial overview status (ACTIVE, FROZEN, CLOSED)
   - **Wallet-to-wallet transfers: NOT INCLUDED in MVP**

5. **Transactions** ✓
   - Manual transaction entry
   - Transaction categorization (predefined categories)
   - Transaction search and filtering
   - Transaction editing with audit trail
   - Transaction history
   - Transaction reports

6. **Budget** ✓
   - Budget creation by category
   - Budget limit setting
   - Budget vs. actual tracking
   - Budget alerts
   - Budget progress visualization
   - Budget period management

7. **Savings Goals** ✓
   - Savings goal creation
   - Target amount and timeline
   - Progress tracking
   - Goal contributions (manual entry)
   - Goal visualization
   - Goal achievements

8. **Bills** ✓
   - Manual bill entry
   - Bill due date tracking
   - Bill reminders
   - Bill payment recording (manual entry, no actual payment)
   - Bill history
   - Recurring bills

9. **Notifications** ✓
   - In-app notifications
   - Email notifications
   - Push notifications (mobile)
   - Notification preferences
   - Notification history

10. **Neo AI Concierge (Controlled Read-Only Mode)** ✓
    - Natural language interface
    - Spending analysis
    - Budget explanation
    - Budget recommendations
    - Financial summaries
    - Bill analysis
    - Savings recommendations
    - Financial health explanation
    - STRICTLY NO AUTONOMOUS FINANCIAL TRANSACTIONS

11. **Financial Health** ✓
    - Financial health scoring
    - Spending trend analysis
    - Savings rate tracking
    - Health visualization
    - Improvement suggestions

### MVP Exclusions (Explicitly Deferred)

**Payment Execution** (deferred to Phase 1):
- Payment gateway integration
- Actual payment processing
- Payment provider integration
- Bill payment execution

**Fund Management** (deferred to Phase 1 or future):
- Add money to wallet (not applicable - NeoWallet doesn't hold funds)
- Withdraw money from wallet (not applicable - NeoWallet doesn't hold funds)
- Cash-out (not applicable - NeoWallet doesn't hold funds)

**Transfers** (deferred to Phase 2 or future):
- Wallet-to-wallet transfers (deferred to Phase 2)
- Peer-to-peer transfers (deferred to Phase 2)
- Bank transfers (deferred to Phase 2)

**Advanced Features** (deferred to future phases):
- Utility provider integration (deferred to Phase 2)
- Banking API integration (deferred to Phase 3)
- Marketplace (deferred to Phase 3)
- Vendor management (deferred to Phase 3)
- Recharge (deferred to Phase 2)
- Advanced AI agents (deferred to Phase 2)
- Administrative portal (deferred to Phase 4)
- Multi-factor authentication (deferred to Phase 1)
- Social login (deferred to Phase 1)

---

## 6. Deferred Capabilities

### Capability Classification

#### Phase 1 Capabilities (Payment Execution)
- Payment gateway integration
- Actual payment processing (bill payments, merchant payments)
- Payment provider integration
- Payment reconciliation
- Multi-factor authentication
- Social login

#### Phase 2 Capabilities (Enhanced Transfers)
- Wallet-to-wallet transfers (financial overview transfers)
- Peer-to-peer transfers
- Utility provider integration
- Recharge
- Advanced AI agents (Financial Agent, Budget Agent)

#### Phase 3 Capabilities (Marketplace)
- Banking API integration
- Marketplace
- Vendor management
- Advanced payment features

#### Phase 4 Capabilities (Scale)
- Administrative portal
- Advanced analytics
- International features

#### Not Planned
- Stored-value wallet functionality
- PPI issuance
- Fund custody
- Money transmission
- Direct payment processing (without authorized providers)

---

## 7. Regulatory Review Areas

### Regulatory Review Required

**IMPORTANT**: The following areas require review by qualified legal and regulatory professionals. This document does not claim compliance or provide legal advice.

#### 7.1 Stored-Value/PPI Implications
**Review Required**: Confirm that NeoWallet's financial overview model does not constitute a stored-value wallet or PPI.

**Considerations**:
- Does financial overview constitute "stored value"?
- Are budget allocations considered "prepaid instruments"?
- Does planned allocation trigger PPI regulations?
- What is the regulatory classification of financial planning tools?

**Professional Review Required**: Yes - Legal/Regulatory counsel

#### 7.2 Payment System Activity
**Review Required**: Confirm regulatory requirements for payment orchestration activities.

**Considerations**:
- Payment aggregator regulations
- Payment facilitator requirements
- Payment system operator licensing
- Cross-border payment regulations

**Professional Review Required**: Yes - Legal/Regulatory counsel

#### 7.3 Payment Aggregation
**Review Required**: Confirm whether payment orchestration constitutes payment aggregation.

**Considerations**:
- Payment aggregator licensing requirements
- Settlement responsibilities
- Fund flow regulations
- Liability for payment failures

**Professional Review Required**: Yes - Legal/Regulatory counsel

#### 7.4 Bill Payment/BBPS
**Review Required**: Confirm requirements for bill payment orchestration through BBPS.

**Considerations**:
- BBPS participant requirements
- Biller onboarding regulations
- Transaction reporting requirements
- Consumer protection regulations

**Professional Review Required**: Yes - Legal/Regulatory counsel

#### 7.5 UPI Integration
**Review Required**: Confirm requirements for UPI integration.

**Considerations**:
- UPI participant licensing
- NPCI requirements
- Transaction limits
- Compliance reporting

**Professional Review Required**: Yes - Legal/Regulatory counsel

#### 7.6 KYC/AML Requirements
**Review Required**: Confirm KYC and AML requirements for financial management platform.

**Considerations**:
- KYC requirements for financial planning
- AML obligations for payment orchestration
- Transaction monitoring requirements
- Suspicious activity reporting

**Professional Review Required**: Yes - Legal/Regulatory counsel

#### 7.7 Customer Funds Handling
**Review Required**: Confirm that NeoWallet does not handle customer funds.

**Considerations**:
- Fund flow verification
- Custody confirmation
- Trust account requirements (if any)
- Segregation of funds

**Professional Review Required**: Yes - Legal/Regulatory counsel

#### 7.8 Data Protection/Privacy
**Review Required**: Confirm compliance with data protection regulations.

**Considerations**:
- DPDP Act compliance
- GDPR compliance (if international)
- Data localization requirements
- Consent management
- Right to erasure

**Professional Review Required**: Yes - Legal/Regulatory counsel

#### 7.9 Financial Advisory Implications of AI
**Review Required**: Confirm regulatory requirements for AI-powered financial recommendations.

**Considerations**:
- Investment advisory regulations
- Financial advice licensing
- AI recommendation liability
- Disclosure requirements
- Suitability assessments

**Professional Review Required**: Yes - Legal/Regulatory counsel

### Compliance Framework

**Target Compliance Standards**:
- DPDP Act (Data Protection)
- ISO 27001 (Information Security)
- SOC 2 (Security and Availability)
- PCI DSS (if card payments integrated)

**Compliance Status**: To be validated by qualified professionals

**Disclaimer**: This document does not constitute legal advice. Regulatory compliance must be validated by qualified legal and regulatory professionals.

---

## 8. Security Considerations

### Security Principles

**Data Security**:
- Encryption at rest (AES-256)
- Encryption in transit (TLS 1.3)
- Field-level encryption for sensitive data
- Secure key management (Cloud KMS)

**Authentication & Authorization**:
- JWT token-based authentication
- RBAC (Role-Based Access Control)
- Device registration and binding
- Session management
- Multi-factor authentication (future)

**Payment Security**:
- Explicit user confirmation for all payments
- Authorization checks before payment execution
- Idempotency to prevent duplicate payments
- Audit trail for all payment operations
- No direct database access by AI

**AI Security**:
- AI operates in read-only mode
- No autonomous financial transactions
- No direct payment gateway access by AI
- Human-in-the-loop for critical decisions
- Clear AI capability boundaries

**Audit & Compliance**:
- Comprehensive audit logging
- Immutable audit trail
- Regular security audits
- Compliance reporting
- Penetration testing

### Risk Mitigation

**Financial Risk**: NeoWallet does not hold funds, reducing financial risk
**Data Privacy Risk**: Data protection measures, compliance with DPDP Act
**Payment Risk**: Integration with authorized payment providers, no direct processing
**AI Risk**: Read-only mode, human-in-the-loop, clear boundaries

---

## 9. Open Decisions

### Decisions Still Required

#### 9.1 Payment Provider Selection
**Decision**: Which payment providers to integrate for Phase 1?

**Options**: Razorpay, Stripe, Multiple providers, UPI direct integration

**Decision Owner**: Product Owner
**Timeline**: Before Phase 1 implementation

#### 9.2 UPI Integration Strategy
**Decision**: How to integrate UPI for payments?

**Options**: Direct NPCI integration, Payment gateway with UPI, Both

**Decision Owner**: Product Owner/Tech Lead
**Timeline**: Before Phase 1 implementation

#### 9.3 Bill Payment Strategy
**Decision**: How to implement bill payment execution?

**Options**: BBPS integration, Payment gateway, Direct biller integration

**Decision Owner**: Product Owner
**Timeline**: Before Phase 1 implementation

#### 9.4 Wallet-to-Wallet Transfer Implementation
**Decision**: How to implement wallet-to-wallet transfers in Phase 2?

**Options**: Financial overview transfer only, Actual fund transfer, Both

**Decision Owner**: Product Owner
**Timeline**: Before Phase 2 implementation

#### 9.5 AI Recommendation Liability
**Decision**: How to handle liability for AI financial recommendations?

**Options**: Disclaimers, User acceptance, Professional review, Limited scope

**Decision Owner**: Legal/Regulatory Counsel
**Timeline**: Before AI implementation

#### 9.6 Regulatory Jurisdiction
**Decision**: Which regulatory jurisdictions apply?

**Options**: India only, India + International, Global

**Decision Owner**: Legal/Regulatory Counsel
**Timeline**: Before MVP launch

---

## 10. Recommended Product Architecture

### Architecture Overview

**Product Architecture**:
```
Mobile Application (Flutter)
    ↓
API Layer (Spring Boot)
    ↓
Business Logic Layer (Modular Monolith)
    ↓
Financial Overview Engine
    ↓
Budget Engine
    ↓
Transaction Engine
    ↓
Payment Orchestration Layer (Phase 1)
    ↓
Authorized Payment Providers (Phase 1)
```

### Data Architecture

**Data Model**:
- Financial Overview (calculated from planning data)
- Planned Allocation (user-defined budgets)
- Committed Amount (scheduled payments)
- Actual Transactions (completed payments)
- Budget Categories (planning categories)
- Savings Goals (planning goals)

**No Fund Custody**: No tables or mechanisms for holding actual customer funds

### Payment Architecture

**Payment Orchestration**:
- Payment Intent Creation
- User Confirmation
- Authorization
- Provider Integration
- Status Tracking
- Reconciliation

**No Direct Payment Processing**: All payments executed through authorized providers

### AI Architecture

**AI Capabilities**:
- Read-only data access via APIs
- Spending analysis
- Budget recommendations
- Financial summaries
- No autonomous transactions

**AI Security**:
- No direct database access
- No payment gateway access
- Human-in-the-loop
- Clear capability boundaries

---

## Conclusion

This document defines NeoWallet as a financial management and payment orchestration platform, not a stored-value wallet. The key principles are:

1. **NeoWallet does NOT hold customer funds**
2. **NeoWallet maintains financial planning, allocation, budgeting, transaction records, and financial intelligence**
3. **Actual payment execution occurs through authorized/regulated payment providers**
4. **Wallet is a Financial Overview and Planning Container, not a stored-value wallet**
5. **AI operates in read-only mode with no autonomous financial transactions**

**Next Steps**:
1. Product Owner approval of this product and payment model
2. Regulatory review by qualified professionals
3. Resolve open decisions
4. Proceed to NW-003 (implementation)

**Disclaimer**: This document does not constitute legal advice. Regulatory compliance must be validated by qualified legal and regulatory professionals.

---

**Document Version**: v1
**Date**: August 17, 2026
**Status**: Ready for Product Owner Approval
**Next Step**: Regulatory review, then NW-003

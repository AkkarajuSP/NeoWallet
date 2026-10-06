# NeoWallet Dependency Map

## Executive Summary

This document outlines the dependencies across the NeoWallet ecosystem, including service dependencies, technology dependencies, and integration dependencies. Understanding these dependencies is critical for implementation planning and risk management.

## Service Dependency Map

### Microservice Dependencies

```
┌─────────────────────────────────────────────────────────────────┐
│                        API Gateway                                │
│  (Ambassador/Kong - Authentication, Routing, Rate Limiting)     │
└─────────────────────────────────────────────────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
        ▼                     ▼                     ▼
┌───────────────┐    ┌───────────────┐    ┌───────────────┐
│ Auth Service  │    │ User Service  │    │ Wallet Service│
│ (identity_db) │    │ (identity_db) │    │ (wallet_db)   │
└───────────────┘    └───────────────┘    └───────────────┘
        │                     │                     │
        └─────────────────────┼─────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
        ▼                     ▼                     ▼
┌───────────────┐    ┌───────────────┐    ┌───────────────┐
│ Budget Service│    │ Bill Service  │    │Vendor Service │
│ (budget_db)   │    │ (billing_db)  │    │ (vendor_db)   │
└───────────────┘    └───────────────┘    └───────────────┘
        │                     │                     │
        └─────────────────────┼─────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
        ▼                     ▼                     ▼
┌───────────────┐    ┌───────────────┐    ┌───────────────┐
│ Order Service │    │Payment Service│    │Notification   │
│ (order_db)    │    │ (payment_db)  │    │Service        │
└───────────────┘    └───────────────┘    │(notification  │
                                           │    _db)       │
                                           └───────────────┘
                              │
                              ▼
                    ┌──────────────────┐
                    │   AI Service     │
                    │   (ai_db)        │
                    │   + Vector DB    │
                    └──────────────────┘
                              │
                              ▼
                    ┌──────────────────┐
                    │Analytics Service │
                    │ (analytics_db)    │
                    │ + BigQuery       │
                    └──────────────────┘
```

### Service Dependency Details

| Service | Depends On | Dependency Type | Criticality |
|---------|------------|-----------------|-------------|
| **Auth Service** | None | - | - |
| **User Service** | Auth Service | Authentication | High |
| **Wallet Service** | Auth Service, User Service | Authentication, User Data | High |
| **Budget Service** | Auth Service, User Service, Wallet Service | Authentication, User Data, Balance | High |
| **Bill Service** | Auth Service, User Service, Wallet Service | Authentication, User Data, Payment | High |
| **Vendor Service** | Auth Service | Authentication | Medium |
| **Order Service** | Auth Service, User Service, Wallet Service, Vendor Service | Auth, User, Payment, Catalog | High |
| **Payment Service** | Auth Service, Wallet Service, Order Service | Auth, Balance, Order Context | Critical |
| **Notification Service** | All Services | Event Subscriptions | High |
| **AI Service** | All Services | Data Access | High |
| **Analytics Service** | All Services | Data Aggregation | Medium |

## Technology Dependency Map

### Frontend Dependencies

```
Flutter Mobile App
├── Flutter SDK
├── Dart Programming Language
├── REST API Clients
├── Local Storage (SQLite/Hive)
├── State Management (Provider/Riverpod)
└── Push Notification Services

React Web App
├── React Framework
├── TypeScript
├── REST API Clients (Axios/Fetch)
├── State Management (Redux/Context)
├── UI Components (Material-UI/Ant Design)
└── Build Tools (Webpack/Vite)
```

### Backend Dependencies

```
Spring Boot Services
├── Java 21
├── Spring Boot Framework
├── Spring Security
├── Spring Data JPA
├── PostgreSQL JDBC Driver
├── Redis Client
├── Kafka Client
├── OpenSearch Client
├── JWT Libraries
└── Testing Frameworks (JUnit, Mockito)

FastAPI AI Services
├── Python 3.11+
├── FastAPI Framework
├── LLM Client Libraries
├── Vector Database Client
├── ML Frameworks (scikit-learn, TensorFlow)
├── Data Processing (Pandas, NumPy)
└── Testing Frameworks (pytest)
```

### Infrastructure Dependencies

```
Google Cloud Platform
├── Google Kubernetes Engine (GKE)
├── Cloud SQL (PostgreSQL)
├── Redis Memorystore
├── Cloud Storage
├── BigQuery
├── Cloud Load Balancing
├── Cloud CDN
├── Cloud Armor (WAF)
├── Cloud Logging
├── Cloud Monitoring
├── Secret Manager
└── VPC Networking

Kubernetes Ecosystem
├── Docker
├── Istio (Service Mesh)
├── Helm (Package Management)
├── Argo CD (GitOps)
└── Prometheus/Grafana (Monitoring)
```

## Data Flow Dependencies

### Authentication Flow
```
User Request → API Gateway → Auth Service → identity_db
    ↓                ↓              ↓            ↓
JWT Token ← JWT ← JWT Validation ← User Lookup
```

### Transaction Flow
```
User Request → API Gateway → Wallet Service → wallet_db
    ↓                ↓              ↓            ↓
Response ← Kafka Event ← Transaction ← Balance Update
                           ↓
                    Notification Service
```

### AI Recommendation Flow
```
User Data → Kafka Events → AI Service → Vector DB
    ↓           ↓              ↓           ↓
Request ← Feature Store ← LLM Query ← Embeddings
    ↓
Recommendation
```

## External System Dependencies

### Critical External Dependencies
| System | Purpose | Dependency Type | SLA Required | Fallback Strategy |
|--------|---------|-----------------|--------------|-------------------|
| **Payment Gateway** | Payment Processing | Critical | 99.9% | Multiple gateways |
| **SMS Gateway** | SMS Notifications | High | 99.5% | Email fallback |
| **Email Gateway** | Email Notifications | High | 99.5% | In-app fallback |
| **Utility Providers** | Bill Fetching | Medium | 98% | Manual entry |
| **Banking APIs** | Account Verification | Medium | 98% | Manual verification |
| **LLM Provider** | AI Services | High | 99% | Local model fallback |
| **Push Notification** | Mobile Notifications | High | 99% | In-app notification |

### External Integration Complexity
| Integration | Complexity | Risk Level | Implementation Priority |
|-------------|------------|------------|------------------------|
| Payment Gateway | High | High | P0 |
| SMS Gateway | Medium | Medium | P1 |
| Email Gateway | Low | Low | P1 |
| Utility Providers | High | High | P2 |
| Banking APIs | High | High | P2 |
| LLM Provider | Medium | High | P0 |
| Push Notifications | Medium | Medium | P1 |

## Database Dependency Map

### Database Schema Dependencies
```
identity_db (Users, Roles, Permissions)
    ↓
wallet_db (Wallets, Transactions) - REFERENCES identity_db.users
    ↓
budget_db (Budgets, Categories) - REFERENCES identity_db.users, wallet_db.wallets
    ↓
billing_db (Bills, Billers) - REFERENCES identity_db.users, wallet_db.wallets
    ↓
vendor_db (Vendors, Products) - Independent
    ↓
order_db (Orders) - REFERENCES identity_db.users, wallet_db.wallets, vendor_db.vendors
    ↓
payment_db (Payments) - REFERENCES order_db.orders, wallet_db.wallets
    ↓
notification_db (Notifications) - REFERENCES identity_db.users
    ↓
ai_db (Recommendations) - REFERENCES identity_db.users
    ↓
analytics_db (Reports) - Aggregated from all databases
```

### Cross-Database Query Dependencies
- **Analytics Service**: Requires read access to all operational databases
- **AI Service**: Requires read access to user, wallet, transaction, budget data
- **Reporting**: Requires data aggregation across multiple databases

## Build & Deployment Dependencies

### CI/CD Pipeline Dependencies
```
Code Push → GitHub → GitHub Actions → Build → Test → Security Scan
    ↓
Docker Image Build → Docker Registry → Argo CD → Kubernetes Deployment
    ↓
Health Checks → Monitoring → Rollback (if needed)
```

### Deployment Order Dependencies
1. **Infrastructure First**: Terraform → GCP Resources → Kubernetes Cluster
2. **Data Layer**: PostgreSQL → Redis → Kafka → OpenSearch
3. **Core Services**: Auth → User → Wallet
4. **Business Services**: Budget → Bill → Vendor → Order → Payment
5. **Support Services**: Notification → AI → Analytics
6. **Frontend**: Mobile App → Web App
7. **Monitoring**: Prometheus → Grafana → Dashboards

## Security Dependency Map

### Security Control Dependencies
```
Zero Trust Architecture
├── Identity & Access Management
│   ├── OAuth2/OIDC Provider
│   ├── JWT Token Service
│   └── MFA Service
├── Network Security
│   ├── VPC Configuration
│   ├── Firewall Rules
│   └── TLS Certificates
├── Application Security
│   ├── API Gateway Authentication
│   ├── Service-to-Service Auth
│   └── Input Validation
└── Data Security
    ├── Encryption Keys
    ├── Secrets Management
    └── Data Masking
```

### Compliance Dependencies
```
ISO 27001 ←→ Security Controls ←→ Audit Logging
    ↓              ↓                  ↓
SOC 2 ←→ Access Management ←→ Incident Response
    ↓              ↓                  ↓
PCI DSS ←→ Payment Security ←→ Penetration Testing
    ↓              ↓                  ↓
DPDP Act ←→ Data Privacy ←→ Consent Management
```

## Monitoring Dependency Map

### Observability Stack Dependencies
```
Application Metrics → Prometheus → Grafana Dashboards
    ↓                    ↓              ↓
Application Logs → Cloud Logging → Log Queries
    ↓                    ↓              ↓
Distributed Traces → Tracing System → Performance Analysis
    ↓                    ↓              ↓
Alerts → Alert Manager → Incident Response
```

### Monitoring Service Dependencies
| Service | Monitoring Required | Metrics Collected | Alert Thresholds |
|---------|-------------------|-------------------|------------------|
| **Auth Service** | Authentication metrics | Login rate, failure rate, latency | >5% failure rate |
| **Wallet Service** | Transaction metrics | Transaction volume, balance accuracy | Data inconsistency |
| **Payment Service** | Payment metrics | Success rate, gateway latency | <95% success rate |
| **AI Service** | AI metrics | Response time, model accuracy | >5s latency |
| **Database** | Database metrics | Connection pool, query performance | >80% connection usage |

## Risk Dependencies

### Single Points of Failure
1. **API Gateway**: Single entry point for all traffic
2. **PostgreSQL**: Primary database for transactional data
3. **Kafka**: Event streaming backbone
4. **LLM Provider**: AI service dependency
5. **Payment Gateway**: Payment processing dependency

### Cascading Failure Risks
```
API Gateway Failure → All Services Unavailable
PostgreSQL Failure → Database-Dependent Services Down
Kafka Failure → Event-Driven Communication Broken
LLM Provider Failure → AI Services Degraded
Payment Gateway Failure → Payment Processing Halted
```

## Dependency Management Strategy

### Dependency Governance
1. **Version Control**: All dependencies versioned and pinned
2. **Security Scanning**: Regular dependency vulnerability scanning
3. **Update Policy**: Monthly dependency review and updates
4. **Fallback Planning**: Critical dependencies have fallback options
5. **Monitoring**: Dependency health monitoring and alerting

### Dependency Risk Mitigation
1. **Redundancy**: Critical services have multiple instances
2. **Circuit Breakers**: Prevent cascading failures
3. **Rate Limiting**: Protect against dependency overload
4. **Graceful Degradation**: Functionality reduction vs. complete failure
5. **Health Checks**: Continuous dependency health monitoring

## Conclusion

The NeoWallet dependency map reveals a complex interconnected system with critical dependencies on external services and internal service chains. Key areas of focus include:

1. **Service Dependencies**: Clear chain from Auth → Wallet → Business Services
2. **External Dependencies**: Payment gateway and LLM provider are critical
3. **Database Dependencies**: Schema references require careful migration planning
4. **Deployment Dependencies**: Strict order required for service rollout
5. **Security Dependencies**: Comprehensive security control chain

**Critical Path**: Infrastructure → Data Layer → Core Services → Business Services → Frontend → Monitoring

**Risk Mitigation**: Implement redundancy, circuit breakers, health checks, and fallback strategies for all critical dependencies.
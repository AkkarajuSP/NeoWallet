# NeoWallet

NeoWallet is a financial management and payment orchestration platform. The MVP focuses on personal and family financial planning, including budgeting, savings goals, bill tracking, transactions, notifications, and a read-only AI concierge.

**IMPORTANT PRODUCT BOUNDARY**:

NeoWallet MVP does NOT:

- Hold customer funds
- Maintain custodial wallet balances
- Issue stored-value/PPI
- Execute payments
- Execute bill payments
- Perform wallet-to-wallet transfers
- Perform P2P money movement
- Provide cash-out
- Allow autonomous AI money movement

## Architecture

```
Flutter Mobile
      ↓
API Gateway/BFF
      ↓
Spring Boot Modular Monolith
      ↓
PostgreSQL
Redis where justified
      ↓
Neo AI Service / AI Orchestration
      ↓
Authorized AI Tools
      ↓
Deterministic Financial Services
```

- **Mobile**: Flutter
- **Backend**: Java 21 + Spring Boot
- **Database**: PostgreSQL
- **Migrations**: Flyway
- **Cache**: Redis (where justified)
- **AI**: Separate Python FastAPI service
- **Deployment**: Google Cloud Run
- **CI/CD**: GitHub Actions

## Repository Structure

```
neowallet/
├── mobile/neowallet-app/        Flutter mobile application
├── backend/neowallet-api/       Spring Boot API
├── ai/neowallet-ai/             Python FastAPI AI orchestration
├── database/documentation/      Database documentation
├── infrastructure/documentation/ Infrastructure documentation
├── docs/                        Additional documentation
├── scripts/                     Utility scripts
└── .github/workflows/           CI/CD pipelines
```

## Prerequisites

- Java 21
- Gradle 8.7+ (or use wrapper)
- Flutter 3.19+
- Python 3.11+
- PostgreSQL 16+
- Redis 7+ (optional for local dev)

## Local Setup

### 1. Clone and configure environment

```bash
cp env.example .env
# Edit .env with your local values
```

### 2. PostgreSQL

```bash
createdb neowallet_dev
# Create user and grant access
```

### 3. Backend

```bash
cd backend/neowallet-api
gradle bootRun
# or
./gradlew bootRun
```

Spring Boot will run on `http://localhost:8080`.

Health endpoints:

- `http://localhost:8080/actuator/health`
- `http://localhost:8080/api/v1/health`

### 4. AI Service

```bash
cd ai/neowallet-ai
python -m venv .venv
source .venv/bin/activate  # Windows: .venv\Scripts\activate
pip install -r requirements-dev.txt
uvicorn app.main:app --reload --port 8000
```

### 5. Mobile

```bash
cd mobile/neowallet-app
cp .env.example .env
flutter pub get
flutter run
```

## Build Commands

### Backend

```bash
cd backend/neowallet-api
gradle build
```

### AI

```bash
cd ai/neowallet-ai
pytest
```

### Mobile

```bash
cd mobile/neowallet-app
flutter analyze
flutter test
```

## Test Commands

### Backend

```bash
cd backend/neowallet-api
gradle test
```

### AI

```bash
cd ai/neowallet-ai
pytest
```

### Mobile

```bash
cd mobile/neowallet-app
flutter test
```

## Security Rules

- No secrets are committed to the repository.
- Use environment variables or a secrets manager for all credentials.
- Do not log passwords, OTP, tokens, secrets, or sensitive financial data.
- All AI tool authorization is server-side.
- AI does not have direct database access.
- Money uses `NUMERIC`/`DECIMAL` (never floating point).
- No custodial wallet balance in the data model.

## Contribution Guidelines

- Use conventional commits.
- Require PR review for `main` and `develop`.
- Keep feature changes small and focused.
- Maintain the approved MVP boundary; do not introduce payment execution or money movement in MVP.

## Authoritative Documents

- `NeoWallet_Implementation_Baseline_v1.md`
- `NeoWallet_Product_Payment_Model_v1.md`
- `NeoWallet_User_Stories_v1.md`
- `NeoWallet_Business_Rules_v1.md`
- `NeoWallet_Budget_Allocation_Algorithm_v1.md`
- `NeoWallet_Financial_Health_Algorithm_v1.md`
- `NeoWallet_AI_Functional_Specification_v1.md`
- `NeoWallet_API_Contract_Specification_v1.md`
- `NeoWallet_OpenAPI_v1.yaml`
- `NeoWallet_Database_Architecture_v1.md`
- `NeoWallet_ER_Model_v1.md`
- `NeoWallet_Data_Dictionary_v1.md`
- `NeoWallet_Security_Implementation_Guide_v1.md`
- `NeoWallet_Threat_Model_v1.md`
- `NeoWallet_Security_Control_Matrix_v1.md`
- `NeoWallet_Compliance_MVP_Blocking_Analysis_v1.md`
- `NeoWallet_Final_PreDevelopment_Gate_v1.md`

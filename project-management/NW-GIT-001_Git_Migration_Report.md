# NW-GIT-001 — Git Repository Migration & Baseline Report

**Date:** 2026-10-06
**Ticket:** NW-GIT-001
**Status:** COMPLETE — baseline pushed and verified

---

## 1. Source & Target

| Item | Value |
|---|---|
| Source project root | `C:\Users\sri26\NeoWallet_Project_Analysis\neowallet` |
| Documentation source | `C:\Users\sri26\NeoWallet_Project_Analysis` (copied, not moved — originals preserved) |
| Target repository | https://github.com/AkkarajuSP/NeoWallet |
| Remote state before push | Empty (no branches, no files — verified via `git ls-remote`) |
| Prior Git history | None — project was not under version control; fresh baseline created |

## 2. Repository Structure

```
neowallet/
├── .github/workflows/    CI: backend-ci.yml, mobile-ci.yml, ai-ci.yml
├── ai/neowallet-ai/      Neo AI service (Python/FastAPI, tests, pyproject)
├── api/                  NeoWallet_OpenAPI_v1.yaml
├── backend/neowallet-api/ Spring Boot 3 / Java 21 modular monolith
│   └── src/main/resources/db/migration/  Flyway V1–V11
├── database/documentation/  Database documentation home
├── docs/                 40 architecture/security/requirements specs (NeoWallet_*.md, 00–09 assessments)
├── infrastructure/documentation/  DevOps documentation home
├── mobile/neowallet-app/ Flutter app (lib, test, assets incl. NeoWallet branding)
├── project-management/   42 NW-003.x delivery reports + this report
├── scripts/              Utility scripts
├── testing/              NW-003.14 UAT test plan & test cases
├── .gitignore            Comprehensive multi-stack ignore rules
├── env.example           Placeholder environment template (no secrets)
└── README.md
```

## 3. Branch Strategy

All four environment branches created at the same verified baseline commit:

- **main** → Production
- **staging** → Staging
- **uat** → UAT
- **develop** → Development

No unfinished work merged into `main`. No force-push used anywhere.

## 4. Commit & Push

| Item | Value |
|---|---|
| Baseline commit | `768f5b710c26036bc9f633b4eaab306a0cdd67bc` — "NW-GIT-001: Establish NeoWallet repository baseline" |
| Report commit | Follow-up commit adding this report — see `git log` |
| Push result | All 4 branches created on remote; remote HEAD = `main` @ baseline → updated to report commit |
| Push method | Normal push (`git push -u origin main develop staging uat`); **no force-push** |
| Working tree | Clean after commit; only intentionally ignored files remain untracked |

## 5. Files Included

- Backend source (Java 21, Spring Boot): auth, OTP, SendGrid provider impl, family management, financial overview, transactions, budgets, savings goals/contributions, bills, Financial Health v1.1, Neo AI integration, AI safety/policy enforcement
- Flyway migrations `V1__baseline.sql` … `V11__financial_health.sql`
- Flutter mobile app: full `lib/`, `test/`, branding assets, splash routing fix
- Neo AI Python service with tests
- OpenAPI specification `NeoWallet_OpenAPI_v1.yaml`
- GitHub Actions CI for backend, mobile, and AI
- 40 specification/architecture/security documents (`docs/`)
- 42 project reports (`project-management/`), UAT plan/cases (`testing/`)
- `env.example` files (root + mobile) with placeholders only

## 6. Files Intentionally Excluded

| File / Pattern | Reason |
|---|---|
| `sendGrid-OTP-Serv.txt` | **Contains a real SendGrid API key — never committed** |
| `mobile/neowallet-app/.env` | Contains developer LAN IP (`192.168.1.5`) — machine-specific, ignored |
| `android/local.properties` | Machine-specific SDK paths — ignored |
| `*.out`, `*.err`, `*.pid`, `*.log`, `diag_*` | Build/diagnostic run artifacts |
| `target/`, `build/`, `bin/`, `.gradle/` | Compiled outputs |
| `.dart_tool/`, `.flutter-plugins-dependencies`, `.metadata` | Flutter generated files |
| `.venv/`, `__pycache__/`, `.pytest_cache/` | Python environment/caches |
| `.idea/`, `*.iml`, `.vscode/` | IDE configuration |
| `*.apk`, `*.aab` | Build artifacts — not required in repo |
| Empty dirs (`NW-003.12.4/reports`, `NW-003.12.4/tests`) | Empty — nothing to migrate |

## 7. Secret Scan Result — PASS

- **SendGrid API key**: real key found locally in `sendGrid-OTP-Serv.txt` → file excluded via `.gitignore` and confirmed absent from all tracked files (content-level scan of all staged files and of `HEAD` post-commit — **zero matches**).
- **No `SG.<key>` pattern** in any tracked file.
- **No private keys** (`PRIVATE KEY-----`), AWS keys (`AKIA*`), GitHub tokens (`ghp_*`), Slack tokens (`xox*`), or GCP keys (`AIza*`) in tracked files.
- **Configuration files**: `application-{dev,staging,prod,test}.yml` use env-var placeholders only (`${NEOWALLET_DB_PASSWORD}`, `${NEOWALLET_JWT_SECRET}`, etc.). Only trivial non-production dev/test fallbacks exist (local DB password `neowallet`, throwaway base64 dev JWT key) — standard practice, no real credentials.
- **`SendGridOtpProvider.java`** reads `SENDGRID_API_KEY` / `SENDGRID_FROM_EMAIL` from environment — implementation committed, credentials not.
- **Git history**: no prior history existed; the only commits are the baseline + report — no historical secret exposure possible.
- **LAN config**: `API_BASE_URL=http://192.168.1.5:8080` exists only in the untracked `.env`; committed `env.example` uses `http://localhost:8080`. The `192.168.1.5` value appearing in `router_provider_test.dart` is a test fixture, not production config.

## 8. Verification Results — ALL PASS

| Suite | Command | Result |
|---|---|---|
| Backend | `mvn clean verify` | **BUILD SUCCESS** — 246 tests, 0 failures, 0 errors |
| Flutter | `flutter analyze` | PASS — no issues |
| Flutter | `flutter test` | PASS — 40/40 tests |
| Flutter | `flutter build apk --debug` | PASS — `app-debug.apk` built |
| OpenAPI | `npx swagger-cli validate api/NeoWallet_OpenAPI_v1.yaml` | PASS — spec valid |

**Test fix applied during migration (not ignored):** `FinancialHealthControllerTest` had 4 date-dependent failures (expected 200, got 422). Root cause: test fixtures used hardcoded period `2026-08` and fixed dates while `FinancialHealthService` resolves `YearMonth.now()` — fixtures fell outside the 3-month calculation window. Fixed by making fixture periods/dates relative to `YearMonth.now()`. Verified: class passes 7/7, then full suite re-ran green.

## 9. Post-Push Verification

- `git status` — clean, `main` up to date with `origin/main`
- `git remote -v` — `origin = https://github.com/AkkarajuSP/NeoWallet.git`
- `git ls-remote origin` — all 4 branches present on GitHub at the pushed commits
- Tracked-file secret scan — clean (see §7)

## 10. Issues & Warnings

- **SendGrid key exists only on local disk** in `sendGrid-OTP-Serv.txt` (untracked). Recommend rotating that key since it lived outside version control in plaintext, and revoking it if it was ever shared.
- **BillControllerTest** uses hardcoded `2026-08` dates like the fixed test — it currently passes but is the same class of time-bomb; recommend a follow-up ticket to make bill fixtures date-relative.
- Dev/test JWT fallback secrets in `application-dev.yml` / `application-test.yml` are placeholders only; ensure real secrets are injected via environment in deployed environments.
- Git committer identity is `devin <devin@example.com>` (global git config) — update local git config if a different identity is desired.

---

*No secrets are included in this report.*

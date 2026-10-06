# NeoWallet UI Preview Environment

A compile-time demo mode for the Flutter application that lets you visually navigate the currently implemented screens without a real backend, OTP service, payment providers, AI providers, or production services.

## Enabling Preview Mode

Preview mode is controlled by the compile-time `--dart-define=PREVIEW=true` flag. When the flag is **not** present the app behaves normally (real auth, real backend, no mock data).

## How to Start

### Option A — Android emulator or device

```powershell
cd neowallet/mobile/neowallet-app
flutter run --debug --dart-define=PREVIEW=true
```

### Option B — Install a debug APK

```powershell
cd neowallet/mobile/neowallet-app
flutter build apk --debug --dart-define=PREVIEW=true
```

The resulting APK is at:

```
build/app/outputs/flutter-apk/app-debug.apk
```

Install it on a device or emulator with:

```powershell
adb install build/app/outputs/flutter-apk/app-debug.apk
```

### Option C — VS Code / IDE launch

Add a `launch.json` configuration or set the run argument:

```json
{
  "toolArgs": ["--dart-define=PREVIEW=true"]
}
```

## Emulator / Device Requirements

- Flutter 3.x SDK
- Android SDK (emulator or physical device with USB debugging)
- Windows/macOS/Linux host with `flutter` and `adb` on PATH
- No backend, PostgreSQL, OTP, payment, or AI services are required

## Demo Credentials

Any credentials are accepted in preview mode. The pre-seeded demo user is:

- **Email:** `demo@neowallet.local`
- **Password:** `demo123`
- **OTP code:** `123456`

Login and OTP are bypassed because the app starts pre-authenticated using an in-memory secure storage that contains a mock token.

## Preview Menu

After launch the app opens on the `NeoWallet UI Preview` menu. Tap an item to open the corresponding screen:

1. **Splash** — `SplashScreen`
2. **Login** — not yet implemented
3. **OTP** — not yet implemented
4. **Profile** — `ProfileScreen`
5. **Preferences** — `PreferencesScreen`
6. **Family** — `FamilyScreen`
7. **Family Members** — shown on `FamilyScreen`
8. **Financial Overview** — `FinancialOverviewScreen`
9. **Transactions** — `TransactionListScreen`
10. **Budget** — `BudgetListScreen`
11. **Savings Goals** — `SavingsGoalListScreen`

From the list screens you can tap a row to view the detail screens:
- Budget detail
- Transaction detail
- Savings goal detail

Tap the **+** buttons to open the create screens (form prefill works; saves are mocked and stay in memory).

## What is Mocked

- `AuthService` — accepts any credentials, returns a preview token
- `ProfileService` — returns a fixed `UserProfile` and `UserPreferences`
- `FamilyService` — returns a fixed `FamilyModel` with three members
- `FinancialOverviewService` — returns static overview and summary
- `TransactionService` — returns two mock transactions
- `BudgetService` — returns one mock budget with categories
- `SavingsGoalService` — returns one mock savings goal, progress, and forecast
- `SecureStorage` — in-memory, pre-seeded with a demo token

## Safety Notes

- Preview is a **compile-time** opt-in only.
- No mock services are used in staging or production.
- Real authentication is unchanged; the `--dart-define=PREVIEW=true` flag must be present for the preview behavior to activate.
- No real payments, OTPs, or AI calls are made.

## Screens Not Yet Implemented

- **Login screen**
- **OTP verification screen**

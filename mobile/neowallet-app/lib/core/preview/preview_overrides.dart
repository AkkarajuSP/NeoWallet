import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../di/providers.dart';
import 'mock_secure_storage.dart';
import 'mock_services.dart';

/// Riverpod overrides for the UI preview environment.
///
/// Replaces real network-bound services and secure storage with in-memory,
/// mock-backed implementations. This keeps all business logic and routing
/// identical while allowing visual navigation without a backend.
List<Override> buildPreviewOverrides() {
  return [
    secureStorageProvider.overrideWith((ref) => MockSecureStorage()),
    authServiceProvider.overrideWith((ref) => PreviewAuthService(
          dio: ref.watch(apiClientProvider).dio,
          secureStorage: ref.watch(secureStorageProvider),
        )),
    profileServiceProvider.overrideWith((ref) => PreviewProfileService(
          dio: ref.watch(apiClientProvider).dio,
        )),
    familyServiceProvider.overrideWith((ref) => PreviewFamilyService(
          dio: ref.watch(apiClientProvider).dio,
        )),
    financialOverviewServiceProvider.overrideWith((ref) => PreviewFinancialOverviewService(
          dio: ref.watch(apiClientProvider).dio,
        )),
    transactionServiceProvider.overrideWith((ref) => PreviewTransactionService(
          dio: ref.watch(apiClientProvider).dio,
        )),
    budgetServiceProvider.overrideWith((ref) => PreviewBudgetService(
          dio: ref.watch(apiClientProvider).dio,
        )),
    savingsGoalServiceProvider.overrideWith((ref) => PreviewSavingsGoalService(
          dio: ref.watch(apiClientProvider).dio,
        )),
  ];
}

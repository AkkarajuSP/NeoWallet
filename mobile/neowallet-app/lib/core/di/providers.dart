import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../features/auth/providers/auth_provider.dart';
import '../../features/auth/services/auth_service.dart';
import '../../features/family/providers/families_provider.dart';
import '../../features/family/providers/family_provider.dart';
import '../../features/family/services/family_service.dart';
import '../../features/finance/providers/bill_list_provider.dart';
import '../../features/finance/providers/budget_list_provider.dart';
import '../../features/finance/providers/financial_health_provider.dart';
import '../../features/finance/providers/financial_overview_provider.dart';
import '../../features/finance/providers/savings_goal_list_provider.dart';
import '../../features/finance/providers/transaction_list_provider.dart';
import '../../features/finance/services/bill_service.dart';
import '../../features/finance/services/budget_service.dart';
import '../../features/finance/services/financial_health_service.dart';
import '../../features/finance/services/financial_overview_service.dart';
import '../../features/finance/services/savings_goal_service.dart';
import '../../features/finance/services/transaction_service.dart';
import '../../features/ai/providers/ai_chat_provider.dart';
import '../../features/ai/services/ai_service.dart';
import '../../features/profile/providers/profile_provider.dart';
import '../../features/profile/services/profile_service.dart';
import '../network/api_client.dart';
import '../network/session_expired_channel.dart';
import '../storage/flutter_secure_storage_impl.dart';
import '../storage/secure_storage.dart';

final secureStorageProvider = Provider<SecureStorage>((ref) {
  return FlutterSecureStorageImpl();
});

final apiClientProvider = Provider<ApiClient>((ref) {
  final baseUrl = dotenv.env['API_BASE_URL'] ?? 'http://localhost:8080';
  final secureStorage = ref.watch(secureStorageProvider);
  final sessionController = ref.watch(sessionExpiredControllerProvider);
  return ApiClient(
    baseUrl: baseUrl,
    secureStorage: secureStorage,
    onSessionExpired: () => sessionController.add(null),
  );
});

final authServiceProvider = Provider<AuthService>((ref) {
  return AuthService(
    dio: ref.watch(apiClientProvider).dio,
    secureStorage: ref.watch(secureStorageProvider),
  );
});

final authProvider = StateNotifierProvider<AuthNotifier, AuthState>((ref) {
  return AuthNotifier(
    authService: ref.watch(authServiceProvider),
    secureStorage: ref.watch(secureStorageProvider),
    sessionExpiredStream: ref.watch(sessionExpiredStreamProvider),
  );
});

final profileServiceProvider = Provider<ProfileService>((ref) {
  return ProfileService(dio: ref.watch(apiClientProvider).dio);
});

final profileProvider = StateNotifierProvider<ProfileNotifier, ProfileState>((ref) {
  return ProfileNotifier(profileService: ref.watch(profileServiceProvider));
});

final familyServiceProvider = Provider<FamilyService>((ref) {
  return FamilyService(dio: ref.watch(apiClientProvider).dio);
});

final familiesProvider = StateNotifierProvider<FamiliesNotifier, FamiliesState>((ref) {
  return FamiliesNotifier(ref.watch(familyServiceProvider));
});

final familyProvider = StateNotifierProvider<FamilyStateNotifier, FamilyState>((ref) {
  return FamilyStateNotifier(familyService: ref.watch(familyServiceProvider));
});

final financialOverviewServiceProvider = Provider<FinancialOverviewService>((ref) {
  return FinancialOverviewService(dio: ref.watch(apiClientProvider).dio);
});

final financialOverviewProvider = StateNotifierProvider.family<FinancialOverviewNotifier, FinancialOverviewState, String?>((ref, familyId) {
  final notifier = FinancialOverviewNotifier(
    service: ref.watch(financialOverviewServiceProvider),
  );
  notifier.load(familyId: familyId);
  return notifier;
});

final transactionServiceProvider = Provider<TransactionService>((ref) {
  return TransactionService(dio: ref.watch(apiClientProvider).dio);
});

final transactionListProvider = StateNotifierProvider.family<TransactionListNotifier, TransactionListState, String?>((ref, familyId) {
  final notifier = TransactionListNotifier(
    service: ref.watch(transactionServiceProvider),
  );
  notifier.load(familyId: familyId);
  return notifier;
});

final budgetServiceProvider = Provider<BudgetService>((ref) {
  return BudgetService(dio: ref.watch(apiClientProvider).dio);
});

final budgetListProvider = StateNotifierProvider.family<BudgetListNotifier, BudgetListState, String?>((ref, familyId) {
  final notifier = BudgetListNotifier(ref.watch(budgetServiceProvider));
  notifier.load(familyId: familyId);
  return notifier;
});

final savingsGoalServiceProvider = Provider<SavingsGoalService>((ref) {
  return SavingsGoalService(dio: ref.watch(apiClientProvider).dio);
});

final savingsGoalListProvider = StateNotifierProvider.family<SavingsGoalListNotifier, SavingsGoalListState, String?>((ref, familyId) {
  final notifier = SavingsGoalListNotifier(ref.watch(savingsGoalServiceProvider));
  notifier.load(familyId: familyId);
  return notifier;
});

final billServiceProvider = Provider<BillService>((ref) {
  return BillService(dio: ref.watch(apiClientProvider).dio);
});

final billListProvider = StateNotifierProvider.family<BillListNotifier, BillListState, String?>((ref, familyId) {
  final notifier = BillListNotifier(ref.watch(billServiceProvider));
  notifier.load(familyId: familyId);
  return notifier;
});

final financialHealthServiceProvider = Provider<FinancialHealthService>((ref) {
  return FinancialHealthService(ref.watch(apiClientProvider).dio);
});

final financialHealthProvider = StateNotifierProvider.family<FinancialHealthNotifier, FinancialHealthState, String?>((ref, familyId) {
  final notifier = FinancialHealthNotifier(ref.watch(financialHealthServiceProvider), familyId);
  notifier.loadAll();
  return notifier;
});

final aiServiceProvider = Provider<AiService>((ref) {
  return AiService(dio: ref.watch(apiClientProvider).dio);
});

final aiChatProvider = StateNotifierProvider.family<AiChatNotifier, AiChatState, String?>((ref, familyId) {
  return AiChatNotifier(ref.watch(aiServiceProvider), familyId: familyId);
});

// ignore_for_file: use_super_parameters

import 'package:dio/dio.dart';

import '../../features/auth/models/auth_token.dart';
import '../../features/auth/services/auth_service.dart';
import '../../features/family/models/family_models.dart';
import '../../features/family/services/family_service.dart';
import '../../features/finance/models/budget_model.dart';
import '../../features/finance/models/financial_overview_model.dart';
import '../../features/finance/models/savings_goal_model.dart';
import '../../features/finance/models/transaction_model.dart';
import '../../features/finance/services/budget_service.dart';
import '../../features/finance/services/financial_overview_service.dart';
import '../../features/finance/services/savings_goal_service.dart';
import '../../features/finance/services/transaction_service.dart';
import '../../features/profile/models/user_preferences_model.dart';
import '../../features/profile/models/user_profile_model.dart';
import '../../features/profile/services/profile_service.dart';
import '../storage/secure_storage.dart';
import 'preview_data.dart';

/// Mock auth service that accepts any credentials and bypasses OTP.
class PreviewAuthService extends AuthService {
  final SecureStorage _secureStorage;

  PreviewAuthService({required Dio dio, required SecureStorage secureStorage})
      : _secureStorage = secureStorage,
        super(dio: dio, secureStorage: secureStorage);

  @override
  Future<void> register({
    required String email,
    required String password,
    required String firstName,
    required String lastName,
  }) async {
    return;
  }

  @override
  Future<String> requestOtp({required String email, required String purpose}) async {
    return 'preview-otp-id';
  }

  @override
  Future<bool> verifyOtp({required String otpId, required String code}) async {
    return true;
  }

  @override
  Future<AuthToken> login({
    required String email,
    required String password,
    String? deviceType,
    String? deviceName,
  }) async {
    await _secureStorage.writeAccessToken(previewToken.accessToken);
    await _secureStorage.writeRefreshToken(previewToken.refreshToken);
    return previewToken;
  }

  @override
  Future<AuthToken> refresh() async {
    return previewToken;
  }

  @override
  Future<void> logout() async {
    await _secureStorage.deleteAll();
  }
}

/// Mock profile service.
class PreviewProfileService extends ProfileService {
  PreviewProfileService({required Dio dio}) : super(dio: dio);

  @override
  Future<UserProfile> getProfile() async => previewUser;

  @override
  Future<UserProfile> updateProfile({String? firstName, String? lastName, String? phoneNumber}) async {
    return previewUser.copyWith(
      firstName: firstName,
      lastName: lastName,
      phoneNumber: phoneNumber,
    );
  }

  @override
  Future<void> deleteAccount({required String confirmation}) async {}

  @override
  Future<UserPreferences> getPreferences() async => previewPreferences;

  @override
  Future<UserPreferences> updatePreferences(UserPreferences preferences) async => preferences;
}

/// Mock family service.
class PreviewFamilyService extends FamilyService {
  PreviewFamilyService({required Dio dio}) : super(dio: dio);

  @override
  Future<FamilyModel> createFamily({required String name, String currency = 'USD'}) async {
    return previewFamily;
  }

  @override
  Future<FamilyModel> getFamily(String familyId) async => previewFamily;

  @override
  Future<FamilyModel> updateFamily(String familyId, {String? name, String? currency}) async {
    return previewFamily;
  }

  @override
  Future<void> deleteFamily(String familyId) async {}

  @override
  Future<FamilyInvitationModel> inviteMember(
    String familyId, {
    required String email,
    String role = 'MEMBER',
  }) async {
    return FamilyInvitationModel(
      invitationId: '00000000-0000-0000-0000-000000000013',
      token: 'preview-invite-token',
      email: email,
      role: role,
      expiresAt: '2026-09-19T00:00:00.000Z',
      createdAt: '2026-08-19T00:00:00.000Z',
    );
  }

  @override
  Future<List<FamilyMemberModel>> getMembers(String familyId) async => previewMembers;

  @override
  Future<FamilyMemberModel> updateMemberRole(String familyId, String memberId, String role) async {
    return previewMembers.firstWhere((m) => m.memberId == memberId);
  }

  @override
  Future<void> removeMember(String familyId, String memberId) async {}

  @override
  Future<void> acceptInvitation(String token) async {}

  @override
  Future<void> rejectInvitation(String token) async {}
}

/// Mock financial overview service.
class PreviewFinancialOverviewService extends FinancialOverviewService {
  PreviewFinancialOverviewService({required Dio dio}) : super(dio: dio);

  @override
  Future<FinancialOverviewModel> getOverview({String? familyId}) async => previewOverview;

  @override
  Future<FinancialSummaryModel> getSummary({String? familyId}) async => previewSummary;
}

/// Mock transaction service.
class PreviewTransactionService extends TransactionService {
  PreviewTransactionService({required Dio dio}) : super(dio: dio);

  @override
  Future<TransactionModel> create({
    required Map<String, dynamic> data,
    String? familyId,
  }) async {
    return previewTransactions.first;
  }

  @override
  Future<TransactionModel> get(String transactionId) async {
    return previewTransactions.firstWhere(
      (t) => t.transactionId == transactionId,
      orElse: () => previewTransactions.first,
    );
  }

  @override
  Future<TransactionsListModel> list({
    String? familyId,
    int page = 1,
    int limit = 20,
    String? filterType,
    String? filterCategory,
    String? filterStatus,
  }) async {
    return previewTransactionsList;
  }

  @override
  Future<TransactionModel> update(String transactionId, Map<String, dynamic> data) async {
    return previewTransactions.first;
  }

  @override
  Future<void> delete(String transactionId) async {}
}

/// Mock budget service.
class PreviewBudgetService extends BudgetService {
  PreviewBudgetService({required Dio dio}) : super(dio: dio);

  @override
  Future<BudgetModel> create({required Map<String, dynamic> data, String? familyId}) async {
    return previewBudgets.first;
  }

  @override
  Future<BudgetModel> get(String budgetId, {String? familyId}) async {
    return previewBudgets.firstWhere(
      (b) => b.budgetId == budgetId,
      orElse: () => previewBudgets.first,
    );
  }

  @override
  Future<BudgetListModel> list({
    String? familyId,
    int page = 1,
    int limit = 20,
    String? period,
  }) async {
    return previewBudgetList;
  }

  @override
  Future<BudgetModel> update(String budgetId, Map<String, dynamic> data) async {
    return previewBudgets.first;
  }

  @override
  Future<void> delete(String budgetId) async {}

  @override
  Future<Map<String, dynamic>> getUtilization(String budgetId) async {
    return {
      'budgetId': budgetId,
      'totalLimit': 1100.0,
      'totalSpent': 850.0,
      'utilizationPercentage': 77.0,
      'categories': [],
      'calculatedAt': '2026-08-19T00:00:00.000Z',
    };
  }

  @override
  Future<Map<String, dynamic>> getForecast(String budgetId) async {
    return {
      'budgetId': budgetId,
      'forecast': {
        'projectedSpent': 1050.0,
        'remainingBudget': 50.0,
        'confidence': 'MEDIUM',
      },
      'calculatedAt': '2026-08-19T00:00:00.000Z',
    };
  }

  @override
  Future<Map<String, dynamic>> getRecommendation({
    required String period,
    String? familyId,
  }) async {
    return {
      'recommendationId': 'preview-rec',
      'period': period,
      'currency': 'USD',
      'recommendedAllocation': 0.15,
      'categories': [],
      'confidence': 'MEDIUM',
      'historicalMonthsUsed': 3,
      'generatedAt': '2026-08-19T00:00:00.000Z',
      'disclaimer': 'Demo recommendation.',
    };
  }
}

/// Mock savings goal service.
class PreviewSavingsGoalService extends SavingsGoalService {
  PreviewSavingsGoalService({required Dio dio}) : super(dio: dio);

  @override
  Future<SavingsGoalModel> create({
    required Map<String, dynamic> data,
    String? familyId,
  }) async {
    return previewSavingsGoals.first;
  }

  @override
  Future<SavingsGoalModel> get(String goalId, {String? familyId}) async {
    return previewSavingsGoals.firstWhere(
      (g) => g.goalId == goalId,
      orElse: () => previewSavingsGoals.first,
    );
  }

  @override
  Future<SavingsGoalListModel> list({
    String? familyId,
    int page = 1,
    int limit = 20,
    String? status,
    String? priority,
    String sort = 'targetDate:asc',
  }) async {
    return previewSavingsGoalList;
  }

  @override
  Future<SavingsGoalModel> update(String goalId, Map<String, dynamic> data) async {
    return previewSavingsGoals.first;
  }

  @override
  Future<void> delete(String goalId) async {}

  @override
  Future<Map<String, dynamic>> getProgress(String goalId) async => previewProgress;

  @override
  Future<Map<String, dynamic>> getForecast(String goalId) async => previewForecast;
}

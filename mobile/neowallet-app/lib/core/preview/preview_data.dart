import '../../features/auth/models/auth_token.dart';
import '../../features/family/models/family_models.dart';
import '../../features/finance/models/budget_model.dart' hide BudgetListModel, PaginationModel;
import '../../features/finance/models/budget_model.dart' as bgt show BudgetListModel, PaginationModel;
import '../../features/finance/models/financial_overview_model.dart';
import '../../features/finance/models/savings_goal_model.dart' hide PaginationModel, SavingsGoalListModel;
import '../../features/finance/models/savings_goal_model.dart' as sgt show PaginationModel, SavingsGoalListModel;
import '../../features/finance/models/transaction_model.dart' hide PaginationModel, TransactionsListModel;
import '../../features/finance/models/transaction_model.dart' as txn show PaginationModel, TransactionsListModel;
import '../../features/profile/models/user_preferences_model.dart';
import '../../features/profile/models/user_profile_model.dart';

/// IDs used for demo navigation and mock data.
class PreviewIds {
  static const userId = '00000000-0000-0000-0000-000000000001';
  static const familyId = '00000000-0000-0000-0000-000000000002';
  static const memberId = '00000000-0000-0000-0000-000000000003';
  static const budgetId = '00000000-0000-0000-0000-000000000004';
  static const transactionId = '00000000-0000-0000-0000-000000000005';
  static const savingsGoalId = '00000000-0000-0000-0000-000000000006';
}

/// Demo user credentials. Any values are accepted in preview mode.
class PreviewCredentials {
  static const email = 'demo@neowallet.local';
  static const password = 'demo123';
  static const otpCode = '123456';
}

/// Static timestamps for consistent demo data.
const _now = '2026-08-19T00:00:00.000Z';

final previewToken = AuthToken(
  accessToken: 'preview-access-token',
  refreshToken: 'preview-refresh-token',
  tokenType: 'Bearer',
  expiresIn: 86400,
  issuedAt: DateTime(2026, 8, 19),
);

const previewUser = UserProfile(
  userId: PreviewIds.userId,
  email: PreviewCredentials.email,
  firstName: 'Demo',
  lastName: 'User',
  phoneNumber: '+1 555 0100',
  familyId: PreviewIds.familyId,
  familyRole: 'OWNER',
  createdAt: _now,
  updatedAt: _now,
);

const previewPreferences = UserPreferences(
  userId: PreviewIds.userId,
  locale: 'en-US',
  currency: 'USD',
  timezone: 'America/New_York',
  notificationPreferences: NotificationPreferences(
    budgetAlerts: true,
    billReminders: true,
    savingsUpdates: true,
    financialHealthUpdates: false,
  ),
  aiPreferences: AiPreferences(
    responseStyle: 'balanced',
    language: 'en',
  ),
);

const previewFamily = FamilyModel(
  familyId: PreviewIds.familyId,
  name: 'Demo Family',
  currency: 'USD',
  ownerId: PreviewIds.userId,
  memberCount: 3,
  createdAt: _now,
  updatedAt: _now,
);

const previewMembers = [
  FamilyMemberModel(
    memberId: PreviewIds.memberId,
    userId: PreviewIds.userId,
    firstName: 'Demo',
    lastName: 'User',
    email: PreviewCredentials.email,
    role: 'OWNER',
    joinedAt: _now,
  ),
  FamilyMemberModel(
    memberId: '00000000-0000-0000-0000-000000000007',
    userId: '00000000-0000-0000-0000-000000000008',
    firstName: 'Jane',
    lastName: 'Doe',
    email: 'jane@neowallet.local',
    role: 'MEMBER',
    joinedAt: _now,
  ),
  FamilyMemberModel(
    memberId: '00000000-0000-0000-0000-000000000009',
    userId: '00000000-0000-0000-0000-000000000010',
    firstName: 'Junior',
    lastName: 'Doe',
    email: 'junior@neowallet.local',
    role: 'RESTRICTED',
    joinedAt: _now,
  ),
];

const previewOverview = FinancialOverviewModel(
  overviewId: '00000000-0000-0000-0000-000000000011',
  userId: PreviewIds.userId,
  familyId: PreviewIds.familyId,
  planningIncome: 6500.00,
  mandatoryCommitments: 2200.00,
  essentialAllocation: 1500.00,
  savingsAllocation: 500.00,
  emergencyAllocation: 300.00,
  discretionaryPlanning: 1000.00,
  committedAmount: 4500.00,
  pendingPayments: 150.00,
  actualTransactions: 4200.00,
  availableFinancialCapacity: 1050.00,
  currency: 'USD',
  period: '2026-08',
  calculatedAt: _now,
  disclaimer: 'This overview is for planning purposes only.',
);

const previewSummary = FinancialSummaryModel(
  totalIncome: 6500.00,
  totalExpenses: 4200.00,
  totalSavings: 500.00,
  availableCapacity: 1050.00,
  budgetAdherence: 92.5,
  currency: 'USD',
  period: '2026-08',
);

const previewTransactions = [
  TransactionModel(
    transactionId: PreviewIds.transactionId,
    userId: PreviewIds.userId,
    familyId: PreviewIds.familyId,
    type: 'EXPENSE',
    category: 'Groceries',
    amount: 125.50,
    currency: 'USD',
    description: 'Weekly groceries',
    transactionDate: '2026-08-18',
    status: 'COMPLETED',
    isRecurring: false,
    source: 'MANUAL',
    createdAt: _now,
    updatedAt: _now,
  ),
  TransactionModel(
    transactionId: '00000000-0000-0000-0000-000000000012',
    userId: PreviewIds.userId,
    familyId: PreviewIds.familyId,
    type: 'INCOME',
    category: 'Salary',
    amount: 6500.00,
    currency: 'USD',
    description: 'Monthly salary',
    transactionDate: '2026-08-01',
    status: 'COMPLETED',
    isRecurring: true,
    source: 'MANUAL',
    createdAt: _now,
    updatedAt: _now,
  ),
];

const previewTransactionsList = txn.TransactionsListModel(
  transactions: previewTransactions,
  pagination: txn.PaginationModel(
    page: 1,
    limit: 20,
    total: 2,
    totalPages: 1,
  ),
);

const previewBudgets = [
  BudgetModel(
    budgetId: PreviewIds.budgetId,
    userId: PreviewIds.userId,
    familyId: PreviewIds.familyId,
    name: 'August Household',
    period: '2026-08',
    currency: 'USD',
    categories: [
      BudgetCategoryModel(category: 'Groceries', limit: 600, spent: 450, utilizationPercentage: 75, priority: 'HIGH'),
      BudgetCategoryModel(category: 'Utilities', limit: 300, spent: 280, utilizationPercentage: 93, priority: 'HIGH'),
      BudgetCategoryModel(category: 'Entertainment', limit: 200, spent: 120, utilizationPercentage: 60, priority: 'LOW'),
    ],
    totalLimit: 1100,
    totalSpent: 850,
    utilizationPercentage: 77,
    createdAt: _now,
    updatedAt: _now,
  ),
];

const previewBudgetList = bgt.BudgetListModel(
  budgets: previewBudgets,
  pagination: bgt.PaginationModel(
    page: 1,
    limit: 20,
    total: 1,
    totalPages: 1,
  ),
);

const previewSavingsGoals = [
  SavingsGoalModel(
    goalId: PreviewIds.savingsGoalId,
    userId: PreviewIds.userId,
    familyId: PreviewIds.familyId,
    name: 'Emergency Fund',
    targetAmount: 10000,
    currentAmount: 4500,
    progressPercentage: 45.0,
    targetDate: '2026-12-31',
    currency: 'USD',
    priority: 'HIGH',
    category: 'Emergency',
    status: 'ACTIVE',
    createdAt: _now,
    updatedAt: _now,
  ),
];

const previewSavingsGoalList = sgt.SavingsGoalListModel(
  goals: previewSavingsGoals,
  pagination: sgt.PaginationModel(
    page: 1,
    limit: 20,
    total: 1,
    totalPages: 1,
  ),
);

const previewProgress = {
  'goalId': PreviewIds.savingsGoalId,
  'currentAmount': 4500.0,
  'targetAmount': 10000.0,
  'progressPercentage': 45.0,
  'remainingAmount': 5500.0,
  'monthsRemaining': 4,
  'requiredMonthlyContribution': 1375.0,
  'actualMonthlyContribution': 500.0,
  'onTrack': false,
  'status': 'ACTIVE',
  'calculatedAt': _now,
};

const previewForecast = {
  'goalId': PreviewIds.savingsGoalId,
  'forecast': {
    'projectedCompletionDate': '2027-06-01',
    'projectedCompletionAmount': 10000.0,
    'monthsToCompletion': 11,
    'confidence': 'MEDIUM',
  },
  'scenarios': [
    {
      'monthlyContribution': 500.0,
      'projectedCompletionDate': '2027-06-01',
      'monthsToCompletion': 11,
    },
    {
      'monthlyContribution': 1375.0,
      'projectedCompletionDate': '2026-12-31',
      'monthsToCompletion': 4,
    },
  ],
  'calculatedAt': _now,
};

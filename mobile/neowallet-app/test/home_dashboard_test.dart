import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

import 'package:neowallet_app/core/components/cards.dart';
import 'package:neowallet_app/core/theme/app_theme.dart';
import 'package:neowallet_app/core/theme/design_tokens.dart';
import 'package:neowallet_app/features/finance/models/bill_model.dart';
import 'package:neowallet_app/features/finance/models/budget_model.dart';
import 'package:neowallet_app/features/finance/models/financial_health_model.dart';
import 'package:neowallet_app/features/finance/models/financial_overview_model.dart';
import 'package:neowallet_app/features/finance/models/savings_goal_model.dart';
import 'package:neowallet_app/features/finance/models/transaction_model.dart';

const _overview = FinancialOverviewModel(
  overviewId: '1',
  planningIncome: 100000,
  mandatoryCommitments: 20000,
  essentialAllocation: 30000,
  savingsAllocation: 10000,
  emergencyAllocation: 5000,
  discretionaryPlanning: 20000,
  committedAmount: 45000,
  pendingPayments: 15000,
  actualTransactions: 40000,
  availableFinancialCapacity: 25000,
  currency: 'USD',
  period: '2026-08',
  calculatedAt: '2026-08-20T00:00:00Z',
  disclaimer: 'Planning values are estimates.',
);

const _summary = FinancialSummaryModel(
  totalIncome: 100000,
  totalExpenses: 40000,
  totalSavings: 10000,
  availableCapacity: 25000,
  budgetAdherence: 95,
  currency: 'USD',
  period: '2026-08',
);

final _health = FinancialHealthModel(
  healthScoreId: 'h1',
  overallScore: 72,
  scoreLabel: 'Good',
  confidence: 'HIGH',
  status: 'HEALTHY',
  calculatedAt: DateTime(2026, 8, 20),
  disclaimer: 'Health estimate.',
);

const _budget = BudgetModel(
  budgetId: 'b1',
  name: 'Groceries',
  period: '2026-08',
  currency: 'USD',
  categories: [],
  totalLimit: 1000,
  totalSpent: 750,
  utilizationPercentage: 75,
  createdAt: '2026-08-01T00:00:00Z',
);

const _goal = SavingsGoalModel(
  goalId: 's1',
  name: 'Vacation',
  targetAmount: 5000,
  currentAmount: 2500,
  progressPercentage: 50,
  targetDate: '2027-01-01',
  currency: 'USD',
  priority: 'MEDIUM',
  status: 'ACTIVE',
  createdAt: '2026-08-01T00:00:00Z',
);

const _bill = BillModel(
  billId: 'bi1',
  name: 'Electricity',
  amount: 120,
  currency: 'USD',
  dueDate: '2026-08-25',
  isRecurring: false,
  status: 'PENDING',
  createdAt: '2026-08-01T00:00:00Z',
);

const _transaction = TransactionModel(
  transactionId: 't1',
  type: 'EXPENSE',
  category: 'Food',
  amount: 45,
  currency: 'USD',
  transactionDate: '2026-08-19',
  status: 'COMPLETED',
  isRecurring: false,
  source: 'MANUAL',
  createdAt: '2026-08-19T00:00:00Z',
  updatedAt: '2026-08-19T00:00:00Z',
);

void main() {
  group('Home dashboard cards', () {
    testWidgets('FinancialSummaryCard shows planning and capacity labels', (tester) async {
      await tester.pumpWidget(
        MaterialApp(
          theme: AppTheme.light,
          home: const Scaffold(body: SingleChildScrollView(child: FinancialSummaryCard(overview: _overview, summary: _summary))),
        ),
      );
      expect(find.text('PLANNING'), findsOneWidget);
      expect(find.text('AVAILABLE CAPACITY'), findsOneWidget);
      expect(find.text(DesignTokens.planningDisclaimer), findsOneWidget);
      expect(find.text('USD 25,000.00'), findsWidgets);
    });

    testWidgets('FinancialHealthCard renders score and label', (tester) async {
      await tester.pumpWidget(
        MaterialApp(
          theme: AppTheme.light,
          home: Scaffold(body: SingleChildScrollView(child: FinancialHealthCard(health: _health))),
        ),
      );
      expect(find.text('72'), findsOneWidget);
      expect(find.text('Good'), findsOneWidget);
      expect(find.text('Confidence: HIGH'), findsOneWidget);
    });

    testWidgets('BudgetProgressCard renders progress and percentage', (tester) async {
      await tester.pumpWidget(
        MaterialApp(
          theme: AppTheme.light,
          home: const Scaffold(body: SingleChildScrollView(child: BudgetProgressCard(budgets: [_budget]))),
        ),
      );
      expect(find.text('Groceries'), findsOneWidget);
      expect(find.text('75%'), findsOneWidget);
      expect(find.byType(LinearProgressIndicator), findsOneWidget);
    });

    testWidgets('SavingsProgressCard renders goal progress', (tester) async {
      await tester.pumpWidget(
        MaterialApp(
          theme: AppTheme.light,
          home: const Scaffold(body: SingleChildScrollView(child: SavingsProgressCard(goals: [_goal]))),
        ),
      );
      expect(find.text('Vacation'), findsOneWidget);
      expect(find.text('50%'), findsOneWidget);
    });

    testWidgets('UpcomingBillsCard shows due bill', (tester) async {
      await tester.pumpWidget(
        MaterialApp(
          theme: AppTheme.light,
          home: const Scaffold(body: SingleChildScrollView(child: UpcomingBillsCard(bills: [_bill]))),
        ),
      );
      expect(find.text('Electricity'), findsOneWidget);
      expect(find.text('PENDING'), findsOneWidget);
    });

    testWidgets('RecentTransactionsCard shows transaction', (tester) async {
      await tester.pumpWidget(
        MaterialApp(
          theme: AppTheme.light,
          home: const Scaffold(body: SingleChildScrollView(child: RecentTransactionsCard(transactions: [_transaction]))),
        ),
      );
      expect(find.text('Food'), findsOneWidget);
      expect(find.text('COMPLETED'), findsOneWidget);
    });
  });
}

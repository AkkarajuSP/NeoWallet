import 'package:flutter_test/flutter_test.dart';

import 'package:neowallet_app/features/finance/providers/bill_list_provider.dart';
import 'package:neowallet_app/features/finance/providers/budget_list_provider.dart';
import 'package:neowallet_app/features/finance/providers/savings_goal_list_provider.dart';
import 'package:neowallet_app/features/finance/providers/transaction_list_provider.dart';

void main() {
  group('TransactionListState', () {
    test('copyWith preserves values and overrides filters', () {
      const base = TransactionListState(
        transactions: [],
        page: 1,
        totalPages: 5,
        familyId: 'f1',
        filterType: 'EXPENSE',
      );
      final next = base.copyWith(page: 2, filterCategory: 'GROCERY');
      expect(next.page, 2);
      expect(next.filterCategory, 'GROCERY');
      expect(next.filterType, 'EXPENSE');
      expect(next.familyId, 'f1');
    });

    test('pagination helpers default to 1 and 0', () {
      const state = TransactionListState();
      expect(state.page, 1);
      expect(state.totalPages, 0);
    });
  });

  group('BudgetListState', () {
    test('copyWith updates budgets and pagination', () {
      const base = BudgetListState(budgets: [], page: 1);
      final next = base.copyWith(budgets: [], page: 2, hasMore: false);
      expect(next.page, 2);
      expect(next.hasMore, false);
    });
  });

  group('SavingsGoalListState', () {
    test('copyWith updates filters and family context', () {
      const base = SavingsGoalListState(
        goals: [],
        familyId: 'f3',
        filterStatus: 'ACTIVE',
      );
      final next = base.copyWith(filterPriority: 'HIGH');
      expect(next.familyId, 'f3');
      expect(next.filterStatus, 'ACTIVE');
      expect(next.filterPriority, 'HIGH');
    });
  });

  group('BillListState', () {
    test('copyWith updates status and category filters', () {
      const base = BillListState(
        bills: [],
        familyId: 'f4',
        filterStatus: 'PENDING',
      );
      final next = base.copyWith(filterCategory: 'UTILITIES');
      expect(next.familyId, 'f4');
      expect(next.filterStatus, 'PENDING');
      expect(next.filterCategory, 'UTILITIES');
    });
  });

  group('Family-scoped state identity', () {
    test('each state carries its familyId for refresh', () {
      const tx = TransactionListState(familyId: 'famA');
      const sg = SavingsGoalListState(familyId: 'famA');
      const bl = BillListState(familyId: 'famA');
      expect(tx.familyId, 'famA');
      expect(sg.familyId, 'famA');
      expect(bl.familyId, 'famA');
    });
  });
}

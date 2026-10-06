import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../models/budget_model.dart';
import '../services/budget_service.dart';

class BudgetListState {
  final List<BudgetModel> budgets;
  final bool isLoading;
  final bool hasMore;
  final int page;
  final String? error;

  const BudgetListState({
    this.budgets = const [],
    this.isLoading = false,
    this.hasMore = true,
    this.page = 1,
    this.error,
  });

  BudgetListState copyWith({
    List<BudgetModel>? budgets,
    bool? isLoading,
    bool? hasMore,
    int? page,
    String? error,
  }) {
    return BudgetListState(
      budgets: budgets ?? this.budgets,
      isLoading: isLoading ?? this.isLoading,
      hasMore: hasMore ?? this.hasMore,
      page: page ?? this.page,
      error: error ?? this.error,
    );
  }
}

class BudgetListNotifier extends StateNotifier<BudgetListState> {
  final BudgetService _service;

  BudgetListNotifier(this._service) : super(const BudgetListState());

  Future<void> load({String? familyId, String? period, bool refresh = false}) async {
    if (state.isLoading) return;
    if (refresh) {
      state = const BudgetListState();
    }
    state = state.copyWith(isLoading: true, error: null);
    try {
      final result = await _service.list(familyId: familyId, period: period, page: state.page);
      final budgets = refresh ? result.budgets : [...state.budgets, ...result.budgets];
      state = state.copyWith(
        budgets: budgets,
        isLoading: false,
        hasMore: result.pagination.page < result.pagination.totalPages,
        page: state.page + 1,
      );
    } catch (e) {
      state = state.copyWith(isLoading: false, error: e.toString());
    }
  }
}

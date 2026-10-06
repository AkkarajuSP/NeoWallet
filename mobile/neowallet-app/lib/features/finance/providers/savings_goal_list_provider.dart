import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../models/savings_goal_model.dart';
import '../services/savings_goal_service.dart';

class SavingsGoalListState {
  final List<SavingsGoalModel> goals;
  final bool isLoading;
  final String? error;
  final int page;
  final int totalPages;
  final String? familyId;
  final String? filterStatus;
  final String? filterPriority;

  const SavingsGoalListState({
    this.goals = const [],
    this.isLoading = false,
    this.error,
    this.page = 1,
    this.totalPages = 0,
    this.familyId,
    this.filterStatus,
    this.filterPriority,
  });

  SavingsGoalListState copyWith({
    List<SavingsGoalModel>? goals,
    bool? isLoading,
    String? error,
    int? page,
    int? totalPages,
    String? familyId,
    String? filterStatus,
    String? filterPriority,
  }) {
    return SavingsGoalListState(
      goals: goals ?? this.goals,
      isLoading: isLoading ?? this.isLoading,
      error: error,
      page: page ?? this.page,
      totalPages: totalPages ?? this.totalPages,
      familyId: familyId ?? this.familyId,
      filterStatus: filterStatus ?? this.filterStatus,
      filterPriority: filterPriority ?? this.filterPriority,
    );
  }
}

class SavingsGoalListNotifier extends StateNotifier<SavingsGoalListState> {
  final SavingsGoalService _service;

  SavingsGoalListNotifier(this._service) : super(const SavingsGoalListState());

  Future<void> load({String? familyId, int page = 1, bool append = false}) async {
    state = state.copyWith(isLoading: true, error: null, familyId: familyId, page: page);
    try {
      final result = await _service.list(
        familyId: familyId,
        page: page,
        status: state.filterStatus,
        priority: state.filterPriority,
      );
      final goals = append ? [...state.goals, ...result.goals] : result.goals;
      state = state.copyWith(
        isLoading: false,
        goals: goals,
        totalPages: result.pagination.totalPages,
        page: result.pagination.page,
      );
    } catch (e) {
      state = state.copyWith(isLoading: false, error: e.toString());
    }
  }

  Future<void> loadMore() async {
    if (state.isLoading || state.page >= state.totalPages) return;
    await load(familyId: state.familyId, page: state.page + 1, append: true);
  }

  Future<void> refresh({String? familyId}) => load(familyId: familyId ?? state.familyId, page: 1);

  void setFilter({String? status, String? priority}) {
    state = state.copyWith(filterStatus: status, filterPriority: priority);
    load(familyId: state.familyId, page: 1);
  }
}

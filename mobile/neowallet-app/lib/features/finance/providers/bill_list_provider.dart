import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../models/bill_model.dart';
import '../services/bill_service.dart';

class BillListState {
  final List<BillModel> bills;
  final bool isLoading;
  final String? error;
  final int page;
  final int totalPages;
  final String? familyId;
  final String? filterStatus;
  final String? filterCategory;

  const BillListState({
    this.bills = const [],
    this.isLoading = false,
    this.error,
    this.page = 1,
    this.totalPages = 0,
    this.familyId,
    this.filterStatus,
    this.filterCategory,
  });

  BillListState copyWith({
    List<BillModel>? bills,
    bool? isLoading,
    String? error,
    int? page,
    int? totalPages,
    String? familyId,
    String? filterStatus,
    String? filterCategory,
  }) {
    return BillListState(
      bills: bills ?? this.bills,
      isLoading: isLoading ?? this.isLoading,
      error: error,
      page: page ?? this.page,
      totalPages: totalPages ?? this.totalPages,
      familyId: familyId ?? this.familyId,
      filterStatus: filterStatus ?? this.filterStatus,
      filterCategory: filterCategory ?? this.filterCategory,
    );
  }
}

class BillListNotifier extends StateNotifier<BillListState> {
  final BillService _service;

  BillListNotifier(this._service) : super(const BillListState());

  Future<void> load({String? familyId, int page = 1, bool append = false}) async {
    state = state.copyWith(isLoading: true, error: null, familyId: familyId, page: page);
    try {
      final result = await _service.list(
        familyId: familyId,
        page: page,
        status: state.filterStatus,
        category: state.filterCategory,
      );
      final bills = append ? [...state.bills, ...result.bills] : result.bills;
      state = state.copyWith(
        isLoading: false,
        bills: bills,
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

  void setFilter({String? status, String? category}) {
    state = state.copyWith(filterStatus: status, filterCategory: category);
    load(familyId: state.familyId, page: 1);
  }
}

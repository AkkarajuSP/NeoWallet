import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../models/transaction_model.dart';
import '../services/transaction_service.dart';

class TransactionListState {
  final List<TransactionModel> transactions;
  final bool isLoading;
  final String? error;
  final int page;
  final int totalPages;
  final String? familyId;
  final String? filterType;
  final String? filterCategory;
  final String? filterStatus;

  const TransactionListState({
    this.transactions = const [],
    this.isLoading = false,
    this.error,
    this.page = 1,
    this.totalPages = 0,
    this.familyId,
    this.filterType,
    this.filterCategory,
    this.filterStatus,
  });

  TransactionListState copyWith({
    List<TransactionModel>? transactions,
    bool? isLoading,
    String? error,
    int? page,
    int? totalPages,
    String? familyId,
    String? filterType,
    String? filterCategory,
    String? filterStatus,
  }) {
    return TransactionListState(
      transactions: transactions ?? this.transactions,
      isLoading: isLoading ?? this.isLoading,
      error: error,
      page: page ?? this.page,
      totalPages: totalPages ?? this.totalPages,
      familyId: familyId ?? this.familyId,
      filterType: filterType ?? this.filterType,
      filterCategory: filterCategory ?? this.filterCategory,
      filterStatus: filterStatus ?? this.filterStatus,
    );
  }
}

class TransactionListNotifier extends StateNotifier<TransactionListState> {
  final TransactionService _service;

  TransactionListNotifier({required TransactionService service})
      : _service = service,
        super(const TransactionListState());

  Future<void> load({String? familyId, int page = 1, bool append = false}) async {
    state = state.copyWith(isLoading: true, error: null, familyId: familyId, page: page);
    try {
      final result = await _service.list(
        familyId: familyId,
        page: page,
        filterType: state.filterType,
        filterCategory: state.filterCategory,
        filterStatus: state.filterStatus,
      );
      final transactions = append ? [...state.transactions, ...result.transactions] : result.transactions;
      state = state.copyWith(
        isLoading: false,
        transactions: transactions,
        totalPages: result.pagination.totalPages,
        page: result.pagination.page,
      );
    } catch (e) {
      state = state.copyWith(isLoading: false, error: _mapError(e));
    }
  }

  Future<void> loadMore() async {
    if (state.isLoading || state.page >= state.totalPages) return;
    await load(familyId: state.familyId, page: state.page + 1, append: true);
  }

  Future<void> refresh({String? familyId}) => load(familyId: familyId ?? state.familyId, page: 1);

  void setFilter({String? type, String? category, String? status}) {
    state = state.copyWith(
      filterType: type,
      filterCategory: category,
      filterStatus: status,
    );
    load(familyId: state.familyId, page: 1);
  }

  String _mapError(Object e) {
    if (e is DioException) return e.message ?? 'Network error';
    return e.toString();
  }
}

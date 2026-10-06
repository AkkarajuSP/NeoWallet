import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../models/financial_overview_model.dart';
import '../services/financial_overview_service.dart';

class FinancialOverviewState {
  final FinancialOverviewModel? overview;
  final FinancialSummaryModel? summary;
  final bool isLoading;
  final String? error;

  const FinancialOverviewState({
    this.overview,
    this.summary,
    this.isLoading = false,
    this.error,
  });

  FinancialOverviewState copyWith({
    FinancialOverviewModel? overview,
    FinancialSummaryModel? summary,
    bool? isLoading,
    String? error,
  }) {
    return FinancialOverviewState(
      overview: overview ?? this.overview,
      summary: summary ?? this.summary,
      isLoading: isLoading ?? this.isLoading,
      error: error,
    );
  }
}

class FinancialOverviewNotifier extends StateNotifier<FinancialOverviewState> {
  final FinancialOverviewService _service;

  FinancialOverviewNotifier({required FinancialOverviewService service})
      : _service = service,
        super(const FinancialOverviewState());

  Future<void> load({String? familyId}) async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      final overview = await _service.getOverview(familyId: familyId);
      final summary = await _service.getSummary(familyId: familyId);
      state = FinancialOverviewState(overview: overview, summary: summary, isLoading: false);
    } catch (e) {
      state = state.copyWith(isLoading: false, error: _mapError(e));
    }
  }

  void clearError() {
    state = state.copyWith(error: null);
  }

  String _mapError(Object e) {
    if (e is DioException) {
      final data = e.response?.data;
      if (data is Map<String, dynamic> && data['message'] != null) {
        return data['message'] as String;
      }
      return e.message ?? 'Network error';
    }
    return e.toString();
  }
}

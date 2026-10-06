import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../models/financial_health_model.dart';
import '../services/financial_health_service.dart';

class FinancialHealthState {
  final bool isLoading;
  final FinancialHealthModel? health;
  final FinancialHealthFactorsModel? factors;
  final FinancialHealthExplanationModel? explanation;
  final FinancialHealthHistoryModel? history;
  final String? error;

  const FinancialHealthState({
    this.isLoading = false,
    this.health,
    this.factors,
    this.explanation,
    this.history,
    this.error,
  });

  FinancialHealthState copyWith({
    bool? isLoading,
    FinancialHealthModel? health,
    FinancialHealthFactorsModel? factors,
    FinancialHealthExplanationModel? explanation,
    FinancialHealthHistoryModel? history,
    String? error,
  }) {
    return FinancialHealthState(
      isLoading: isLoading ?? this.isLoading,
      health: health ?? this.health,
      factors: factors ?? this.factors,
      explanation: explanation ?? this.explanation,
      history: history ?? this.history,
      error: error ?? this.error,
    );
  }
}

class FinancialHealthNotifier extends StateNotifier<FinancialHealthState> {
  final FinancialHealthService _service;
  final String? _familyId;

  FinancialHealthNotifier(this._service, this._familyId)
      : super(const FinancialHealthState(isLoading: true));

  Future<void> loadAll() async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      final health = await _service.getFinancialHealth(_familyId);
      final factors = await _service.getFactors(_familyId);
      final explanation = await _service.getExplanation(_familyId);
      final history = await _service.getHistory(familyId: _familyId);
      state = state.copyWith(
        isLoading: false,
        health: health,
        factors: factors,
        explanation: explanation,
        history: history,
      );
    } catch (e) {
      state = state.copyWith(isLoading: false, error: e.toString());
    }
  }
}

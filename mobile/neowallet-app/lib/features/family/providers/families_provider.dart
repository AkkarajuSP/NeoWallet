import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../models/family_models.dart';
import '../services/family_service.dart';

class FamiliesState {
  final List<FamilyModel> families;
  final bool isLoading;
  final String? error;

  const FamiliesState({
    this.families = const [],
    this.isLoading = false,
    this.error,
  });

  FamiliesState copyWith({
    List<FamilyModel>? families,
    bool? isLoading,
    String? error,
  }) {
    return FamiliesState(
      families: families ?? this.families,
      isLoading: isLoading ?? this.isLoading,
      error: error,
    );
  }
}

class FamiliesNotifier extends StateNotifier<FamiliesState> {
  final FamilyService _service;

  FamiliesNotifier(this._service) : super(const FamiliesState());

  Future<void> load() async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      final families = await _service.listMyFamilies();
      state = FamiliesState(families: families, isLoading: false);
    } catch (e) {
      state = state.copyWith(isLoading: false, error: e.toString());
    }
  }
}

import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../models/user_preferences_model.dart';
import '../models/user_profile_model.dart';
import '../services/profile_service.dart';

class ProfileState {
  final UserProfile? user;
  final UserPreferences? preferences;
  final bool isLoading;
  final String? error;

  const ProfileState({
    this.user,
    this.preferences,
    this.isLoading = false,
    this.error,
  });

  ProfileState copyWith({
    UserProfile? user,
    UserPreferences? preferences,
    bool? isLoading,
    String? error,
  }) {
    return ProfileState(
      user: user ?? this.user,
      preferences: preferences ?? this.preferences,
      isLoading: isLoading ?? this.isLoading,
      error: error,
    );
  }
}

class ProfileNotifier extends StateNotifier<ProfileState> {
  final ProfileService _profileService;

  ProfileNotifier({required ProfileService profileService})
      : _profileService = profileService,
        super(const ProfileState());

  Future<void> loadProfile() async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      final user = await _profileService.getProfile();
      state = ProfileState(user: user, isLoading: false);
    } catch (e) {
      state = state.copyWith(isLoading: false, error: _mapError(e));
    }
  }

  Future<void> updateProfile({
    String? firstName,
    String? lastName,
    String? phoneNumber,
  }) async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      final user = await _profileService.updateProfile(
        firstName: firstName,
        lastName: lastName,
        phoneNumber: phoneNumber,
      );
      state = ProfileState(user: user, isLoading: false);
    } catch (e) {
      state = state.copyWith(isLoading: false, error: _mapError(e));
    }
  }

  Future<void> loadPreferences() async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      final preferences = await _profileService.getPreferences();
      state = state.copyWith(preferences: preferences, isLoading: false);
    } catch (e) {
      state = state.copyWith(isLoading: false, error: _mapError(e));
    }
  }

  Future<void> updatePreferences(UserPreferences preferences) async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      final updated = await _profileService.updatePreferences(preferences);
      state = state.copyWith(preferences: updated, isLoading: false);
    } catch (e) {
      state = state.copyWith(isLoading: false, error: _mapError(e));
    }
  }

  Future<void> deleteAccount({required String confirmation}) async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      await _profileService.deleteAccount(confirmation: confirmation);
      state = const ProfileState(isLoading: false);
    } catch (e) {
      state = state.copyWith(isLoading: false, error: _mapError(e));
    }
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

  void clearError() {
    state = state.copyWith(error: null);
  }
}

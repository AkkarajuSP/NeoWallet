import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../models/family_models.dart';
import '../services/family_service.dart';

class FamilyState {
  final FamilyModel? family;
  final List<FamilyMemberModel>? members;
  final FamilyInvitationModel? lastInvitation;
  final bool isLoading;
  final String? error;

  const FamilyState({
    this.family,
    this.members,
    this.lastInvitation,
    this.isLoading = false,
    this.error,
  });

  FamilyState copyWith({
    FamilyModel? family,
    List<FamilyMemberModel>? members,
    FamilyInvitationModel? lastInvitation,
    bool? isLoading,
    String? error,
  }) {
    return FamilyState(
      family: family ?? this.family,
      members: members ?? this.members,
      lastInvitation: lastInvitation ?? this.lastInvitation,
      isLoading: isLoading ?? this.isLoading,
      error: error,
    );
  }
}

class FamilyStateNotifier extends StateNotifier<FamilyState> {
  final FamilyService _familyService;

  FamilyStateNotifier({required FamilyService familyService})
      : _familyService = familyService,
        super(const FamilyState());

  Future<void> loadFamily(String familyId) async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      final family = await _familyService.getFamily(familyId);
      final members = await _familyService.getMembers(familyId);
      state = FamilyState(family: family, members: members, isLoading: false);
    } catch (e) {
      state = state.copyWith(isLoading: false, error: _mapError(e));
    }
  }

  Future<void> createFamily({required String name, String currency = 'USD'}) async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      final family = await _familyService.createFamily(name: name, currency: currency);
      state = FamilyState(family: family, members: const [], isLoading: false);
    } catch (e) {
      state = state.copyWith(isLoading: false, error: _mapError(e));
    }
  }

  Future<void> inviteMember({required String familyId, required String email, String role = 'MEMBER'}) async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      final invitation = await _familyService.inviteMember(familyId, email: email, role: role);
      state = state.copyWith(lastInvitation: invitation, isLoading: false);
    } catch (e) {
      state = state.copyWith(isLoading: false, error: _mapError(e));
    }
  }

  Future<void> updateMemberRole({required String familyId, required String memberId, required String role}) async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      final updated = await _familyService.updateMemberRole(familyId, memberId, role);
      final members = state.members?.map((m) => m.memberId == updated.memberId ? updated : m).toList() ?? [];
      state = state.copyWith(members: members, isLoading: false);
    } catch (e) {
      state = state.copyWith(isLoading: false, error: _mapError(e));
    }
  }

  Future<void> removeMember({required String familyId, required String memberId}) async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      await _familyService.removeMember(familyId, memberId);
      final members = state.members?.where((m) => m.memberId != memberId).toList() ?? [];
      state = state.copyWith(members: members, isLoading: false);
    } catch (e) {
      state = state.copyWith(isLoading: false, error: _mapError(e));
    }
  }

  Future<void> acceptInvitation(String token) async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      await _familyService.acceptInvitation(token);
      state = state.copyWith(isLoading: false);
    } catch (e) {
      state = state.copyWith(isLoading: false, error: _mapError(e));
    }
  }

  Future<void> rejectInvitation(String token) async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      await _familyService.rejectInvitation(token);
      state = state.copyWith(isLoading: false);
    } catch (e) {
      state = state.copyWith(isLoading: false, error: _mapError(e));
    }
  }

  void clearInvitation() {
    state = state.copyWith(lastInvitation: null);
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

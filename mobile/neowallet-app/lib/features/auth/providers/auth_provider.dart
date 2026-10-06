import 'dart:async';

import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/storage/secure_storage.dart';
import '../models/auth_token.dart';
import '../services/auth_service.dart';

class AuthState {
  final AuthToken? token;
  final bool isLoading;
  final bool isAuthCheckPending;
  final String? email;
  final String? otpId;
  final bool isOtpVerified;
  final String? error;

  const AuthState({
    this.token,
    this.isLoading = false,
    this.isAuthCheckPending = true,
    this.email,
    this.otpId,
    this.isOtpVerified = false,
    this.error,
  });

  bool get isAuthenticated => token != null;

  AuthState copyWith({
    AuthToken? token,
    bool? isLoading,
    bool? isAuthCheckPending,
    String? email,
    String? otpId,
    bool? isOtpVerified,
    String? error,
    bool clearError = false,
  }) {
    return AuthState(
      token: token ?? this.token,
      isLoading: isLoading ?? this.isLoading,
      isAuthCheckPending: isAuthCheckPending ?? this.isAuthCheckPending,
      email: email ?? this.email,
      otpId: otpId ?? this.otpId,
      isOtpVerified: isOtpVerified ?? this.isOtpVerified,
      error: clearError ? null : error ?? this.error,
    );
  }
}

class AuthNotifier extends StateNotifier<AuthState> {
  final AuthService _authService;
  final SecureStorage _secureStorage;
  StreamSubscription<void>? _sessionExpiredSub;

  AuthNotifier({
    required AuthService authService,
    required SecureStorage secureStorage,
    required Stream<void> sessionExpiredStream,
  })  : _authService = authService,
        _secureStorage = secureStorage,
        super(const AuthState(isAuthCheckPending: true)) {
    _loadFromStorage();
    _sessionExpiredSub = sessionExpiredStream.listen((_) => sessionExpired());
  }

  @override
  void dispose() {
    _sessionExpiredSub?.cancel();
    super.dispose();
  }

  Future<void> _loadFromStorage() async {
    final accessToken = await _secureStorage.readAccessToken();
    if (accessToken != null && accessToken.isNotEmpty) {
      final refreshToken = await _secureStorage.readRefreshToken() ?? '';
      state = state.copyWith(
        isAuthCheckPending: false,
        token: AuthToken(
          accessToken: accessToken,
          refreshToken: refreshToken,
          tokenType: 'Bearer',
          expiresIn: 0,
          issuedAt: DateTime.now(),
        ),
      );
    } else {
      state = state.copyWith(isAuthCheckPending: false);
    }
  }

  Future<void> register({
    required String email,
    required String password,
    required String firstName,
    required String lastName,
  }) async {
    state = state.copyWith(isLoading: true, clearError: true);
    try {
      await _authService.register(
        email: email,
        password: password,
        firstName: firstName,
        lastName: lastName,
      );
      state = state.copyWith(isLoading: false, email: email);
    } catch (e) {
      state = state.copyWith(isLoading: false, error: _mapError(e));
    }
  }

  Future<void> requestOtp({String? email, String purpose = 'REGISTRATION'}) async {
    final target = email ?? state.email;
    if (target == null || target.isEmpty) {
      state = state.copyWith(error: 'Email is required');
      return;
    }
    state = state.copyWith(isLoading: true, clearError: true);
    try {
      final otpId = await _authService.requestOtp(email: target, purpose: purpose);
      state = state.copyWith(isLoading: false, email: target, otpId: otpId);
    } catch (e) {
      state = state.copyWith(isLoading: false, error: _mapError(e));
    }
  }

  Future<void> resendOtp({String purpose = 'REGISTRATION'}) async {
    await requestOtp(email: state.email, purpose: purpose);
  }

  Future<void> verifyOtp({required String code, String purpose = 'REGISTRATION'}) async {
    final otpId = state.otpId;
    if (otpId == null || otpId.isEmpty) {
      state = state.copyWith(error: 'No active OTP request');
      return;
    }
    state = state.copyWith(isLoading: true, clearError: true);
    try {
      final verified = await _authService.verifyOtp(otpId: otpId, code: code);
      if (verified) {
        state = state.copyWith(isLoading: false, isOtpVerified: true, clearError: true);
      } else {
        state = state.copyWith(isLoading: false, error: 'Invalid OTP. Please try again or request a new code.');
      }
    } catch (e) {
      state = state.copyWith(isLoading: false, error: _mapError(e));
    }
  }

  Future<void> login({
    required String email,
    required String password,
    String? deviceType,
    String? deviceName,
  }) async {
    state = state.copyWith(isLoading: true, clearError: true);
    try {
      final token = await _authService.login(
        email: email,
        password: password,
        deviceType: deviceType,
        deviceName: deviceName,
      );
      state = state.copyWith(
        isLoading: false,
        isAuthCheckPending: false,
        token: token,
        email: email,
        clearError: true,
      );
    } catch (e) {
      state = state.copyWith(isLoading: false, error: _mapError(e));
    }
  }

  Future<void> logout() async {
    state = state.copyWith(isLoading: true);
    try {
      await _authService.logout();
    } catch (e) {
      debugPrint('Logout warning: $e');
    } finally {
      state = const AuthState(isAuthCheckPending: false);
    }
  }

  Future<bool> refresh() async {
    if (state.token == null) return false;
    try {
      final token = await _authService.refresh();
      state = state.copyWith(token: token);
      return true;
    } catch (e) {
      debugPrint('Refresh failed: $e');
      await logout();
      return false;
    }
  }

  Future<void> sessionExpired() async {
    await logout();
  }

  void clearError() {
    state = state.copyWith(clearError: true);
  }

  String _mapError(Object e) {
    if (e is DioException) {
      final data = e.response?.data;
      if (data is Map<String, dynamic> && data['message'] != null) {
        return data['message'] as String;
      }
      return e.message ?? 'Authentication failed';
    }
    return e.toString();
  }
}

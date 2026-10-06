import 'dart:async';

import '../storage/secure_storage.dart';
import 'preview_data.dart';

/// In-memory secure storage used in preview mode.
///
/// Pre-seeds an access token so the app starts in an authenticated state.
class MockSecureStorage implements SecureStorage {
  String? _accessToken = previewToken.accessToken;
  String? _refreshToken = previewToken.refreshToken;

  @override
  Future<void> deleteAll() async {
    _accessToken = null;
    _refreshToken = null;
  }

  @override
  Future<String?> readAccessToken() async => _accessToken;

  @override
  Future<String?> readRefreshToken() async => _refreshToken;

  @override
  Future<void> writeAccessToken(String? value) async => _accessToken = value;

  @override
  Future<void> writeRefreshToken(String? value) async => _refreshToken = value;
}

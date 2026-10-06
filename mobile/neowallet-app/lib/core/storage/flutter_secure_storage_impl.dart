import 'dart:async';

import 'package:flutter_secure_storage/flutter_secure_storage.dart';

import 'secure_storage.dart';

class FlutterSecureStorageImpl implements SecureStorage {
  static const _accessTokenKey = 'neowallet_access_token';
  static const _refreshTokenKey = 'neowallet_refresh_token';

  final FlutterSecureStorage _storage = const FlutterSecureStorage(
    aOptions: AndroidOptions(
      encryptedSharedPreferences: true,
    ),
  );

  @override
  Future<void> writeAccessToken(String? value) async {
    if (value == null) {
      await _storage.delete(key: _accessTokenKey);
    } else {
      await _storage.write(key: _accessTokenKey, value: value);
    }
  }

  @override
  Future<void> writeRefreshToken(String? value) async {
    if (value == null) {
      await _storage.delete(key: _refreshTokenKey);
    } else {
      await _storage.write(key: _refreshTokenKey, value: value);
    }
  }

  @override
  Future<String?> readAccessToken() => _storage.read(key: _accessTokenKey);

  @override
  Future<String?> readRefreshToken() => _storage.read(key: _refreshTokenKey);

  @override
  Future<void> deleteAll() => _storage.deleteAll();
}

import 'dart:async';

abstract class SecureStorage {
  Future<void> writeAccessToken(String? value);
  Future<void> writeRefreshToken(String? value);
  Future<String?> readAccessToken();
  Future<String?> readRefreshToken();
  Future<void> deleteAll();
}

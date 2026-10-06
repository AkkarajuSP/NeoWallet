import 'package:dio/dio.dart';
import 'package:flutter_test/flutter_test.dart';

import 'package:neowallet_app/core/storage/secure_storage.dart';
import 'package:neowallet_app/features/auth/models/auth_token.dart';
import 'package:neowallet_app/features/auth/providers/auth_provider.dart';
import 'package:neowallet_app/features/auth/services/auth_service.dart';

class _FakeSecureStorage implements SecureStorage {
  final _values = <String, String?>{};

  @override
  Future<void> deleteAll() async => _values.clear();

  @override
  Future<String?> readAccessToken() async => _values['access'];

  @override
  Future<String?> readRefreshToken() async => _values['refresh'];

  @override
  Future<void> writeAccessToken(String? value) async => _values['access'] = value;

  @override
  Future<void> writeRefreshToken(String? value) async => _values['refresh'] = value;
}

class _FakeAuthService extends AuthService {
  // ignore: use_super_parameters
  _FakeAuthService({required super.dio, required SecureStorage secureStorage})
      : _storage = secureStorage,
        super(secureStorage: secureStorage);

  final SecureStorage _storage;

  final _token = AuthToken(
    accessToken: 'access',
    refreshToken: 'refresh',
    tokenType: 'Bearer',
    expiresIn: 900,
    issuedAt: DateTime.now(),
  );

  bool loggedOut = false;

  @override
  Future<AuthToken> login({
    required String email,
    required String password,
    String? deviceType,
    String? deviceName,
  }) async {
    await _storage.writeAccessToken(_token.accessToken);
    await _storage.writeRefreshToken(_token.refreshToken);
    return _token;
  }

  @override
  Future<void> logout() async {
    await _storage.deleteAll();
    loggedOut = true;
  }
}

void main() {
  group('AuthNotifier', () {
    test('login stores token and updates state', () async {
      final storage = _FakeSecureStorage();
      final service = _FakeAuthService(
        dio: Dio(),
        secureStorage: storage,
      );
      final notifier = AuthNotifier(
        authService: service,
        secureStorage: storage,
        sessionExpiredStream: const Stream<void>.empty(),
      );

      await notifier.login(
        email: 'test@example.com',
        password: 'password',
      );

      expect(notifier.state.isAuthenticated, true);
      expect(notifier.state.token?.accessToken, 'access');
      expect(await storage.readAccessToken(), 'access');
      expect(await storage.readRefreshToken(), 'refresh');
    });

    test('logout clears state and storage', () async {
      final storage = _FakeSecureStorage();
      final service = _FakeAuthService(
        dio: Dio(),
        secureStorage: storage,
      );
      final notifier = AuthNotifier(
        authService: service,
        secureStorage: storage,
        sessionExpiredStream: const Stream<void>.empty(),
      );

      await notifier.login(
        email: 'test@example.com',
        password: 'password',
      );
      await notifier.logout();

      expect(notifier.state.isAuthenticated, false);
      expect(await storage.readAccessToken(), null);
      expect(service.loggedOut, true);
    });
  });
}

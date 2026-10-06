// ignore_for_file: prefer_const_constructors

import 'dart:async';

import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:neowallet_app/core/di/providers.dart';
import 'package:neowallet_app/core/network/api_client.dart';
import 'package:neowallet_app/core/routing/router_provider.dart';
import 'package:neowallet_app/core/storage/secure_storage.dart';
import 'package:neowallet_app/features/auth/providers/auth_provider.dart';
import 'package:neowallet_app/features/auth/services/auth_service.dart';
import 'package:neowallet_app/features/auth/screens/login_screen.dart';
import 'package:neowallet_app/features/family/providers/families_provider.dart';
import 'package:neowallet_app/features/family/services/family_service.dart';
import 'package:neowallet_app/features/foundation/screens/splash_screen.dart';
import 'package:neowallet_app/features/home/screens/home_screen.dart';
import 'package:neowallet_app/features/profile/providers/profile_provider.dart';
import 'package:neowallet_app/features/profile/services/profile_service.dart';

class _FakeSecureStorage implements SecureStorage {
  const _FakeSecureStorage({
    this.accessToken,
    this.refreshToken,
    this.pending = false,
  });

  final String? accessToken;
  final String? refreshToken;
  final bool pending;

  @override
  Future<void> writeAccessToken(String? value) => SynchronousFuture(null);

  @override
  Future<void> writeRefreshToken(String? value) => SynchronousFuture(null);

  @override
  Future<String?> readAccessToken() {
    if (pending) return Completer<String?>().future;
    return SynchronousFuture(accessToken);
  }

  @override
  Future<String?> readRefreshToken() {
    if (pending) return Completer<String?>().future;
    return SynchronousFuture(refreshToken);
  }

  @override
  Future<void> deleteAll() => SynchronousFuture(null);
}

class _FakeAuthNotifier extends AuthNotifier {
  _FakeAuthNotifier._({
    required super.authService,
    required super.secureStorage,
    required super.sessionExpiredStream,
  });

  factory _FakeAuthNotifier.pending() {
    const storage = _FakeSecureStorage(pending: true);
    return _FakeAuthNotifier._(
      authService: AuthService(dio: Dio(), secureStorage: storage),
      secureStorage: storage,
      sessionExpiredStream: Stream<void>.empty(),
    );
  }

  factory _FakeAuthNotifier.unauthenticated() {
    const storage = _FakeSecureStorage();
    return _FakeAuthNotifier._(
      authService: AuthService(dio: Dio(), secureStorage: storage),
      secureStorage: storage,
      sessionExpiredStream: Stream<void>.empty(),
    );
  }

  factory _FakeAuthNotifier.authenticated() {
    const storage = _FakeSecureStorage(
      accessToken: 'fake-access-token',
      refreshToken: 'fake-refresh-token',
    );
    return _FakeAuthNotifier._(
      authService: AuthService(dio: Dio(), secureStorage: storage),
      secureStorage: storage,
      sessionExpiredStream: Stream<void>.empty(),
    );
  }
}

class _FakeProfileNotifier extends ProfileNotifier {
  _FakeProfileNotifier({required super.profileService});

  @override
  Future<void> loadProfile() => SynchronousFuture(null);
}

class _FakeFamiliesNotifier extends FamiliesNotifier {
  _FakeFamiliesNotifier(super.service);

  @override
  Future<void> load() => SynchronousFuture(null);
}

class _TestRouterApp extends ConsumerWidget {
  const _TestRouterApp({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final router = ref.watch(routerProvider);
    return MaterialApp.router(
      routerConfig: router,
    );
  }
}

void main() {
  testWidgets(
    'splash + pending auth remains on /splash',
    (tester) async {
      await tester.pumpWidget(
        ProviderScope(
          overrides: [
            authProvider.overrideWith(
              (ref) => _FakeAuthNotifier.pending(),
            ),
          ],
          child: const _TestRouterApp(key: ValueKey('test-pending')),
        ),
      );

      expect(find.byType(SplashScreen), findsOneWidget);
    },
  );

  testWidgets(
    'splash + unauthenticated goes to /login',
    (tester) async {
      await tester.pumpWidget(
        ProviderScope(
          overrides: [
            authProvider.overrideWith(
              (ref) => _FakeAuthNotifier.unauthenticated(),
            ),
          ],
          child: const _TestRouterApp(key: ValueKey('test-login')),
        ),
      );

      expect(find.byType(LoginScreen), findsOneWidget);
    },
  );

  testWidgets(
    'splash + authenticated goes to /home',
    (tester) async {
      await tester.pumpWidget(
        ProviderScope(
          overrides: [
            apiClientProvider.overrideWith(
              (ref) => ApiClient(
                baseUrl: 'http://192.168.1.5:8080',
                secureStorage: const _FakeSecureStorage(),
                onSessionExpired: () {},
              ),
            ),
            profileProvider.overrideWith(
              (ref) => _FakeProfileNotifier(
                profileService: ProfileService(
                  dio: ref.read(apiClientProvider).dio,
                ),
              ),
            ),
            familiesProvider.overrideWith(
              (ref) => _FakeFamiliesNotifier(
                FamilyService(
                  dio: ref.read(apiClientProvider).dio,
                ),
              ),
            ),
            authProvider.overrideWith(
              (ref) => _FakeAuthNotifier.authenticated(),
            ),
          ],
          child: const _TestRouterApp(key: ValueKey('test-home')),
        ),
      );

      expect(find.byType(HomeScreen), findsOneWidget);
    },
  );
}

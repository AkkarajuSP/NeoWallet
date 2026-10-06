import 'package:flutter/foundation.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/di/providers.dart' show authProvider;
import 'auth_provider.dart';

/// A [Listenable] wrapper around [AuthNotifier] so go_router can react
/// to authentication state changes and re-run the redirect callback.
class AuthListenable extends Listenable {
  final AuthNotifier _notifier;
  final List<VoidCallback> _listeners = [];

  AuthListenable(this._notifier) {
    _notifier.addListener(_onStateChanged);
  }

  void _onStateChanged(AuthState _) {
    for (final listener in _listeners) {
      listener();
    }
  }

  @override
  void addListener(VoidCallback listener) => _listeners.add(listener);

  @override
  void removeListener(VoidCallback listener) => _listeners.remove(listener);
}

final authListenableProvider = Provider<AuthListenable>((ref) {
  return AuthListenable(ref.watch(authProvider.notifier));
});

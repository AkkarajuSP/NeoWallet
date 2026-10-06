import 'dart:async';

import 'package:flutter_riverpod/flutter_riverpod.dart';

/// A broadcast stream used by the auth interceptor to notify the app
/// when a refresh attempt fails and the user must be logged out.
final sessionExpiredControllerProvider = Provider<StreamController<void>>(
  (ref) => StreamController<void>.broadcast(),
);

final sessionExpiredStreamProvider = Provider<Stream<void>>(
  (ref) => ref.watch(sessionExpiredControllerProvider).stream,
);

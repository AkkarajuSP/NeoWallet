import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import 'app/app.dart';
import 'core/preview/preview_mode.dart';
import 'core/preview/preview_overrides.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  try {
    await dotenv.load(fileName: '.env');
  } catch (_) {
    // .env is optional; the app and preview environment have sensible defaults.
  }

  runApp(
    ProviderScope(
      overrides: kPreviewMode ? buildPreviewOverrides() : [],
      child: const NeoWalletApp(),
    ),
  );
}

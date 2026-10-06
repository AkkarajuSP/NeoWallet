import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../core/preview/demo_banner.dart';
import '../core/preview/preview_mode.dart';
import '../core/routing/router_provider.dart';
import '../core/theme/app_theme.dart';

class NeoWalletApp extends ConsumerWidget {
  const NeoWalletApp({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final router = ref.watch(routerProvider);

    return MaterialApp.router(
      title: kPreviewMode ? 'NeoWallet (DEMO)' : 'NeoWallet',
      theme: AppTheme.light,
      darkTheme: AppTheme.dark,
      routerConfig: router,
      builder: (context, child) => DemoBanner(child: child!),
    );
  }
}

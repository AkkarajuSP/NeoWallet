import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import 'preview_data.dart';

/// Central navigation screen for the UI preview environment.
///
/// Allows quick access to every implemented screen. Items that do not have
/// a real screen yet are shown as "Not implemented".
class PreviewMenuScreen extends StatelessWidget {
  const PreviewMenuScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('NeoWallet UI Preview')),
      body: ListView(
        children: const [
          _Tile(label: '1. Splash', route: '/splash'),
          _NotImplementedTile(label: '2. Login'),
          _NotImplementedTile(label: '3. OTP'),
          _Tile(label: '4. Profile', route: '/profile'),
          _Tile(label: '5. Preferences', route: '/profile/preferences'),
          _Tile(label: '6. Family', route: '/family'),
          _Tile(label: '7. Family Members', route: '/family'),
          _Tile(
            label: '8. Financial Overview',
            route: '/finance/overview',
            extra: PreviewIds.familyId,
          ),
          _Tile(
            label: '9. Transactions',
            route: '/transactions',
            extra: PreviewIds.familyId,
          ),
          _Tile(label: '10. Budget', route: '/budgets'),
          _Tile(label: '11. Savings Goals', route: '/savings-goals'),
        ],
      ),
    );
  }
}

class _Tile extends StatelessWidget {
  final String label;
  final String route;
  final Object? extra;

  const _Tile({required this.label, required this.route, this.extra});

  @override
  Widget build(BuildContext context) {
    return ListTile(
      title: Text(label),
      trailing: const Icon(Icons.chevron_right),
      onTap: () => context.push(route, extra: extra),
    );
  }
}

class _NotImplementedTile extends StatelessWidget {
  final String label;

  const _NotImplementedTile({required this.label});

  @override
  Widget build(BuildContext context) {
    return ListTile(
      title: Text(label),
      subtitle: const Text('Screen not yet implemented'),
      trailing: const Icon(Icons.construction, color: Colors.grey),
      enabled: false,
    );
  }
}

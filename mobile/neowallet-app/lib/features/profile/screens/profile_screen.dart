import 'package:flutter/material.dart';

import '../../../core/components/brand_logos.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:neowallet_app/core/di/providers.dart';
import 'package:neowallet_app/features/profile/providers/profile_provider.dart' show ProfileState;


class ProfileScreen extends ConsumerStatefulWidget {
  const ProfileScreen({super.key});

  @override
  ConsumerState<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends ConsumerState<ProfileScreen> {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      ref.read(profileProvider.notifier).loadProfile();
    });
  }

  @override
  Widget build(BuildContext context) {
    final state = ref.watch(profileProvider);

    return Scaffold(
      appBar: const BrandAppBar(title: Text('Profile')),
      body: state.isLoading
          ? const Center(child: CircularProgressIndicator())
          : state.error != null
              ? _buildError(state.error!)
              : state.user == null
                  ? const Center(child: Text('No profile data'))
                  : _buildProfile(state),
    );
  }

  Widget _buildError(String error) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(error, textAlign: TextAlign.center),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: () => ref.read(profileProvider.notifier).loadProfile(),
              child: const Text('Retry'),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildProfile(ProfileState state) {
    final user = state.user!;
    return ListView(
      padding: const EdgeInsets.all(16),
      children: [
        _tile('First Name', user.firstName),
        _tile('Last Name', user.lastName),
        _tile('Email', user.email),
        _tile('Phone', user.phoneNumber ?? '-'),
        _tile('Locale', state.preferences?.locale ?? '-'),
        _tile('Currency', state.preferences?.currency ?? '-'),
        _tile('Timezone', state.preferences?.timezone ?? '-'),
        const SizedBox(height: 24),
        ElevatedButton(
          onPressed: () => context.push('/profile/edit'),
          child: const Text('Edit Profile'),
        ),
        const SizedBox(height: 8),
        ElevatedButton(
          onPressed: () => context.push('/profile/preferences'),
          child: const Text('Preferences'),
        ),
        const SizedBox(height: 8),
        ElevatedButton(
          onPressed: () => context.push('/family'),
          child: const Text('Family'),
        ),
        const SizedBox(height: 8),
        ElevatedButton(
          onPressed: () => context.push('/finance/overview'),
          child: const Text('Financial Overview'),
        ),
        const SizedBox(height: 8),
        ElevatedButton(
          onPressed: () async {
            await ref.read(authProvider.notifier).logout();
            if (!mounted) return;
            context.go('/login');
          },
          style: ElevatedButton.styleFrom(
            backgroundColor: Theme.of(context).colorScheme.error,
            foregroundColor: Theme.of(context).colorScheme.onError,
          ),
          child: const Text('Log out'),
        ),
      ],
    );
  }

  Widget _tile(String label, String value) {
    return ListTile(
      title: Text(label),
      subtitle: Text(value),
      contentPadding: EdgeInsets.zero,
    );
  }
}

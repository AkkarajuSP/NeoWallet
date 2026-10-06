import 'package:flutter/material.dart';

import '../../../core/components/brand_logos.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:neowallet_app/core/di/providers.dart';

class CreateFamilyScreen extends ConsumerStatefulWidget {
  const CreateFamilyScreen({super.key});

  @override
  ConsumerState<CreateFamilyScreen> createState() => _CreateFamilyScreenState();
}

class _CreateFamilyScreenState extends ConsumerState<CreateFamilyScreen> {
  final _nameController = TextEditingController();
  final _currencyController = TextEditingController(text: 'USD');

  @override
  void dispose() {
    _nameController.dispose();
    _currencyController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final state = ref.watch(familyProvider);

    return Scaffold(
      appBar: const BrandAppBar(title: Text('Create Family')),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            TextField(
              controller: _nameController,
              decoration: const InputDecoration(labelText: 'Family Name'),
            ),
            const SizedBox(height: 16),
            TextField(
              controller: _currencyController,
              decoration: const InputDecoration(labelText: 'Currency (3-letter code)'),
              maxLength: 3,
            ),
            const SizedBox(height: 16),
            if (state.error != null)
              Text(state.error!, style: const TextStyle(color: Colors.red)),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: state.isLoading ? null : _create,
              child: state.isLoading ? const CircularProgressIndicator() : const Text('Create'),
            ),
          ],
        ),
      ),
    );
  }

  Future<void> _create() async {
    await ref.read(familyProvider.notifier).createFamily(
          name: _nameController.text.trim(),
          currency: _currencyController.text.trim().toUpperCase(),
        );
    final state = ref.read(familyProvider);
    if (state.error == null && state.family != null && mounted) {
      if (mounted) context.go('/family');
    }
  }
}

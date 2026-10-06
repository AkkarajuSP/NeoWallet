import 'package:flutter/material.dart';

import '../../../core/components/brand_logos.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:neowallet_app/core/di/providers.dart';

class MemberRoleScreen extends ConsumerStatefulWidget {
  final String familyId;
  final String memberId;
  final String currentRole;

  const MemberRoleScreen({
    super.key,
    required this.familyId,
    required this.memberId,
    required this.currentRole,
  });

  @override
  ConsumerState<MemberRoleScreen> createState() => _MemberRoleScreenState();
}

class _MemberRoleScreenState extends ConsumerState<MemberRoleScreen> {
  late String _role;

  @override
  void initState() {
    super.initState();
    _role = widget.currentRole;
  }

  @override
  Widget build(BuildContext context) {
    final state = ref.watch(familyProvider);

    return Scaffold(
      appBar: const BrandAppBar(title: Text('Change Role')),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            DropdownButtonFormField<String>(
              value: _role,
              decoration: const InputDecoration(labelText: 'Role'),
              items: const [
                DropdownMenuItem(value: 'MEMBER', child: Text('MEMBER')),
                DropdownMenuItem(value: 'RESTRICTED', child: Text('RESTRICTED')),
              ],
              onChanged: (value) => setState(() => _role = value!),
            ),
            const SizedBox(height: 16),
            if (state.error != null)
              Text(state.error!, style: const TextStyle(color: Colors.red)),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: state.isLoading ? null : _save,
              child: state.isLoading ? const CircularProgressIndicator() : const Text('Save'),
            ),
          ],
        ),
      ),
    );
  }

  Future<void> _save() async {
    await ref.read(familyProvider.notifier).updateMemberRole(
          familyId: widget.familyId,
          memberId: widget.memberId,
          role: _role,
        );
    final state = ref.read(familyProvider);
    if (state.error == null && mounted) {
      if (mounted) context.pop();
    }
  }
}

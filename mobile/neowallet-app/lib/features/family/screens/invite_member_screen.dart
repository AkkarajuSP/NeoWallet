import 'package:flutter/material.dart';

import '../../../core/components/brand_logos.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:neowallet_app/core/di/providers.dart';

class InviteMemberScreen extends ConsumerStatefulWidget {
  final String familyId;

  const InviteMemberScreen({super.key, required this.familyId});

  @override
  ConsumerState<InviteMemberScreen> createState() => _InviteMemberScreenState();
}

class _InviteMemberScreenState extends ConsumerState<InviteMemberScreen> {
  final _emailController = TextEditingController();
  String _role = 'MEMBER';

  @override
  void dispose() {
    _emailController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final state = ref.watch(familyProvider);

    return Scaffold(
      appBar: const BrandAppBar(title: Text('Invite Member')),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            TextField(
              controller: _emailController,
              decoration: const InputDecoration(labelText: 'Email'),
              keyboardType: TextInputType.emailAddress,
            ),
            const SizedBox(height: 16),
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
            if (state.lastInvitation != null)
              Text('Invitation sent: ${state.lastInvitation!.token}'),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: state.isLoading ? null : _invite,
              child: state.isLoading ? const CircularProgressIndicator() : const Text('Invite'),
            ),
          ],
        ),
      ),
    );
  }

  Future<void> _invite() async {
    await ref.read(familyProvider.notifier).inviteMember(
          familyId: widget.familyId,
          email: _emailController.text.trim(),
          role: _role,
        );
  }
}

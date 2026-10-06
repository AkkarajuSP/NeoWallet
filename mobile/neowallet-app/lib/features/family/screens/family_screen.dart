import 'package:flutter/material.dart';

import '../../../core/components/brand_logos.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/di/providers.dart';
import '../models/family_models.dart';
import '../providers/families_provider.dart';
import '../providers/family_provider.dart';
import '../providers/selected_family_provider.dart';

class FamilyScreen extends ConsumerStatefulWidget {
  const FamilyScreen({super.key});

  @override
  ConsumerState<FamilyScreen> createState() => _FamilyScreenState();
}

class _FamilyScreenState extends ConsumerState<FamilyScreen> {
  @override
  void initState() {
    super.initState();
    Future.microtask(() async {
      await ref.read(familiesProvider.notifier).load();
      final families = ref.read(familiesProvider).families;
      final current = ref.read(selectedFamilyIdProvider);
      if (current == null && families.isNotEmpty) {
        ref.read(selectedFamilyIdProvider.notifier).state = families.first.familyId;
      }
      final familyId = ref.read(selectedFamilyIdProvider) ?? families.firstOrNull?.familyId;
      if (familyId != null) {
        await ref.read(familyProvider.notifier).loadFamily(familyId);
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    final familyId = ref.watch(selectedFamilyIdProvider);
    final familyState = ref.watch(familyProvider);
    final familiesState = ref.watch(familiesProvider);

    return Scaffold(
      appBar: const BrandAppBar(title: Text('Family')),
      body: _buildBody(familyId, familyState, familiesState),
    );
  }

  Widget _buildBody(String? familyId, FamilyState familyState, FamiliesState familiesState) {
    if (familiesState.isLoading) {
      return const Center(child: CircularProgressIndicator());
    }

    if (familyId == null || familiesState.families.isEmpty) {
      return _buildNoFamily();
    }

    if (familyState.isLoading && familyState.family == null) {
      return const Center(child: CircularProgressIndicator());
    }

    if (familyState.error != null) {
      return _buildError(familyState.error!);
    }

    final family = familyState.family;
    final members = familyState.members;

    if (family == null || members == null) {
      return _buildError('Family not loaded');
    }

    return _buildFamily(family, members, familiesState.families);
  }

  Widget _buildNoFamily() {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Text('You are not part of a family yet.'),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: () => context.push('/family/create'),
              child: const Text('Create Family'),
            ),
          ],
        ),
      ),
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
              onPressed: () {
                final familyId = ref.read(selectedFamilyIdProvider);
                if (familyId != null) {
                  ref.read(familyProvider.notifier).loadFamily(familyId);
                }
              },
              child: const Text('Retry'),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildFamily(FamilyModel family, List<FamilyMemberModel> members, List<FamilyModel> families) {
    final isOwner = ref.read(profileProvider).user?.familyRole == 'OWNER';
    final selected = ref.watch(selectedFamilyIdProvider);

    return ListView(
      padding: const EdgeInsets.all(16),
      children: [
        Card(
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                DropdownButton<String>(
                  isExpanded: true,
                  value: selected,
                  onChanged: (value) {
                    if (value == null) return;
                    ref.read(selectedFamilyIdProvider.notifier).state = value;
                    ref.read(familyProvider.notifier).loadFamily(value);
                    ref.invalidate(financialOverviewProvider(value));
                    ref.invalidate(budgetListProvider(value));
                    ref.invalidate(savingsGoalListProvider(value));
                    ref.invalidate(billListProvider(value));
                    ref.invalidate(transactionListProvider(value));
                    ref.invalidate(financialHealthProvider(value));
                  },
                  items: families
                      .map((f) => DropdownMenuItem(
                            value: f.familyId,
                            child: Text('${f.name} (${f.currency})'),
                          ))
                      .toList(),
                ),
                const SizedBox(height: 8),
                Text(family.name, style: Theme.of(context).textTheme.titleLarge),
                Text('Currency: ${family.currency}'),
                Text('Members: ${family.memberCount}'),
              ],
            ),
          ),
        ),
        const SizedBox(height: 16),
        Row(
          children: [
            if (isOwner)
              ElevatedButton(
                onPressed: () => context.push('/family/invite', extra: family.familyId),
                child: const Text('Invite Member'),
              ),
          ],
        ),
        const SizedBox(height: 16),
        Text('Members', style: Theme.of(context).textTheme.titleMedium),
        const SizedBox(height: 8),
        if (members.isEmpty)
          const Text('No members yet.')
        else
          ...members.map((m) => _buildMember(m, isOwner, family.familyId)),
      ],
    );
  }

  Widget _buildMember(FamilyMemberModel member, bool isOwner, String familyId) {
    final currentUser = ref.read(profileProvider).user?.userId;
    return ListTile(
      title: Text('${member.firstName ?? ''} ${member.lastName ?? ''}'.trim()),
      subtitle: Text('${member.email}\n${member.role}'),
      isThreeLine: true,
      trailing: isOwner && member.userId != currentUser
          ? PopupMenuButton<String>(
              onSelected: (value) => _onMemberAction(value, familyId, member),
              itemBuilder: (context) => [
                const PopupMenuItem(value: 'role', child: Text('Change Role')),
                const PopupMenuItem(value: 'remove', child: Text('Remove')),
              ],
            )
          : null,
    );
  }

  void _onMemberAction(String action, String familyId, FamilyMemberModel member) {
    if (action == 'role') {
      context.push('/family/role', extra: {'familyId': familyId, 'memberId': member.memberId, 'role': member.role});
    } else if (action == 'remove') {
      ref.read(familyProvider.notifier).removeMember(familyId: familyId, memberId: member.memberId);
    }
  }
}

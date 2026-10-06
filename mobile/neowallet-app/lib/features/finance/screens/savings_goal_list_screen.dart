import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import 'package:neowallet_app/core/components/brand_logos.dart';
import 'package:neowallet_app/core/components/neo_context_button.dart';
import 'package:neowallet_app/core/components/state_widgets.dart';
import 'package:neowallet_app/core/di/providers.dart';
import 'package:neowallet_app/core/theme/design_tokens.dart';
import 'package:neowallet_app/features/finance/providers/savings_goal_list_provider.dart';
import 'package:neowallet_app/core/utils/amount_formatter.dart';
import 'package:neowallet_app/features/family/providers/selected_family_provider.dart';
import 'package:neowallet_app/features/finance/models/savings_goal_model.dart';

class SavingsGoalListScreen extends ConsumerStatefulWidget {
  final String? familyId;

  const SavingsGoalListScreen({super.key, this.familyId});

  @override
  ConsumerState<SavingsGoalListScreen> createState() => _SavingsGoalListScreenState();
}

class _SavingsGoalListScreenState extends ConsumerState<SavingsGoalListScreen> {
  @override
  Widget build(BuildContext context) {
    final selected = ref.watch(selectedFamilyIdProvider);
    final familyId = widget.familyId ?? selected;
    final state = ref.watch(savingsGoalListProvider(familyId));

    return Scaffold(
      appBar: const BrandAppBar(title: Text('Savings Goals')),
      body: RefreshIndicator(
        onRefresh: () => ref.read(savingsGoalListProvider(familyId).notifier).refresh(familyId: familyId),
        child: _buildBody(state, familyId: familyId),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => context.push('/savings-goals/create', extra: familyId),
        child: const Icon(Icons.add),
      ),
    );
  }

  Widget _buildBody(SavingsGoalListState state, {required String? familyId}) {
    if (state.isLoading && state.goals.isEmpty) {
      return const LoadingState(message: 'Loading savings goals...');
    }
    if (state.error != null) {
      return ErrorState(
        title: 'Savings goals unavailable',
        message: state.error!,
        onRetry: () => ref.read(savingsGoalListProvider(familyId).notifier).refresh(familyId: familyId),
      );
    }
    if (state.goals.isEmpty) {
      return EmptyState(
        icon: Icons.savings_outlined,
        title: 'No savings goals',
        message: 'Set a savings goal and track progress over time.',
        action: NeoContextButton(
          origin: 'Savings Goals',
          familyId: familyId,
          prompt: 'What savings goals should my family set?',
          label: 'Ask Neo',
        ),
      );
    }

    final notifier = ref.read(savingsGoalListProvider(familyId).notifier);
    final statuses = _uniqueValues(state.goals.map((g) => g.status));
    final priorities = _uniqueValues(state.goals.map((g) => g.priority));

    return CustomScrollView(
      slivers: [
        SliverToBoxAdapter(
          child: Padding(
            padding: const EdgeInsets.all(DesignTokens.spaceMd),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                _filterRow('Status', state.filterStatus, statuses, (v) => notifier.setFilter(status: v)),
                const SizedBox(height: DesignTokens.spaceSm),
                _filterRow('Priority', state.filterPriority, priorities, (v) => notifier.setFilter(priority: v)),
                const SizedBox(height: DesignTokens.spaceSm),
                NeoContextButton(
          origin: 'Savings Goals',
                  familyId: familyId,
                  prompt: 'How can I reach my savings goals faster?',
                  label: 'Reach goals faster',
                ),
              ],
            ),
          ),
        ),
        SliverList(
          delegate: SliverChildBuilderDelegate(
            (context, index) {
              final goal = state.goals[index];
              return _SavingsGoalTile(goal: goal);
            },
            childCount: state.goals.length,
          ),
        ),
        if (state.page < state.totalPages)
          SliverToBoxAdapter(
            child: Padding(
              padding: const EdgeInsets.all(DesignTokens.spaceMd),
              child: Center(
                child: state.isLoading
                    ? const CircularProgressIndicator()
                    : TextButton(
                        onPressed: () => notifier.loadMore(),
                        child: const Text('Load more'),
                      ),
              ),
            ),
          ),
      ],
    );
  }

  List<String> _uniqueValues(Iterable<String> values) {
    return values.where((v) => v.isNotEmpty).toSet().toList()..sort();
  }

  Widget _filterRow(String label, String? selected, List<String> options, void Function(String?) onSelected) {
    if (options.isEmpty) return const SizedBox.shrink();
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(label, style: DesignTokens.label(context)),
        const SizedBox(height: DesignTokens.spaceXs),
        Wrap(
          spacing: DesignTokens.spaceSm,
          children: [
            ChoiceChip(
              label: const Text('All'),
              selected: selected == null,
              onSelected: (_) => onSelected(null),
            ),
            ...options.map((o) => ChoiceChip(
                  label: Text(o),
                  selected: selected == o,
                  onSelected: (_) => onSelected(o),
                )),
          ],
        ),
      ],
    );
  }
}

class _SavingsGoalTile extends StatelessWidget {
  final SavingsGoalModel goal;

  const _SavingsGoalTile({required this.goal});

  @override
  Widget build(BuildContext context) {
    final progress = goal.progressPercentage.clamp(0, 100).toDouble();
    final color = progress >= 100 ? DesignTokens.success : (progress >= 50 ? DesignTokens.info : DesignTokens.warning);

    return Card(
      margin: const EdgeInsets.symmetric(
        horizontal: DesignTokens.spaceMd,
        vertical: DesignTokens.spaceXs,
      ),
      child: ListTile(
        contentPadding: const EdgeInsets.symmetric(
          horizontal: DesignTokens.spaceMd,
          vertical: DesignTokens.spaceSm,
        ),
        title: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(goal.name, style: DesignTokens.label(context)),
            Text(
              '${goal.progressPercentage.toStringAsFixed(0)}%',
              style: DesignTokens.label(context, color: color),
            ),
          ],
        ),
        subtitle: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Target: ${AmountFormatter.format(goal.targetAmount, goal.currency)}'),
            const SizedBox(height: DesignTokens.spaceSm),
            LinearProgressIndicator(
              value: progress / 100,
              color: color,
              backgroundColor: color.withAlpha((0.12 * 255).round()),
            ),
            const SizedBox(height: DesignTokens.spaceSm),
            Text(
              '${AmountFormatter.format(goal.currentAmount, goal.currency)} saved by ${goal.targetDate}',
              style: DesignTokens.bodySmall(context),
            ),
          ],
        ),
        onTap: () => context.push('/savings-goals/${goal.goalId}'),
      ),
    );
  }
}

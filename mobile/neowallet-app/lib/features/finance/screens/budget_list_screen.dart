import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import 'package:neowallet_app/core/components/brand_logos.dart';
import 'package:neowallet_app/core/components/neo_context_button.dart';
import 'package:neowallet_app/core/components/state_widgets.dart';
import 'package:neowallet_app/core/di/providers.dart';
import 'package:neowallet_app/core/theme/design_tokens.dart';
import 'package:neowallet_app/features/finance/providers/budget_list_provider.dart';
import 'package:neowallet_app/core/utils/amount_formatter.dart';
import 'package:neowallet_app/features/family/providers/selected_family_provider.dart';
import 'package:neowallet_app/features/finance/models/budget_model.dart';

class BudgetListScreen extends ConsumerStatefulWidget {
  final String? familyId;

  const BudgetListScreen({super.key, this.familyId});

  @override
  ConsumerState<BudgetListScreen> createState() => _BudgetListScreenState();
}

class _BudgetListScreenState extends ConsumerState<BudgetListScreen> {
  @override
  Widget build(BuildContext context) {
    final selected = ref.watch(selectedFamilyIdProvider);
    final familyId = widget.familyId ?? selected;
    final state = ref.watch(budgetListProvider(familyId));
    final notifier = ref.read(budgetListProvider(familyId).notifier);

    return Scaffold(
      appBar: const BrandAppBar(title: Text('Budgets')),
      floatingActionButton: FloatingActionButton(
        onPressed: () => context.push('/budgets/create', extra: familyId),
        child: const Icon(Icons.add),
      ),
      body: RefreshIndicator(
        onRefresh: () => notifier.load(familyId: familyId, refresh: true),
        child: _buildBody(state, familyId: familyId),
      ),
    );
  }

  Widget _buildBody(BudgetListState state, {required String? familyId}) {
    if (state.isLoading && state.budgets.isEmpty) {
      return const LoadingState(message: 'Loading budgets...');
    }
    if (state.error != null) {
      return ErrorState(
        title: 'Budgets unavailable',
        message: state.error!,
        onRetry: () => ref.read(budgetListProvider(familyId).notifier).load(familyId: familyId, refresh: true),
      );
    }
    if (state.budgets.isEmpty) {
      return EmptyState(
        icon: Icons.account_balance_wallet_outlined,
        title: 'No budgets',
        message: 'Create a budget to plan spending and track limits.',
        action: NeoContextButton(
          origin: 'Budgets',
          familyId: familyId,
          prompt: 'How much should I budget for my family?',
          label: 'Ask Neo',
        ),
      );
    }

    return CustomScrollView(
      slivers: [
        SliverToBoxAdapter(
          child: Padding(
            padding: const EdgeInsets.all(DesignTokens.spaceMd),
            child: NeoContextButton(
          origin: 'Budgets',
              familyId: familyId,
              prompt: 'How can I stay within my budgets?',
              label: 'How can I stay within budget?',
            ),
          ),
        ),
        SliverList(
          delegate: SliverChildBuilderDelegate(
            (context, index) {
              final b = state.budgets[index];
              return _BudgetTile(budget: b);
            },
            childCount: state.budgets.length,
          ),
        ),
        if (state.hasMore)
          SliverToBoxAdapter(
            child: Padding(
              padding: const EdgeInsets.all(DesignTokens.spaceMd),
              child: Center(
                child: state.isLoading
                    ? const CircularProgressIndicator()
                    : TextButton(
                        onPressed: () => ref.read(budgetListProvider(familyId).notifier).load(familyId: familyId),
                        child: const Text('Load more'),
                      ),
              ),
            ),
          ),
      ],
    );
  }
}

class _BudgetTile extends StatelessWidget {
  final BudgetModel budget;

  const _BudgetTile({required this.budget});

  @override
  Widget build(BuildContext context) {
    final utilization = budget.utilizationPercentage.clamp(0, 100).toDouble();
    final isOver = budget.utilizationPercentage > 100;
    final color = isOver ? DesignTokens.error : (budget.utilizationPercentage >= 80 ? DesignTokens.warning : DesignTokens.success);

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
            Text(budget.name, style: DesignTokens.label(context)),
            Text(
              '${budget.utilizationPercentage.toStringAsFixed(0)}%',
              style: DesignTokens.label(context, color: color),
            ),
          ],
        ),
        subtitle: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('${budget.period} \u2022 ${AmountFormatter.format(budget.totalLimit, budget.currency)}'),
            const SizedBox(height: DesignTokens.spaceSm),
            LinearProgressIndicator(
              value: utilization / 100,
              color: color,
              backgroundColor: color.withAlpha((0.12 * 255).round()),
            ),
            const SizedBox(height: DesignTokens.spaceSm),
            Text(
              'Spent: ${AmountFormatter.format(budget.totalSpent, budget.currency)}',
              style: DesignTokens.bodySmall(context),
            ),
          ],
        ),
        onTap: () => context.push('/budgets/${budget.budgetId}'),
      ),
    );
  }
}

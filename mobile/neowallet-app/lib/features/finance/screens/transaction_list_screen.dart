import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import 'package:neowallet_app/core/components/brand_logos.dart';
import 'package:neowallet_app/core/components/neo_context_button.dart';
import 'package:neowallet_app/core/components/state_widgets.dart';
import 'package:neowallet_app/core/di/providers.dart';
import 'package:neowallet_app/core/theme/design_tokens.dart';
import 'package:neowallet_app/core/utils/amount_formatter.dart';
import 'package:neowallet_app/features/family/providers/selected_family_provider.dart';
import 'package:neowallet_app/features/finance/models/transaction_model.dart';
import 'package:neowallet_app/features/finance/providers/transaction_list_provider.dart';

class TransactionListScreen extends ConsumerStatefulWidget {
  final String? familyId;

  const TransactionListScreen({super.key, this.familyId});

  @override
  ConsumerState<TransactionListScreen> createState() => _TransactionListScreenState();
}

class _TransactionListScreenState extends ConsumerState<TransactionListScreen> {
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    final selected = ref.watch(selectedFamilyIdProvider);
    final familyId = widget.familyId ?? selected;
    final state = ref.watch(transactionListProvider(familyId));

    return Scaffold(
      appBar: const BrandAppBar(title: Text('Transactions')),
      body: RefreshIndicator(
        onRefresh: () => ref.read(transactionListProvider(familyId).notifier).refresh(familyId: familyId),
        child: _buildBody(state, familyId: familyId),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => context.push('/transactions/create', extra: familyId),
        child: const Icon(Icons.add),
      ),
    );
  }

  Widget _buildBody(TransactionListState state, {required String? familyId}) {
    if (state.isLoading && state.transactions.isEmpty) {
      return const LoadingState(message: 'Loading transactions...');
    }
    if (state.error != null) {
      return ErrorState(
        title: 'Transactions unavailable',
        message: state.error!,
        onRetry: () => ref.read(transactionListProvider(familyId).notifier).refresh(familyId: familyId),
      );
    }
    if (state.transactions.isEmpty) {
      return EmptyState(
        icon: Icons.swap_horiz_outlined,
        title: 'No transactions',
        message: 'Record a transaction to start tracking your planning.',
        action: NeoContextButton(
          origin: 'Transactions',
          familyId: familyId,
          prompt: 'What transactions should I record for my family?',
          label: 'Ask Neo',
        ),
      );
    }

    final notifier = ref.read(transactionListProvider(familyId).notifier);
    final types = _uniqueValues(state.transactions.map((t) => t.type));
    final categories = _uniqueValues(state.transactions.map((t) => t.category));
    final statuses = _uniqueValues(state.transactions.map((t) => t.status));

    return CustomScrollView(
      slivers: [
        SliverToBoxAdapter(
          child: Padding(
            padding: const EdgeInsets.all(DesignTokens.spaceMd),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                _filterRow('Type', state.filterType, types, (v) => notifier.setFilter(type: v)),
                const SizedBox(height: DesignTokens.spaceSm),
                _filterRow('Category', state.filterCategory, categories, (v) => notifier.setFilter(category: v)),
                const SizedBox(height: DesignTokens.spaceSm),
                _filterRow('Status', state.filterStatus, statuses, (v) => notifier.setFilter(status: v)),
                const SizedBox(height: DesignTokens.spaceSm),
                NeoContextButton(
          origin: 'Transactions',
                  familyId: familyId,
                  prompt: 'Is this spending unusual?',
                  label: 'Is this spending unusual?',
                ),
              ],
            ),
          ),
        ),
        SliverList(
          delegate: SliverChildBuilderDelegate(
            (context, index) {
              final tx = state.transactions[index];
              return _TransactionTile(tx: tx, familyId: familyId);
            },
            childCount: state.transactions.length,
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
    final set = <String>{};
    for (final v in values) {
      if (v.isNotEmpty) set.add(v);
    }
    return set.toList()..sort();
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

class _TransactionTile extends StatelessWidget {
  final TransactionModel tx;
  final String? familyId;

  const _TransactionTile({required this.tx, this.familyId});

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.symmetric(
        horizontal: DesignTokens.spaceMd,
        vertical: DesignTokens.spaceXs,
      ),
      child: ListTile(
        leading: CircleAvatar(
          backgroundColor: _typeColor(tx.type).withAlpha((0.12 * 255).round()),
          child: Icon(_typeIcon(tx.type), color: _typeColor(tx.type), size: 18),
        ),
        title: Text('${tx.category} \u2022 ${tx.type}'),
        subtitle: Text('${tx.transactionDate} \u2022 ${tx.status}'),
        trailing: Text(
          AmountFormatter.format(tx.amount, tx.currency),
          style: DesignTokens.label(context),
        ),
        onTap: () => context.push('/transactions/${tx.transactionId}', extra: familyId),
      ),
    );
  }

  IconData _typeIcon(String type) {
    switch (type) {
      case 'INCOME':
        return Icons.arrow_downward;
      case 'EXPENSE':
        return Icons.arrow_upward;
      case 'REFUND':
        return Icons.replay;
      case 'TRANSFER_RECORD':
        return Icons.sync_alt;
      default:
        return Icons.swap_horiz;
    }
  }

  Color _typeColor(String type) {
    switch (type) {
      case 'INCOME':
        return Colors.green;
      case 'EXPENSE':
        return Colors.red;
      case 'REFUND':
        return Colors.orange;
      case 'TRANSFER_RECORD':
        return Colors.blue;
      default:
        return Colors.grey;
    }
  }
}

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import 'package:neowallet_app/core/components/brand_logos.dart';
import 'package:neowallet_app/core/components/neo_context_button.dart';
import 'package:neowallet_app/core/components/state_widgets.dart';
import 'package:neowallet_app/core/di/providers.dart';
import 'package:neowallet_app/core/theme/design_tokens.dart';
import 'package:neowallet_app/features/finance/providers/bill_list_provider.dart';
import 'package:neowallet_app/core/utils/amount_formatter.dart';
import 'package:neowallet_app/features/family/providers/selected_family_provider.dart';
import 'package:neowallet_app/features/finance/models/bill_model.dart';

class BillListScreen extends ConsumerStatefulWidget {
  final String? familyId;

  const BillListScreen({super.key, this.familyId});

  @override
  ConsumerState<BillListScreen> createState() => _BillListScreenState();
}

class _BillListScreenState extends ConsumerState<BillListScreen> {
  @override
  Widget build(BuildContext context) {
    final selected = ref.watch(selectedFamilyIdProvider);
    final familyId = widget.familyId ?? selected;
    final state = ref.watch(billListProvider(familyId));

    return Scaffold(
      appBar: const BrandAppBar(title: Text('Bills')),
      body: RefreshIndicator(
        onRefresh: () => ref.read(billListProvider(familyId).notifier).refresh(familyId: familyId),
        child: _buildBody(state, familyId: familyId),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => context.push('/bills/create', extra: familyId),
        child: const Icon(Icons.add),
      ),
    );
  }

  Widget _buildBody(BillListState state, {required String? familyId}) {
    if (state.isLoading && state.bills.isEmpty) {
      return const LoadingState(message: 'Loading bills...');
    }
    if (state.error != null) {
      return ErrorState(
        title: 'Bills unavailable',
        message: state.error!,
        onRetry: () => ref.read(billListProvider(familyId).notifier).refresh(familyId: familyId),
      );
    }
    if (state.bills.isEmpty) {
      return EmptyState(
        icon: Icons.receipt_long_outlined,
        title: 'No bills',
        message: 'Add upcoming and recurring bills to track due dates.',
        action: NeoContextButton(
          origin: 'Bills',
          familyId: familyId,
          prompt: 'What bills should my family plan for?',
          label: 'Ask Neo',
        ),
      );
    }

    final notifier = ref.read(billListProvider(familyId).notifier);
    final statuses = _uniqueValues(state.bills.map((b) => b.status));
    final categories = _uniqueValues(state.bills.map((b) => b.category ?? 'Uncategorized'));

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
                _filterRow('Category', state.filterCategory, categories, (v) => notifier.setFilter(category: v)),
                const SizedBox(height: DesignTokens.spaceSm),
                NeoContextButton(
          origin: 'Bills',
                  familyId: familyId,
                  prompt: 'Which bills are coming due soon?',
                  label: 'Upcoming bills',
                ),
              ],
            ),
          ),
        ),
        SliverList(
          delegate: SliverChildBuilderDelegate(
            (context, index) {
              final bill = state.bills[index];
              return _BillTile(bill: bill);
            },
            childCount: state.bills.length,
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

class _BillTile extends StatelessWidget {
  final BillModel bill;

  const _BillTile({required this.bill});

  @override
  Widget build(BuildContext context) {
    final color = _statusColor(bill.status);
    final isRecurring = bill.isRecurring;

    return Card(
      margin: const EdgeInsets.symmetric(
        horizontal: DesignTokens.spaceMd,
        vertical: DesignTokens.spaceXs,
      ),
      child: ListTile(
        leading: CircleAvatar(
          backgroundColor: color.withAlpha((0.12 * 255).round()),
          child: Icon(
            isRecurring ? Icons.repeat : Icons.receipt,
            color: color,
            size: 18,
          ),
        ),
        title: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(bill.name, style: DesignTokens.label(context)),
            Chip(
              label: Text(bill.status),
              backgroundColor: color,
              labelStyle: const TextStyle(color: Colors.white),
              padding: EdgeInsets.zero,
            ),
          ],
        ),
        subtitle: Text('${bill.dueDate} \u2022 ${AmountFormatter.format(bill.amount, bill.currency)} ${bill.isRecurring ? "\u2022 ${bill.recurringPeriod}" : ""}'),
        onTap: () => context.push('/bills/${bill.billId}'),
      ),
    );
  }

  Color _statusColor(String status) {
    switch (status) {
      case 'PAID':
        return DesignTokens.success;
      case 'OVERDUE':
        return DesignTokens.error;
      case 'PENDING':
      default:
        return DesignTokens.warning;
    }
  }
}

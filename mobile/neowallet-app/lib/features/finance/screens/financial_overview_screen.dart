import 'package:flutter/material.dart';

import '../../../core/components/brand_logos.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:neowallet_app/core/di/providers.dart';
import 'package:neowallet_app/features/finance/models/financial_overview_model.dart';

class FinancialOverviewScreen extends ConsumerStatefulWidget {
  final String? familyId;

  const FinancialOverviewScreen({super.key, this.familyId});

  @override
  ConsumerState<FinancialOverviewScreen> createState() => _FinancialOverviewScreenState();
}

class _FinancialOverviewScreenState extends ConsumerState<FinancialOverviewScreen> {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      ref.read(financialOverviewProvider(widget.familyId).notifier).load(familyId: widget.familyId);
    });
  }

  @override
  Widget build(BuildContext context) {
    final state = ref.watch(financialOverviewProvider(widget.familyId));

    return Scaffold(
      appBar: const BrandAppBar(title: Text('Financial Overview')),
      body: state.isLoading
          ? const Center(child: CircularProgressIndicator())
          : state.error != null
              ? _buildError(state.error!)
              : state.overview == null
                  ? const Center(child: Text('No financial data available'))
                  : _buildOverview(state.overview!, state.summary),
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
              onPressed: () => ref.read(financialOverviewProvider(widget.familyId).notifier).load(familyId: widget.familyId),
              child: const Text('Retry'),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildOverview(FinancialOverviewModel overview, FinancialSummaryModel? summary) {
    return ListView(
      padding: const EdgeInsets.all(16),
      children: [
        if (overview.disclaimer.isNotEmpty)
          Card(
            color: Theme.of(context).colorScheme.primaryContainer,
            child: Padding(
              padding: const EdgeInsets.all(12),
              child: Text(
                overview.disclaimer,
                style: TextStyle(color: Theme.of(context).colorScheme.onPrimaryContainer),
              ),
            ),
          ),
        const SizedBox(height: 16),
        _sectionTitle('Planning Values'),
        _tile('Planning Income', overview.planningIncome, overview.currency),
        _tile('Mandatory Commitments', overview.mandatoryCommitments, overview.currency),
        _tile('Essential Allocation', overview.essentialAllocation, overview.currency),
        _tile('Savings Allocation', overview.savingsAllocation, overview.currency),
        _tile('Emergency Allocation', overview.emergencyAllocation, overview.currency),
        _tile('Discretionary Planning', overview.discretionaryPlanning, overview.currency),
        const SizedBox(height: 16),
        _sectionTitle('Actual & Pending Records'),
        _tile('Actual Transactions', overview.actualTransactions, overview.currency),
        _tile('Pending Payments', overview.pendingPayments, overview.currency),
        _tile('Committed Amount', overview.committedAmount, overview.currency),
        const SizedBox(height: 16),
        _sectionTitle('Available Financial Capacity'),
        _tile('Capacity', overview.availableFinancialCapacity, overview.currency),
        if (summary != null) ...[
          const SizedBox(height: 16),
          _sectionTitle('Summary'),
          _tile('Total Income', summary.totalIncome, summary.currency),
          _tile('Total Expenses', summary.totalExpenses, summary.currency),
          _tile('Total Savings', summary.totalSavings, summary.currency),
          _tile('Budget Adherence', summary.budgetAdherence, '%'),
        ],
      ],
    );
  }

  Widget _sectionTitle(String title) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Text(title, style: Theme.of(context).textTheme.titleMedium),
    );
  }

  Widget _tile(String label, num value, String suffix) {
    return ListTile(
      title: Text(label),
      trailing: Text('${value.toStringAsFixed(2)} $suffix'),
      contentPadding: EdgeInsets.zero,
    );
  }
}

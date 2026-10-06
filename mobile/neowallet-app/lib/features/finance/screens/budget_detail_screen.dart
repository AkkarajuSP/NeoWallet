import 'package:flutter/material.dart';

import '../../../core/components/brand_logos.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../models/budget_model.dart';
import '../../../core/di/providers.dart';

class BudgetDetailScreen extends ConsumerStatefulWidget {
  final String budgetId;

  const BudgetDetailScreen({super.key, required this.budgetId});

  @override
  ConsumerState<BudgetDetailScreen> createState() => _BudgetDetailScreenState();
}

class _BudgetDetailScreenState extends ConsumerState<BudgetDetailScreen> {
  BudgetModel? _budget;
  Map<String, dynamic>? _utilization;
  Map<String, dynamic>? _forecast;
  bool _loading = true;
  String? _error;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    final service = ref.read(budgetServiceProvider);
    try {
      final budget = await service.get(widget.budgetId);
      final utilization = await service.getUtilization(widget.budgetId);
      final forecast = await service.getForecast(widget.budgetId);
      if (mounted) {
        setState(() {
          _budget = budget;
          _utilization = utilization;
          _forecast = forecast;
          _loading = false;
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() {
          _error = e.toString();
          _loading = false;
        });
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_loading) return const Scaffold(body: Center(child: CircularProgressIndicator()));
    if (_error != null) return Scaffold(body: Center(child: Text('Error: $_error')));

    final budget = _budget!;
    final categories = budget.categories;

    return Scaffold(
      appBar: BrandAppBar(title: Text(budget.name)),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Period: ${budget.period}', style: Theme.of(context).textTheme.titleMedium),
            Text('Currency: ${budget.currency}'),
            Text('Total Limit: ${budget.currency} ${budget.totalLimit.toStringAsFixed(2)}'),
            Text('Total Spent: ${budget.currency} ${budget.totalSpent.toStringAsFixed(2)}'),
            Text('Utilization: ${budget.utilizationPercentage.toStringAsFixed(2)}%'),
            const SizedBox(height: 16),
            Text('Categories', style: Theme.of(context).textTheme.titleLarge),
            ...categories.map((c) => ListTile(
                  title: Text(c.category),
                  subtitle: Text('Limit: ${c.limit.toStringAsFixed(2)}'),
                  trailing: Text(c.priority),
                )),
            if (_utilization != null) ...[
              const SizedBox(height: 16),
              Text('Utilization', style: Theme.of(context).textTheme.titleLarge),
              Text('Overall Status: ${_utilization!['overallStatus']}'),
              Text('Total Spent: ${_utilization!['totalSpent'].toStringAsFixed(2)}'),
            ],
            if (_forecast != null) ...[
              const SizedBox(height: 16),
              Text('Forecast', style: Theme.of(context).textTheme.titleLarge),
              Text('Confidence: ${_forecast!['forecast']['confidence']}'),
              Text('Projected Spending: ${_forecast!['forecast']['projectedSpending'].toStringAsFixed(2)}'),
            ],
          ],
        ),
      ),
    );
  }
}

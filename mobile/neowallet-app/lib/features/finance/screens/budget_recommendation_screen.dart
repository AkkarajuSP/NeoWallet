import 'package:flutter/material.dart';

import '../../../core/components/brand_logos.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/di/providers.dart';

class BudgetRecommendationScreen extends ConsumerStatefulWidget {
  final String? familyId;

  const BudgetRecommendationScreen({super.key, this.familyId});

  @override
  ConsumerState<BudgetRecommendationScreen> createState() => _BudgetRecommendationScreenState();
}

class _BudgetRecommendationScreenState extends ConsumerState<BudgetRecommendationScreen> {
  final _periodController = TextEditingController();
  Map<String, dynamic>? _recommendation;
  bool _loading = false;
  String? _error;

  Future<void> _fetch() async {
    if (_periodController.text.isEmpty) return;
    setState(() => _loading = true);
    try {
      final service = ref.read(budgetServiceProvider);
      final result = await service.getRecommendation(period: _periodController.text, familyId: widget.familyId);
      setState(() {
        _recommendation = result;
        _loading = false;
        _error = null;
      });
    } catch (e) {
      setState(() {
        _error = e.toString();
        _loading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final rec = _recommendation;
    final allocation = rec?['recommendedAllocation'] as Map<String, dynamic>?;
    final categories = (rec?['categories'] as List<dynamic>?) ?? [];

    return Scaffold(
      appBar: const BrandAppBar(title: Text('Budget Recommendation')),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            TextField(
              controller: _periodController,
              decoration: const InputDecoration(labelText: 'Period (YYYY-MM)'),
            ),
            const SizedBox(height: 8),
            ElevatedButton(
              onPressed: _loading ? null : _fetch,
              child: _loading ? const CircularProgressIndicator() : const Text('Get Recommendation'),
            ),
            if (_error != null) Text(_error!, style: const TextStyle(color: Colors.red)),
            const SizedBox(height: 16),
            if (rec != null) ...[
              Text('Confidence: ${rec['confidence']}'),
              Text('Historical Months: ${rec['historicalMonthsUsed']}'),
              if (allocation != null) ...[
                Text('Planning Income: ${allocation['planningIncome']}'),
                Text('Essential: ${allocation['essentialAllocation']}'),
                Text('Variable: ${allocation['variableAllocation']}'),
                Text('Savings: ${allocation['savingsAllocation']}'),
                Text('Emergency: ${allocation['emergencyAllocation']}'),
                Text('Discretionary: ${allocation['discretionaryPlanning']}'),
              ],
              const SizedBox(height: 8),
              Text('Categories', style: Theme.of(context).textTheme.titleLarge),
              Expanded(
                child: ListView.builder(
                  itemCount: categories.length,
                  itemBuilder: (context, index) {
                    final c = categories[index] as Map<String, dynamic>;
                    return ListTile(
                      title: Text(c['category'] as String),
                      subtitle: Text('Recommended: ${c['recommendedLimit']}'),
                      trailing: Text(c['priority'] as String),
                    );
                  },
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }
}

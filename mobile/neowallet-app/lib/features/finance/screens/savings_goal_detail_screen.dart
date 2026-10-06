import 'package:flutter/material.dart';

import '../../../core/components/brand_logos.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../models/savings_goal_model.dart';
import '../widgets/add_contribution_dialog.dart';
import '../../../../core/di/providers.dart';

class SavingsGoalDetailScreen extends ConsumerStatefulWidget {
  final String goalId;

  const SavingsGoalDetailScreen({super.key, required this.goalId});

  @override
  ConsumerState<SavingsGoalDetailScreen> createState() => _SavingsGoalDetailScreenState();
}

class _SavingsGoalDetailScreenState extends ConsumerState<SavingsGoalDetailScreen> {
  SavingsGoalModel? _goal;
  Map<String, dynamic>? _progress;
  Map<String, dynamic>? _forecast;
  bool _isLoading = true;
  String? _error;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    try {
      final service = ref.read(savingsGoalServiceProvider);
      final goal = await service.get(widget.goalId);
      final progress = await service.getProgress(widget.goalId);
      final forecast = await service.getForecast(widget.goalId);
      setState(() {
        _goal = goal;
        _progress = progress;
        _forecast = forecast;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _error = e.toString();
        _isLoading = false;
      });
    }
  }

  Future<void> _delete() async {
    try {
      await ref.read(savingsGoalServiceProvider).delete(widget.goalId);
      if (mounted) context.pop();
    } catch (e) {
      setState(() => _error = e.toString());
    }
  }

  Future<void> _addContribution() async {
    if (_goal == null) return;
    await showDialog(
      context: context,
      builder: (context) => AddContributionDialog(
        currency: _goal!.currency,
        onSave: (data) => ref.read(savingsGoalServiceProvider).contribute(
          widget.goalId,
          data,
          familyId: _goal!.familyId,
        ),
        onSuccess: _load,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) return const Scaffold(body: Center(child: CircularProgressIndicator()));
    if (_error != null) return Scaffold(body: Center(child: Text('Error: $_error')));
    if (_goal == null) return const Scaffold(body: Center(child: Text('Goal not found')));

    final goal = _goal!;
    final progress = _progress ?? {};
    final forecast = _forecast ?? {};

    return Scaffold(
      appBar: BrandAppBar(
        title: Text(goal.name),
        actions: [
          IconButton(
            icon: const Icon(Icons.edit),
            onPressed: () => context.push('/savings-goals/${goal.goalId}'),
          ),
          IconButton(
            icon: const Icon(Icons.delete),
            onPressed: _delete,
          ),
        ],
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Target: ${goal.targetAmount} ${goal.currency}'),
            Text('Saved: ${goal.currentAmount} ${goal.currency}'),
            Text('Progress: ${goal.progressPercentage.toStringAsFixed(2)}%'),
            Text('Status: ${goal.status}'),
            Text('Priority: ${goal.priority}'),
            Text('Target Date: ${goal.targetDate}'),
            const SizedBox(height: 16),
            if (progress.isNotEmpty) ...[
              const Text('Progress', style: TextStyle(fontWeight: FontWeight.bold)),
              Text('Remaining: ${progress['remainingAmount']}'),
              Text('Required Monthly: ${progress['requiredMonthlyContribution']}'),
              Text('On Track: ${progress['onTrack'] == true ? 'Yes' : 'No'}'),
              Text('Status: ${progress['status']}'),
            ],
            const SizedBox(height: 16),
            if (forecast.isNotEmpty && forecast['forecast'] != null) ...[
              const Text('Forecast', style: TextStyle(fontWeight: FontWeight.bold)),
              Text('Confidence: ${forecast['forecast']['confidence']}'),
              Text('Months to Completion: ${forecast['forecast']['monthsToCompletion']}'),
            ],
            const SizedBox(height: 16),
            FilledButton.icon(
              onPressed: _addContribution,
              icon: const Icon(Icons.add),
              label: const Text('Add Contribution'),
            ),
          ],
        ),
      ),
    );
  }
}

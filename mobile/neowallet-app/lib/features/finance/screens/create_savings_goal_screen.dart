import 'package:flutter/material.dart';

import '../../../core/components/brand_logos.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../core/di/providers.dart';

class CreateSavingsGoalScreen extends ConsumerStatefulWidget {
  final String? goalId;

  const CreateSavingsGoalScreen({super.key, this.goalId});

  @override
  ConsumerState<CreateSavingsGoalScreen> createState() => _CreateSavingsGoalScreenState();
}

class _CreateSavingsGoalScreenState extends ConsumerState<CreateSavingsGoalScreen> {
  final _nameController = TextEditingController();
  final _targetController = TextEditingController();
  final _currentController = TextEditingController();
  DateTime? _targetDate;
  String _priority = 'MEDIUM';
  bool _isLoading = false;
  String? _error;

  @override
  void initState() {
    super.initState();
    if (widget.goalId != null) {
      _loadGoal();
    }
  }

  Future<void> _loadGoal() async {
    try {
      final goal = await ref.read(savingsGoalServiceProvider).get(widget.goalId!);
      _nameController.text = goal.name;
      _targetController.text = goal.targetAmount.toString();
      _currentController.text = goal.currentAmount.toString();
      _targetDate = DateTime.parse(goal.targetDate);
      _priority = goal.priority;
    } catch (e) {
      setState(() => _error = e.toString());
    }
  }

  Future<void> _save() async {
    setState(() => _isLoading = true);
    try {
      final data = {
        'name': _nameController.text,
        'targetAmount': num.tryParse(_targetController.text) ?? 0,
        if (_currentController.text.isNotEmpty)
          'currentAmount': num.tryParse(_currentController.text) ?? 0,
        'targetDate': _targetDate != null
            ? '${_targetDate!.year.toString().padLeft(4, '0')}-${_targetDate!.month.toString().padLeft(2, '0')}-${_targetDate!.day.toString().padLeft(2, '0')}'
            : null,
        'priority': _priority,
      };

      if (widget.goalId != null) {
        await ref.read(savingsGoalServiceProvider).update(widget.goalId!, data);
      } else {
        await ref.read(savingsGoalServiceProvider).create(data: data);
      }
      if (mounted) context.pop();
    } catch (e) {
      setState(() => _error = e.toString());
    } finally {
      setState(() => _isLoading = false);
    }
  }

  Future<void> _pickDate() async {
    final now = DateTime.now();
    final picked = await showDatePicker(
      context: context,
      initialDate: _targetDate ?? now.add(const Duration(days: 365)),
      firstDate: now,
      lastDate: now.add(const Duration(days: 3650)),
    );
    if (picked != null) {
      setState(() => _targetDate = picked);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: BrandAppBar(title: Text(widget.goalId != null ? 'Edit Goal' : 'Create Savings Goal')),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            TextField(
              controller: _nameController,
              decoration: const InputDecoration(labelText: 'Goal Name'),
            ),
            TextField(
              controller: _targetController,
              keyboardType: TextInputType.number,
              decoration: const InputDecoration(labelText: 'Target Amount'),
            ),
            TextField(
              controller: _currentController,
              keyboardType: TextInputType.number,
              decoration: const InputDecoration(labelText: 'Current Amount (optional)'),
            ),
            ListTile(
              title: const Text('Target Date'),
              subtitle: Text(_targetDate != null
                  ? '${_targetDate!.year}-${_targetDate!.month.toString().padLeft(2, '0')}-${_targetDate!.day.toString().padLeft(2, '0')}'
                  : 'Select a date'),
              trailing: const Icon(Icons.calendar_today),
              onTap: _pickDate,
            ),
            DropdownButtonFormField<String>(
              value: _priority,
              items: const [
                DropdownMenuItem(value: 'HIGH', child: Text('High')),
                DropdownMenuItem(value: 'MEDIUM', child: Text('Medium')),
                DropdownMenuItem(value: 'LOW', child: Text('Low')),
              ],
              onChanged: (v) => setState(() => _priority = v ?? 'MEDIUM'),
              decoration: const InputDecoration(labelText: 'Priority'),
            ),
            if (_error != null) Text('Error: $_error', style: const TextStyle(color: Colors.red)),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: _isLoading ? null : _save,
              child: _isLoading ? const CircularProgressIndicator() : const Text('Save'),
            ),
          ],
        ),
      ),
    );
  }

  @override
  void dispose() {
    _nameController.dispose();
    _targetController.dispose();
    _currentController.dispose();
    super.dispose();
  }
}

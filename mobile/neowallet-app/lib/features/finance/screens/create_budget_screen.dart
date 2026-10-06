import 'package:flutter/material.dart';

import '../../../core/components/brand_logos.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/di/providers.dart';
import '../models/budget_model.dart';

class CreateBudgetScreen extends ConsumerStatefulWidget {
  final String? familyId;

  const CreateBudgetScreen({super.key, this.familyId});

  @override
  ConsumerState<CreateBudgetScreen> createState() => _CreateBudgetScreenState();
}

class _CreateBudgetScreenState extends ConsumerState<CreateBudgetScreen> {
  final _nameController = TextEditingController();
  final _periodController = TextEditingController();
  final _currencyController = TextEditingController(text: 'USD');
  final List<BudgetCategoryModel> _categories = [];
  bool _loading = false;
  String? _error;

  void _addCategory() {
    setState(() {
      _categories.add(const BudgetCategoryModel(category: '', limit: 0, priority: 'VARIABLE'));
    });
  }

  Future<void> _save() async {
    setState(() => _error = null);
    final data = {
      'name': _nameController.text,
      'period': _periodController.text,
      'currency': _currencyController.text,
      'categories': _categories.where((c) => c.category.isNotEmpty && c.limit > 0).map((c) => c.toJson()).toList(),
    };

    if (data['categories'] == null || (data['categories'] as List).isEmpty) {
      setState(() => _error = 'Add at least one category');
      return;
    }

    setState(() => _loading = true);
    try {
      final service = ref.read(budgetServiceProvider);
      await service.create(data: data, familyId: widget.familyId);
      if (mounted) {
        context.pop();
      }
    } catch (e) {
      setState(() {
        _error = e.toString();
        _loading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const BrandAppBar(title: Text('Create Budget')),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: ListView(
          children: [
            TextField(
              controller: _nameController,
              decoration: const InputDecoration(labelText: 'Budget Name'),
            ),
            TextField(
              controller: _periodController,
              decoration: const InputDecoration(labelText: 'Period (YYYY-MM)'),
            ),
            TextField(
              controller: _currencyController,
              decoration: const InputDecoration(labelText: 'Currency'),
            ),
            const SizedBox(height: 16),
            Text('Categories', style: Theme.of(context).textTheme.titleLarge),
            ..._categories.asMap().entries.map((entry) {
              final i = entry.key;
              final c = entry.value;
              return Row(
                children: [
                  Expanded(
                    flex: 2,
                    child: TextField(
                      decoration: const InputDecoration(labelText: 'Category'),
                      onChanged: (v) => setState(() => _categories[i] = BudgetCategoryModel(
                        category: v,
                        limit: c.limit,
                        priority: c.priority,
                      )),
                    ),
                  ),
                  const SizedBox(width: 8),
                  Expanded(
                    child: TextField(
                      decoration: const InputDecoration(labelText: 'Limit'),
                      keyboardType: TextInputType.number,
                      onChanged: (v) => setState(() => _categories[i] = BudgetCategoryModel(
                        category: c.category,
                        limit: num.tryParse(v) ?? 0,
                        priority: c.priority,
                      )),
                    ),
                  ),
                  const SizedBox(width: 8),
                  Expanded(
                    child: DropdownButtonFormField<String>(
                      value: c.priority,
                      items: const ['ESSENTIAL', 'VARIABLE', 'DISCRETIONARY']
                          .map((p) => DropdownMenuItem(value: p, child: Text(p)))
                          .toList(),
                      onChanged: (v) => setState(() => _categories[i] = BudgetCategoryModel(
                        category: c.category,
                        limit: c.limit,
                        priority: v ?? 'VARIABLE',
                      )),
                    ),
                  ),
                ],
              );
            }),
            TextButton.icon(
              onPressed: _addCategory,
              icon: const Icon(Icons.add),
              label: const Text('Add Category'),
            ),
            if (_error != null) Text(_error!, style: const TextStyle(color: Colors.red)),
            ElevatedButton(
              onPressed: _loading ? null : _save,
              child: _loading ? const CircularProgressIndicator() : const Text('Save'),
            ),
          ],
        ),
      ),
    );
  }
}

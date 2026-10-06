import 'package:flutter/material.dart';

import '../../../core/components/brand_logos.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../core/di/providers.dart';

class CreateBillScreen extends ConsumerStatefulWidget {
  final String? billId;

  const CreateBillScreen({super.key, this.billId});

  @override
  ConsumerState<CreateBillScreen> createState() => _CreateBillScreenState();
}

class _CreateBillScreenState extends ConsumerState<CreateBillScreen> {
  final _nameController = TextEditingController();
  final _amountController = TextEditingController();
  final _categoryController = TextEditingController();
  final _vendorController = TextEditingController();
  final _notesController = TextEditingController();
  DateTime? _dueDate;
  bool _isRecurring = false;
  String? _recurringPeriod;
  bool _isLoading = false;
  String? _error;

  @override
  void initState() {
    super.initState();
    if (widget.billId != null) {
      _loadBill();
    }
  }

  Future<void> _loadBill() async {
    setState(() => _isLoading = true);
    try {
      final bill = await ref.read(billServiceProvider).get(widget.billId!);
      _nameController.text = bill.name;
      _amountController.text = bill.amount.toString();
      _categoryController.text = bill.category ?? '';
      _vendorController.text = bill.vendor ?? '';
      _notesController.text = bill.notes ?? '';
      _dueDate = DateTime.tryParse(bill.dueDate);
      _isRecurring = bill.isRecurring;
      _recurringPeriod = bill.recurringPeriod;
    } catch (e) {
      setState(() => _error = e.toString());
    } finally {
      setState(() => _isLoading = false);
    }
  }

  Future<void> _save() async {
    setState(() => _isLoading = true);
    try {
      final data = {
        'name': _nameController.text,
        'amount': num.tryParse(_amountController.text) ?? 0,
        'currency': 'USD',
        'dueDate': _dueDate != null
            ? '${_dueDate!.year.toString().padLeft(4, '0')}-${_dueDate!.month.toString().padLeft(2, '0')}-${_dueDate!.day.toString().padLeft(2, '0')}'
            : null,
        if (_categoryController.text.isNotEmpty) 'category': _categoryController.text,
        'isRecurring': _isRecurring,
        if (_isRecurring && _recurringPeriod != null) 'recurringPeriod': _recurringPeriod,
        if (_vendorController.text.isNotEmpty) 'vendor': _vendorController.text,
        if (_notesController.text.isNotEmpty) 'notes': _notesController.text,
      };

      if (widget.billId != null) {
        await ref.read(billServiceProvider).update(widget.billId!, data);
      } else {
        await ref.read(billServiceProvider).create(data: data);
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
      initialDate: _dueDate ?? now,
      firstDate: now.subtract(const Duration(days: 365)),
      lastDate: now.add(const Duration(days: 3650)),
    );
    if (picked != null) {
      setState(() => _dueDate = picked);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: BrandAppBar(title: Text(widget.billId != null ? 'Edit Bill' : 'Create Bill')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            TextField(
              controller: _nameController,
              decoration: const InputDecoration(labelText: 'Bill Name'),
            ),
            TextField(
              controller: _amountController,
              keyboardType: TextInputType.number,
              decoration: const InputDecoration(labelText: 'Amount'),
            ),
            ListTile(
              title: const Text('Due Date'),
              subtitle: Text(_dueDate != null
                  ? '${_dueDate!.year}-${_dueDate!.month.toString().padLeft(2, '0')}-${_dueDate!.day.toString().padLeft(2, '0')}'
                  : 'Select a date'),
              trailing: const Icon(Icons.calendar_today),
              onTap: _pickDate,
            ),
            TextField(
              controller: _categoryController,
              decoration: const InputDecoration(labelText: 'Category'),
            ),
            TextField(
              controller: _vendorController,
              decoration: const InputDecoration(labelText: 'Vendor'),
            ),
            TextField(
              controller: _notesController,
              decoration: const InputDecoration(labelText: 'Notes'),
            ),
            SwitchListTile(
              title: const Text('Recurring'),
              value: _isRecurring,
              onChanged: (v) => setState(() => _isRecurring = v),
            ),
            if (_isRecurring)
              DropdownButtonFormField<String>(
                value: _recurringPeriod,
                decoration: const InputDecoration(labelText: 'Recurring Period'),
                items: const [
                  DropdownMenuItem(value: 'MONTHLY', child: Text('Monthly')),
                  DropdownMenuItem(value: 'WEEKLY', child: Text('Weekly')),
                  DropdownMenuItem(value: 'YEARLY', child: Text('Yearly')),
                ],
                onChanged: (v) => setState(() => _recurringPeriod = v),
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
    _amountController.dispose();
    _categoryController.dispose();
    _vendorController.dispose();
    _notesController.dispose();
    super.dispose();
  }
}

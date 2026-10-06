import 'package:flutter/material.dart';

import '../../../core/theme/design_tokens.dart';

/// Dialog for recording a contribution toward a savings goal.
///
/// Validates amount and contribution date, then invokes [onSave].
/// On success [onSuccess] is called before the dialog is popped.
class AddContributionDialog extends StatefulWidget {
  final String currency;
  final Future<void> Function(Map<String, dynamic>) onSave;
  final VoidCallback onSuccess;

  const AddContributionDialog({
    super.key,
    required this.currency,
    required this.onSave,
    required this.onSuccess,
  });

  @override
  State<AddContributionDialog> createState() => _AddContributionDialogState();
}

class _AddContributionDialogState extends State<AddContributionDialog> {
  final _amountController = TextEditingController();
  final _noteController = TextEditingController();
  DateTime? _contributionDate;
  bool _isLoading = false;
  String? _error;

  @override
  void dispose() {
    _amountController.dispose();
    _noteController.dispose();
    super.dispose();
  }

  Future<void> _pickDate() async {
    final now = DateTime.now();
    final picked = await showDatePicker(
      context: context,
      initialDate: _contributionDate ?? now,
      firstDate: DateTime(2000),
      lastDate: now,
    );
    if (picked != null) {
      setState(() => _contributionDate = picked);
    }
  }

  Future<void> _submit() async {
    setState(() => _error = null);

    final amount = num.tryParse(_amountController.text);
    if (amount == null || amount <= 0) {
      setState(() => _error = 'Enter a positive amount');
      return;
    }
    if (_contributionDate == null) {
      setState(() => _error = 'Select a contribution date');
      return;
    }

    final data = <String, dynamic>{
      'amount': amount,
      'contributionDate':
          '${_contributionDate!.year.toString().padLeft(4, '0')}-${_contributionDate!.month.toString().padLeft(2, '0')}-${_contributionDate!.day.toString().padLeft(2, '0')}',
      if (_noteController.text.isNotEmpty) 'notes': _noteController.text,
    };

    setState(() => _isLoading = true);
    try {
      await widget.onSave(data);
      widget.onSuccess();
      if (mounted) Navigator.of(context).pop();
    } catch (e) {
      if (mounted) {
        setState(() {
          _error = e.toString();
          _isLoading = false;
        });
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: const Text('Add Contribution'),
      content: SingleChildScrollView(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            TextFormField(
              controller: _amountController,
              keyboardType: const TextInputType.numberWithOptions(decimal: true),
              decoration: InputDecoration(
                labelText: 'Amount (${widget.currency})',
                hintText: '0.00',
              ),
            ),
            const SizedBox(height: DesignTokens.spaceSm),
            ListTile(
              contentPadding: EdgeInsets.zero,
              title: const Text('Contribution Date'),
              subtitle: Text(_contributionDate == null
                  ? 'Select a date'
                  : '${_contributionDate!.year}-${_contributionDate!.month.toString().padLeft(2, '0')}-${_contributionDate!.day.toString().padLeft(2, '0')}'),
              trailing: const Icon(Icons.calendar_today),
              onTap: _pickDate,
            ),
            const SizedBox(height: DesignTokens.spaceSm),
            TextFormField(
              controller: _noteController,
              decoration: const InputDecoration(
                labelText: 'Note (optional)',
                hintText: 'Source or reason',
              ),
            ),
            if (_error != null) ...[
              const SizedBox(height: DesignTokens.spaceSm),
              Text(_error!, style: TextStyle(color: Theme.of(context).colorScheme.error)),
            ],
            if (_isLoading) ...[
              const SizedBox(height: DesignTokens.spaceMd),
              const Center(child: CircularProgressIndicator()),
            ],
          ],
        ),
      ),
      actions: [
        TextButton(
          onPressed: _isLoading ? null : () => Navigator.of(context).pop(),
          child: const Text('Cancel'),
        ),
        FilledButton(
          onPressed: _isLoading ? null : _submit,
          child: const Text('Save'),
        ),
      ],
    );
  }
}

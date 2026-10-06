import 'package:flutter/material.dart';

import '../../../core/components/brand_logos.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../models/bill_model.dart';
import '../../../../core/di/providers.dart';

class BillDetailScreen extends ConsumerStatefulWidget {
  final String billId;

  const BillDetailScreen({super.key, required this.billId});

  @override
  ConsumerState<BillDetailScreen> createState() => _BillDetailScreenState();
}

class _BillDetailScreenState extends ConsumerState<BillDetailScreen> {
  BillModel? _bill;
  bool _isLoading = true;
  String? _error;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    try {
      final bill = await ref.read(billServiceProvider).get(widget.billId);
      setState(() {
        _bill = bill;
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
      await ref.read(billServiceProvider).delete(widget.billId);
      if (mounted) context.pop();
    } catch (e) {
      setState(() => _error = e.toString());
    }
  }

  Future<void> _markPaid() async {
    setState(() => _isLoading = true);
    try {
      final updated = await ref.read(billServiceProvider).markPaid(widget.billId);
      setState(() {
        _bill = updated;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _error = e.toString();
        _isLoading = false;
      });
    }
  }

  Color _statusColor(String status) {
    switch (status) {
      case 'PAID':
        return Colors.green;
      case 'OVERDUE':
        return Colors.red;
      case 'PENDING':
      default:
        return Colors.orange;
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) return const Scaffold(body: Center(child: CircularProgressIndicator()));
    if (_error != null) return Scaffold(body: Center(child: Text('Error: $_error')));
    if (_bill == null) return const Scaffold(body: Center(child: Text('Bill not found')));

    final bill = _bill!;

    return Scaffold(
      appBar: BrandAppBar(
        title: Text(bill.name),
        actions: [
          IconButton(
            icon: const Icon(Icons.edit),
            onPressed: () => context.push('/bills/${bill.billId}'),
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
            Text('Amount: ${bill.amount} ${bill.currency}'),
            Text('Due: ${bill.dueDate}'),
            if (bill.category != null) Text('Category: ${bill.category}'),
            if (bill.vendor != null) Text('Vendor: ${bill.vendor}'),
            if (bill.notes != null) Text('Notes: ${bill.notes}'),
            const SizedBox(height: 8),
            Chip(
              label: Text(bill.status),
              backgroundColor: _statusColor(bill.status),
              labelStyle: const TextStyle(color: Colors.white),
            ),
            if (bill.status == 'PAID') ...[
              Text('Paid Date: ${bill.paidDate ?? 'N/A'}'),
              if (bill.paymentMethod != null) Text('Payment Method: ${bill.paymentMethod}'),
            ],
            const SizedBox(height: 16),
            if (bill.status != 'PAID')
              ElevatedButton(
                onPressed: _markPaid,
                child: const Text('Record as Paid'),
              ),
            if (_error != null) Text('Error: $_error', style: const TextStyle(color: Colors.red)),
          ],
        ),
      ),
    );
  }
}

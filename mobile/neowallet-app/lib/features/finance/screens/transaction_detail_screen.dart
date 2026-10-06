import 'package:flutter/material.dart';

import '../../../core/components/brand_logos.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:neowallet_app/core/di/providers.dart';
import 'package:neowallet_app/features/finance/models/transaction_model.dart';

class TransactionDetailScreen extends ConsumerStatefulWidget {
  final String transactionId;
  final String? familyId;

  const TransactionDetailScreen({super.key, required this.transactionId, this.familyId});

  @override
  ConsumerState<TransactionDetailScreen> createState() => _TransactionDetailScreenState();
}

class _TransactionDetailScreenState extends ConsumerState<TransactionDetailScreen> {
  TransactionModel? _transaction;
  bool _isLoading = true;
  String? _error;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    try {
      final service = ref.read(transactionServiceProvider);
      final tx = await service.get(widget.transactionId);
      setState(() { _transaction = tx; _isLoading = false; });
    } catch (e) {
      setState(() { _error = e.toString(); _isLoading = false; });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const BrandAppBar(title: Text('Transaction Detail')),
      body: _buildBody(),
    );
  }

  Widget _buildBody() {
    if (_isLoading) return const Center(child: CircularProgressIndicator());
    if (_error != null) return Center(child: Text(_error!));
    if (_transaction == null) return const Center(child: Text('Transaction not found'));

    final tx = _transaction!;
    return ListView(
      padding: const EdgeInsets.all(16),
      children: [
        Text('Type: ${tx.type}', style: Theme.of(context).textTheme.titleMedium),
        Text('Category: ${tx.category}'),
        Text('Amount: ${tx.amount.toStringAsFixed(2)} ${tx.currency}'),
        Text('Date: ${tx.transactionDate}'),
        Text('Status: ${tx.status}'),
        if (tx.description != null) Text('Description: ${tx.description}'),
        Text('Source: ${tx.source}'),
        const SizedBox(height: 24),
        if (tx.status != 'COMPLETED')
          ElevatedButton(
            onPressed: _delete,
            style: ElevatedButton.styleFrom(backgroundColor: Colors.red),
            child: const Text('Delete'),
          ),
      ],
    );
  }

  Future<void> _delete() async {
    try {
      final service = ref.read(transactionServiceProvider);
      await service.delete(widget.transactionId);
      if (mounted) context.pop();
    } catch (e) {
      setState(() => _error = e.toString());
    }
  }
}

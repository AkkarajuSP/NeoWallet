import 'package:dio/dio.dart';

import '../models/transaction_model.dart';

class TransactionService {
  final Dio _dio;

  TransactionService({required Dio dio}) : _dio = dio;

  Future<TransactionModel> create({
    required Map<String, dynamic> data,
    String? familyId,
  }) async {
    final options = familyId != null ? Options(headers: {'X-Family-ID': familyId}) : null;
    final response = await _dio.post<Map<String, dynamic>>(
      '/api/v1/transactions',
      data: data,
      options: options,
    );
    return TransactionModel.fromJson(response.data!);
  }

  Future<TransactionModel> get(String transactionId) async {
    final response = await _dio.get<Map<String, dynamic>>('/api/v1/transactions/$transactionId');
    return TransactionModel.fromJson(response.data!);
  }

  Future<TransactionsListModel> list({
    String? familyId,
    int page = 1,
    int limit = 20,
    String? filterType,
    String? filterCategory,
    String? filterStatus,
  }) async {
    final options = familyId != null ? Options(headers: {'X-Family-ID': familyId}) : null;
    final response = await _dio.get<Map<String, dynamic>>(
      '/api/v1/transactions',
      queryParameters: {
        'page': page,
        'limit': limit,
        if (filterType != null) 'filterType': filterType,
        if (filterCategory != null) 'filterCategory': filterCategory,
        if (filterStatus != null) 'filterStatus': filterStatus,
      },
      options: options,
    );
    return TransactionsListModel.fromJson(response.data!);
  }

  Future<TransactionModel> update(String transactionId, Map<String, dynamic> data) async {
    final response = await _dio.put<Map<String, dynamic>>(
      '/api/v1/transactions/$transactionId',
      data: data,
    );
    return TransactionModel.fromJson(response.data!);
  }

  Future<void> delete(String transactionId) async {
    await _dio.delete('/api/v1/transactions/$transactionId');
  }
}

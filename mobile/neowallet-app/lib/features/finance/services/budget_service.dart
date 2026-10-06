import 'package:dio/dio.dart';

import '../models/budget_model.dart';

class BudgetService {
  final Dio _dio;

  BudgetService({required Dio dio}) : _dio = dio;

  Future<BudgetModel> create({
    required Map<String, dynamic> data,
    String? familyId,
  }) async {
    final options = familyId != null ? Options(headers: {'X-Family-ID': familyId}) : null;
    final response = await _dio.post<Map<String, dynamic>>(
      '/api/v1/budgets',
      data: data,
      options: options,
    );
    return BudgetModel.fromJson(response.data!);
  }

  Future<BudgetModel> get(String budgetId, {String? familyId}) async {
    final options = familyId != null ? Options(headers: {'X-Family-ID': familyId}) : null;
    final response = await _dio.get<Map<String, dynamic>>(
      '/api/v1/budgets/$budgetId',
      options: options,
    );
    return BudgetModel.fromJson(response.data!);
  }

  Future<BudgetListModel> list({
    String? familyId,
    int page = 1,
    int limit = 20,
    String? period,
  }) async {
    final options = familyId != null ? Options(headers: {'X-Family-ID': familyId}) : null;
    final response = await _dio.get<Map<String, dynamic>>(
      '/api/v1/budgets',
      queryParameters: {
        'page': page,
        'limit': limit,
        if (period != null) 'filter[period]': period,
      },
      options: options,
    );
    return BudgetListModel.fromJson(response.data!);
  }

  Future<BudgetModel> update(String budgetId, Map<String, dynamic> data) async {
    final response = await _dio.put<Map<String, dynamic>>(
      '/api/v1/budgets/$budgetId',
      data: data,
    );
    return BudgetModel.fromJson(response.data!);
  }

  Future<void> delete(String budgetId) async {
    await _dio.delete('/api/v1/budgets/$budgetId');
  }

  Future<Map<String, dynamic>> getUtilization(String budgetId) async {
    final response = await _dio.get<Map<String, dynamic>>('/api/v1/budgets/$budgetId/utilization');
    return response.data!;
  }

  Future<Map<String, dynamic>> getForecast(String budgetId) async {
    final response = await _dio.get<Map<String, dynamic>>('/api/v1/budgets/$budgetId/forecast');
    return response.data!;
  }

  Future<Map<String, dynamic>> getRecommendation({
    required String period,
    String? familyId,
  }) async {
    final options = familyId != null ? Options(headers: {'X-Family-ID': familyId}) : null;
    final response = await _dio.get<Map<String, dynamic>>(
      '/api/v1/budgets/recommendation',
      queryParameters: {'period': period},
      options: options,
    );
    return response.data!;
  }
}

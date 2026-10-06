import 'package:dio/dio.dart';

import '../models/savings_goal_model.dart';

class SavingsGoalService {
  final Dio _dio;

  SavingsGoalService({required Dio dio}) : _dio = dio;

  Future<SavingsGoalModel> create({
    required Map<String, dynamic> data,
    String? familyId,
  }) async {
    final options = familyId != null ? Options(headers: {'X-Family-ID': familyId}) : null;
    final response = await _dio.post<Map<String, dynamic>>(
      '/api/v1/savings-goals',
      data: data,
      options: options,
    );
    return SavingsGoalModel.fromJson(response.data!);
  }

  Future<SavingsGoalModel> get(String goalId, {String? familyId}) async {
    final options = familyId != null ? Options(headers: {'X-Family-ID': familyId}) : null;
    final response = await _dio.get<Map<String, dynamic>>(
      '/api/v1/savings-goals/$goalId',
      options: options,
    );
    return SavingsGoalModel.fromJson(response.data!);
  }

  Future<SavingsGoalListModel> list({
    String? familyId,
    int page = 1,
    int limit = 20,
    String? status,
    String? priority,
    String sort = 'targetDate:asc',
  }) async {
    final options = familyId != null ? Options(headers: {'X-Family-ID': familyId}) : null;
    final response = await _dio.get<Map<String, dynamic>>(
      '/api/v1/savings-goals',
      queryParameters: {
        'page': page,
        'limit': limit,
        'sort': sort,
        if (status != null) 'filter[status]': status,
        if (priority != null) 'filter[priority]': priority,
      },
      options: options,
    );
    return SavingsGoalListModel.fromJson(response.data!);
  }

  Future<SavingsGoalModel> update(String goalId, Map<String, dynamic> data) async {
    final response = await _dio.put<Map<String, dynamic>>(
      '/api/v1/savings-goals/$goalId',
      data: data,
    );
    return SavingsGoalModel.fromJson(response.data!);
  }

  Future<void> delete(String goalId) async {
    await _dio.delete('/api/v1/savings-goals/$goalId');
  }

  Future<SavingsGoalModel> contribute(
    String goalId,
    Map<String, dynamic> data, {
    String? familyId,
  }) async {
    final options = familyId != null ? Options(headers: {'X-Family-ID': familyId}) : null;
    final response = await _dio.post<Map<String, dynamic>>(
      '/api/v1/savings-goals/$goalId/contributions',
      data: data,
      options: options,
    );
    final goal = response.data!['goal'] as Map<String, dynamic>;
    return SavingsGoalModel.fromJson(goal);
  }

  Future<Map<String, dynamic>> getProgress(String goalId) async {
    final response = await _dio.get<Map<String, dynamic>>('/api/v1/savings-goals/$goalId/progress');
    return response.data!;
  }

  Future<Map<String, dynamic>> getForecast(String goalId) async {
    final response = await _dio.get<Map<String, dynamic>>('/api/v1/savings-goals/$goalId/forecast');
    return response.data!;
  }
}

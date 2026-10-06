import 'package:dio/dio.dart';

import '../models/financial_health_model.dart';

class FinancialHealthService {
  final Dio _dio;

  FinancialHealthService(this._dio);

  Future<FinancialHealthModel> getFinancialHealth(String? familyId) async {
    final response = await _dio.get(
      '/api/v1/financial-health',
      options: _options(familyId),
    );
    return FinancialHealthModel.fromJson(response.data);
  }

  Future<FinancialHealthFactorsModel> getFactors(String? familyId) async {
    final response = await _dio.get(
      '/api/v1/financial-health/factors',
      options: _options(familyId),
    );
    return FinancialHealthFactorsModel.fromJson(response.data);
  }

  Future<FinancialHealthHistoryModel> getHistory({
    String? familyId,
    int page = 1,
    int limit = 12,
    String sort = 'calculatedAt:desc',
  }) async {
    final response = await _dio.get(
      '/api/v1/financial-health/history',
      queryParameters: {'page': page, 'limit': limit, 'sort': sort},
      options: _options(familyId),
    );
    return FinancialHealthHistoryModel.fromJson(response.data);
  }

  Future<FinancialHealthExplanationModel> getExplanation(String? familyId) async {
    final response = await _dio.get(
      '/api/v1/financial-health/explanation',
      options: _options(familyId),
    );
    return FinancialHealthExplanationModel.fromJson(response.data);
  }

  Options _options(String? familyId) {
    final headers = <String, String>{};
    if (familyId != null && familyId.isNotEmpty) {
      headers['X-Family-ID'] = familyId;
    }
    return Options(headers: headers);
  }
}

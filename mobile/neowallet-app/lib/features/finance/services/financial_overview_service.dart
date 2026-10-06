import 'package:dio/dio.dart';

import '../models/financial_overview_model.dart';

class FinancialOverviewService {
  final Dio _dio;

  FinancialOverviewService({required Dio dio}) : _dio = dio;

  Future<FinancialOverviewModel> getOverview({String? familyId}) async {
    final options = familyId != null ? Options(headers: {'X-Family-ID': familyId}) : null;
    final response = await _dio.get<Map<String, dynamic>>(
      '/api/v1/financial-overview',
      options: options,
    );
    return FinancialOverviewModel.fromJson(response.data!);
  }

  Future<FinancialSummaryModel> getSummary({String? familyId}) async {
    final options = familyId != null ? Options(headers: {'X-Family-ID': familyId}) : null;
    final response = await _dio.get<Map<String, dynamic>>(
      '/api/v1/financial-overview/summary',
      options: options,
    );
    return FinancialSummaryModel.fromJson(response.data!);
  }
}

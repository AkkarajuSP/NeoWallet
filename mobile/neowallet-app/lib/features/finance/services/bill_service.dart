import 'package:dio/dio.dart';

import '../models/bill_model.dart';

class BillService {
  final Dio _dio;

  BillService({required Dio dio}) : _dio = dio;

  Future<BillModel> create({
    required Map<String, dynamic> data,
    String? familyId,
  }) async {
    final options = familyId != null ? Options(headers: {'X-Family-ID': familyId}) : null;
    final response = await _dio.post<Map<String, dynamic>>(
      '/api/v1/bills',
      data: data,
      options: options,
    );
    return BillModel.fromJson(response.data!);
  }

  Future<BillModel> get(String billId, {String? familyId}) async {
    final options = familyId != null ? Options(headers: {'X-Family-ID': familyId}) : null;
    final response = await _dio.get<Map<String, dynamic>>(
      '/api/v1/bills/$billId',
      options: options,
    );
    return BillModel.fromJson(response.data!);
  }

  Future<BillListModel> list({
    String? familyId,
    int page = 1,
    int limit = 20,
    String? status,
    String? category,
    String? dueDateFrom,
    String? dueDateTo,
    String sort = 'dueDate:asc',
  }) async {
    final options = familyId != null ? Options(headers: {'X-Family-ID': familyId}) : null;
    final response = await _dio.get<Map<String, dynamic>>(
      '/api/v1/bills',
      queryParameters: {
        'page': page,
        'limit': limit,
        'sort': sort,
        if (status != null) 'filter[status]': status,
        if (category != null) 'filter[category]': category,
        if (dueDateFrom != null) 'filter[dueDateFrom]': dueDateFrom,
        if (dueDateTo != null) 'filter[dueDateTo]': dueDateTo,
      },
      options: options,
    );
    return BillListModel.fromJson(response.data!);
  }

  Future<BillModel> update(String billId, Map<String, dynamic> data, {String? familyId}) async {
    final options = familyId != null ? Options(headers: {'X-Family-ID': familyId}) : null;
    final response = await _dio.put<Map<String, dynamic>>(
      '/api/v1/bills/$billId',
      data: data,
      options: options,
    );
    return BillModel.fromJson(response.data!);
  }

  Future<void> delete(String billId, {String? familyId}) async {
    final options = familyId != null ? Options(headers: {'X-Family-ID': familyId}) : null;
    await _dio.delete('/api/v1/bills/$billId', options: options);
  }

  Future<BillModel> markPaid(String billId, {String? familyId, Map<String, dynamic>? data}) async {
    final options = familyId != null ? Options(headers: {'X-Family-ID': familyId}) : null;
    final response = await _dio.post<Map<String, dynamic>>(
      '/api/v1/bills/$billId/mark-paid',
      data: data,
      options: options,
    );
    return BillModel.fromJson(response.data!);
  }
}

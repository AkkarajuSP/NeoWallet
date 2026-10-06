import 'package:dio/dio.dart';

import '../models/ai_message_model.dart';

class AiService {
  final Dio _dio;

  AiService({required Dio dio}) : _dio = dio;

  Future<AiMessageModel> sendMessage({
    required String message,
    String? familyId,
    String? sessionId,
  }) async {
    try {
      final response = await _dio.post<Map<String, dynamic>>(
        '/api/v1/ai/chat',
        data: {
          'familyId': familyId,
          'message': message,
          'sessionId': sessionId,
        },
      );

      if (response.data == null) {
        throw Exception('Empty response from Neo AI');
      }

      return AiMessageModel.fromJson(response.data!);
    } on DioException catch (e) {
      throw Exception('Neo AI request failed: ${e.message}');
    }
  }
}

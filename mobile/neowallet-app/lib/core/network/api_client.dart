import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';

import '../storage/secure_storage.dart';
import 'auth_interceptor.dart';

/// API client abstraction.
/// Base URL and interceptors are configurable per environment.
class ApiClient {
  late final Dio _dio;

  ApiClient({
    required String baseUrl,
    required SecureStorage secureStorage,
    List<Interceptor>? interceptors,
    VoidCallback? onSessionExpired,
  }) {
    final dio = Dio(
      BaseOptions(
        baseUrl: baseUrl,
        connectTimeout: const Duration(seconds: 10),
        receiveTimeout: const Duration(seconds: 10),
      ),
    );

    dio.interceptors.add(_correlationIdInterceptor());
    dio.interceptors.add(_secureLogInterceptor());
    dio.interceptors.add(
      AuthInterceptor(
        dio: dio,
        secureStorage: secureStorage,
        onSessionExpired: onSessionExpired,
      ),
    );

    if (interceptors != null) {
      dio.interceptors.addAll(interceptors);
    }

    _dio = dio;
  }

  Dio get dio => _dio;

  Interceptor _correlationIdInterceptor() {
    return InterceptorsWrapper(
      onRequest: (options, handler) {
        options.headers['X-Correlation-Id'] = _generateCorrelationId();
        return handler.next(options);
      },
    );
  }

  Interceptor _secureLogInterceptor() {
    return InterceptorsWrapper(
      onRequest: (options, handler) {
        if (kDebugMode) {
          // Do NOT log headers that may contain tokens, OTP, or passwords.
          debugPrint('API Request: ${options.method} ${options.path}');
        }
        return handler.next(options);
      },
    );
  }

  String _generateCorrelationId() {
    return DateTime.now().millisecondsSinceEpoch.toString();
  }
}

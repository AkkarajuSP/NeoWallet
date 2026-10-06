import 'dart:async';

import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';

import '../storage/secure_storage.dart';

class _PendingRetry {
  final DioException err;
  final ErrorInterceptorHandler handler;
  _PendingRetry(this.err, this.handler);
}

class AuthInterceptor extends Interceptor {
  final SecureStorage _secureStorage;
  final VoidCallback? _onSessionExpired;
  final Dio _dio;

  static const _publicPaths = {
    '/api/v1/auth/register',
    '/api/v1/auth/login',
    '/api/v1/auth/otp/request',
    '/api/v1/auth/otp/verify',
    '/api/v1/auth/refresh',
    '/api/v1/health',
  };

  bool _isRefreshing = false;
  final _pending = <_PendingRetry>[];

  AuthInterceptor({
    required Dio dio,
    required SecureStorage secureStorage,
    VoidCallback? onSessionExpired,
  })  : _dio = dio,
        _secureStorage = secureStorage,
        _onSessionExpired = onSessionExpired;

  @override
  Future<void> onRequest(
    RequestOptions options,
    RequestInterceptorHandler handler,
  ) async {
    if (_publicPaths.contains(options.path)) {
      return handler.next(options);
    }

    final token = await _secureStorage.readAccessToken();
    if (token != null && token.isNotEmpty) {
      options.headers['Authorization'] = 'Bearer $token';
    }

    return handler.next(options);
  }

  @override
  void onError(DioException err, ErrorInterceptorHandler handler) {
    final isRetry = err.requestOptions.extra['isRetry'] == true;
    final isPublic = _publicPaths.contains(err.requestOptions.path);

    if (err.response?.statusCode != 401 || isRetry || isPublic) {
      handler.next(err);
      return;
    }

    if (_isRefreshing) {
      _pending.add(_PendingRetry(err, handler));
      return;
    }

    _isRefreshing = true;
    _handle401(err, handler);
  }

  Future<void> _handle401(
    DioException originalErr,
    ErrorInterceptorHandler originalHandler,
  ) async {
    try {
      final success = await _refreshToken();
      if (!success) {
        throw Exception('Token refresh failed');
      }
      await _retry(originalErr, originalHandler);
    } catch (e) {
      await _secureStorage.deleteAll();
      _onSessionExpired?.call();
      _isRefreshing = false;
      _rejectPending(originalErr);
      originalHandler.reject(originalErr);
      return;
    }

    _isRefreshing = false;
    _resolvePending();
  }

  Future<bool> _refreshToken() async {
    final refreshToken = await _secureStorage.readRefreshToken();
    if (refreshToken == null || refreshToken.isEmpty) {
      return false;
    }

    try {
      final response = await _dio.post<Map<String, dynamic>>(
        '/api/v1/auth/refresh',
        data: {'refreshToken': refreshToken},
        options: Options(extra: {'isRefresh': true}),
      );

      final data = response.data;
      if (data == null) return false;

      final accessToken = data['accessToken'] as String?;
      final newRefreshToken = data['refreshToken'] as String?;
      if (accessToken == null || newRefreshToken == null) return false;

      await _secureStorage.writeAccessToken(accessToken);
      await _secureStorage.writeRefreshToken(newRefreshToken);
      return true;
    } on DioException {
      return false;
    } catch (e) {
      return false;
    }
  }

  Future<void> _retry(DioException err, ErrorInterceptorHandler handler) async {
    final token = await _secureStorage.readAccessToken();
    if (token == null || token.isEmpty) {
      throw Exception('No access token after refresh');
    }
    final options = err.requestOptions;
    options.headers['Authorization'] = 'Bearer $token';
    options.extra['isRetry'] = true;
    final response = await _dio.fetch(options);
    handler.resolve(response);
  }

  void _rejectPending(DioException original) {
    for (final p in _pending) {
      p.handler.reject(original);
    }
    _pending.clear();
  }

  Future<void> _resolvePending() async {
    while (_pending.isNotEmpty) {
      final p = _pending.removeAt(0);
      try {
        await _retry(p.err, p.handler);
      } on DioException catch (e) {
        p.handler.reject(e);
      } catch (e) {
        p.handler.reject(p.err);
      }
    }
  }
}

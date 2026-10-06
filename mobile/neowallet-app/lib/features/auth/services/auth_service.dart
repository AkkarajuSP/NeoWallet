import 'package:dio/dio.dart';

import '../../../core/storage/secure_storage.dart';
import '../models/auth_token.dart';

class AuthService {
  final Dio _dio;
  final SecureStorage _secureStorage;

  AuthService({required Dio dio, required SecureStorage secureStorage})
      : _dio = dio,
        _secureStorage = secureStorage;

  Future<void> register({
    required String email,
    required String password,
    required String firstName,
    required String lastName,
  }) async {
    final response = await _dio.post<Map<String, Object?>>
      ('/api/v1/auth/register',
      data: {
        'email': email,
        'password': password,
        'firstName': firstName,
        'lastName': lastName,
      },
    );
    if (response.statusCode != 201) {
      throw Exception('Registration failed');
    }
  }

  Future<String> requestOtp({
    required String email,
    required String purpose,
  }) async {
    final response = await _dio.post<Map<String, Object?>>(
      '/api/v1/auth/otp/request',
      data: {'email': email, 'purpose': purpose},
    );
    final data = response.data;
    if (data == null || !data.containsKey('otpId')) {
      throw Exception('OTP request failed');
    }
    return data['otpId']! as String;
  }

  Future<bool> verifyOtp({
    required String otpId,
    required String code,
  }) async {
    final response = await _dio.post<Map<String, Object?>>(
      '/api/v1/auth/otp/verify',
      data: {'otpId': otpId, 'code': code},
    );
    final data = response.data;
    return data != null && (data['verified'] as bool? ?? false);
  }

  Future<AuthToken> login({
    required String email,
    required String password,
    String? deviceType,
    String? deviceName,
  }) async {
    final response = await _dio.post<Map<String, Object?>>(
      '/api/v1/auth/login',
      data: {
        'email': email,
        'password': password,
        'deviceType': deviceType,
        'deviceName': deviceName,
      },
    );
    final token = _extractToken(response.data!);
    await _persistToken(token);
    return token;
  }

  Future<AuthToken> refresh() async {
    final refreshToken = await _secureStorage.readRefreshToken();
    if (refreshToken == null || refreshToken.isEmpty) {
      throw Exception('No refresh token');
    }
    final response = await _dio.post<Map<String, Object?>>(
      '/api/v1/auth/refresh',
      data: {'refreshToken': refreshToken},
    );
    final token = _extractToken(response.data!);
    await _persistToken(token);
    return token;
  }

  Future<void> logout() async {
    final refreshToken = await _secureStorage.readRefreshToken();
    if (refreshToken != null && refreshToken.isNotEmpty) {
      await _dio.post<Map<String, Object?>>(
        '/api/v1/auth/logout',
        data: {'refreshToken': refreshToken},
      );
    }
    await _secureStorage.deleteAll();
  }

  AuthToken _extractToken(Map<String, Object?> data) {
    return AuthToken(
      accessToken: data['accessToken']! as String,
      refreshToken: data['refreshToken']! as String,
      tokenType: data['tokenType'] as String? ?? 'Bearer',
      expiresIn: data['expiresIn'] as int? ?? 900,
      issuedAt: DateTime.now(),
    );
  }

  Future<void> _persistToken(AuthToken token) async {
    await _secureStorage.writeAccessToken(token.accessToken);
    await _secureStorage.writeRefreshToken(token.refreshToken);
  }
}

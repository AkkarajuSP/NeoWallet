import 'package:dio/dio.dart';

import '../models/user_preferences_model.dart';
import '../models/user_profile_model.dart';

class ProfileService {
  final Dio _dio;

  ProfileService({required Dio dio}) : _dio = dio;

  Future<UserProfile> getProfile() async {
    final response = await _dio.get<Map<String, dynamic>>('/api/v1/users/me');
    return UserProfile.fromJson(response.data!);
  }

  Future<UserProfile> updateProfile({
    String? firstName,
    String? lastName,
    String? phoneNumber,
  }) async {
    final data = <String, dynamic>{};
    if (firstName != null) data['firstName'] = firstName;
    if (lastName != null) data['lastName'] = lastName;
    if (phoneNumber != null) data['phoneNumber'] = phoneNumber;
    final response = await _dio.put<Map<String, dynamic>>(
      '/api/v1/users/me',
      data: data,
    );
    return UserProfile.fromJson(response.data!);
  }

  Future<void> deleteAccount({required String confirmation}) async {
    await _dio.delete(
      '/api/v1/users/me',
      data: {'confirmation': confirmation},
    );
  }

  Future<UserPreferences> getPreferences() async {
    final response = await _dio.get<Map<String, dynamic>>('/api/v1/users/me/preferences');
    return UserPreferences.fromJson(response.data!);
  }

  Future<UserPreferences> updatePreferences(UserPreferences preferences) async {
    final response = await _dio.put<Map<String, dynamic>>(
      '/api/v1/users/me/preferences',
      data: preferences.toJson(),
    );
    return UserPreferences.fromJson(response.data!);
  }
}

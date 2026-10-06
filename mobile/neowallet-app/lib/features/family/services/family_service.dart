import 'package:dio/dio.dart';

import '../models/family_models.dart';

class FamilyService {
  final Dio _dio;

  FamilyService({required Dio dio}) : _dio = dio;

  Future<FamilyModel> createFamily({required String name, String currency = 'USD'}) async {
    final response = await _dio.post<Map<String, dynamic>>(
      '/api/v1/families',
      data: {'name': name, 'currency': currency},
    );
    return FamilyModel.fromJson(response.data!);
  }

  Future<List<FamilyModel>> listMyFamilies() async {
    final response = await _dio.get<Map<String, dynamic>>('/api/v1/families');
    final data = response.data;
    if (data == null) return [];
    final families = data['families'];
    if (families is List) {
      return families.map((e) => FamilyModel.fromJson(e as Map<String, dynamic>)).toList();
    }
    return [];
  }

  Future<FamilyModel> getFamily(String familyId) async {
    final response = await _dio.get<Map<String, dynamic>>('/api/v1/families/$familyId');
    return FamilyModel.fromJson(response.data!);
  }

  Future<FamilyModel> updateFamily(String familyId, {String? name, String? currency}) async {
    final data = <String, dynamic>{};
    if (name != null) data['name'] = name;
    if (currency != null) data['currency'] = currency;
    final response = await _dio.put<Map<String, dynamic>>(
      '/api/v1/families/$familyId',
      data: data,
    );
    return FamilyModel.fromJson(response.data!);
  }

  Future<void> deleteFamily(String familyId) async {
    await _dio.delete(
      '/api/v1/families/$familyId',
      data: {'confirmation': 'DELETE'},
    );
  }

  Future<FamilyInvitationModel> inviteMember(String familyId, {required String email, String role = 'MEMBER'}) async {
    final response = await _dio.post<Map<String, dynamic>>(
      '/api/v1/families/$familyId/invitations',
      data: {'email': email, 'role': role},
    );
    return FamilyInvitationModel.fromJson(response.data!);
  }

  Future<List<FamilyMemberModel>> getMembers(String familyId) async {
    final response = await _dio.get<List<dynamic>>('/api/v1/families/$familyId/members');
    return response.data!.map((e) => FamilyMemberModel.fromJson(e as Map<String, dynamic>)).toList();
  }

  Future<FamilyMemberModel> updateMemberRole(String familyId, String memberId, String role) async {
    final response = await _dio.put<Map<String, dynamic>>(
      '/api/v1/families/$familyId/members/$memberId',
      data: {'role': role},
    );
    return FamilyMemberModel.fromJson(response.data!);
  }

  Future<void> removeMember(String familyId, String memberId) async {
    await _dio.delete('/api/v1/families/$familyId/members/$memberId');
  }

  Future<void> acceptInvitation(String token) async {
    await _dio.post<Map<String, dynamic>>('/api/v1/family-invitations/$token/accept');
  }

  Future<void> rejectInvitation(String token) async {
    await _dio.post<Map<String, dynamic>>('/api/v1/family-invitations/$token/reject');
  }
}

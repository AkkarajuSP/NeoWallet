class FamilyModel {
  final String familyId;
  final String name;
  final String currency;
  final String ownerId;
  final int memberCount;
  final String createdAt;
  final String updatedAt;

  const FamilyModel({
    required this.familyId,
    required this.name,
    required this.currency,
    required this.ownerId,
    required this.memberCount,
    required this.createdAt,
    required this.updatedAt,
  });

  factory FamilyModel.fromJson(Map<String, dynamic> json) {
    return FamilyModel(
      familyId: json['familyId'] as String,
      name: json['name'] as String,
      currency: json['currency'] as String,
      ownerId: json['ownerId'] as String,
      memberCount: json['memberCount'] as int,
      createdAt: json['createdAt'] as String,
      updatedAt: json['updatedAt'] as String,
    );
  }
}

class FamilyMemberModel {
  final String memberId;
  final String userId;
  final String? firstName;
  final String? lastName;
  final String email;
  final String role;
  final String joinedAt;

  const FamilyMemberModel({
    required this.memberId,
    required this.userId,
    this.firstName,
    this.lastName,
    required this.email,
    required this.role,
    required this.joinedAt,
  });

  factory FamilyMemberModel.fromJson(Map<String, dynamic> json) {
    return FamilyMemberModel(
      memberId: json['memberId'] as String,
      userId: json['userId'] as String,
      firstName: json['firstName'] as String?,
      lastName: json['lastName'] as String?,
      email: json['email'] as String,
      role: json['role'] as String,
      joinedAt: json['joinedAt'] as String,
    );
  }
}

class FamilyInvitationModel {
  final String invitationId;
  final String token;
  final String email;
  final String role;
  final String expiresAt;
  final String createdAt;

  const FamilyInvitationModel({
    required this.invitationId,
    required this.token,
    required this.email,
    required this.role,
    required this.expiresAt,
    required this.createdAt,
  });

  factory FamilyInvitationModel.fromJson(Map<String, dynamic> json) {
    return FamilyInvitationModel(
      invitationId: json['invitationId'] as String,
      token: json['token'] as String,
      email: json['email'] as String,
      role: json['role'] as String,
      expiresAt: json['expiresAt'] as String,
      createdAt: json['createdAt'] as String,
    );
  }
}

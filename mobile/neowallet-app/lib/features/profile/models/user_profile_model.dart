class UserProfile {
  final String userId;
  final String email;
  final String? phoneNumber;
  final String firstName;
  final String lastName;
  final String? familyId;
  final String? familyRole;
  final String createdAt;
  final String updatedAt;

  const UserProfile({
    required this.userId,
    required this.email,
    this.phoneNumber,
    required this.firstName,
    required this.lastName,
    this.familyId,
    this.familyRole,
    required this.createdAt,
    required this.updatedAt,
  });

  factory UserProfile.fromJson(Map<String, dynamic> json) {
    return UserProfile(
      userId: json['userId'] as String,
      email: json['email'] as String,
      phoneNumber: json['phoneNumber'] as String?,
      firstName: json['firstName'] as String,
      lastName: json['lastName'] as String,
      familyId: json['familyId'] as String?,
      familyRole: json['familyRole'] as String?,
      createdAt: json['createdAt'] as String,
      updatedAt: json['updatedAt'] as String,
    );
  }

  UserProfile copyWith({
    String? phoneNumber,
    String? firstName,
    String? lastName,
  }) {
    return UserProfile(
      userId: userId,
      email: email,
      phoneNumber: phoneNumber ?? this.phoneNumber,
      firstName: firstName ?? this.firstName,
      lastName: lastName ?? this.lastName,
      familyId: familyId,
      familyRole: familyRole,
      createdAt: createdAt,
      updatedAt: updatedAt,
    );
  }
}

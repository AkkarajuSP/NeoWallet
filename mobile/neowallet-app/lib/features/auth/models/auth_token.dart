class AuthToken {
  final String accessToken;
  final String refreshToken;
  final String tokenType;
  final int expiresIn;
  final DateTime issuedAt;

  AuthToken({
    required this.accessToken,
    required this.refreshToken,
    required this.tokenType,
    required this.expiresIn,
    required this.issuedAt,
  });

  DateTime get expiresAt => issuedAt.add(Duration(seconds: expiresIn));

  bool get isExpired => DateTime.now().isAfter(expiresAt);

  bool get shouldRefresh {
    final now = DateTime.now();
    final halfLife = Duration(seconds: expiresIn ~/ 2);
    return now.isAfter(issuedAt.add(halfLife));
  }
}

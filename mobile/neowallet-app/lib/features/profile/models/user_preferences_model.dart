class NotificationPreferences {
  final bool budgetAlerts;
  final bool billReminders;
  final bool savingsUpdates;
  final bool financialHealthUpdates;

  const NotificationPreferences({
    required this.budgetAlerts,
    required this.billReminders,
    required this.savingsUpdates,
    required this.financialHealthUpdates,
  });

  factory NotificationPreferences.fromJson(Map<String, dynamic> json) {
    return NotificationPreferences(
      budgetAlerts: json['budgetAlerts'] as bool,
      billReminders: json['billReminders'] as bool,
      savingsUpdates: json['savingsUpdates'] as bool,
      financialHealthUpdates: json['financialHealthUpdates'] as bool,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'budgetAlerts': budgetAlerts,
      'billReminders': billReminders,
      'savingsUpdates': savingsUpdates,
      'financialHealthUpdates': financialHealthUpdates,
    };
  }

  NotificationPreferences copyWith({
    bool? budgetAlerts,
    bool? billReminders,
    bool? savingsUpdates,
    bool? financialHealthUpdates,
  }) {
    return NotificationPreferences(
      budgetAlerts: budgetAlerts ?? this.budgetAlerts,
      billReminders: billReminders ?? this.billReminders,
      savingsUpdates: savingsUpdates ?? this.savingsUpdates,
      financialHealthUpdates: financialHealthUpdates ?? this.financialHealthUpdates,
    );
  }
}

class AiPreferences {
  final String responseStyle;
  final String language;

  const AiPreferences({
    required this.responseStyle,
    required this.language,
  });

  factory AiPreferences.fromJson(Map<String, dynamic> json) {
    return AiPreferences(
      responseStyle: json['responseStyle'] as String,
      language: json['language'] as String,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'responseStyle': responseStyle,
      'language': language,
    };
  }

  AiPreferences copyWith({
    String? responseStyle,
    String? language,
  }) {
    return AiPreferences(
      responseStyle: responseStyle ?? this.responseStyle,
      language: language ?? this.language,
    );
  }
}

class UserPreferences {
  final String userId;
  final String locale;
  final String currency;
  final String timezone;
  final NotificationPreferences notificationPreferences;
  final AiPreferences aiPreferences;

  const UserPreferences({
    required this.userId,
    required this.locale,
    required this.currency,
    required this.timezone,
    required this.notificationPreferences,
    required this.aiPreferences,
  });

  factory UserPreferences.fromJson(Map<String, dynamic> json) {
    return UserPreferences(
      userId: json['userId'] as String,
      locale: json['locale'] as String,
      currency: json['currency'] as String,
      timezone: json['timezone'] as String,
      notificationPreferences: NotificationPreferences.fromJson(json['notificationPreferences'] as Map<String, dynamic>),
      aiPreferences: AiPreferences.fromJson(json['aiPreferences'] as Map<String, dynamic>),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'locale': locale,
      'currency': currency,
      'timezone': timezone,
      'notificationPreferences': notificationPreferences.toJson(),
      'aiPreferences': aiPreferences.toJson(),
    };
  }

  UserPreferences copyWith({
    String? locale,
    String? currency,
    String? timezone,
    NotificationPreferences? notificationPreferences,
    AiPreferences? aiPreferences,
  }) {
    return UserPreferences(
      userId: userId,
      locale: locale ?? this.locale,
      currency: currency ?? this.currency,
      timezone: timezone ?? this.timezone,
      notificationPreferences: notificationPreferences ?? this.notificationPreferences,
      aiPreferences: aiPreferences ?? this.aiPreferences,
    );
  }
}

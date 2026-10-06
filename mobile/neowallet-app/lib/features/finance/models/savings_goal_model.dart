class SavingsGoalModel {
  final String goalId;
  final String? userId;
  final String? familyId;
  final String name;
  final num targetAmount;
  final num currentAmount;
  final num progressPercentage;
  final String targetDate;
  final String currency;
  final String priority;
  final String? category;
  final String status;
  final String createdAt;
  final String? updatedAt;

  const SavingsGoalModel({
    required this.goalId,
    this.userId,
    this.familyId,
    required this.name,
    required this.targetAmount,
    required this.currentAmount,
    required this.progressPercentage,
    required this.targetDate,
    required this.currency,
    required this.priority,
    this.category,
    required this.status,
    required this.createdAt,
    this.updatedAt,
  });

  factory SavingsGoalModel.fromJson(Map<String, dynamic> json) {
    return SavingsGoalModel(
      goalId: json['goalId'] as String,
      userId: json['userId'] as String?,
      familyId: json['familyId'] as String?,
      name: json['name'] as String,
      targetAmount: json['targetAmount'] as num,
      currentAmount: json['currentAmount'] as num,
      progressPercentage: json['progressPercentage'] as num,
      targetDate: json['targetDate'] as String,
      currency: json['currency'] as String,
      priority: json['priority'] as String,
      category: json['category'] as String?,
      status: json['status'] as String,
      createdAt: json['createdAt'] as String,
      updatedAt: json['updatedAt'] as String?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'name': name,
      'targetAmount': targetAmount,
      'currentAmount': currentAmount,
      'targetDate': targetDate,
      'currency': currency,
      'priority': priority,
      if (category != null) 'category': category,
      if (status != 'ACTIVE') 'status': status,
    };
  }
}

class SavingsGoalListModel {
  final List<SavingsGoalModel> goals;
  final PaginationModel pagination;

  const SavingsGoalListModel({
    required this.goals,
    required this.pagination,
  });

  factory SavingsGoalListModel.fromJson(Map<String, dynamic> json) {
    return SavingsGoalListModel(
      goals: (json['goals'] as List<dynamic>)
          .map((e) => SavingsGoalModel.fromJson(e as Map<String, dynamic>))
          .toList(),
      pagination: PaginationModel.fromJson(json['pagination'] as Map<String, dynamic>),
    );
  }
}

class PaginationModel {
  final int page;
  final int limit;
  final int total;
  final int totalPages;

  const PaginationModel({
    required this.page,
    required this.limit,
    required this.total,
    required this.totalPages,
  });

  factory PaginationModel.fromJson(Map<String, dynamic> json) {
    return PaginationModel(
      page: json['page'] as int,
      limit: json['limit'] as int,
      total: (json['total'] as num).toInt(),
      totalPages: (json['totalPages'] as num).toInt(),
    );
  }
}

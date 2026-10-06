class BudgetModel {
  final String budgetId;
  final String? userId;
  final String? familyId;
  final String name;
  final String period;
  final String currency;
  final List<BudgetCategoryModel> categories;
  final num totalLimit;
  final num totalSpent;
  final num utilizationPercentage;
  final String createdAt;
  final String? updatedAt;

  const BudgetModel({
    required this.budgetId,
    this.userId,
    this.familyId,
    required this.name,
    required this.period,
    required this.currency,
    required this.categories,
    required this.totalLimit,
    required this.totalSpent,
    required this.utilizationPercentage,
    required this.createdAt,
    this.updatedAt,
  });

  factory BudgetModel.fromJson(Map<String, dynamic> json) {
    return BudgetModel(
      budgetId: json['budgetId'] as String,
      userId: json['userId'] as String?,
      familyId: json['familyId'] as String?,
      name: json['name'] as String,
      period: json['period'] as String,
      currency: json['currency'] as String,
      categories: (json['categories'] as List<dynamic>? ?? [])
          .map((e) => BudgetCategoryModel.fromJson(e as Map<String, dynamic>))
          .toList(),
      totalLimit: json['totalLimit'] as num,
      totalSpent: json['totalSpent'] as num,
      utilizationPercentage: json['utilizationPercentage'] as num,
      createdAt: json['createdAt'] as String,
      updatedAt: json['updatedAt'] as String?,
    );
  }
}

class BudgetCategoryModel {
  final String category;
  final num limit;
  final num? spent;
  final num? utilizationPercentage;
  final String priority;

  const BudgetCategoryModel({
    required this.category,
    required this.limit,
    this.spent,
    this.utilizationPercentage,
    required this.priority,
  });

  factory BudgetCategoryModel.fromJson(Map<String, dynamic> json) {
    return BudgetCategoryModel(
      category: json['category'] as String,
      limit: json['limit'] as num,
      spent: json['spent'] as num?,
      utilizationPercentage: json['utilizationPercentage'] as num?,
      priority: json['priority'] as String,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'category': category,
      'limit': limit,
      'priority': priority,
    };
  }
}

class BudgetListModel {
  final List<BudgetModel> budgets;
  final PaginationModel pagination;

  const BudgetListModel({
    required this.budgets,
    required this.pagination,
  });

  factory BudgetListModel.fromJson(Map<String, dynamic> json) {
    return BudgetListModel(
      budgets: (json['budgets'] as List<dynamic>)
          .map((e) => BudgetModel.fromJson(e as Map<String, dynamic>))
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

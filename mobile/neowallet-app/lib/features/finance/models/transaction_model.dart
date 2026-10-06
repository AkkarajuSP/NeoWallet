class TransactionModel {
  final String transactionId;
  final String? userId;
  final String? familyId;
  final String? familyMemberId;
  final String type;
  final String category;
  final num amount;
  final String currency;
  final String? description;
  final String transactionDate;
  final String status;
  final bool isRecurring;
  final String source;
  final String createdAt;
  final String updatedAt;

  const TransactionModel({
    required this.transactionId,
    this.userId,
    this.familyId,
    this.familyMemberId,
    required this.type,
    required this.category,
    required this.amount,
    required this.currency,
    this.description,
    required this.transactionDate,
    required this.status,
    required this.isRecurring,
    required this.source,
    required this.createdAt,
    required this.updatedAt,
  });

  factory TransactionModel.fromJson(Map<String, dynamic> json) {
    return TransactionModel(
      transactionId: json['transactionId'] as String,
      userId: json['userId'] as String?,
      familyId: json['familyId'] as String?,
      familyMemberId: json['familyMemberId'] as String?,
      type: json['type'] as String,
      category: json['category'] as String,
      amount: json['amount'] as num,
      currency: json['currency'] as String,
      description: json['description'] as String?,
      transactionDate: json['transactionDate'] as String,
      status: json['status'] as String,
      isRecurring: json['isRecurring'] as bool,
      source: json['source'] as String,
      createdAt: json['createdAt'] as String,
      updatedAt: json['updatedAt'] as String,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'type': type,
      'category': category,
      'amount': amount,
      'currency': currency,
      'description': description,
      'transactionDate': transactionDate,
      'status': status,
      'isRecurring': isRecurring,
    };
  }
}

class TransactionsListModel {
  final List<TransactionModel> transactions;
  final PaginationModel pagination;

  const TransactionsListModel({
    required this.transactions,
    required this.pagination,
  });

  factory TransactionsListModel.fromJson(Map<String, dynamic> json) {
    return TransactionsListModel(
      transactions: (json['transactions'] as List<dynamic>)
          .map((e) => TransactionModel.fromJson(e as Map<String, dynamic>))
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
      totalPages: json['totalPages'] as int,
    );
  }
}

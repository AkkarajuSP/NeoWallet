class BillModel {
  final String billId;
  final String? userId;
  final String? familyId;
  final String name;
  final num amount;
  final String currency;
  final String dueDate;
  final String? category;
  final bool isRecurring;
  final String? recurringPeriod;
  final String? vendor;
  final String? notes;
  final String status;
  final String? paidDate;
  final String? paymentMethod;
  final String createdAt;
  final String? updatedAt;

  const BillModel({
    required this.billId,
    this.userId,
    this.familyId,
    required this.name,
    required this.amount,
    required this.currency,
    required this.dueDate,
    this.category,
    required this.isRecurring,
    this.recurringPeriod,
    this.vendor,
    this.notes,
    required this.status,
    this.paidDate,
    this.paymentMethod,
    required this.createdAt,
    this.updatedAt,
  });

  factory BillModel.fromJson(Map<String, dynamic> json) {
    return BillModel(
      billId: json['billId'] as String,
      userId: json['userId'] as String?,
      familyId: json['familyId'] as String?,
      name: json['name'] as String,
      amount: json['amount'] as num,
      currency: json['currency'] as String,
      dueDate: json['dueDate'] as String,
      category: json['category'] as String?,
      isRecurring: json['isRecurring'] as bool? ?? false,
      recurringPeriod: json['recurringPeriod'] as String?,
      vendor: json['vendor'] as String?,
      notes: json['notes'] as String?,
      status: json['status'] as String,
      paidDate: json['paidDate'] as String?,
      paymentMethod: json['paymentMethod'] as String?,
      createdAt: json['createdAt'] as String,
      updatedAt: json['updatedAt'] as String?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'name': name,
      'amount': amount,
      'currency': currency,
      'dueDate': dueDate,
      if (category != null) 'category': category,
      'isRecurring': isRecurring,
      if (recurringPeriod != null) 'recurringPeriod': recurringPeriod,
      if (vendor != null) 'vendor': vendor,
      if (notes != null) 'notes': notes,
    };
  }

  BillModel copyWith({
    String? status,
    String? paidDate,
    String? paymentMethod,
    String? notes,
    String? updatedAt,
  }) {
    return BillModel(
      billId: billId,
      userId: userId,
      familyId: familyId,
      name: name,
      amount: amount,
      currency: currency,
      dueDate: dueDate,
      category: category,
      isRecurring: isRecurring,
      recurringPeriod: recurringPeriod,
      vendor: vendor,
      notes: notes ?? this.notes,
      status: status ?? this.status,
      paidDate: paidDate ?? this.paidDate,
      paymentMethod: paymentMethod ?? this.paymentMethod,
      createdAt: createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
    );
  }
}

class BillListModel {
  final List<BillModel> bills;
  final PaginationModel pagination;

  const BillListModel({
    required this.bills,
    required this.pagination,
  });

  factory BillListModel.fromJson(Map<String, dynamic> json) {
    return BillListModel(
      bills: (json['bills'] as List<dynamic>)
          .map((e) => BillModel.fromJson(e as Map<String, dynamic>))
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

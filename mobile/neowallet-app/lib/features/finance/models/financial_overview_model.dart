class FinancialOverviewModel {
  final String overviewId;
  final String? userId;
  final String? familyId;
  final num planningIncome;
  final num mandatoryCommitments;
  final num essentialAllocation;
  final num savingsAllocation;
  final num emergencyAllocation;
  final num discretionaryPlanning;
  final num committedAmount;
  final num pendingPayments;
  final num actualTransactions;
  final num availableFinancialCapacity;
  final String currency;
  final String period;
  final String calculatedAt;
  final String disclaimer;

  const FinancialOverviewModel({
    required this.overviewId,
    this.userId,
    this.familyId,
    required this.planningIncome,
    required this.mandatoryCommitments,
    required this.essentialAllocation,
    required this.savingsAllocation,
    required this.emergencyAllocation,
    required this.discretionaryPlanning,
    required this.committedAmount,
    required this.pendingPayments,
    required this.actualTransactions,
    required this.availableFinancialCapacity,
    required this.currency,
    required this.period,
    required this.calculatedAt,
    required this.disclaimer,
  });

  factory FinancialOverviewModel.fromJson(Map<String, dynamic> json) {
    return FinancialOverviewModel(
      overviewId: json['overviewId'] as String,
      userId: json['userId'] as String?,
      familyId: json['familyId'] as String?,
      planningIncome: json['planningIncome'] as num,
      mandatoryCommitments: json['mandatoryCommitments'] as num,
      essentialAllocation: json['essentialAllocation'] as num,
      savingsAllocation: json['savingsAllocation'] as num,
      emergencyAllocation: json['emergencyAllocation'] as num,
      discretionaryPlanning: json['discretionaryPlanning'] as num,
      committedAmount: json['committedAmount'] as num,
      pendingPayments: json['pendingPayments'] as num,
      actualTransactions: json['actualTransactions'] as num,
      availableFinancialCapacity: json['availableFinancialCapacity'] as num,
      currency: json['currency'] as String,
      period: json['period'] as String,
      calculatedAt: json['calculatedAt'] as String,
      disclaimer: json['disclaimer'] as String,
    );
  }
}

class FinancialSummaryModel {
  final num totalIncome;
  final num totalExpenses;
  final num totalSavings;
  final num availableCapacity;
  final num budgetAdherence;
  final String currency;
  final String period;

  const FinancialSummaryModel({
    required this.totalIncome,
    required this.totalExpenses,
    required this.totalSavings,
    required this.availableCapacity,
    required this.budgetAdherence,
    required this.currency,
    required this.period,
  });

  factory FinancialSummaryModel.fromJson(Map<String, dynamic> json) {
    return FinancialSummaryModel(
      totalIncome: json['totalIncome'] as num,
      totalExpenses: json['totalExpenses'] as num,
      totalSavings: json['totalSavings'] as num,
      availableCapacity: json['availableCapacity'] as num,
      budgetAdherence: json['budgetAdherence'] as num,
      currency: json['currency'] as String,
      period: json['period'] as String,
    );
  }
}

class FinancialHealthModel {
  final String healthScoreId;
  final String? userId;
  final String? familyId;
  final int overallScore;
  final String scoreLabel;
  final String confidence;
  final String status;
  final DateTime calculatedAt;
  final String? disclaimer;

  FinancialHealthModel({
    required this.healthScoreId,
    this.userId,
    this.familyId,
    required this.overallScore,
    required this.scoreLabel,
    required this.confidence,
    required this.status,
    required this.calculatedAt,
    this.disclaimer,
  });

  factory FinancialHealthModel.fromJson(Map<String, dynamic> json) {
    return FinancialHealthModel(
      healthScoreId: json['healthScoreId'] as String,
      userId: json['userId'] as String?,
      familyId: json['familyId'] as String?,
      overallScore: json['overallScore'] as int,
      scoreLabel: json['scoreLabel'] as String,
      confidence: json['confidence'] as String,
      status: json['status'] as String,
      calculatedAt: DateTime.parse(json['calculatedAt'] as String),
      disclaimer: json['disclaimer'] as String?,
    );
  }
}

class FactorScoreModel {
  final int score;
  final double weight;
  final double contribution;

  FactorScoreModel({
    required this.score,
    required this.weight,
    required this.contribution,
  });

  factory FactorScoreModel.fromJson(Map<String, dynamic> json) {
    return FactorScoreModel(
      score: json['score'] as int,
      weight: (json['weight'] as num).toDouble(),
      contribution: (json['contribution'] as num).toDouble(),
    );
  }
}

class FinancialHealthFactorsModel {
  final String healthScoreId;
  final Map<String, FactorScoreModel> factorScores;
  final DateTime calculatedAt;

  FinancialHealthFactorsModel({
    required this.healthScoreId,
    required this.factorScores,
    required this.calculatedAt,
  });

  factory FinancialHealthFactorsModel.fromJson(Map<String, dynamic> json) {
    final scoresMap = (json['factorScores'] as Map<String, dynamic>).map(
      (key, value) => MapEntry(key, FactorScoreModel.fromJson(value as Map<String, dynamic>)),
    );
    return FinancialHealthFactorsModel(
      healthScoreId: json['healthScoreId'] as String,
      factorScores: scoresMap,
      calculatedAt: DateTime.parse(json['calculatedAt'] as String),
    );
  }
}

class FinancialHealthHistoryModel {
  final List<FinancialHealthModel> history;
  final PaginationModel pagination;

  FinancialHealthHistoryModel({
    required this.history,
    required this.pagination,
  });

  factory FinancialHealthHistoryModel.fromJson(Map<String, dynamic> json) {
    return FinancialHealthHistoryModel(
      history: (json['history'] as List<dynamic>)
          .map((e) => FinancialHealthModel.fromJson(e as Map<String, dynamic>))
          .toList(),
      pagination: PaginationModel.fromJson(json['pagination'] as Map<String, dynamic>),
    );
  }
}

class ContributorModel {
  final String factor;
  final int score;
  final String reason;

  ContributorModel({
    required this.factor,
    required this.score,
    required this.reason,
  });

  factory ContributorModel.fromJson(Map<String, dynamic> json) {
    return ContributorModel(
      factor: json['factor'] as String,
      score: json['score'] as int,
      reason: json['reason'] as String,
    );
  }
}

class RecommendedActionModel {
  final String action;
  final String priority;

  RecommendedActionModel({
    required this.action,
    required this.priority,
  });

  factory RecommendedActionModel.fromJson(Map<String, dynamic> json) {
    return RecommendedActionModel(
      action: json['action'] as String,
      priority: json['priority'] as String,
    );
  }
}

class ScoreChangeModel {
  final int? previousScore;
  final int? currentScore;
  final int? change;
  final String? direction;
  final String? magnitude;
  final List<String>? reasons;

  ScoreChangeModel({
    this.previousScore,
    this.currentScore,
    this.change,
    this.direction,
    this.magnitude,
    this.reasons,
  });

  factory ScoreChangeModel.fromJson(Map<String, dynamic> json) {
    return ScoreChangeModel(
      previousScore: json['previousScore'] as int?,
      currentScore: json['currentScore'] as int?,
      change: json['change'] as int?,
      direction: json['direction'] as String?,
      magnitude: json['magnitude'] as String?,
      reasons: (json['reasons'] as List<dynamic>?)?.map((e) => e as String).toList(),
    );
  }
}

class FinancialHealthExplanationModel {
  final String healthScoreId;
  final int overallScore;
  final String scoreLabel;
  final String explanation;
  final List<ContributorModel> positiveContributors;
  final List<ContributorModel> negativeContributors;
  final List<RecommendedActionModel> recommendedActions;
  final ScoreChangeModel? scoreChange;
  final DateTime calculatedAt;

  FinancialHealthExplanationModel({
    required this.healthScoreId,
    required this.overallScore,
    required this.scoreLabel,
    required this.explanation,
    required this.positiveContributors,
    required this.negativeContributors,
    required this.recommendedActions,
    this.scoreChange,
    required this.calculatedAt,
  });

  factory FinancialHealthExplanationModel.fromJson(Map<String, dynamic> json) {
    return FinancialHealthExplanationModel(
      healthScoreId: json['healthScoreId'] as String,
      overallScore: json['overallScore'] as int,
      scoreLabel: json['scoreLabel'] as String,
      explanation: json['explanation'] as String,
      positiveContributors: (json['positiveContributors'] as List<dynamic>)
          .map((e) => ContributorModel.fromJson(e as Map<String, dynamic>))
          .toList(),
      negativeContributors: (json['negativeContributors'] as List<dynamic>)
          .map((e) => ContributorModel.fromJson(e as Map<String, dynamic>))
          .toList(),
      recommendedActions: (json['recommendedActions'] as List<dynamic>)
          .map((e) => RecommendedActionModel.fromJson(e as Map<String, dynamic>))
          .toList(),
      scoreChange: json['scoreChange'] != null
          ? ScoreChangeModel.fromJson(json['scoreChange'] as Map<String, dynamic>)
          : null,
      calculatedAt: DateTime.parse(json['calculatedAt'] as String),
    );
  }
}

class PaginationModel {
  final int page;
  final int limit;
  final int total;
  final int totalPages;

  PaginationModel({
    required this.page,
    required this.limit,
    required this.total,
    required this.totalPages,
  });

  factory PaginationModel.fromJson(Map<String, dynamic> json) {
    return PaginationModel(
      page: json['page'] as int,
      limit: json['limit'] as int,
      total: json['total'] as int,
      totalPages: json['totalPages'] as int,
    );
  }
}

import 'package:flutter_test/flutter_test.dart';
import 'package:neowallet_app/features/finance/models/financial_health_model.dart';

void main() {
  test('FinancialHealthModel parses basic score', () {
    final json = {
      'healthScoreId': 'id-1',
      'userId': 'user-1',
      'familyId': null,
      'overallScore': 58,
      'scoreLabel': 'NEEDS_ATTENTION',
      'confidence': 'LOW',
      'status': 'PROVISIONAL',
      'calculatedAt': '2026-08-19T14:30:00.000Z',
      'disclaimer': 'Disclaimer text',
    };

    final model = FinancialHealthModel.fromJson(json);

    expect(model.healthScoreId, 'id-1');
    expect(model.overallScore, 58);
    expect(model.scoreLabel, 'NEEDS_ATTENTION');
    expect(model.confidence, 'LOW');
    expect(model.status, 'PROVISIONAL');
    expect(model.calculatedAt.year, 2026);
    expect(model.disclaimer, 'Disclaimer text');
  });

  test('FinancialHealthFactorsModel parses factor scores', () {
    final json = {
      'healthScoreId': 'id-1',
      'factorScores': {
        'budgetAdherence': {'score': 50, 'weight': 0.2, 'contribution': 10.0},
        'savingsBehavior': {'score': 50, 'weight': 0.2, 'contribution': 10.0},
      },
      'calculatedAt': '2026-08-19T14:30:00.000Z',
    };

    final model = FinancialHealthFactorsModel.fromJson(json);

    expect(model.factorScores.length, 2);
    expect(model.factorScores['budgetAdherence']!.score, 50);
    expect(model.factorScores['savingsBehavior']!.contribution, 10.0);
  });

  test('FinancialHealthExplanationModel parses explanation', () {
    final json = {
      'healthScoreId': 'id-1',
      'overallScore': 58,
      'scoreLabel': 'NEEDS_ATTENTION',
      'explanation': 'Explanation text',
      'positiveContributors': [],
      'negativeContributors': [
        {'factor': 'emergencyPreparedness', 'score': 50, 'reason': 'Needs improvement'}
      ],
      'recommendedActions': [
        {'action': 'Review savings', 'priority': 'HIGH'}
      ],
      'scoreChange': {
        'previousScore': null,
        'currentScore': 58,
        'change': null,
        'direction': 'STABLE',
        'magnitude': 'MINOR',
        'reasons': []
      },
      'calculatedAt': '2026-08-19T14:30:00.000Z',
    };

    final model = FinancialHealthExplanationModel.fromJson(json);

    expect(model.overallScore, 58);
    expect(model.negativeContributors.length, 1);
    expect(model.recommendedActions.first.action, 'Review savings');
    expect(model.scoreChange!.direction, 'STABLE');
  });
}

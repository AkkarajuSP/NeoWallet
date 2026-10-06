import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import 'package:neowallet_app/core/components/brand_logos.dart';
import 'package:neowallet_app/core/components/neo_context_button.dart';
import 'package:neowallet_app/core/components/state_widgets.dart';
import 'package:neowallet_app/core/di/providers.dart';
import 'package:neowallet_app/core/theme/design_tokens.dart';
import 'package:neowallet_app/features/finance/models/financial_health_model.dart';
import 'package:neowallet_app/features/finance/providers/financial_health_provider.dart';

class FinancialHealthScreen extends ConsumerWidget {
  final String? familyId;

  const FinancialHealthScreen({super.key, this.familyId});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final state = ref.watch(financialHealthProvider(familyId));

    return Scaffold(
      appBar: const BrandAppBar(title: Text('Financial Health')),
      body: _buildBody(context, ref, state),
    );
  }

  Widget _buildBody(BuildContext context, WidgetRef ref, FinancialHealthState state) {
    if (state.isLoading && state.health == null) {
      return const LoadingState(message: 'Loading financial health...');
    }
    if (state.error != null) {
      return ErrorState(
        title: 'Financial health unavailable',
        message: state.error!,
        onRetry: () => ref.read(financialHealthProvider(familyId).notifier).loadAll(),
      );
    }
    return RefreshIndicator(
      onRefresh: () => ref.read(financialHealthProvider(familyId).notifier).loadAll(),
      child: _HealthContent(state: state, familyId: familyId),
    );
  }
}


class _HealthContent extends StatelessWidget {
  final FinancialHealthState state;
  final String? familyId;

  const _HealthContent({required this.state, this.familyId});

  @override
  Widget build(BuildContext context) {
    final health = state.health;
    final factors = state.factors;
    final explanation = state.explanation;
    final history = state.history;

    if (health == null) {
      return EmptyState(
        icon: Icons.favorite_border,
        title: 'No financial health data',
        message: 'Ask Neo to evaluate your family financial health.',
        action: NeoContextButton(
          origin: 'Financial Health',
          familyId: familyId,
          prompt: 'What is my family financial health score?',
          label: 'Ask Neo',
        ),
      );
    }

    return SingleChildScrollView(
      padding: const EdgeInsets.all(DesignTokens.spaceMd),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          NeoContextButton(
          origin: 'Financial Health',
            familyId: familyId,
            prompt: 'How can I improve my family financial health score?',
            label: 'How can I improve?',
          ),
          const SizedBox(height: DesignTokens.spaceMd),
          _ScoreCard(health: health),
          const SizedBox(height: 16),
          if (factors != null) _FactorSection(factors: factors),
          const SizedBox(height: 16),
          if (explanation != null) _ExplanationSection(explanation: explanation),
          const SizedBox(height: 16),
          if (history != null) _HistorySection(history: history),
        ],
      ),
    );
  }
}

class _ScoreCard extends StatelessWidget {
  final FinancialHealthModel health;

  const _ScoreCard({required this.health});

  @override
  Widget build(BuildContext context) {
    final color = _scoreColor(health.overallScore);

    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            Text(
              '${health.overallScore}',
              style: TextStyle(
                fontSize: 64,
                fontWeight: FontWeight.bold,
                color: color,
              ),
            ),
            Text(
              health.scoreLabel,
              style: Theme.of(context).textTheme.headlineSmall,
            ),
            const SizedBox(height: 8),
            Text('Confidence: ${health.confidence}'),
            if (health.status == 'PROVISIONAL')
              Chip(
                label: const Text('Provisional'),
                backgroundColor: Colors.orange.shade100,
              ),
            if (health.disclaimer != null)
              Padding(
                padding: const EdgeInsets.only(top: 8.0),
                child: Text(
                  health.disclaimer!,
                  style: Theme.of(context).textTheme.bodySmall,
                ),
              ),
          ],
        ),
      ),
    );
  }

  Color _scoreColor(int score) {
    if (score >= 90) return Colors.green;
    if (score >= 75) return Colors.blue;
    if (score >= 60) return Colors.orange;
    if (score >= 40) return Colors.deepOrange;
    return Colors.red;
  }
}

class _FactorSection extends StatelessWidget {
  final FinancialHealthFactorsModel factors;

  const _FactorSection({required this.factors});

  @override
  Widget build(BuildContext context) {
    final entries = factors.factorScores.entries.toList();

    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Factor Breakdown', style: Theme.of(context).textTheme.titleMedium),
            const SizedBox(height: 8),
            ...entries.map((e) => ListTile(
                  title: Text(e.key),
                  subtitle: Text('Score: ${e.value.score}, Weight: ${(e.value.weight * 100).toInt()}%'),
                  trailing: Text('${e.value.contribution.toStringAsFixed(1)} pts'),
                )),
          ],
        ),
      ),
    );
  }
}

class _ExplanationSection extends StatelessWidget {
  final FinancialHealthExplanationModel explanation;

  const _ExplanationSection({required this.explanation});

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Explanation', style: Theme.of(context).textTheme.titleMedium),
            const SizedBox(height: 8),
            Text(explanation.explanation),
            if (explanation.positiveContributors.isNotEmpty)
              _ContributorList(
                title: 'Positive Contributors',
                contributors: explanation.positiveContributors,
              ),
            if (explanation.negativeContributors.isNotEmpty)
              _ContributorList(
                title: 'Needs Attention',
                contributors: explanation.negativeContributors,
              ),
            if (explanation.recommendedActions.isNotEmpty)
              _ActionsList(actions: explanation.recommendedActions),
          ],
        ),
      ),
    );
  }
}

class _ContributorList extends StatelessWidget {
  final String title;
  final List<ContributorModel> contributors;

  const _ContributorList({required this.title, required this.contributors});

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const SizedBox(height: 8),
        Text(title, style: Theme.of(context).textTheme.bodyLarge),
        ...contributors.map((c) => ListTile(
              title: Text(c.factor),
              subtitle: Text(c.reason),
              trailing: Text('${c.score}'),
            )),
      ],
    );
  }
}

class _ActionsList extends StatelessWidget {
  final List<RecommendedActionModel> actions;

  const _ActionsList({required this.actions});

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const SizedBox(height: 8),
        Text('Recommended Actions', style: Theme.of(context).textTheme.bodyLarge),
        ...actions.map((a) => ListTile(
              title: Text(a.action),
              trailing: Chip(label: Text(a.priority)),
            )),
      ],
    );
  }
}

class _HistorySection extends StatelessWidget {
  final FinancialHealthHistoryModel history;

  const _HistorySection({required this.history});

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('History', style: Theme.of(context).textTheme.titleMedium),
            const SizedBox(height: 8),
            ...history.history.map((h) => ListTile(
                  title: Text('${h.overallScore} - ${h.scoreLabel}'),
                  subtitle: Text('${h.confidence} (${h.status})'),
                  trailing: Text('${h.calculatedAt.month}/${h.calculatedAt.year}'),
                )),
          ],
        ),
      ),
    );
  }
}

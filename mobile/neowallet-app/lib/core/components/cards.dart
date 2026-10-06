import 'package:flutter/material.dart';

import '../../core/theme/design_tokens.dart';
import '../../core/utils/amount_formatter.dart';
import '../../features/finance/models/bill_model.dart';
import '../../features/finance/models/budget_model.dart';
import '../../features/finance/models/financial_health_model.dart';
import '../../features/finance/models/financial_overview_model.dart';
import '../../features/finance/models/savings_goal_model.dart';
import '../../features/finance/models/transaction_model.dart';

// ---------------------------------------------------------------------------
// Financial summary: planning / actual / pending / capacity
// ---------------------------------------------------------------------------
class FinancialSummaryCard extends StatelessWidget {
  final FinancialOverviewModel overview;
  final FinancialSummaryModel? summary;

  const FinancialSummaryCard({
    super.key,
    required this.overview,
    this.summary,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return _DashboardCard(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const _SectionChip(
            label: 'PLANNING',
            color: DesignTokens.planningTint,
            textColor: DesignTokens.info,
          ),
          const SizedBox(height: DesignTokens.spaceSm),
          _disclaimer(context),
          const SizedBox(height: DesignTokens.spaceSm),
          _AmountRow(
            label: 'Planning income',
            value: overview.planningIncome,
            currency: overview.currency,
            tint: DesignTokens.planningTint,
          ),
          _AmountRow(
            label: 'Mandatory commitments',
            value: overview.mandatoryCommitments,
            currency: overview.currency,
            tint: DesignTokens.planningTint,
          ),
          _AmountRow(
            label: 'Essential allocation',
            value: overview.essentialAllocation,
            currency: overview.currency,
            tint: DesignTokens.planningTint,
          ),
          _AmountRow(
            label: 'Savings allocation',
            value: overview.savingsAllocation,
            currency: overview.currency,
            tint: DesignTokens.planningTint,
          ),
          _AmountRow(
            label: 'Discretionary planning',
            value: overview.discretionaryPlanning,
            currency: overview.currency,
            tint: DesignTokens.planningTint,
          ),
          const Divider(height: DesignTokens.spaceLg),
          const _SectionChip(
            label: 'ACTUAL & PENDING',
            color: DesignTokens.actualTint,
            textColor: DesignTokens.success,
          ),
          const SizedBox(height: DesignTokens.spaceSm),
          _AmountRow(
            label: 'Actual transactions',
            value: overview.actualTransactions,
            currency: overview.currency,
            tint: DesignTokens.actualTint,
          ),
          _AmountRow(
            label: 'Pending payments',
            value: overview.pendingPayments,
            currency: overview.currency,
            tint: DesignTokens.pendingTint,
          ),
          _AmountRow(
            label: 'Committed amount',
            value: overview.committedAmount,
            currency: overview.currency,
            tint: DesignTokens.actualTint,
          ),
          const Divider(height: DesignTokens.spaceLg),
          _SectionChip(
            label: 'AVAILABLE CAPACITY',
            color: DesignTokens.capacityTint,
            textColor: theme.colorScheme.primary,
          ),
          const SizedBox(height: DesignTokens.spaceSm),
          _AmountRow(
            label: 'Available financial capacity',
            value: overview.availableFinancialCapacity,
            currency: overview.currency,
            tint: DesignTokens.capacityTint,
            emphasis: true,
          ),
          if (summary != null) ...[
            const Divider(height: DesignTokens.spaceLg),
            _AmountRow(
              label: 'Total income',
              value: summary!.totalIncome,
              currency: summary!.currency,
            ),
            _AmountRow(
              label: 'Total expenses',
              value: summary!.totalExpenses,
              currency: summary!.currency,
            ),
            _AmountRow(
              label: 'Total savings',
              value: summary!.totalSavings,
              currency: summary!.currency,
            ),
          ],
        ],
      ),
    );
  }

  Widget _disclaimer(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(DesignTokens.spaceSm),
      decoration: BoxDecoration(
        color: DesignTokens.infoSurface,
        borderRadius: BorderRadius.circular(DesignTokens.radiusSm),
      ),
      child: Row(
        children: [
          const Icon(Icons.info_outline, size: 16, color: DesignTokens.info),
          const SizedBox(width: DesignTokens.spaceSm),
          Expanded(
            child: Text(
              DesignTokens.planningDisclaimer,
              style: DesignTokens.caption(context, color: DesignTokens.onSurfaceMediumEmphasis),
            ),
          ),
        ],
      ),
    );
  }
}

// ---------------------------------------------------------------------------
// Financial health
// ---------------------------------------------------------------------------
class FinancialHealthCard extends StatelessWidget {
  final FinancialHealthModel health;

  const FinancialHealthCard({super.key, required this.health});

  @override
  Widget build(BuildContext context) {
    return _DashboardCard(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              _ScoreCircle(score: health.overallScore),
              const SizedBox(width: DesignTokens.spaceMd),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      health.scoreLabel,
                      style: DesignTokens.title(context),
                    ),
                    Text(
                      'Confidence: ${health.confidence}',
                      style: DesignTokens.bodySmall(context),
                    ),
                    _StatusChip(label: health.status),
                  ],
                ),
              ),
            ],
          ),
          if (health.disclaimer?.isNotEmpty ?? false) ...[
            const SizedBox(height: DesignTokens.spaceMd),
            Text(
              health.disclaimer!,
              style: DesignTokens.caption(context, color: DesignTokens.onSurfaceMediumEmphasis),
            ),
          ],
        ],
      ),
    );
  }
}

class _ScoreCircle extends StatelessWidget {
  final int score;

  const _ScoreCircle({required this.score});

  @override
  Widget build(BuildContext context) {
    final color = _scoreColor(score);
    return Container(
      width: 72,
      height: 72,
      decoration: BoxDecoration(
        color: color.withValues(alpha: 0.15),
        shape: BoxShape.circle,
      ),
      alignment: Alignment.center,
      child: Text(
        score.toString(),
        style: DesignTokens.display(context, color: color),
      ),
    );
  }

  Color _scoreColor(int score) {
    if (score >= 80) return DesignTokens.success;
    if (score >= 50) return DesignTokens.warning;
    return DesignTokens.error;
  }
}

// ---------------------------------------------------------------------------
// Budget progress
// ---------------------------------------------------------------------------
class BudgetProgressCard extends StatelessWidget {
  final List<BudgetModel> budgets;

  const BudgetProgressCard({super.key, required this.budgets});

  @override
  Widget build(BuildContext context) {
    return _DashboardCard(
      child: Column(
        children: budgets.take(3).map((budget) {
          return Padding(
            padding: const EdgeInsets.only(bottom: DesignTokens.spaceMd),
            child: _ProgressRow(
              label: budget.name,
              spent: budget.totalSpent,
              limit: budget.totalLimit,
              percentage: budget.utilizationPercentage,
              currency: budget.currency,
            ),
          );
        }).toList(),
      ),
    );
  }
}

// ---------------------------------------------------------------------------
// Savings progress
// ---------------------------------------------------------------------------
class SavingsProgressCard extends StatelessWidget {
  final List<SavingsGoalModel> goals;

  const SavingsProgressCard({super.key, required this.goals});

  @override
  Widget build(BuildContext context) {
    return _DashboardCard(
      child: Column(
        children: goals.take(3).map((goal) {
          return Padding(
            padding: const EdgeInsets.only(bottom: DesignTokens.spaceMd),
            child: _ProgressRow(
              label: goal.name,
              spent: goal.currentAmount,
              limit: goal.targetAmount,
              percentage: goal.progressPercentage,
              currency: goal.currency,
            ),
          );
        }).toList(),
      ),
    );
  }
}

class _ProgressRow extends StatelessWidget {
  final String label;
  final num spent;
  final num limit;
  final num percentage;
  final String currency;

  const _ProgressRow({
    required this.label,
    required this.spent,
    required this.limit,
    required this.percentage,
    required this.currency,
  });

  @override
  Widget build(BuildContext context) {
    final clamped = percentage.clamp(0, 100);
    final color = _progressColor(clamped);
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Expanded(
              child: Text(
                label,
                style: DesignTokens.label(context),
                overflow: TextOverflow.ellipsis,
              ),
            ),
            Text(
              '${clamped.toInt()}%',
              style: DesignTokens.label(context, color: color),
            ),
          ],
        ),
        const SizedBox(height: DesignTokens.spaceXs),
        ClipRRect(
          borderRadius: BorderRadius.circular(DesignTokens.radiusSm),
          child: LinearProgressIndicator(
            value: clamped / 100,
            minHeight: 8,
            backgroundColor: color.withValues(alpha: 0.2),
            valueColor: AlwaysStoppedAnimation<Color>(color),
          ),
        ),
        const SizedBox(height: DesignTokens.spaceXs),
        Text(
          '${AmountFormatter.formatCompact(spent, currency)} / ${AmountFormatter.formatCompact(limit, currency)}',
          style: DesignTokens.caption(context),
        ),
      ],
    );
  }

  Color _progressColor(num value) {
    if (value >= 90) return DesignTokens.error;
    if (value >= 70) return DesignTokens.warning;
    return DesignTokens.success;
  }
}

// ---------------------------------------------------------------------------
// Upcoming bills
// ---------------------------------------------------------------------------
class UpcomingBillsCard extends StatelessWidget {
  final List<BillModel> bills;

  const UpcomingBillsCard({super.key, required this.bills});

  @override
  Widget build(BuildContext context) {
    final upcoming = bills.where((b) => b.status != 'PAID').take(3).toList();
    return _DashboardCard(
      child: Column(
        children: upcoming.map((bill) {
          return Padding(
            padding: const EdgeInsets.only(bottom: DesignTokens.spaceMd),
            child: Row(
              children: [
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        bill.name,
                        style: DesignTokens.label(context),
                        overflow: TextOverflow.ellipsis,
                      ),
                      const SizedBox(height: DesignTokens.spaceXs),
                      Text(
                        'Due ${bill.dueDate}',
                        style: DesignTokens.caption(context),
                      ),
                    ],
                  ),
                ),
                _StatusChip(label: bill.status),
                const SizedBox(width: DesignTokens.spaceSm),
                Text(
                  AmountFormatter.format(bill.amount, bill.currency),
                  style: DesignTokens.label(context),
                ),
              ],
            ),
          );
        }).toList(),
      ),
    );
  }
}

// ---------------------------------------------------------------------------
// Recent transactions
// ---------------------------------------------------------------------------
class RecentTransactionsCard extends StatelessWidget {
  final List<TransactionModel> transactions;

  const RecentTransactionsCard({super.key, required this.transactions});

  @override
  Widget build(BuildContext context) {
    return _DashboardCard(
      child: Column(
        children: transactions.take(3).map((tx) {
          return Padding(
            padding: const EdgeInsets.only(bottom: DesignTokens.spaceMd),
            child: Row(
              children: [
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        tx.category,
                        style: DesignTokens.label(context),
                        overflow: TextOverflow.ellipsis,
                      ),
                      const SizedBox(height: DesignTokens.spaceXs),
                      Text(
                        '${tx.transactionDate} \u2022 ${tx.type}',
                        style: DesignTokens.caption(context),
                      ),
                    ],
                  ),
                ),
                _StatusChip(label: tx.status),
                const SizedBox(width: DesignTokens.spaceSm),
                Text(
                  AmountFormatter.format(tx.amount, tx.currency),
                  style: DesignTokens.label(context),
                ),
              ],
            ),
          );
        }).toList(),
      ),
    );
  }
}

// ---------------------------------------------------------------------------
// Shared private widgets
// ---------------------------------------------------------------------------
class _DashboardCard extends StatelessWidget {
  final Widget child;

  const _DashboardCard({required this.child});

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: EdgeInsets.zero,
      elevation: 1,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(DesignTokens.radiusLg),
      ),
      child: Padding(
        padding: const EdgeInsets.all(DesignTokens.spaceMd),
        child: child,
      ),
    );
  }
}

class _AmountRow extends StatelessWidget {
  final String label;
  final num value;
  final String currency;
  final Color? tint;
  final bool emphasis;

  const _AmountRow({
    required this.label,
    required this.value,
    required this.currency,
    this.tint,
    this.emphasis = false,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(bottom: DesignTokens.spaceSm),
      padding: const EdgeInsets.symmetric(
        vertical: DesignTokens.spaceSm,
        horizontal: DesignTokens.spaceSm,
      ),
      decoration: tint != null
          ? BoxDecoration(
              color: tint!.withValues(alpha: 0.08),
              borderRadius: BorderRadius.circular(DesignTokens.radiusSm),
            )
          : null,
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Expanded(
            child: Text(
              label,
              style: emphasis ? DesignTokens.label(context) : DesignTokens.bodySmall(context),
            ),
          ),
          Text(
            AmountFormatter.format(value, currency),
            style: emphasis
                ? DesignTokens.label(context)
                : DesignTokens.body(context, color: DesignTokens.onSurfaceHighEmphasis),
          ),
        ],
      ),
    );
  }
}

class _SectionChip extends StatelessWidget {
  final String label;
  final Color color;
  final Color textColor;

  const _SectionChip({
    required this.label,
    required this.color,
    required this.textColor,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(
        horizontal: DesignTokens.spaceSm,
        vertical: DesignTokens.spaceXs,
      ),
      decoration: BoxDecoration(
        color: color,
        borderRadius: BorderRadius.circular(DesignTokens.radiusSm),
      ),
      child: Text(
        label,
        style: DesignTokens.caption(context, color: textColor),
      ),
    );
  }
}

class _StatusChip extends StatelessWidget {
  final String label;

  const _StatusChip({required this.label});

  @override
  Widget build(BuildContext context) {
    final (color, bg) = _colorsFor(label);
    return Container(
      padding: const EdgeInsets.symmetric(
        horizontal: DesignTokens.spaceSm,
        vertical: 2,
      ),
      decoration: BoxDecoration(
        color: bg,
        borderRadius: BorderRadius.circular(DesignTokens.radiusSm),
      ),
      child: Text(
        label,
        style: DesignTokens.caption(context, color: color),
      ),
    );
  }

  (Color, Color) _colorsFor(String status) {
    final upper = status.toUpperCase();
    if (upper == 'PAID' || upper == 'COMPLETED' || upper == 'SUCCESS') {
      return (DesignTokens.success, DesignTokens.successSurface);
    }
    if (upper == 'PENDING' || upper == 'UPCOMING') {
      return (DesignTokens.warning, DesignTokens.warningSurface);
    }
    if (upper == 'FAILED' || upper == 'OVERDUE' || upper == 'CANCELLED') {
      return (DesignTokens.error, DesignTokens.errorSurface);
    }
    return (DesignTokens.info, DesignTokens.infoSurface);
  }
}

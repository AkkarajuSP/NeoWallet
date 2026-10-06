import 'package:flutter/material.dart';

import '../../../core/components/brand_logos.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/components/cards.dart';
import '../../../core/components/family_selector.dart';
import '../../../core/components/neo_insight_card.dart';
import '../../../core/components/section_header.dart';
import '../../../core/components/state_widgets.dart';
import '../../../core/di/providers.dart';
import '../../../core/theme/design_tokens.dart';
import '../../../features/family/models/family_models.dart';
import '../../../features/family/providers/families_provider.dart';
import '../../../features/family/providers/selected_family_provider.dart';
import '../../../features/profile/models/user_profile_model.dart';
import '../../../features/profile/providers/profile_provider.dart';

class HomeScreen extends ConsumerStatefulWidget {
  const HomeScreen({super.key});

  @override
  ConsumerState<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends ConsumerState<HomeScreen> {
  @override
  void initState() {
    super.initState();
    Future.microtask(_bootstrap);
  }

  Future<void> _bootstrap() async {
    await ref.read(profileProvider.notifier).loadProfile();
    final profile = ref.read(profileProvider).user;
    final profileFamily = profile?.familyId;
    if (profileFamily != null && profileFamily.isNotEmpty) {
      ref.read(selectedFamilyIdProvider.notifier).state = profileFamily;
    }
    await ref.read(familiesProvider.notifier).load();
    final families = ref.read(familiesProvider).families;
    final current = ref.read(selectedFamilyIdProvider);
    if ((current == null || current.isEmpty) && families.isNotEmpty) {
      ref.read(selectedFamilyIdProvider.notifier).state = families.first.familyId;
    }
  }

  @override
  Widget build(BuildContext context) {
    final profileState = ref.watch(profileProvider);
    final familiesState = ref.watch(familiesProvider);
    final familyId = ref.watch(selectedFamilyIdProvider);

    return Scaffold(
      appBar: const BrandAppBar(
        title: Text('Home'),
        centerTitle: true,
        actions: [
          Padding(
            padding: EdgeInsets.only(right: DesignTokens.spaceSm),
            child: FamilySelector(),
          ),
        ],
      ),
      body: RefreshIndicator(
        onRefresh: () => _refresh(familyId),
        child: LayoutBuilder(
          builder: (context, constraints) {
            return SingleChildScrollView(
              physics: const AlwaysScrollableScrollPhysics(),
              padding: const EdgeInsets.symmetric(
                horizontal: DesignTokens.spaceMd,
                vertical: DesignTokens.spaceMd,
              ),
              child: ConstrainedBox(
                constraints: BoxConstraints(minHeight: constraints.maxHeight),
                child: _buildBody(profileState, familiesState, familyId),
              ),
            );
          },
        ),
      ),
    );
  }

  Widget _buildBody(ProfileState profileState, FamiliesState familiesState, String? familyId) {
    if (profileState.isLoading || familiesState.isLoading) {
      return const LoadingState(message: 'Loading your dashboard...');
    }

    if (profileState.error != null) {
      return ErrorState(
        title: 'Unable to load profile',
        message: profileState.error!,
        onRetry: () => _bootstrap(),
      );
    }

    if (familiesState.error != null) {
      return ErrorState(
        title: 'Unable to load families',
        message: familiesState.error!,
        onRetry: () => ref.read(familiesProvider.notifier).load(),
      );
    }

    if (familiesState.families.isEmpty) {
      return EmptyState(
        icon: Icons.family_restroom,
        title: 'No family yet',
        message: 'Create or join a family to start planning your finances.',
        action: FilledButton(
          onPressed: () => context.push('/family'),
          child: const Text('Set up Family'),
        ),
      );
    }

    if (familyId == null || familyId.isEmpty) {
      return EmptyState(
        icon: Icons.account_balance_wallet_outlined,
        title: 'Select a family',
        message: 'Use the family menu in the app bar to choose a family.',
        action: FilledButton(
          onPressed: () => context.push('/family'),
          child: const Text('Choose Family'),
        ),
      );
    }

    final user = profileState.user;
    final overviewState = ref.watch(financialOverviewProvider(familyId));
    final budgetState = ref.watch(budgetListProvider(familyId));
    final savingsState = ref.watch(savingsGoalListProvider(familyId));
    final billState = ref.watch(billListProvider(familyId));
    final healthState = ref.watch(financialHealthProvider(familyId));
    final txState = ref.watch(transactionListProvider(familyId));

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _Greeting(user: user, family: _findFamily(familiesState.families, familyId)),
        _section(
          title: 'Financial Overview',
          onViewAll: () => context.push('/finance/overview'),
          isLoading: overviewState.isLoading,
          error: overviewState.error,
          isEmpty: overviewState.overview == null,
          emptyIcon: Icons.pie_chart_outline,
          emptyTitle: 'No overview',
          emptyMessage: 'We could not build your financial overview.',
          onRetry: () => ref.read(financialOverviewProvider(familyId).notifier).load(familyId: familyId),
          child: overviewState.overview != null
              ? FinancialSummaryCard(
                  overview: overviewState.overview!,
                  summary: overviewState.summary,
                )
              : const SizedBox.shrink(),
        ),
        _section(
          title: 'Financial Health',
          onViewAll: () => context.push('/finance/health'),
          isLoading: healthState.isLoading,
          error: healthState.error,
          isEmpty: healthState.health == null,
          emptyIcon: Icons.favorite_border,
          emptyTitle: 'No health score',
          emptyMessage: 'Health score is not available yet.',
          onRetry: () => ref.read(financialHealthProvider(familyId).notifier).loadAll(),
          child: healthState.health != null
              ? FinancialHealthCard(health: healthState.health!)
              : const SizedBox.shrink(),
        ),
        _section(
          title: 'Budget Utilization',
          onViewAll: () => context.push('/budgets'),
          isLoading: budgetState.isLoading,
          error: budgetState.error,
          isEmpty: budgetState.budgets.isEmpty,
          emptyIcon: Icons.account_balance_wallet_outlined,
          emptyTitle: 'No budgets',
          emptyMessage: 'You have not created any budgets for this family.',
          onRetry: () => ref.read(budgetListProvider(familyId).notifier).load(familyId: familyId, refresh: true),
          child: budgetState.budgets.isNotEmpty
              ? BudgetProgressCard(budgets: budgetState.budgets)
              : const SizedBox.shrink(),
        ),
        _section(
          title: 'Savings Progress',
          onViewAll: () => context.push('/savings-goals'),
          isLoading: savingsState.isLoading,
          error: savingsState.error,
          isEmpty: savingsState.goals.isEmpty,
          emptyIcon: Icons.savings_outlined,
          emptyTitle: 'No savings goals',
          emptyMessage: 'Start a savings goal to track your progress.',
          onRetry: () => ref.read(savingsGoalListProvider(familyId).notifier).load(familyId: familyId),
          child: savingsState.goals.isNotEmpty
              ? SavingsProgressCard(goals: savingsState.goals)
              : const SizedBox.shrink(),
        ),
        _section(
          title: 'Upcoming Bills',
          onViewAll: () => context.push('/bills'),
          isLoading: billState.isLoading,
          error: billState.error,
          isEmpty: billState.bills.isEmpty,
          emptyIcon: Icons.receipt_long_outlined,
          emptyTitle: 'No upcoming bills',
          emptyMessage: 'You have no pending bills for this family.',
          onRetry: () => ref.read(billListProvider(familyId).notifier).load(familyId: familyId),
          child: billState.bills.isNotEmpty
              ? UpcomingBillsCard(bills: billState.bills)
              : const SizedBox.shrink(),
        ),
        _section(
          title: 'Recent Transactions',
          onViewAll: () => context.push('/transactions'),
          isLoading: txState.isLoading,
          error: txState.error,
          isEmpty: txState.transactions.isEmpty,
          emptyIcon: Icons.swap_horiz_outlined,
          emptyTitle: 'No transactions',
          emptyMessage: 'No transactions have been recorded yet.',
          onRetry: () => ref.read(transactionListProvider(familyId).notifier).load(familyId: familyId),
          child: txState.transactions.isNotEmpty
              ? RecentTransactionsCard(transactions: txState.transactions)
              : const SizedBox.shrink(),
        ),
        const SizedBox(height: DesignTokens.spaceLg),
        NeoInsightCard(onTap: () => context.push('/neo')),
        const SizedBox(height: DesignTokens.spaceXxl),
      ],
    );
  }

  FamilyModel? _findFamily(List<FamilyModel> families, String familyId) {
    try {
      return families.firstWhere((f) => f.familyId == familyId);
    } catch (_) {
      return null;
    }
  }

  Future<void> _refresh(String? familyId) async {
    if (familyId == null || familyId.isEmpty) return;
    await Future.wait([
      ref.read(financialOverviewProvider(familyId).notifier).load(familyId: familyId),
      ref.read(budgetListProvider(familyId).notifier).load(familyId: familyId, refresh: true),
      ref.read(savingsGoalListProvider(familyId).notifier).load(familyId: familyId),
      ref.read(billListProvider(familyId).notifier).load(familyId: familyId),
      ref.read(financialHealthProvider(familyId).notifier).loadAll(),
      ref.read(transactionListProvider(familyId).notifier).load(familyId: familyId),
    ]);
  }

  Widget _section({
    required String title,
    VoidCallback? onViewAll,
    required bool isLoading,
    required String? error,
    required bool isEmpty,
    required Widget child,
    required IconData emptyIcon,
    required String emptyTitle,
    required String emptyMessage,
    required VoidCallback onRetry,
  }) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        SectionHeader(title: title, onViewAll: onViewAll),
        if (isLoading)
          const SizedBox(height: 120, child: LoadingState())
        else if (error != null)
          ErrorState(
            title: '$title unavailable',
            message: error,
            onRetry: onRetry,
          )
        else if (isEmpty)
          EmptyState(
            icon: emptyIcon,
            title: emptyTitle,
            message: emptyMessage,
            action: FilledButton(
              onPressed: onRetry,
              child: const Text('Retry'),
            ),
          )
        else
          child,
        const SizedBox(height: DesignTokens.spaceMd),
      ],
    );
  }
}

class _Greeting extends StatelessWidget {
  final UserProfile? user;
  final FamilyModel? family;

  const _Greeting({this.user, this.family});

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'Hello, ${user?.firstName ?? 'there'}',
          style: DesignTokens.headline(context),
        ),
        if (family != null) ...[
          const SizedBox(height: DesignTokens.spaceXs),
          Text(
            family!.name,
            style: DesignTokens.body(
              context,
              color: DesignTokens.onSurfaceMediumEmphasis,
            ),
          ),
        ],
        const SizedBox(height: DesignTokens.spaceLg),
      ],
    );
  }
}

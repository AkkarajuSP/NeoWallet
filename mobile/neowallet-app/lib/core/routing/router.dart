import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import '../../core/preview/preview_menu_screen.dart';
import '../../core/preview/preview_mode.dart';
import '../../features/family/screens/create_family_screen.dart';
import '../../features/family/screens/family_screen.dart';
import '../../features/family/screens/invite_member_screen.dart';
import '../../features/family/screens/member_role_screen.dart';
import '../../features/finance/screens/budget_detail_screen.dart';
import '../../features/finance/screens/budget_list_screen.dart';
import '../../features/finance/screens/bill_detail_screen.dart';
import '../../features/finance/screens/bill_list_screen.dart';
import '../../features/finance/screens/budget_recommendation_screen.dart';
import '../../features/finance/screens/create_bill_screen.dart';
import '../../features/finance/screens/create_budget_screen.dart';
import '../../features/finance/screens/create_savings_goal_screen.dart';
import '../../features/finance/screens/create_transaction_screen.dart';
import '../../features/finance/screens/financial_health_screen.dart';
import '../../features/finance/screens/financial_overview_screen.dart';
import '../../features/finance/screens/savings_goal_detail_screen.dart';
import '../../features/finance/screens/savings_goal_list_screen.dart';
import '../../features/finance/screens/transaction_detail_screen.dart';
import '../../features/finance/screens/transaction_list_screen.dart';
import '../../features/foundation/screens/splash_screen.dart';
import '../../features/profile/screens/edit_profile_screen.dart';
import '../../features/profile/screens/preferences_screen.dart';
import '../../features/ai/screens/ai_chat_screen.dart';
import '../../features/profile/screens/profile_screen.dart';

final appRouter = GoRouter(
  initialLocation: kPreviewMode ? '/preview' : '/splash',
  routes: [
    GoRoute(
      path: '/preview',
      builder: (context, state) => const PreviewMenuScreen(),
    ),
    GoRoute(
      path: '/splash',
      builder: (context, state) => const SplashScreen(),
    ),
    GoRoute(
      path: '/profile',
      builder: (context, state) => const ProfileScreen(),
    ),
    GoRoute(
      path: '/profile/edit',
      builder: (context, state) => const EditProfileScreen(),
    ),
    GoRoute(
      path: '/profile/preferences',
      builder: (context, state) => const PreferencesScreen(),
    ),
    GoRoute(
      path: '/family',
      builder: (context, state) => const FamilyScreen(),
    ),
    GoRoute(
      path: '/finance/overview',
      builder: (context, state) => FinancialOverviewScreen(familyId: state.extra as String?),
    ),
    GoRoute(
      path: '/transactions',
      builder: (context, state) => TransactionListScreen(familyId: state.extra as String?),
    ),
    GoRoute(
      path: '/transactions/create',
      builder: (context, state) => CreateTransactionScreen(familyId: state.extra as String?),
    ),
    GoRoute(
      path: '/transactions/:id',
      builder: (context, state) => TransactionDetailScreen(
        transactionId: state.pathParameters['id']!,
        familyId: state.extra as String?,
      ),
    ),
    GoRoute(
      path: '/budgets',
      builder: (context, state) => const BudgetListScreen(),
    ),
    GoRoute(
      path: '/budgets/create',
      builder: (context, state) => CreateBudgetScreen(familyId: state.extra as String?),
    ),
    GoRoute(
      path: '/budgets/recommendation',
      builder: (context, state) => BudgetRecommendationScreen(familyId: state.extra as String?),
    ),
    GoRoute(
      path: '/budgets/:id',
      builder: (context, state) => BudgetDetailScreen(
        budgetId: state.pathParameters['id']!,
      ),
    ),
    GoRoute(
      path: '/savings-goals',
      builder: (context, state) => const SavingsGoalListScreen(),
    ),
    GoRoute(
      path: '/savings-goals/create',
      builder: (context, state) => const CreateSavingsGoalScreen(),
    ),
    GoRoute(
      path: '/savings-goals/:id',
      builder: (context, state) => SavingsGoalDetailScreen(
        goalId: state.pathParameters['id']!,
      ),
    ),
    GoRoute(
      path: '/finance/health',
      builder: (context, state) => FinancialHealthScreen(familyId: state.extra as String?),
    ),
    GoRoute(
      path: '/bills',
      builder: (context, state) => const BillListScreen(),
    ),
    GoRoute(
      path: '/bills/create',
      builder: (context, state) => CreateBillScreen(billId: state.extra as String?),
    ),
    GoRoute(
      path: '/bills/:id',
      builder: (context, state) => BillDetailScreen(
        billId: state.pathParameters['id']!,
      ),
    ),
    GoRoute(
      path: '/family/create',
      builder: (context, state) => const CreateFamilyScreen(),
    ),
    GoRoute(
      path: '/family/invite',
      builder: (context, state) => InviteMemberScreen(familyId: state.extra as String),
    ),
    GoRoute(
      path: '/family/role',
      builder: (context, state) {
        final extra = state.extra as Map<String, dynamic>;
        return MemberRoleScreen(
          familyId: extra['familyId'] as String,
          memberId: extra['memberId'] as String,
          currentRole: extra['role'] as String,
        );
      },
    ),
    GoRoute(
      path: '/ai/chat',
      builder: (context, state) {
        final extra = state.extra;
        if (extra is Map<String, dynamic>) {
          return AiChatScreen(
            familyId: extra['familyId'] as String?,
            initialPrompt: extra['prompt'] as String?,
          );
        }
        return AiChatScreen(familyId: extra as String?);
      },
    ),
    GoRoute(
      path: '/neo',
      builder: (context, state) {
        final extra = state.extra;
        if (extra is Map<String, dynamic>) {
          return AiChatScreen(
            familyId: extra['familyId'] as String?,
            initialPrompt: extra['prompt'] as String?,
          );
        }
        return AiChatScreen(familyId: extra as String?);
      },
    ),
  ],
  errorBuilder: (context, state) => Scaffold(
    body: Center(
      child: Text('Page not found: ${state.uri}'),
    ),
  ),
);

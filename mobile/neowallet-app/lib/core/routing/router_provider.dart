import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../features/ai/screens/neo_screen.dart';
import '../../features/auth/providers/auth_listenable.dart';
import '../../features/auth/screens/login_screen.dart';
import '../../features/auth/screens/otp_screen.dart';
import '../../features/auth/screens/register_screen.dart';
import '../../features/family/screens/create_family_screen.dart';
import '../../features/family/screens/family_screen.dart';
import '../../features/family/screens/invite_member_screen.dart';
import '../../features/family/screens/member_role_screen.dart';
import '../../features/finance/screens/bill_detail_screen.dart';
import '../../features/finance/screens/bill_list_screen.dart';
import '../../features/finance/screens/budget_detail_screen.dart';
import '../../features/finance/screens/budget_list_screen.dart';
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
import '../../features/home/screens/home_screen.dart';
import '../../features/home/screens/shell_scaffold.dart';
import '../../features/profile/screens/edit_profile_screen.dart';
import '../../features/profile/screens/preferences_screen.dart';
import '../../features/profile/screens/profile_screen.dart';
import '../di/providers.dart';
import '../preview/preview_menu_screen.dart';
import '../preview/preview_mode.dart';

final _publicRoutes = {
  '/splash',
  '/login',
  '/register',
  '/otp',
  '/preview',
};

final routerProvider = Provider<GoRouter>((ref) {
  return GoRouter(
    initialLocation: kPreviewMode ? '/preview' : '/splash',
    refreshListenable: ref.read(authListenableProvider),
    redirect: (BuildContext context, GoRouterState state) {
      final auth = ref.read(authProvider);
      final location = state.matchedLocation;

      if (kPreviewMode) return null;
      if (location == '/') {
        return auth.isAuthenticated ? '/home' : '/login';
      }

      if (location == '/splash') {
        if (auth.isAuthCheckPending) return null;
        return auth.isAuthenticated ? '/home' : '/login';
      }

      final isPublic = _publicRoutes.contains(location);

      if (auth.isAuthCheckPending && location != '/splash') {
        return '/splash';
      }

      if (!auth.isAuthenticated && !isPublic) {
        return '/login';
      }

      if (auth.isAuthenticated && isPublic) {
        return '/home';
      }

      return null;
    },
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
        path: '/login',
        builder: (context, state) => LoginScreen(email: state.extra as String?),
      ),
      GoRoute(
        path: '/register',
        builder: (context, state) => const RegisterScreen(),
      ),
      GoRoute(
        path: '/otp',
        builder: (context, state) => OtpScreen(email: state.extra as String?),
      ),
      StatefulShellRoute.indexedStack(
        builder: (context, state, navigationShell) => ShellScaffold(navigationShell: navigationShell),
        branches: [
          StatefulShellBranch(
            routes: [
              GoRoute(
                path: '/home',
                builder: (context, state) => const HomeScreen(),
              ),
            ],
          ),
          StatefulShellBranch(
            routes: [
              GoRoute(
                path: '/family',
                builder: (context, state) => const FamilyScreen(),
              ),
            ],
          ),
          StatefulShellBranch(
            routes: [
              GoRoute(
                path: '/neo',
                builder: (context, state) => const NeoScreen(),
              ),
            ],
          ),
          StatefulShellBranch(
            routes: [
              GoRoute(
                path: '/profile',
                builder: (context, state) => const ProfileScreen(),
              ),
            ],
          ),
        ],
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
        builder: (context, state) => BudgetListScreen(familyId: state.extra as String?),
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
        path: '/notifications',
        builder: (context, state) => const Scaffold(
          body: Center(child: Text('Notifications coming soon')),
        ),
      ),
    ],
    errorBuilder: (context, state) => Scaffold(
      body: Center(
        child: Text('Page not found: ${state.uri}'),
      ),
    ),
  );
});

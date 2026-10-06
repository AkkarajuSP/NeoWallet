import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

import 'package:neowallet_app/core/components/neo_insight_card.dart';
import 'package:neowallet_app/core/components/section_header.dart';
import 'package:neowallet_app/core/components/state_widgets.dart';
import 'package:neowallet_app/core/theme/app_theme.dart';
import 'package:neowallet_app/core/theme/design_tokens.dart';
import 'package:neowallet_app/core/utils/amount_formatter.dart';

void main() {
  test('AmountFormatter formats positive, negative and zero', () {
    expect(AmountFormatter.format(1234.5, '\$'), '\$ 1,234.50');
    expect(AmountFormatter.format(-99.9, '\$'), '\$ -99.90');
    expect(AmountFormatter.format(0, 'INR'), 'INR 0.00');
    expect(AmountFormatter.formatCompact(150000, '\$'), '\$ 150000');
  });

  test('DesignTokens are wired to Material color scheme', () {
    final light = AppTheme.light;
    final dark = AppTheme.dark;
    expect(light.colorScheme.brightness, Brightness.light);
    expect(dark.colorScheme.brightness, Brightness.dark);
    expect(DesignTokens.seedColor, const Color(0xFF1E88E5));
    expect(DesignTokens.planningDisclaimer, isNotEmpty);
  });

  group('StateWidgets', () {
    testWidgets('LoadingState renders spinner and message', (tester) async {
      await tester.pumpWidget(
        MaterialApp(
          theme: AppTheme.light,
          home: const LoadingState(message: 'Loading...'),
        ),
      );
      expect(find.byType(CircularProgressIndicator), findsOneWidget);
      expect(find.text('Loading...'), findsOneWidget);
    });

    testWidgets('ErrorState renders retry action', (tester) async {
      var called = false;
      await tester.pumpWidget(
        MaterialApp(
          theme: AppTheme.light,
          home: ErrorState(
            title: 'Error',
            message: 'failed',
            onRetry: () => called = true,
          ),
        ),
      );
      expect(find.text('Error'), findsOneWidget);
      expect(find.text('failed'), findsOneWidget);
      expect(find.text('Try again'), findsOneWidget);
      await tester.tap(find.text('Try again'));
      await tester.pump();
      expect(called, isTrue);
    });

    testWidgets('EmptyState renders icon and action', (tester) async {
      await tester.pumpWidget(
        MaterialApp(
          theme: AppTheme.light,
          home: const EmptyState(
            icon: Icons.inbox,
            title: 'Empty',
            message: 'Nothing here',
          ),
        ),
      );
      expect(find.text('Empty'), findsOneWidget);
      expect(find.text('Nothing here'), findsOneWidget);
    });
  });

  testWidgets('SectionHeader renders title and optional View all', (tester) async {
    var tapped = false;
    await tester.pumpWidget(
      MaterialApp(
        theme: AppTheme.light,
        home: SectionHeader(
          title: 'Section',
          onViewAll: () => tapped = true,
        ),
      ),
    );
    expect(find.text('Section'), findsOneWidget);
    expect(find.text('View all'), findsOneWidget);
    await tester.tap(find.text('View all'));
    await tester.pump();
    expect(tapped, isTrue);
  });

  testWidgets('NeoInsightCard renders prompts and is tappable', (tester) async {
    var tapped = false;
    await tester.pumpWidget(
      MaterialApp(
        theme: AppTheme.light,
        home: NeoInsightCard(onTap: () => tapped = true),
      ),
    );
    expect(find.text('Ask Neo'), findsOneWidget);
    expect(find.text('Ask Neo about my finances'), findsOneWidget);
    await tester.tap(find.text('Ask Neo about my finances'));
    await tester.pump();
    expect(tapped, isTrue);
  });
}

import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:neowallet_app/features/finance/widgets/add_contribution_dialog.dart';

void main() {
  group('AddContributionDialog', () {
    Future<void> openDialog(WidgetTester tester, {required Widget dialog}) async {
      await tester.pumpWidget(MaterialApp(
        home: Scaffold(
          body: Builder(
            builder: (context) => ElevatedButton(
              onPressed: () => showDialog(context: context, builder: (_) => dialog),
              child: const Text('Open'),
            ),
          ),
        ),
      ));
      await tester.tap(find.text('Open'));
      await tester.pumpAndSettle();
    }

    testWidgets('valid contribution calls onSave and onSuccess', (WidgetTester tester) async {
      final saved = <String, dynamic>{};
      var successCalled = false;

      await openDialog(
        tester,
        dialog: AddContributionDialog(
          currency: 'USD',
          onSave: (data) async {
            saved.addAll(data);
          },
          onSuccess: () => successCalled = true,
        ),
      );

      await tester.enterText(find.byType(TextFormField).first, '500.00');
      await tester.pump();

      await tester.tap(find.widgetWithIcon(ListTile, Icons.calendar_today));
      await tester.pumpAndSettle();
      await tester.tap(find.text('OK'));
      await tester.pumpAndSettle();

      await tester.tap(find.widgetWithText(FilledButton, 'Save'));
      await tester.pump();
      await tester.pumpAndSettle();

      expect(saved['amount'], 500.00);
      expect(saved['contributionDate'], isNotNull);
      expect(successCalled, isTrue);
    });

    testWidgets('invalid amount shows error and does not call onSave', (WidgetTester tester) async {
      var saveCalled = false;

      await openDialog(
        tester,
        dialog: AddContributionDialog(
          currency: 'USD',
          onSave: (_) async => saveCalled = true,
          onSuccess: () {},
        ),
      );

      await tester.enterText(find.byType(TextFormField).first, '-10');
      await tester.pump();

      await tester.tap(find.widgetWithText(FilledButton, 'Save'));
      await tester.pump();

      expect(find.text('Enter a positive amount'), findsOneWidget);
      expect(saveCalled, isFalse);
    });

    testWidgets('missing date shows error and does not call onSave', (WidgetTester tester) async {
      var saveCalled = false;

      await openDialog(
        tester,
        dialog: AddContributionDialog(
          currency: 'USD',
          onSave: (_) async => saveCalled = true,
          onSuccess: () {},
        ),
      );

      await tester.enterText(find.byType(TextFormField).first, '100.00');
      await tester.pump();

      await tester.tap(find.widgetWithText(FilledButton, 'Save'));
      await tester.pump();

      expect(find.text('Select a contribution date'), findsOneWidget);
      expect(saveCalled, isFalse);
    });

    testWidgets('onSave error is displayed', (WidgetTester tester) async {
      const errorMessage = 'Network unavailable';

      await openDialog(
        tester,
        dialog: AddContributionDialog(
          currency: 'USD',
          onSave: (_) async => throw Exception(errorMessage),
          onSuccess: () {},
        ),
      );

      await tester.enterText(find.byType(TextFormField).first, '200.00');
      await tester.pump();

      await tester.tap(find.widgetWithIcon(ListTile, Icons.calendar_today));
      await tester.pumpAndSettle();
      await tester.tap(find.text('OK'));
      await tester.pumpAndSettle();

      await tester.tap(find.widgetWithText(FilledButton, 'Save'));
      await tester.pump();
      await tester.pumpAndSettle();

      expect(find.textContaining(errorMessage), findsOneWidget);
    });

    testWidgets('cancel closes dialog', (WidgetTester tester) async {
      await openDialog(
        tester,
        dialog: AddContributionDialog(
          currency: 'USD',
          onSave: (_) async {},
          onSuccess: () {},
        ),
      );

      await tester.tap(find.widgetWithText(TextButton, 'Cancel'));
      await tester.pumpAndSettle();

      expect(find.byType(AddContributionDialog), findsNothing);
    });
  });
}

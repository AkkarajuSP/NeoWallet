import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:neowallet_app/core/components/brand_logos.dart';
import 'package:neowallet_app/features/foundation/screens/splash_screen.dart';

void main() {
  testWidgets('SplashScreen renders branding', (tester) async {
    await tester.pumpWidget(const MaterialApp(home: SplashScreen()));

    expect(find.byType(NeoWalletSplashLogo), findsOneWidget);
    expect(find.byType(CircularProgressIndicator), findsOneWidget);
  });
}

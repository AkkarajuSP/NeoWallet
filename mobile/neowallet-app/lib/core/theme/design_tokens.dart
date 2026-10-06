import 'package:flutter/material.dart';

/// Centralized NeoWallet design tokens. Use these instead of hard-coding
/// colors, spacing, radii or typography values across the app.
class DesignTokens {
  DesignTokens._();

  // --------------------------------------------------------------------------
  // Brand palette
  // --------------------------------------------------------------------------
  static const Color seedColor = Color(0xFF1E88E5);
  static const Color onSurfaceHighEmphasis = Color(0xDD000000);
  static const Color onSurfaceMediumEmphasis = Color(0x99000000);
  static const Color onSurfaceDisabled = Color(0x61000000);

  // Semantic colors (Material 3 will provide these too, but these helpers are
  // useful for custom surfaces / chips / status indicators).
  static const Color success = Color(0xFF2E7D32);
  static const Color successSurface = Color(0xFFE8F5E9);
  static const Color warning = Color(0xFFF57F17);
  static const Color warningSurface = Color(0xFFFFF8E1);
  static const Color error = Color(0xFFC62828);
  static const Color errorSurface = Color(0xFFFFEBEE);
  static const Color info = Color(0xFF1565C0);
  static const Color infoSurface = Color(0xFFE3F2FD);

  // Planning vs actual vs pending / capacity. These are tints, not status.
  static const Color planningTint = Color(0xFFE3F2FD);
  static const Color actualTint = Color(0xFFE8F5E9);
  static const Color pendingTint = Color(0xFFFFF8E1);
  static const Color capacityTint = Color(0xFFF3E5F5);

  // --------------------------------------------------------------------------
  // Spacing
  // --------------------------------------------------------------------------
  static const double spaceXs = 4;
  static const double spaceSm = 8;
  static const double spaceMd = 16;
  static const double spaceLg = 24;
  static const double spaceXl = 32;
  static const double spaceXxl = 48;

  // --------------------------------------------------------------------------
  // Border radius
  // --------------------------------------------------------------------------
  static const double radiusSm = 8;
  static const double radiusMd = 12;
  static const double radiusLg = 16;

  // --------------------------------------------------------------------------
  // Typography helpers
  // --------------------------------------------------------------------------
  static TextStyle display(BuildContext context, {Color? color}) {
    final textTheme = Theme.of(context).textTheme;
    return textTheme.headlineLarge!.copyWith(
      fontWeight: FontWeight.w800,
      color: color,
    );
  }

  static TextStyle headline(BuildContext context, {Color? color}) {
    final textTheme = Theme.of(context).textTheme;
    return textTheme.headlineSmall!.copyWith(
      fontWeight: FontWeight.w700,
      color: color,
    );
  }

  static TextStyle title(BuildContext context, {Color? color}) {
    final textTheme = Theme.of(context).textTheme;
    return textTheme.titleLarge!.copyWith(
      fontWeight: FontWeight.w600,
      color: color,
    );
  }

  static TextStyle body(BuildContext context, {Color? color}) {
    final textTheme = Theme.of(context).textTheme;
    return textTheme.bodyLarge!.copyWith(
      color: color,
    );
  }

  static TextStyle bodySmall(BuildContext context, {Color? color}) {
    final textTheme = Theme.of(context).textTheme;
    return textTheme.bodySmall!.copyWith(
      color: color,
    );
  }

  static TextStyle label(BuildContext context, {Color? color}) {
    final textTheme = Theme.of(context).textTheme;
    return textTheme.labelLarge!.copyWith(
      fontWeight: FontWeight.w600,
      color: color,
    );
  }

  static TextStyle caption(BuildContext context, {Color? color}) {
    final textTheme = Theme.of(context).textTheme;
    return textTheme.labelSmall!.copyWith(
      color: color,
      fontWeight: FontWeight.w500,
    );
  }

  // --------------------------------------------------------------------------
  // Financial safety copy
  // --------------------------------------------------------------------------
  static const String planningDisclaimer =
      'Planning values are not bank balances. They are the family plan.';
}

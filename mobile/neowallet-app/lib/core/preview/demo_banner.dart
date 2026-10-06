import 'package:flutter/material.dart';

import 'preview_mode.dart';

/// Visually marks the app as running in DEVELOPMENT / DEMO mode.
///
/// Added as a `MaterialApp.builder` overlay; it does not affect the
/// production layout when preview mode is disabled.
class DemoBanner extends StatelessWidget {
  final Widget child;

  const DemoBanner({super.key, required this.child});

  @override
  Widget build(BuildContext context) {
    if (!kPreviewMode) return child;

    return Column(
      children: [
        Expanded(child: child),
        Container(
          width: double.infinity,
          color: Colors.orange,
          padding: const EdgeInsets.symmetric(vertical: 6, horizontal: 12),
          child: const SafeArea(
            top: false,
            child: Text(
              'DEVELOPMENT / DEMO MODE — NO REAL BACKEND',
              textAlign: TextAlign.center,
              style: TextStyle(
                color: Colors.white,
                fontWeight: FontWeight.bold,
                fontSize: 12,
              ),
            ),
          ),
        ),
      ],
    );
  }
}

import 'package:flutter/material.dart';

import '../../core/theme/design_tokens.dart';

/// A visually prominent but non-intrusive Neo AI entry point for the Home
/// dashboard. Provides a primary action and a few example prompts.
class NeoInsightCard extends StatelessWidget {
  final VoidCallback onTap;

  const NeoInsightCard({super.key, required this.onTap});

  static const List<String> _prompts = [
    'Ask Neo about my finances',
    'Why did my spending increase?',
    'How can I improve my financial health?',
  ];

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return Card(
      margin: EdgeInsets.zero,
      elevation: 1,
      color: theme.colorScheme.primaryContainer,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(DesignTokens.radiusLg),
      ),
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(DesignTokens.radiusLg),
        child: Padding(
          padding: const EdgeInsets.all(DesignTokens.spaceMd),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  CircleAvatar(
                    backgroundColor: theme.colorScheme.onPrimaryContainer,
                    child: Icon(
                      Icons.auto_awesome,
                      color: theme.colorScheme.primaryContainer,
                    ),
                  ),
                  const SizedBox(width: DesignTokens.spaceMd),
                  Expanded(
                    child: Text(
                      'Ask Neo',
                      style: DesignTokens.title(
                        context,
                        color: theme.colorScheme.onPrimaryContainer,
                      ),
                    ),
                  ),
                  Icon(
                    Icons.chevron_right,
                    color: theme.colorScheme.onPrimaryContainer,
                  ),
                ],
              ),
              const SizedBox(height: DesignTokens.spaceMd),
              ..._prompts.map((prompt) {
                return Padding(
                  padding: const EdgeInsets.only(bottom: DesignTokens.spaceSm),
                  child: Container(
                    width: double.infinity,
                    padding: const EdgeInsets.symmetric(
                      horizontal: DesignTokens.spaceMd,
                      vertical: DesignTokens.spaceSm,
                    ),
                    decoration: BoxDecoration(
                      color: theme.colorScheme.surface.withValues(alpha: 0.15),
                      borderRadius: BorderRadius.circular(DesignTokens.radiusMd),
                    ),
                    child: Text(
                      prompt,
                      style: DesignTokens.body(
                        context,
                        color: theme.colorScheme.onPrimaryContainer,
                      ),
                    ),
                  ),
                );
              }),
            ],
          ),
        ),
      ),
    );
  }
}

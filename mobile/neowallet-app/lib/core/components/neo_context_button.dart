import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import '../theme/design_tokens.dart';

/// A small contextual button that opens Neo with a pre-populated question.
class NeoContextButton extends StatelessWidget {
  final String prompt;
  final String? familyId;
  final String label;
  final String? origin;

  const NeoContextButton({
    super.key,
    required this.prompt,
    this.familyId,
    this.label = 'Ask Neo',
    this.origin,
  });

  @override
  Widget build(BuildContext context) {
    return TextButton.icon(
      onPressed: () => context.push('/neo', extra: {
        'familyId': familyId,
        'prompt': prompt,
        'context': origin,
      }),
      icon: const Icon(Icons.auto_awesome, size: 18),
      label: Text(label),
      style: TextButton.styleFrom(
        foregroundColor: Theme.of(context).colorScheme.primary,
        padding: const EdgeInsets.symmetric(
          horizontal: DesignTokens.spaceSm,
          vertical: DesignTokens.spaceXs,
        ),
      ),
    );
  }
}

/// A compact chip variant for contextual Neo entry points.
class NeoContextChip extends StatelessWidget {
  final String prompt;
  final String? familyId;
  final String? label;

  const NeoContextChip({
    super.key,
    required this.prompt,
    this.familyId,
    this.label,
  });

  @override
  Widget build(BuildContext context) {
    return ActionChip(
      avatar: const Icon(Icons.auto_awesome, size: 18),
      label: Text(label ?? 'Ask Neo'),
      onPressed: () => context.push('/neo', extra: {
        'familyId': familyId,
        'prompt': prompt,
      }),
    );
  }
}

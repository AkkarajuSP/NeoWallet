import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../core/di/providers.dart';
import '../../core/theme/design_tokens.dart';
import '../../features/family/providers/selected_family_provider.dart';

/// A reusable family selector that loads the family list and lets the user
/// switch the active family. When used in the Home app bar it updates every
/// family-scoped provider through `selectedFamilyIdProvider`.
class FamilySelector extends ConsumerWidget {
  const FamilySelector({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final familiesState = ref.watch(familiesProvider);
    final selectedId = ref.watch(selectedFamilyIdProvider);

    if (familiesState.isLoading) {
      return const SizedBox(
        height: 24,
        width: 24,
        child: CircularProgressIndicator(strokeWidth: 2),
      );
    }

    final families = familiesState.families;
    if (families.isEmpty || selectedId == null) {
      return TextButton.icon(
        onPressed: () => _showSelectFamilyHint(context),
        icon: const Icon(Icons.family_restroom),
        label: const Text('Select family'),
      );
    }

    final selected = families.firstWhere(
      (f) => f.familyId == selectedId,
      orElse: () => families.first,
    );

    return DropdownButtonHideUnderline(
      child: DropdownButton<String?>(
        value: selectedId,
        icon: const Icon(Icons.expand_more),
        borderRadius: BorderRadius.circular(DesignTokens.radiusMd),
        isDense: true,
        onChanged: (value) {
          if (value != null) {
            ref.read(selectedFamilyIdProvider.notifier).state = value;
          }
        },
        items: families.map((family) {
          return DropdownMenuItem<String?>(
            value: family.familyId,
            child: ConstrainedBox(
              constraints: const BoxConstraints(maxWidth: 180),
              child: Text(
                family.name,
                overflow: TextOverflow.ellipsis,
              ),
            ),
          );
        }).toList(),
        selectedItemBuilder: (_) => [
          Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              Icon(
                Icons.family_restroom,
                size: 18,
                color: Theme.of(context).colorScheme.primary,
              ),
              const SizedBox(width: DesignTokens.spaceSm),
              ConstrainedBox(
                constraints: const BoxConstraints(maxWidth: 140),
                child: Text(
                  selected.name,
                  overflow: TextOverflow.ellipsis,
                  style: DesignTokens.label(context),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  void _showSelectFamilyHint(BuildContext context) {
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('Open the Family tab to set up a family.')),
    );
  }
}

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../family/providers/selected_family_provider.dart';
import 'ai_chat_screen.dart';

class NeoScreen extends ConsumerWidget {
  const NeoScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final familyId = ref.watch(selectedFamilyIdProvider);
    return AiChatScreen(familyId: familyId);
  }
}

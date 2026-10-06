import 'package:flutter/material.dart';

import '../../../core/components/brand_logos.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/di/providers.dart';
import '../models/ai_message_model.dart';

class AiChatScreen extends ConsumerStatefulWidget {
  final String? familyId;
  final String? initialPrompt;

  const AiChatScreen({super.key, this.familyId, this.initialPrompt});

  @override
  ConsumerState<AiChatScreen> createState() => _AiChatScreenState();
}

class _AiChatScreenState extends ConsumerState<AiChatScreen> {
  final TextEditingController _controller = TextEditingController();
  bool _sentInitial = false;

  @override
  void initState() {
    super.initState();
    if (widget.initialPrompt != null && widget.initialPrompt!.isNotEmpty) {
      _controller.text = widget.initialPrompt!;
    }
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    if (!_sentInitial && widget.initialPrompt != null && widget.initialPrompt!.isNotEmpty) {
      _sentInitial = true;
      WidgetsBinding.instance.addPostFrameCallback((_) {
        ref.read(aiChatProvider(widget.familyId).notifier).sendMessage(widget.initialPrompt!);
      });
    }
    final state = ref.watch(aiChatProvider(widget.familyId));

    return Scaffold(
      appBar: const BrandAppBar(
        title: Text('Neo AI'),
        centerTitle: true,
      ),
      body: Column(
        children: [
          Expanded(
            child: ListView.builder(
              padding: const EdgeInsets.all(16),
              itemCount: state.messages.length,
              itemBuilder: (context, index) {
                final message = state.messages[index];
                return _MessageBubble(message: message);
              },
            ),
          ),
          if (state.isLoading) const LinearProgressIndicator(),
          if (state.error != null)
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              child: Row(
                children: [
                  Expanded(
                    child: Text(
                      'Error: ${state.error}',
                      style: const TextStyle(color: Colors.red),
                    ),
                  ),
                  TextButton.icon(
                    onPressed: () {
                      final lastUser = state.messages.lastWhere(
                        (m) => m.role == 'USER',
                        orElse: () => AiMessageModel(
                          messageId: '',
                          role: 'USER',
                          content: '',
                        ),
                      );
                      if (lastUser.content.isNotEmpty) {
                        ref.read(aiChatProvider(widget.familyId).notifier).sendMessage(lastUser.content);
                      }
                    },
                    icon: const Icon(Icons.refresh, size: 16),
                    label: const Text('Retry'),
                  ),
                ],
              ),
            ),
          _InputBar(
            controller: _controller,
            onSend: () {
              final text = _controller.text;
              if (text.trim().isNotEmpty) {
                ref.read(aiChatProvider(widget.familyId).notifier).sendMessage(text);
                _controller.clear();
              }
            },
          ),
        ],
      ),
    );
  }
}

class _MessageBubble extends StatelessWidget {
  final AiMessageModel message;

  const _MessageBubble({required this.message});

  @override
  Widget build(BuildContext context) {
    final isUser = message.role == 'USER';
    final isRefusal = message.refusal;
    final isInsufficient = !isUser && message.responseType == 'INSUFFICIENT_DATA';
    final isRecommendation = !isUser && message.responseType == 'RECOMMENDATION';

    Color backgroundColor;
    if (isUser) {
      backgroundColor = Colors.blue.shade100;
    } else if (isRefusal) {
      backgroundColor = Colors.orange.shade100;
    } else if (isInsufficient) {
      backgroundColor = Colors.yellow.shade100;
    } else if (isRecommendation) {
      backgroundColor = Colors.green.shade100;
    } else {
      backgroundColor = Colors.grey.shade200;
    }

    return Align(
      alignment: isUser ? Alignment.centerRight : Alignment.centerLeft,
      child: Container(
        margin: const EdgeInsets.symmetric(vertical: 4),
        padding: const EdgeInsets.all(12),
        decoration: BoxDecoration(
          color: backgroundColor,
          borderRadius: BorderRadius.circular(12),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            if (message.responseType != null && !isUser)
              Text(
                message.responseType!.toUpperCase(),
                style: const TextStyle(fontSize: 10, fontWeight: FontWeight.bold),
              ),
            Text(message.content),
          ],
        ),
      ),
    );
  }
}

class _InputBar extends StatelessWidget {
  final TextEditingController controller;
  final VoidCallback onSend;

  const _InputBar({required this.controller, required this.onSend});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(8.0),
      child: Row(
        children: [
          Expanded(
            child: TextField(
              controller: controller,
              decoration: const InputDecoration(
                hintText: 'Ask Neo...',
                border: OutlineInputBorder(),
              ),
              onSubmitted: (_) => onSend(),
            ),
          ),
          const SizedBox(width: 8),
          IconButton(
            icon: const Icon(Icons.send),
            onPressed: onSend,
          ),
        ],
      ),
    );
  }
}

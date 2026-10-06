import 'dart:math';

import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../models/ai_message_model.dart';
import '../services/ai_service.dart';

class AiChatState {
  final List<AiMessageModel> messages;
  final bool isLoading;
  final String? error;
  final String? sessionId;

  const AiChatState({
    this.messages = const [],
    this.isLoading = false,
    this.error,
    this.sessionId,
  });

  AiChatState copyWith({
    List<AiMessageModel>? messages,
    bool? isLoading,
    String? error,
    String? sessionId,
  }) {
    return AiChatState(
      messages: messages ?? this.messages,
      isLoading: isLoading ?? this.isLoading,
      error: error ?? this.error,
      sessionId: sessionId ?? this.sessionId,
    );
  }
}

class AiChatNotifier extends StateNotifier<AiChatState> {
  final AiService _aiService;
  final String? familyId;

  AiChatNotifier(this._aiService, {this.familyId}) : super(const AiChatState());

  Future<void> sendMessage(String text) async {
    if (text.trim().isEmpty) return;

    var sessionId = state.sessionId ?? _generateSessionId();
    final userMessage = AiMessageModel(
      messageId: DateTime.now().millisecondsSinceEpoch.toString(),
      role: 'USER',
      content: text,
    );

    state = state.copyWith(
      messages: [...state.messages, userMessage],
      isLoading: true,
      error: null,
      sessionId: sessionId,
    );

    try {
      final response = await _aiService.sendMessage(
        message: text,
        familyId: familyId,
        sessionId: sessionId,
      );
      state = state.copyWith(
        messages: [...state.messages, response],
        isLoading: false,
      );
    } catch (e) {
      state = state.copyWith(
        isLoading: false,
        error: e.toString(),
      );
    }
  }

  void clearError() {
    state = state.copyWith(error: null);
  }

  String _generateSessionId() {
    final now = DateTime.now().millisecondsSinceEpoch;
    final random = Random().nextInt(0x7FFFFFFF);
    return '${now}_$random';
  }
}

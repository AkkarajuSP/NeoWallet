import 'package:dio/dio.dart';
import 'package:flutter_test/flutter_test.dart';

import 'package:neowallet_app/features/ai/models/ai_message_model.dart';
import 'package:neowallet_app/features/ai/providers/ai_chat_provider.dart';
import 'package:neowallet_app/features/ai/services/ai_service.dart';

class _FakeAiService extends AiService {
  _FakeAiService() : super(dio: Dio());

  final List<_Call> calls = [];

  @override
  Future<AiMessageModel> sendMessage({
    required String message,
    String? familyId,
    String? sessionId,
  }) async {
    calls.add(_Call(message, familyId, sessionId));
    return AiMessageModel(
      messageId: 'resp-${calls.length}',
      role: 'NEO',
      content: 'Neo reply to $message',
      responseType: message.contains('over budget')
          ? 'ANALYSIS'
          : (message.contains('pay') ? 'REFUSAL' : 'FACT'),
    );
  }
}

class _Call {
  final String message;
  final String? familyId;
  final String? sessionId;

  _Call(this.message, this.familyId, this.sessionId);
}

void main() {
  group('AiChatNotifier', () {
    test('generates and reuses sessionId across multi-turn conversation', () async {
      final service = _FakeAiService();
      final notifier = AiChatNotifier(service, familyId: 'fam-1');

      await notifier.sendMessage('Why am I over budget?');
      await notifier.sendMessage('Which category contributed the most?');

      expect(service.calls.length, 2);
      final firstSession = service.calls.first.sessionId;
      expect(firstSession, isNotNull);
      expect(firstSession, isNotEmpty);
      expect(service.calls[1].sessionId, firstSession);
      expect(notifier.state.sessionId, firstSession);
    });

    test('passes familyId to every message', () async {
      final service = _FakeAiService();
      final notifier = AiChatNotifier(service, familyId: 'fam-2');

      await notifier.sendMessage('What is my balance?');

      expect(service.calls.first.familyId, 'fam-2');
    });

    test('includes user message and assistant response in state', () async {
      final service = _FakeAiService();
      final notifier = AiChatNotifier(service, familyId: 'fam-3');

      await notifier.sendMessage('Why am I over budget?');

      expect(notifier.state.messages.length, 2);
      expect(notifier.state.messages.first.role, 'USER');
      expect(notifier.state.messages.last.role, 'NEO');
      expect(notifier.state.isLoading, false);
      expect(notifier.state.error, isNull);
    });

    test('surfaces error without losing conversation history', () async {
      final service = _AiServiceThatFails();
      final notifier = AiChatNotifier(service, familyId: 'fam-4');

      await notifier.sendMessage('Hello');

      expect(notifier.state.messages.length, 1);
      expect(notifier.state.messages.first.role, 'USER');
      expect(notifier.state.error, isNotNull);
      expect(notifier.state.isLoading, false);
    });

    test('ignores empty messages', () async {
      final service = _FakeAiService();
      final notifier = AiChatNotifier(service, familyId: 'fam-5');

      await notifier.sendMessage('   ');

      expect(service.calls, isEmpty);
    });
  });
}

class _AiServiceThatFails extends AiService {
  _AiServiceThatFails() : super(dio: Dio());

  @override
  Future<AiMessageModel> sendMessage({
    required String message,
    String? familyId,
    String? sessionId,
  }) async {
    throw Exception('network failure');
  }
}

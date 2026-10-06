import 'package:flutter_test/flutter_test.dart';
import 'package:neowallet_app/features/ai/models/ai_message_model.dart';

void main() {
  group('AiMessageModel', () {
    test('parses Neo AI response JSON', () {
      final json = {
        'responseId': 'resp-1',
        'text': 'Your score is 80.',
        'type': 'FACT',
        'refusal': false,
      };
      final model = AiMessageModel.fromJson(json);
      expect(model.messageId, 'resp-1');
      expect(model.content, 'Your score is 80.');
      expect(model.responseType, 'FACT');
      expect(model.refusal, false);
    });

    test('defaults role to NEO when not present', () {
      final json = {'text': 'Hello'};
      final model = AiMessageModel.fromJson(json);
      expect(model.role, 'NEO');
    });
  });
}

class AiMessageModel {
  final String messageId;
  final String role;
  final String content;
  final String? responseType;
  final DateTime? timestamp;
  final bool refusal;

  AiMessageModel({
    required this.messageId,
    required this.role,
    required this.content,
    this.responseType,
    this.timestamp,
    this.refusal = false,
  });

  factory AiMessageModel.fromJson(Map<String, dynamic> json) {
    return AiMessageModel(
      messageId: json['responseId'] ?? json['messageId'] ?? '',
      role: json['role'] ?? 'NEO',
      content: json['text'] ?? json['content'] ?? '',
      responseType: json['type'],
      timestamp: json['timestamp'] != null ? DateTime.tryParse(json['timestamp']) : null,
      refusal: json['refusal'] ?? false,
    );
  }
}

import 'package:flutter_riverpod/flutter_riverpod.dart';

/// Holds the currently selected familyId for the active session.
/// Consumers should watch this provider to remain reactive to family switching.
final selectedFamilyIdProvider = StateProvider<String?>((ref) => null);

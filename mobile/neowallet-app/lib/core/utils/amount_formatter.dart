/// Formats financial amounts consistently across the app.
///
/// Examples:
///  - 1234.5  -> "$ 1,234.50"
///  - -99.9   -> "$ -99.90"
class AmountFormatter {
  AmountFormatter._();

  static String format(num value, String currency) {
    final parts = value.toStringAsFixed(2).split('.');
    final whole = _withCommas(parts[0]);
    return '$currency $whole.${parts[1]}';
  }

  static String formatCompact(num value, String currency) {
    return '$currency ${value.toStringAsFixed(0)}';
  }

  static String _withCommas(String raw) {
    final isNegative = raw.startsWith('-');
    final digits = isNegative ? raw.substring(1) : raw;
    final buffer = StringBuffer();
    for (var i = 0; i < digits.length; i++) {
      if (i > 0 && (digits.length - i) % 3 == 0) buffer.write(',');
      buffer.write(digits[i]);
    }
    return '${isNegative ? '-' : ''}${buffer.toString()}';
  }
}

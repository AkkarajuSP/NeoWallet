/// Compile-time preview flag.
///
/// Launch with `--dart-define=PREVIEW=true` to enable the UI preview
/// environment. When disabled (the default), the application uses the real
/// production authentication and backend services.
const bool kPreviewMode = bool.fromEnvironment('PREVIEW', defaultValue: false);

bool get isPreviewMode => kPreviewMode;

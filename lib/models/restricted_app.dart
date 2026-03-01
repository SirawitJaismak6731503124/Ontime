/// Simple model for representing an installed app that can be blocked.
/// Not stored in Isar — used only in the UI for app selection.
class RestrictedApp {
  final String packageName;
  final String appName;

  const RestrictedApp({
    required this.packageName,
    required this.appName,
  });
}

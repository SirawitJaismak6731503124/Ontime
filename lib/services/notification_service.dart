import 'package:flutter_local_notifications/flutter_local_notifications.dart';

/// Manages local notifications for OnTime.
/// Notifications alert the user when a blocked app is detected in the foreground.
class NotificationService {
  static final FlutterLocalNotificationsPlugin _plugin =
      FlutterLocalNotificationsPlugin();

  static const _channelId = 'ontime_focus_channel';
  static const _channelName = 'OnTime Focus Alerts';
  static const _channelDesc =
      'High priority alerts when blocked apps are detected during a focus session.';

  static Future<void> initialize() async {
    const androidSettings =
        AndroidInitializationSettings('@mipmap/ic_launcher');

    const initSettings = InitializationSettings(android: androidSettings);

    await _plugin.initialize(
      initSettings,
      onDidReceiveNotificationResponse: _onNotificationResponse,
    );

    // Create the high-priority notification channel
    const channel = AndroidNotificationChannel(
      _channelId,
      _channelName,
      description: _channelDesc,
      importance: Importance.max,
      playSound: true,
    );

    await _plugin
        .resolvePlatformSpecificImplementation<
            AndroidFlutterLocalNotificationsPlugin>()
        ?.createNotificationChannel(channel);
  }

  static void _onNotificationResponse(NotificationResponse response) {
    // Navigate to the payload route when the notification is tapped
    // while the app is running in the foreground or resumed from background.
    final payload = response.payload;
    if (payload != null && payload.isNotEmpty) {
      _navigateTo(payload);
    }
  }

  static void _navigateTo(String route) {
    // If a live navigator callback is registered (app is in foreground), use it.
    if (_onNavigate != null) {
      _onNavigate!(route);
      return;
    }
    // Otherwise queue the route so main.dart can consume it once the app starts.
    _pendingRoute = route;
  }

  /// Optional callback set by the app to handle in-foreground notification taps.
  static void Function(String route)? _onNavigate;

  /// Register a callback to handle notification-tap navigation while the app is
  /// running. Call this from the root widget's [initState] and clear it in
  /// [dispose].
  static void setNavigationCallback(void Function(String route) callback) {
    _onNavigate = callback;
  }

  /// Clears the navigation callback (call from widget [dispose]).
  static void clearNavigationCallback() {
    _onNavigate = null;
  }

  /// Pending route set by a notification tap; consumed once in main.dart after
  /// the navigator is ready.
  static String? _pendingRoute;

  /// Returns and clears the pending navigation route from a notification tap.
  static String? consumePendingRoute() {
    final route = _pendingRoute;
    _pendingRoute = null;
    return route;
  }

  /// Returns the notification payload if the app was launched by tapping a notification.
  static Future<String?> getNotificationLaunchPayload() async {
    final details = await _plugin.getNotificationAppLaunchDetails();
    if (details?.didNotificationLaunchApp == true) {
      return details?.notificationResponse?.payload;
    }
    return null;
  }

  /// Shows a heads-up notification warning the user that [appName] is blocked.
  static Future<void> showBlockedAppNotification({
    required String appName,
    required String motivationMessage,
  }) async {
    final androidDetails = AndroidNotificationDetails(
      _channelId,
      _channelName,
      channelDescription: _channelDesc,
      importance: Importance.max,
      priority: Priority.high,
      fullScreenIntent: true,
      autoCancel: true,
      styleInformation: BigTextStyleInformation(
        motivationMessage,
        contentTitle: '🚫 Stay On Track!',
        summaryText: '$appName is blocked during your focus session.',
      ),
      ticker: 'Blocked app detected',
    );

    final notifDetails = NotificationDetails(android: androidDetails);

    await _plugin.show(
      0,
      '🚫 Stay On Track!',
      '$appName is blocked. Tap to see your motivation.',
      notifDetails,
      payload: '/motivation',
    );
  }
}

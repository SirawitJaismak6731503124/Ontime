import 'dart:async';
import 'dart:developer' as developer;
import 'dart:ui';

import 'package:flutter_background_service/flutter_background_service.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:isar/isar.dart';
import 'package:ontime/models/focus_session.dart';
import 'package:path_provider/path_provider.dart';
import 'package:usage_stats/usage_stats.dart';

/// Maps common package names to human-readable app labels.
const Map<String, String> _packageLabels = {
  'com.instagram.android': 'Instagram',
  'com.twitter.android': 'X (Twitter)',
  'com.facebook.katana': 'Facebook',
  'com.zhiliaoapp.musically': 'TikTok',
  'com.snapchat.android': 'Snapchat',
  'com.google.android.youtube': 'YouTube',
  'com.reddit.frontpage': 'Reddit',
  'com.discord': 'Discord',
  'com.netflix.mediaclient': 'Netflix',
  'com.spotify.music': 'Spotify',
  'com.google.android.gm': 'Gmail',
  'com.whatsapp': 'WhatsApp',
};

/// Configures and registers the Flutter background service.
/// Call this once from main() before runApp().
Future<void> initializeBackgroundService() async {
  final service = FlutterBackgroundService();

  // Dedicated notification channel for the persistent foreground-service notice
  const serviceChannel = AndroidNotificationChannel(
    'ontime_service_channel',
    'OnTime Session Monitor',
    description: 'Keeps OnTime running in the background during a focus session.',
    importance: Importance.low,
  );

  final notifPlugin = FlutterLocalNotificationsPlugin();
  await notifPlugin
      .resolvePlatformSpecificImplementation<
          AndroidFlutterLocalNotificationsPlugin>()
      ?.createNotificationChannel(serviceChannel);

  const androidConfig = AndroidConfiguration(
    onStart: onStart,
    autoStart: false,
    isForegroundMode: true,
    notificationChannelId: 'ontime_service_channel',
    initialNotificationTitle: 'OnTime Active',
    initialNotificationContent: 'Monitoring your focus session…',
    foregroundServiceNotificationId: 888,
  );

  await service.configure(
    androidConfiguration: androidConfig,
    iosConfiguration: IosConfiguration(autoStart: false),
  );
}

/// Entry point for the background isolate.
/// Annotated so the Dart compiler does not tree-shake it.
@pragma('vm:entry-point')
void onStart(ServiceInstance service) async {
  // Required to use Flutter plugins inside a background isolate.
  DartPluginRegistrant.ensureInitialized();

  // Initialise local notifications inside this isolate.
  final notifPlugin = FlutterLocalNotificationsPlugin();
  const androidSettings = AndroidInitializationSettings('@mipmap/ic_launcher');
  await notifPlugin.initialize(
    const InitializationSettings(android: androidSettings),
  );

  // Open Isar in this isolate (must use the same directory / name as main).
  final dir = await getApplicationDocumentsDirectory();
  final isar = await Isar.open(
    [FocusSessionSchema],
    directory: dir.path,
    name: 'default',
  );

  String? _lastBlockedPackage;

  // Poll every second.
  final timer = Timer.periodic(const Duration(seconds: 1), (timer) async {
    // Stop immediately if the service is no longer a foreground service.
    if (service is AndroidServiceInstance &&
        !(await service.isForegroundService())) {
      timer.cancel();
      return;
    }

    try {
      // Fetch the currently active focus session.
      final activeSessions = await isar.focusSessions
          .filter()
          .isActiveEqualTo(true)
          .findAll();

      if (activeSessions.isEmpty) {
        _lastBlockedPackage = null;
        return;
      }

      final session = activeSessions.first;
      final now = DateTime.now();

      // Only enforce blocking within the session's time window.
      if (now.isBefore(session.startTime) || now.isAfter(session.endTime)) {
        _lastBlockedPackage = null;
        return;
      }

      if (session.blockedApps.isEmpty) return;

      // Query usage events for the last 3 seconds to find the foreground app.
      final endTime = now;
      final startTime = endTime.subtract(const Duration(seconds: 3));

      final events = await UsageStats.queryEvents(startTime, endTime);
      if (events == null || events.isEmpty) return;

      // eventType "1" == MOVE_TO_FOREGROUND (ACTIVITY_RESUMED).
      final foregroundEvents =
          events.where((e) => e.eventType == '1').toList();
      if (foregroundEvents.isEmpty) return;

      // Sort descending by timestamp to get the most-recent foreground event.
      foregroundEvents.sort((a, b) {
        final ta = int.tryParse(a.timeStamp ?? '0') ?? 0;
        final tb = int.tryParse(b.timeStamp ?? '0') ?? 0;
        return tb.compareTo(ta);
      });

      final currentPackage = foregroundEvents.first.packageName;
      if (currentPackage == null) return;

      if (session.blockedApps.contains(currentPackage)) {
        // Only fire a new notification when the blocked app changes.
        if (_lastBlockedPackage != currentPackage) {
          _lastBlockedPackage = currentPackage;

          final appLabel =
              _packageLabels[currentPackage] ?? currentPackage.split('.').last;

          const androidDetails = AndroidNotificationDetails(
            'ontime_focus_channel',
            'OnTime Focus Alerts',
            channelDescription:
                'High priority alerts when blocked apps are detected.',
            importance: Importance.max,
            priority: Priority.high,
            fullScreenIntent: true,
            autoCancel: true,
          );

          await notifPlugin.show(
            0,
            '🚫 Stay On Track!',
            '$appLabel is blocked. ${session.motivationMessage}',
            const NotificationDetails(android: androidDetails),
            payload: '/motivation',
          );
        }
      } else {
        _lastBlockedPackage = null;
      }
    } catch (e) {
      developer.log('[OnTime] Background poll error: ${e.runtimeType}: $e',
          name: 'ontime.background');
    }
  });

  // Allow the UI to stop the service cleanly.
  service.on('stopService').listen((_) {
    timer.cancel();
    isar.close();
    service.stopSelf();
  });
}

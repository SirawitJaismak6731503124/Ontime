import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:isar/isar.dart';
import 'package:ontime/models/focus_session.dart';
import 'package:ontime/providers/database_provider.dart';
import 'package:ontime/screens/home_screen.dart';
import 'package:ontime/screens/motivation_screen.dart';
import 'package:ontime/services/background_service.dart';
import 'package:ontime/services/notification_service.dart';
import 'package:path_provider/path_provider.dart';

/// Global navigator key so that notification taps can trigger navigation
/// even when the callback fires outside the widget tree.
final GlobalKey<NavigatorState> navigatorKey = GlobalKey<NavigatorState>();

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // Open the Isar database.
  final dir = await getApplicationDocumentsDirectory();
  final isar = await Isar.open(
    [FocusSessionSchema],
    directory: dir.path,
    name: 'default',
  );

  // Set up local notifications and check if the app was launched via one.
  await NotificationService.initialize();
  final launchPayload = await NotificationService.getNotificationLaunchPayload();

  // Set up the background monitoring service.
  await initializeBackgroundService();

  runApp(
    ProviderScope(
      overrides: [
        isarProvider.overrideWithValue(isar),
      ],
      child: OnTimeApp(initialRoute: launchPayload ?? '/'),
    ),
  );
}

class OnTimeApp extends StatefulWidget {
  final String initialRoute;

  const OnTimeApp({super.key, required this.initialRoute});

  @override
  State<OnTimeApp> createState() => _OnTimeAppState();
}

class _OnTimeAppState extends State<OnTimeApp> {
  @override
  void initState() {
    super.initState();
    // Register navigation callback for foreground notification taps.
    NotificationService.setNavigationCallback((route) {
      navigatorKey.currentState?.pushNamed(route);
    });
    // Consume any route queued while the app was launching.
    WidgetsBinding.instance.addPostFrameCallback((_) {
      final pending = NotificationService.consumePendingRoute();
      if (pending != null && pending.isNotEmpty) {
        navigatorKey.currentState?.pushNamed(pending);
      }
    });
  }

  @override
  void dispose() {
    NotificationService.clearNavigationCallback();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'OnTime',
      debugShowCheckedModeBanner: false,
      navigatorKey: navigatorKey,
      theme: ThemeData(
        brightness: Brightness.dark,
        scaffoldBackgroundColor: Colors.black,
        appBarTheme: const AppBarTheme(
          backgroundColor: Colors.black,
          foregroundColor: Colors.white,
          elevation: 0,
          centerTitle: true,
        ),
        colorScheme: const ColorScheme.dark(
          surface: Colors.black,
          primary: Colors.white,
          onPrimary: Colors.black,
          secondary: Colors.white70,
        ),
        textTheme: const TextTheme(
          bodyLarge: TextStyle(color: Colors.white),
          bodyMedium: TextStyle(color: Colors.white70),
        ),
        inputDecorationTheme: const InputDecorationTheme(
          labelStyle: TextStyle(color: Colors.white60),
          hintStyle: TextStyle(color: Colors.white30),
          enabledBorder: OutlineInputBorder(
            borderSide: BorderSide(color: Colors.white24),
          ),
          focusedBorder: OutlineInputBorder(
            borderSide: BorderSide(color: Colors.white),
          ),
          errorBorder: OutlineInputBorder(
            borderSide: BorderSide(color: Colors.red),
          ),
          focusedErrorBorder: OutlineInputBorder(
            borderSide: BorderSide(color: Colors.red),
          ),
        ),
      ),
      initialRoute: widget.initialRoute,
      routes: {
        '/': (context) => const HomeScreen(),
        '/motivation': (context) => const MotivationScreen(),
      },
    );
  }
}

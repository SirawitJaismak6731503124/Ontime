import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:ontime/providers/session_provider.dart';

/// Full-screen motivation display shown when the user taps a blocked-app
/// notification. Reads the motivation message from the currently active session.
class MotivationScreen extends ConsumerWidget {
  const MotivationScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final sessionsAsync = ref.watch(sessionsProvider);

    final message = sessionsAsync.when(
      data: (sessions) {
        final active = sessions.where((s) => s.isActive).firstOrNull;
        return active?.motivationMessage ?? 'Stay focused and keep going!';
      },
      loading: () => 'Stay focused and keep going!',
      error: (_, __) => 'Stay focused and keep going!',
    );

    final sessionName = sessionsAsync.when(
      data: (sessions) {
        final active = sessions.where((s) => s.isActive).firstOrNull;
        return active?.name;
      },
      loading: () => null,
      error: (_, __) => null,
    );

    return Scaffold(
      backgroundColor: Colors.black,
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 32),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              const Text(
                'Stay\nFocused!',
                textAlign: TextAlign.center,
                style: TextStyle(
                  color: Colors.white,
                  fontSize: 48,
                  fontWeight: FontWeight.bold,
                  letterSpacing: 2,
                  height: 1.1,
                ),
              ),
              if (sessionName != null) ...[
                const SizedBox(height: 12),
                Text(
                  sessionName,
                  textAlign: TextAlign.center,
                  style: const TextStyle(
                    color: Colors.white38,
                    fontSize: 14,
                    letterSpacing: 1.5,
                  ),
                ),
              ],
              const SizedBox(height: 48),
              Container(
                padding: const EdgeInsets.all(28),
                decoration: BoxDecoration(
                  border: Border.all(color: Colors.white12),
                  borderRadius: BorderRadius.circular(16),
                ),
                child: Text(
                  '"$message"',
                  textAlign: TextAlign.center,
                  style: const TextStyle(
                    color: Colors.white70,
                    fontSize: 20,
                    fontStyle: FontStyle.italic,
                    height: 1.6,
                  ),
                ),
              ),
              const SizedBox(height: 56),
              ElevatedButton(
                onPressed: () => Navigator.pushNamedAndRemoveUntil(
                  context,
                  '/',
                  (route) => false,
                ),
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.white,
                  foregroundColor: Colors.black,
                  minimumSize: const Size.fromHeight(52),
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(8)),
                ),
                child: const Text(
                  'Go Back to Study',
                  style: TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.bold,
                    letterSpacing: 0.5,
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:ontime/models/focus_session.dart';
import 'package:ontime/providers/session_provider.dart';
import 'package:ontime/screens/create_session_screen.dart';
import 'package:ontime/widgets/session_card.dart';

class HomeScreen extends ConsumerWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final sessionsAsync = ref.watch(sessionsProvider);

    return Scaffold(
      backgroundColor: Colors.black,
      appBar: AppBar(
        backgroundColor: Colors.black,
        title: const Text(
          'OnTime',
          style: TextStyle(
            color: Colors.white,
            fontSize: 26,
            fontWeight: FontWeight.bold,
            letterSpacing: 3,
          ),
        ),
        centerTitle: true,
      ),
      body: sessionsAsync.when(
        loading: () => const Center(
          child: CircularProgressIndicator(color: Colors.white54),
        ),
        error: (e, _) => Center(
          child: Text(
            'Error loading sessions:\n$e',
            textAlign: TextAlign.center,
            style: const TextStyle(color: Colors.red, fontSize: 14),
          ),
        ),
        data: (sessions) {
          if (sessions.isEmpty) {
            return const Center(
              child: Text(
                'No focus sessions yet.\nTap + to create one.',
                textAlign: TextAlign.center,
                style: TextStyle(color: Colors.white38, fontSize: 16),
              ),
            );
          }

          return ListView.builder(
            padding: const EdgeInsets.fromLTRB(16, 16, 16, 100),
            itemCount: sessions.length,
            itemBuilder: (context, index) {
              final session = sessions[index];
              return SessionCard(
                session: session,
                onTap: () => _showDetails(context, ref, session),
              );
            },
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => Navigator.push(
          context,
          MaterialPageRoute(builder: (_) => const CreateSessionScreen()),
        ),
        backgroundColor: Colors.white,
        foregroundColor: Colors.black,
        child: const Icon(Icons.add),
      ),
    );
  }

  void _showDetails(
      BuildContext context, WidgetRef ref, FocusSession session) {
    showModalBottomSheet(
      context: context,
      backgroundColor: const Color(0xFF111111),
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      builder: (ctx) => Padding(
        padding: const EdgeInsets.fromLTRB(24, 20, 24, 32),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Drag handle
            Center(
              child: Container(
                width: 40,
                height: 4,
                decoration: BoxDecoration(
                  color: Colors.white24,
                  borderRadius: BorderRadius.circular(2),
                ),
              ),
            ),
            const SizedBox(height: 20),
            Text(
              session.name,
              style: const TextStyle(
                color: Colors.white,
                fontSize: 22,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 8),
            _detailRow(
              Icons.schedule,
              '${_fmt(session.startTime)} – ${_fmt(session.endTime)}',
            ),
            const SizedBox(height: 6),
            _detailRow(
              Icons.block,
              '${session.blockedApps.length} app(s) blocked',
            ),
            const SizedBox(height: 10),
            Text(
              '"${session.motivationMessage}"',
              style: const TextStyle(
                color: Colors.white54,
                fontSize: 13,
                fontStyle: FontStyle.italic,
              ),
            ),
            const SizedBox(height: 24),
            Row(
              children: [
                Expanded(
                  child: ElevatedButton(
                    onPressed: () {
                      if (session.isActive) {
                        ref
                            .read(sessionsProvider.notifier)
                            .deactivateSession(session.id);
                      } else {
                        ref
                            .read(sessionsProvider.notifier)
                            .activateSession(session.id);
                      }
                      Navigator.pop(ctx);
                    },
                    style: ElevatedButton.styleFrom(
                      backgroundColor:
                          session.isActive ? Colors.red[900] : Colors.white,
                      foregroundColor:
                          session.isActive ? Colors.white : Colors.black,
                      padding: const EdgeInsets.symmetric(vertical: 14),
                      shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(8)),
                    ),
                    child: Text(
                      session.isActive ? 'Deactivate' : 'Activate',
                      style: const TextStyle(fontWeight: FontWeight.bold),
                    ),
                  ),
                ),
                const SizedBox(width: 12),
                IconButton(
                  tooltip: 'Delete session',
                  onPressed: () {
                    ref
                        .read(sessionsProvider.notifier)
                        .deleteSession(session.id);
                    Navigator.pop(ctx);
                  },
                  icon: const Icon(Icons.delete_outline, color: Colors.red),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _detailRow(IconData icon, String text) {
    return Row(
      children: [
        Icon(icon, color: Colors.white38, size: 16),
        const SizedBox(width: 6),
        Text(text, style: const TextStyle(color: Colors.white60, fontSize: 14)),
      ],
    );
  }

  String _fmt(DateTime dt) =>
      '${dt.hour.toString().padLeft(2, '0')}:${dt.minute.toString().padLeft(2, '0')}';
}

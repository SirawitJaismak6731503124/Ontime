import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:isar/isar.dart';
import 'package:ontime/models/focus_session.dart';
import 'package:ontime/providers/database_provider.dart';

class SessionsNotifier extends AsyncNotifier<List<FocusSession>> {
  @override
  Future<List<FocusSession>> build() async {
    final isar = ref.watch(isarProvider);
    return isar.focusSessions.where().sortByStartTime().findAll();
  }

  Future<void> createSession(FocusSession session) async {
    final isar = ref.read(isarProvider);
    await isar.writeTxn(() async {
      await isar.focusSessions.put(session);
    });
    ref.invalidateSelf();
  }

  /// Deactivates all sessions, then activates the one with [id].
  Future<void> activateSession(Id id) async {
    final isar = ref.read(isarProvider);
    await isar.writeTxn(() async {
      final all = await isar.focusSessions.where().findAll();
      for (final s in all) {
        s.isActive = false;
      }
      await isar.focusSessions.putAll(all);

      final target = await isar.focusSessions.get(id);
      if (target != null) {
        target.isActive = true;
        await isar.focusSessions.put(target);
      }
    });
    ref.invalidateSelf();
  }

  Future<void> deactivateSession(Id id) async {
    final isar = ref.read(isarProvider);
    await isar.writeTxn(() async {
      final session = await isar.focusSessions.get(id);
      if (session != null) {
        session.isActive = false;
        await isar.focusSessions.put(session);
      }
    });
    ref.invalidateSelf();
  }

  Future<void> deleteSession(Id id) async {
    final isar = ref.read(isarProvider);
    await isar.writeTxn(() async {
      await isar.focusSessions.delete(id);
    });
    ref.invalidateSelf();
  }
}

final sessionsProvider =
    AsyncNotifierProvider<SessionsNotifier, List<FocusSession>>(
  SessionsNotifier.new,
);

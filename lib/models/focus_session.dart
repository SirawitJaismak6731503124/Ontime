import 'package:isar/isar.dart';

part 'focus_session.g.dart';

@collection
class FocusSession {
  Id id = Isar.autoIncrement;

  late String name;

  late DateTime startTime;

  late DateTime endTime;

  late String motivationMessage;

  late List<String> blockedApps;

  late bool isActive;
}

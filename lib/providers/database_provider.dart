import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:isar/isar.dart';

/// Holds the global Isar instance.
/// Override this provider in main() once the database is opened.
final isarProvider = Provider<Isar>((ref) {
  throw UnimplementedError(
    'isarProvider must be overridden with an actual Isar instance.',
  );
});

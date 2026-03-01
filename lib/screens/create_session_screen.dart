import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:ontime/models/focus_session.dart';
import 'package:ontime/models/restricted_app.dart';
import 'package:ontime/providers/session_provider.dart';

/// A curated list of commonly distracting apps used as the default blocking menu.
const List<RestrictedApp> _commonApps = [
  RestrictedApp(packageName: 'com.instagram.android', appName: 'Instagram'),
  RestrictedApp(packageName: 'com.twitter.android', appName: 'X (Twitter)'),
  RestrictedApp(packageName: 'com.facebook.katana', appName: 'Facebook'),
  RestrictedApp(packageName: 'com.zhiliaoapp.musically', appName: 'TikTok'),
  RestrictedApp(packageName: 'com.snapchat.android', appName: 'Snapchat'),
  RestrictedApp(
      packageName: 'com.google.android.youtube', appName: 'YouTube'),
  RestrictedApp(packageName: 'com.reddit.frontpage', appName: 'Reddit'),
  RestrictedApp(packageName: 'com.discord', appName: 'Discord'),
  RestrictedApp(
      packageName: 'com.netflix.mediaclient', appName: 'Netflix'),
  RestrictedApp(packageName: 'com.spotify.music', appName: 'Spotify'),
  RestrictedApp(
      packageName: 'com.google.android.gm', appName: 'Gmail'),
  RestrictedApp(
      packageName: 'com.whatsapp', appName: 'WhatsApp'),
];

class CreateSessionScreen extends ConsumerStatefulWidget {
  const CreateSessionScreen({super.key});

  @override
  ConsumerState<CreateSessionScreen> createState() =>
      _CreateSessionScreenState();
}

class _CreateSessionScreenState extends ConsumerState<CreateSessionScreen> {
  final _formKey = GlobalKey<FormState>();
  final _nameController = TextEditingController();
  final _motivationController = TextEditingController();

  DateTime _startTime = DateTime.now();
  DateTime _endTime = DateTime.now().add(const Duration(hours: 2));
  final Set<String> _selectedApps = {};

  @override
  void dispose() {
    _nameController.dispose();
    _motivationController.dispose();
    super.dispose();
  }

  Future<void> _pickTime({required bool isStart}) async {
    final current = isStart ? _startTime : _endTime;
    final picked = await showTimePicker(
      context: context,
      initialTime: TimeOfDay(hour: current.hour, minute: current.minute),
      builder: (ctx, child) => Theme(
        data: ThemeData.dark().copyWith(
          colorScheme: const ColorScheme.dark(primary: Colors.white),
          timePickerTheme: const TimePickerThemeData(
            backgroundColor: Color(0xFF111111),
          ),
        ),
        child: child!,
      ),
    );

    if (picked != null && mounted) {
      setState(() {
        final now = DateTime.now();
        final updated =
            DateTime(now.year, now.month, now.day, picked.hour, picked.minute);
        if (isStart) {
          _startTime = updated;
        } else {
          _endTime = updated;
        }
      });
    }
  }

  Future<void> _save() async {
    if (!_formKey.currentState!.validate()) return;

    final session = FocusSession()
      ..name = _nameController.text.trim()
      ..startTime = _startTime
      ..endTime = _endTime
      ..motivationMessage = _motivationController.text.trim()
      ..blockedApps = _selectedApps.toList()
      ..isActive = false;

    await ref.read(sessionsProvider.notifier).createSession(session);

    if (mounted) Navigator.pop(context);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      appBar: AppBar(
        backgroundColor: Colors.black,
        foregroundColor: Colors.white,
        title: const Text(
          'New Focus Session',
          style: TextStyle(color: Colors.white),
        ),
      ),
      body: Form(
        key: _formKey,
        child: ListView(
          padding: const EdgeInsets.all(20),
          children: [
            _buildTextField(
              controller: _nameController,
              label: 'Session Name',
              hint: 'e.g. Math Study Block',
              validator: (v) =>
                  (v == null || v.trim().isEmpty) ? 'Enter a session name' : null,
            ),
            const SizedBox(height: 20),
            _buildTimeTile(
              label: 'Start Time',
              time: _startTime,
              onTap: () => _pickTime(isStart: true),
            ),
            const SizedBox(height: 12),
            _buildTimeTile(
              label: 'End Time',
              time: _endTime,
              onTap: () => _pickTime(isStart: false),
            ),
            const SizedBox(height: 20),
            _buildTextField(
              controller: _motivationController,
              label: 'Motivation Message',
              hint: 'e.g. Stay strong — future you will thank you!',
              maxLines: 3,
              validator: (v) => (v == null || v.trim().isEmpty)
                  ? 'Enter a motivation message'
                  : null,
            ),
            const SizedBox(height: 28),
            const Text(
              'Apps to Block',
              style: TextStyle(
                color: Colors.white,
                fontSize: 16,
                fontWeight: FontWeight.w600,
                letterSpacing: 0.5,
              ),
            ),
            const SizedBox(height: 4),
            const Text(
              'Select distracting apps to block during this session.',
              style: TextStyle(color: Colors.white38, fontSize: 12),
            ),
            const SizedBox(height: 12),
            ..._commonApps.map(_buildAppToggle),
            const SizedBox(height: 32),
            ElevatedButton(
              onPressed: _save,
              style: ElevatedButton.styleFrom(
                backgroundColor: Colors.white,
                foregroundColor: Colors.black,
                minimumSize: const Size.fromHeight(52),
                shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(8)),
              ),
              child: const Text(
                'Create Session',
                style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildTextField({
    required TextEditingController controller,
    required String label,
    required String hint,
    int maxLines = 1,
    String? Function(String?)? validator,
  }) {
    return TextFormField(
      controller: controller,
      maxLines: maxLines,
      style: const TextStyle(color: Colors.white),
      decoration: InputDecoration(
        labelText: label,
        hintText: hint,
        labelStyle: const TextStyle(color: Colors.white54),
        hintStyle: const TextStyle(color: Colors.white24),
        enabledBorder: const OutlineInputBorder(
          borderSide: BorderSide(color: Colors.white24),
        ),
        focusedBorder: const OutlineInputBorder(
          borderSide: BorderSide(color: Colors.white),
        ),
        errorBorder: const OutlineInputBorder(
          borderSide: BorderSide(color: Colors.red),
        ),
        focusedErrorBorder: const OutlineInputBorder(
          borderSide: BorderSide(color: Colors.red),
        ),
      ),
      validator: validator,
    );
  }

  Widget _buildTimeTile({
    required String label,
    required DateTime time,
    required VoidCallback onTap,
  }) {
    final formatted =
        '${time.hour.toString().padLeft(2, '0')}:${time.minute.toString().padLeft(2, '0')}';
    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(4),
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 16),
        decoration: BoxDecoration(
          border: Border.all(color: Colors.white24),
          borderRadius: BorderRadius.circular(4),
        ),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(label,
                style: const TextStyle(color: Colors.white54, fontSize: 14)),
            Row(
              children: [
                Text(
                  formatted,
                  style: const TextStyle(
                      color: Colors.white,
                      fontSize: 18,
                      fontWeight: FontWeight.w500),
                ),
                const SizedBox(width: 8),
                const Icon(Icons.access_time, color: Colors.white38, size: 18),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildAppToggle(RestrictedApp app) {
    final selected = _selectedApps.contains(app.packageName);
    return CheckboxListTile(
      value: selected,
      onChanged: (val) {
        setState(() {
          if (val == true) {
            _selectedApps.add(app.packageName);
          } else {
            _selectedApps.remove(app.packageName);
          }
        });
      },
      title: Text(app.appName,
          style: const TextStyle(color: Colors.white, fontSize: 14)),
      subtitle: Text(
        app.packageName,
        style: const TextStyle(color: Colors.white30, fontSize: 11),
      ),
      checkColor: Colors.black,
      activeColor: Colors.white,
      side: const BorderSide(color: Colors.white24),
      contentPadding: const EdgeInsets.symmetric(horizontal: 0),
    );
  }
}

# OnTime Troubleshooting Guide

## Common Issues & Solutions

### Build System

#### ❌ "Gradle sync failed"
**Solutions:**
```bash
# 1. Clean and resync
./gradlew clean

# 2. Refresh dependencies
./gradlew build --refresh-dependencies

# 3. Check Java version (need 11+)
java -version

# 4. Clear Gradle cache
rm -rf ~/.gradle/caches
./gradlew build
```

#### ❌ "Cannot find symbol: Room"
**Solution:**
- Check `build.gradle.kts` has Room dependencies
- Run `./gradlew build` to download libraries
- Invalidate caches in Android Studio: File > Invalidate Caches

#### ❌ "Firebase imports not found"
**Solution:**
1. Ensure `google-services.json` is in `app/` folder
2. Run `./gradlew build` to download Firebase libraries
3. Check Firebase dependencies in `build.gradle.kts`

#### ❌ "java.version not compatible"
**Solution:**
```bash
# Check current Java
java -version

# Install Java 11+
# macOS: brew install openjdk@11
# Linux: sudo apt-get install openjdk-11-jdk

# Set JAVA_HOME
export JAVA_HOME=/path/to/openjdk11
./gradlew build
```

---

### Runtime Issues

#### ❌ "Service not monitoring apps"
**Symptoms:** No notifications when opening scheduled activities

**Solutions:**
1. **Enable Usage Stats Permission**
   - Settings > Apps > Special App Access > Usage Access
   - Find OnTime, enable it
   - **This is required!**

2. **Check Service Started**
   - View > Tool Windows > Logcat
   - Filter: "FocusMonitorService"
   - Should see startup logs

3. **Verify Active Session**
   - Create session with correct time
   - Current time should fall within session time block
   - Check system time is correct

4. **Check Blocked Apps List**
   - Session must have apps in activityList list
   - Package names must be exact

#### ❌ "Notification never appears"
**Solutions:**
1. **Grant Notification Permission**
   - Settings > Apps > OnTime > Notifications
   - Enable notifications

2. **Check Notification Channel**
   - Service creates channel automatically
   - Check Settings > Apps > OnTime > Notifications

3. **Verify Do Not Disturb**
   - Check device is not in DND mode
   - Settings > Notifications > Do Not Disturb

4. **Check Notification Priority**
   - Should be set to HIGH in FocusMonitorService.kt
   - Ensure NotificationCompat.PRIORITY_HIGH is used

#### ❌ "App crashes on launch"
**Solution:**
```bash
# Check logcat for error
./gradlew installDebug
adb logcat | grep "E/"

# Common causes:
# 1. Database corrupted: Uninstall app, clear data
# 2. Firebase config: Verify google-services.json
# 3. Permission denied: Grant permissions manually
```

#### ❌ "Database is empty"
**Solutions:**
1. **Check Local DB**
   - Android Studio: View > Tool Windows > Database Inspector
   - Navigate to OnTimeDatabase
   - Check focus_sessions table

2. **Check Firestore**
   - Go to Firebase Console
   - Firestore Database section
   - Check focus_sessions collection

3. **Manually Add Session**
   - Create a new session in app
   - Should appear in both locations

#### ❌ "Sessions not syncing to Firebase"
**Solutions:**
1. **Check Internet Connection**
   ```bash
   adb shell getprop | grep wifi
   ```

2. **Verify Firebase Config**
   - Ensure `google-services.json` is correct
   - Check package name matches
   - Package should be "com.ontime"

3. **Check Firestore Rules**
   - Firebase Console > Firestore > Rules
   - Ensure allow read, write is set
   - Review: `allow read, write: if request.auth != null;`

4. **Enable Offline Persistence**
   - Room DB handles local storage
   - Firestore syncs when online
   - Check network connectivity

5. **Check Logs**
   ```bash
   adb logcat | grep "Firestore"
   adb logcat | grep "Repository"
   ```

---

### UI Issues

#### ❌ "Text not visible (white on white)"
**Solution:**
- Check theme colors in `Color.kt`
- Verify background is DeepBlack
- Text should be WhiteText or LightGrey

#### ❌ "UI elements overlapping"
**Solutions:**
1. Check padding/margin values
2. Verify constraint layout
3. Use Android Studio Layout Inspector
4. Check Compose preview errors

#### ❌ "Button not clickable"
**Solutions:**
1. Check modifier has clickable()
2. Verify onClick lambda is correct
3. Check Compose version compatibility
4. Try in both light and dark theme

#### ❌ "Images not showing"
**Solution:**
- Check drawable resources in res/ folder
- Verify painterResource() path is correct
- Use vector/SVG for better scaling

---

### Device Issues

#### ❌ "Device not detected"
**Solutions:**
```bash
# Check connection
adb devices

# If empty, try:
adb kill-server
adb start-server
adb devices

# Check USB permission on device
# Settings > Applications > Special access > USB

# On Linux, may need udev rules
sudo apt-get install android-sdk-adb-extension
```

#### ❌ "App won't install"
**Solutions:**
```bash
# Uninstall previous version
adb uninstall com.ontime

# Clear cache
adb shell pm clear com.ontime

# Reinstall
./gradlew installDebug
```

#### ❌ "Emulator too slow"
**Solutions:**
1. Use hardware acceleration
   - AMD: Enable AMD V
   - Intel: Enable VT-x
   - NVIDIA: Check CUDA settings

2. Increase RAM for emulator
   - Device Manager > Edit > Advanced Settings

3. Use faster device image
   - Android 11+ recommended
   - Use x86_64, not ARM

---

### Firebase Issues

#### ❌ "com.google.gms.tasks.OnFailureListener error"
**Solutions:**
1. Verify `google-services.json` exists in `app/` folder
2. Check package name matches Firebase project
3. Ensure Firebase project is created
4. Check internet connectivity

#### ❌ "Firestore permission denied"
**Solution:**
Update security rules in Firebase Console:
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /focus_sessions/{document=**} {
      allow read, write: if true;  // For testing only!
    }
  }
}
```

#### ❌ "Firebase initialization fails"
**Solutions:**
```bash
# Check logcat
adb logcat | grep "Firebase"
adb logcat | grep "google-services"

# Rebuild project
./gradlew clean build
```

---

### Memory Issues

#### ❌ "Out of memory error - build fails"
**Solution:**
Increase heap size in `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx8192m
```

#### ❌ "App crashes - OutOfMemoryError"
**Solutions:**
1. Check for memory leaks
2. Profile with Android Studio Profiler
3. Clear large objects after use
4. Reduce high-resolution images

---

### Performance Issues

#### ❌ "App is slow/laggy"
**Solutions:**
1. **Check foreground service monitoring**
   - 1-second interval is reasonable
   - Increase to 2-5 seconds if needed
   - Modify FocusMonitorService.kt: `delay(1000)`

2. **Profile with Android Studio**
   - View > Tool Windows > Profiler
   - Check CPU, memory, network

3. **Optimize database queries**
   - Use Room's Flow for reactive updates
   - Avoid frequent queries

4. **Reduce notification frequency**
   - Don't show notification every second
   - Add cooldown between notifications

---

### Debugging Tips

#### Enable Verbose Logging
```kotlin
// Add to services and ViewModels
import android.util.Log

Log.d("OnTime", "Debug message here")
Log.e("OnTime", "Error: $errorMessage", exception)
```

#### Check Logcat
```bash
# Real-time logs
adb logcat | grep OnTime

# Save to file
adb logcat | grep OnTime > logs.txt

# Filter by level
adb logcat *:E  # Errors only
adb logcat *:W  # Warnings and errors
```

#### Use Android Studio Debugger
1. Set breakpoints (click line number)
2. Run in debug mode: Shift+F9
3. Step through code: F10 (step over), F11 (step into)
4. Inspect variables on hover

#### Check Resource Usage
```bash
# Memory
adb shell dumpsys meminfo | grep ontime

# Battery
adb shell dumpsys batterystats | grep -A20 ontime

# Processes
adb shell ps | grep ontime
```

---

### Code Issues

#### ❌ "CompilationError: Unresolved reference"
**Solutions:**
1. Check imports are correct
2. Verify dependencies in build.gradle
3. Rebuild: `./gradlew build`
4. Invalidate Android Studio cache

#### ❌ "Type mismatch error"
**Solution:**
Check Kotlin type expectations:
- Firestore returns `Map<String, Any>`
- Need to convert to `FocusSession`
- Use `.toObjects(FocusSession::class.java)`

#### ❌ "Coroutine not launching"
**Solutions:**
1. Verify viewModelScope is used
2. Check Dispatchers are correct
3. Ensure suspend functions complete
4. Use `.collect()` for Flow

---

## Diagnostic Checklist

Run this when experiencing issues:

```bash
# 1. Check build
./gradlew build

# 2. Check adb connection
adb devices

# 3. Reinstall app
adb uninstall com.ontime
./gradlew installDebug

# 4. Check logs
adb logcat | grep OnTime

# 5. Check database
# Android Studio > Database Inspector

# 6. Check Firestore
# Firebase Console > Firestore

# 7. Check permissions
adb shell pm list permissions -g | grep ontime

# 8. Check services running
adb shell ps | grep com.ontime
```

---

## When All Else Fails

1. **Full Clean Rebuild**
   ```bash
   ./gradlew clean
   rm -rf build/
   rm -rf .gradle/
   ./gradlew build
   ```

2. **Invalidate Android Studio Caches**
   - File > Invalidate Caches
   - Restart IDE

3. **Uninstall and Reinstall**
   ```bash
   adb uninstall com.ontime
   adb shell pm clear com.ontime
   ./gradlew installDebug
   ```

4. **Check System Requirements**
   - Java 11+ installed
   - Android SDK 28+ available
   - 10GB+ disk space
   - 8GB+ RAM

5. **Reach Out for Help**
   - Check logcat for error messages
   - Review relevant documentation
   - Share error logs when asking for help

---

## Quick Reference

| Issue | File to Check | Command |
|-------|---------------|---------|
| Compile error | build.gradle.kts | `./gradlew build` |
| Runtime crash | MainActivity.kt | `adb logcat` |
| Database issue | OnTimeDatabase.kt | Database Inspector |
| Firebase error | FirebaseRepository.kt | `adb logcat | grep Firebase` |
| Service not running | FocusMonitorService.kt | `adb shell ps` |
| Permission denied | AndroidManifest.xml | Settings > Apps |
| UI broken | *Screen.kt files | Android Studio Preview |
| Notification missing | FocusMonitorService.kt | `adb logcat | grep Notif` |

---

Still stuck? Check:
- [README.md](./README.md) - Full documentation
- [QUICKSTART.md](./QUICKSTART.md) - Setup guide
- [FIREBASE_SETUP.md](./FIREBASE_SETUP.md) - Firebase config
- [SETUP.md](./SETUP.md) - Development environment

**Good luck debugging!** 🐛✨

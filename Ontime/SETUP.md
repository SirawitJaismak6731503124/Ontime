# Local Development Setup

## System Requirements

- **OS**: macOS, Windows (WSL2), or Linux
- **Java**: JDK 11 or higher
- **Android SDK**: API 28+
- **RAM**: 8GB minimum (16GB recommended)
- **Disk Space**: 10GB for SDK and build artifacts

## Installation Steps

### 1. Install Android Studio

Download from https://developer.android.com/studio

**Configuration**:
- Install Android SDK Platform 34
- Install Android SDK Tools
- Install Android Emulator (optional)

### 2. Install Java Development Kit

```bash
# macOS
brew install openjdk@11

# Linux
sudo apt-get install openjdk-11-jdk

# Add to PATH
export JAVA_HOME=/path/to/jdk11
export PATH=$JAVA_HOME/bin:$PATH
```

### 3. Clone Repository

```bash
cd /workspaces/Ontime/Ontime
```

### 4. Configure Gradle

Create `local.properties` in project root:

```properties
sdk.dir=/path/to/android/sdk
org.gradle.jvmargs=-Xmx4096m
```

### 5. Download Dependencies

```bash
./gradlew build --refresh-dependencies
```

This will download:
- Kotlin compiler
- Jetpack Compose compiler
- Room database libraries
- Firebase libraries
- All dependencies specified in `build.gradle.kts`

## IDE Setup

### Android Studio

1. **Open Project**: File > Open > Select Ontime folder
2. **Wait for Indexing**: Let Gradle sync complete
3. **Configure SDK**: 
   - File > Project Structure > SDK Location
   - Point to your Android SDK
4. **Run App**:
   - Connect device or start emulator
   - Click Run button or press Shift+F10

### VS Code (with Extensions)

Install extensions:
- Kotlin Language
- Android Tools (optional)
- Better Comments

Then open folder and use terminal for builds.

## Device Setup

### Physical Android Device

1. **Enable Developer Mode**:
   - Settings > About Phone
   - Tap "Build Number" 7 times
   
2. **Enable USB Debugging**:
   - Settings > Developer Options
   - Toggle "USB Debugging"

3. **Connect Device**:
   ```bash
   adb devices
   ```

4. **Grant Permissions**:
   - Settings > Apps > OnTime > Permissions
   - Enable "Usage Stats"

### Android Emulator

Create emulator in Android Studio:
- Tools > Device Manager > Create Device
- Select Pixel 4 or higher
- Android 10+ (API 29+)
- Launch before running app

## Build from Command Line

### Debug Build

```bash
# Build
./gradlew build

# Install on device
./gradlew installDebug

# Install and run
./gradlew installDebug
adb shell am start -n com.ontime/.MainActivity
```

### Release Build

```bash
# Build
./gradlew bundleRelease

# Or unsigned APK
./gradlew assembleRelease
```

## Troubleshooting

### Gradle Sync Error

```bash
# Clean and rebuild
./gradlew clean build --refresh-dependencies
```

### Java Version Issues

```bash
# Check Java version
java -version

# Should output Java 11+
```

### Build Fails with Memory Error

Increase Gradle memory in `gradle.properties`:

```properties
org.gradle.jvmargs=-Xmx8192m
```

### Cannot Find Android SDK

Set SDK path explicitly:

```bash
export ANDROID_SDK_ROOT=/path/to/android/sdk
```

### ADB Device Not Found

```bash
# Reconnect device
adb kill-server
adb start-server
adb devices
```

## IDE Shortcuts (Android Studio)

| Action | Shortcut |
|--------|----------|
| Run App | Shift+F10 |
| Debug | Shift+F9 |
| Open Project | Cmd/Ctrl+O |
| Open File | Cmd/Ctrl+P |
| Terminal | Alt+F12 |
| Build | Cmd/Ctrl+B |
| Clean | (in menu) |
| Logcat | View > Tool Windows > Logcat |

## Useful Commands

```bash
# List connected devices
adb devices

# Install APK
adb install app-debug.apk

# Capture logcat
adb logcat > logs.txt

# Run tests
./gradlew test

# Check Kotlin compilation
./gradlew build -x test

# Format code
./gradlew spotlessApply
```

## Firebase Local Setup

1. Install Firebase CLI:
   ```bash
   npm install -g firebase-tools
   ```

2. Authenticate:
   ```bash
   firebase login
   ```

3. Connect to project:
   ```bash
   firebase projects:list
   ```

4. Emulate locally:
   ```bash
   firebase emulators:start
   ```

## Performance Monitoring

### Check Build Time

```bash
./gradlew build --profile

# Opens HTML report in build/reports/profile/
```

### Monitor Gradlewrapper Update

Every 6 months, update Gradle wrapper:

```bash
./gradlew wrapper --gradle-version 8.5
```

## Continuous Integration

For CI/CD (GitHub Actions, GitLab CI, etc.):

```yaml
# Example: Install dependencies
./gradlew build --offline

# Run tests
./gradlew test

# Build release
./gradlew bundleRelease
```

## Useful Resources

- [Android Studio Docs](https://developer.android.com/studio/intro)
- [Kotlin Docs](https://kotlinlang.org/docs/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose/documentation)
- [Firebase Documentation](https://firebase.google.com/docs)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Gradle Build Tool](https://docs.gradle.org/)

---

For Firebase-specific setup, see [FIREBASE_SETUP.md](./FIREBASE_SETUP.md)  
For app-specific setup, see [QUICKSTART.md](./QUICKSTART.md)

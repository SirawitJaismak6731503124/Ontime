# OnTime - Student Productivity App

A powerful Android app that helps students stay focused by monitoring their screen time and blocking distracting apps during scheduled focus sessions.

## Features

✨ **Focus Sessions Management**
- Create custom focus sessions with specific time blocks
- Set motivational reminder messages
- Block multiple distracting apps during each session

🔔 **Smart Interruption System**
- Real-time monitoring of foreground apps
- High-priority notifications when scheduled activities are opened
- Full-screen reminder display with motivational messages

💾 **Offline-First Architecture**
- Room Database for 100% offline functionality
- Automatic sync to Firebase Firestore
- Cross-device synchronization

## Architecture Overview

### Data Layer
- **Local Storage**: Room Database with `FocusSession` entity
- **Remote Storage**: Firebase Firestore for cloud backup and sync
- **Repository Pattern**: Separated data access logic

### UI Layer
- **Jetpack Compose**: Modern, declarative UI
- **Design System**: Deep black theme with high contrast
- **Screens**:
  - Dashboard: List of all focus sessions
  - Edit Session: Create/modify sessions with scheduled activities and reminders
  - Reminder: Full-screen motivational display

### Business Logic
- **ViewModel**: `FocusSessionViewModel` manages state and operations
- **Service**: `FocusMonitorService` runs in background, checks foreground app
- **Notification**: Heads-up high-priority notifications with PendingIntent

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Database**: Room + Firebase Firestore
- **Authentication**: Firebase Auth
- **Architecture**: MVVM + Repository Pattern
- **Coroutines**: For async operations
- **Dependency Injection**: Hilt (prepared for future use)

## Project Structure

```
Ontime/
├── app/
│   ├── src/main/
│   │   ├── java/com/ontime/
│   │   │   ├── MainActivity.kt
│   │   │   ├── data/
│   │   │   │   ├── model/FocusSession.kt
│   │   │   │   ├── local/
│   │   │   │   │   ├── FocusSessionDao.kt
│   │   │   │   │   └── OnTimeDatabase.kt
│   │   │   │   └── remote/FirebaseRepository.kt
│   │   │   ├── service/FocusMonitorService.kt
│   │   │   ├── ui/
│   │   │   │   ├── screens/
│   │   │   │   │   ├── DashboardScreen.kt
│   │   │   │   │   ├── EditSessionScreen.kt
│   │   │   │   │   └── ReminderScreen.kt
│   │   │   │   └── theme/
│   │   │   │       ├── Color.kt
│   │   │   │       ├── Theme.kt
│   │   │   │       └── Typography.kt
│   │   │   └── viewmodel/FocusSessionViewModel.kt
│   │   ├── AndroidManifest.xml
│   │   └── res/
│   ├── build.gradle.kts
│   └── google-services.json (configure with your Firebase project)
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties

```

## Setup Instructions

### 1. Clone the Repository
```bash
git clone <repository-url>
cd Ontime/Ontime
```

### 2. Configure Firebase

1. Go to [Firebase Console](https://firebase.google.com/console)
2. Create a new project named "OnTime"
3. Add Android app with package name: `com.ontime`
4. Download `google-services.json` from Firebase
5. Place it in `app/` directory (overwrite the template)

### 3. Build and Run

```bash
# Build the project
./gradlew build

# Run on connected device
./gradlew installDebug

# Or use Android Studio:
# File -> Open -> Select Ontime folder
# Click Run or press Shift + F10
```

### 4. Enable Usage Stats Permission

On your Android device:
- Go to Settings > Apps > Special app access > Usage access
- Find OnTime and enable it
- This allows the app to monitor foreground apps

## Usage Guide

### Creating a Focus Session

1. **Home Screen**: Tap "+ Add Session"
2. **Set Time**: Enter start and end times (e.g., "05:00 PM" - "07:00 PM")
3. **Add Title**: Name your session (e.g., "Doing Homework")
4. **Block Apps**: Type app names and tap "+" to add them
5. **Reminder**: Write a motivational message
6. **Save**: Tap "SAVE SESSION"

### During a Focus Session

- The app monitors your screen in the background
- If you open a scheduled activity, you'll receive a notification
- Tapping the notification shows your motivational message
- This keeps you accountable and focused

## Key Components

### FocusSession Data Model
```kotlin
data class FocusSession(
    val id: String,
    val title: String,
    val startTime: String,
    val endTime: String,
    val activityList: List<String>,
    val reminderMessage: String,
    val createdAt: Timestamp?,
    val isActive: Boolean
)
```

### FocusMonitorService
- Runs as a foreground service
- Checks foreground app every 1 second
- Queries Room database for active sessions
- Triggers notifications via NotificationManager

### Firebase Integration
- Automatic sync of sessions to Firestore
- Cloud backup for user's focus habits
- Foundation for future multi-device sync

## Permissions Required

- `PACKAGE_USAGE_STATS`: Monitor foreground app
- `INTERNET`: Firebase connectivity
- `POST_NOTIFICATIONS`: Show interruption notifications
- `FOREGROUND_SERVICE`: Background monitoring
- `QUERY_ALL_PACKAGES`: List installed apps (optional, for future app picker)

## Future Enhancements

- 📊 Analytics dashboard showing focus statistics
- 🎯 Smart app suggestions based on usage
- 🏆 Gamification with streaks and achievements
- sync across multiple devices
- 🔐 Enhanced privacy controls
- 🎨 Customizable themes and motivational quotes

## Troubleshooting

### Service Not Running
- Ensure "Usage access" permission is granted in Settings
- Check that the app is not in restricted battery mode
- Try killing and restarting the app

### Notifications Not Appearing
- Verify notification permissions are granted
- Check notification channel is created (automatic)
- Ensure app is not in Do Not Disturb mode

### Database Not Syncing
- Verify Firebase is properly configured
- Check internet connectivity
- Ensure user is authenticated (setup if using Auth)

## License

This project is provided as-is for educational purposes.

## Support

For issues or feature requests, please open an issue in the repository.

---

**Stay Focused. Stay Productive. OnTime!** ⏰

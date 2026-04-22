# OnTime - Quick Start Guide

## 5-Minute Setup

### Prerequisites
- Android Studio (latest)
- Android SDK 28+ installed
- Coffee ☕ (optional but recommended)

### Step 1: Open Project
```bash
# Navigate to workspace
cd /workspaces/Ontime/Ontime

# Open in Android Studio or build from command line
./gradlew build
```

### Step 2: Firebase Setup (5 minutes)
1. Go to https://firebase.google.com/console
2. Create project "OnTime"
3. Add Android app (package: `com.ontime`)
4. Download `google-services.json`
5. Place in `Ontime/Ontime/app/google-services.json`

### Step 3: Enable Permissions
1. Connect Android device
2. Go to Settings > Apps > Special app access > Usage access
3. Find OnTime and enable

### Step 4: Run
```bash
./gradlew installDebug
```

## Using the App

### 1️⃣ Create Your First Session
- Tap "+ Add Session"
- Set time (e.g., 5:00 PM - 7:00 PM)
- Choose title (Homework, Reading, Coding, etc.)
- Add distracting apps (TikTok, Instagram, etc.)
- Write motivational message
- Tap "SAVE SESSION"

### 2️⃣ Get Interrupted (Intentionally)
- Start your session
- Open a scheduled activity
- 📲 Notification appears
- See your motivational reminder
- Get back to work!

### 3️⃣ Analyze Your Data
- All sessions saved locally (offline works!)
- Synced to Firebase automatically
- Check analytics on web dashboard

## File Structure

```
app/
├── src/main/java/com/ontime/
│   ├── MainActivity.kt              # App entry point
│   ├── data/
│   │   ├── model/FocusSession.kt    # Data class
│   │   ├── local/                   # Room database
│   │   └── remote/                  # Firebase sync
│   ├── service/
│   │   └── FocusMonitorService.kt   # Background monitoring
│   ├── ui/
│   │   ├── screens/                 # Dashboard, Edit, Reminder
│   │   └── theme/                   # Colors, Typography
│   └── viewmodel/
│       └── FocusSessionViewModel.kt # State management
├── build.gradle.kts                 # Dependencies
├── AndroidManifest.xml              # Permissions, services
└── google-services.json             # Firebase config (configure!)
```

## Key Features

### 🎯 Dashboard
- See all focus sessions
- Tap to edit
- Visual app indicators

### 📝 Edit Screen
- Time block selector
- Scheduled activities manager
- Reminder message composer
- Save/Delete buttons

### 🔔 Reminder View
- Full-screen motivational message
- Shows scheduled activity name
- "Got it! Let's focus" button

### 🔄 Sync
- Offline: Uses Room database
- Online: Auto-syncs to Firebase
- Cross-device ready

## Common Issues & fixes

| Issue | Fix |
|-------|-----|
| Build fails | `./gradlew clean build` |
| Service not monitoring | Enable "Usage access" in Settings |
| No notifications | Check notification permissions |
| Firebase not syncing | Verify `google-services.json` is correct |
| Database empty | Check local DB or check Firestore console |

## Development Tips

### Debug Foreground App Detection
```kotlin
// Add logging in FocusMonitorService
Log.d("ForegroundApp", "Current: $currentApp, Blocked: ${session.activityList}")
```

### Test Database
- Use Android Studio's Database Inspector
- Path: `View > Tool Windows > Database Inspector`

### Test Notifications
- Tap a session to trigger mock interruption
- Check notification permission status

## What Works
✅ Local session management  
✅ Foreground app monitoring  
✅ High-priority notifications  
✅ Room database persistence  
✅ Firebase Firestore sync setup  
✅ Beautiful dark theme UI  

## What's Next
🚀 User authentication  
🚀 Analytics dashboard  
🚀 App suggestions based on usage  
🚀 Focus streaks & gamification  
🚀 multi-device sync  

## Need Help?
- Check `FIREBASE_SETUP.md` for Firebase config
- Check `README.md` for full documentation
- Enable debug logging in each component
- Use Logcat to debug service

## Testing Checklist

- [ ] Project builds successfully
- [ ] Firebase is configured
- [ ] Usage stats permission granted
- [ ] Can create a session
- [ ] Can see session in dashboard
- [ ] Session appears in Firebase Firestore
- [ ] Service starts on app launch
- [ ] Notification appears when scheduled activity opens

---

**Congratulations! You're ready to stay focused with OnTime!** 🚀

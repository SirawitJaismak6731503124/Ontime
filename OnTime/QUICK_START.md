# OnTime - Quick Start Guide

## 📱 Project Overview

**OnTime** is a focus-oriented productivity app built with **Jetpack Compose** that helps users maintain focus by blocking distracting apps during scheduled study sessions.

## 🏗️ Project Structure

```
OnTime/
├── app/
│   ├── src/main/kotlin/com/ontime/
│   │   ├── MainActivity.kt
│   │   ├── data/
│   │   │   └── FocusSession.kt
│   │   ├── ui/
│   │   │   ├── Navigation.kt
│   │   │   ├── components/
│   │   │   ├── screens/
│   │   │   └── theme/
│   │   └── viewmodel/
│   │       └── SessionViewModel.kt
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## 🚀 Getting Started

### Step 1: Open Project
1. Launch **Android Studio**
2. Select **"Open"** and navigate to `/workspaces/Ontime/OnTime`
3. Wait for Gradle sync to complete ⏳

### Step 2: Verify Setup
- Select **Android API 34** in SDK Manager (Tools → SDK Manager)
- Ensure minimum SDK is set to API 24

### Step 3: Build & Run
```bash
# Option 1: Use Android Studio UI
Click "Run" button or press Shift + F10

# Option 2: Build from terminal
./gradlew build

# Option 3: Install on device
./gradlew installDebug
```

## 📋 Key Files to Know

### Data Layer
- **[data/FocusSession.kt](app/src/main/kotlin/com/ontime/data/FocusSession.kt)** - Data class defining session structure

### UI Layer
- **[ui/screens/DashboardScreen.kt](app/src/main/kotlin/com/ontime/ui/screens/DashboardScreen.kt)** - Home screen
- **[ui/screens/SessionEditScreen.kt](app/src/main/kotlin/com/ontime/ui/screens/SessionEditScreen.kt)** - Create/Edit screens
- **[ui/components/SessionCard.kt](app/src/main/kotlin/com/ontime/ui/components/SessionCard.kt)** - Reusable session display
- **[ui/theme/Color.kt](app/src/main/kotlin/com/ontime/ui/theme/Color.kt)** - Color definitions

### State Management
- **[viewmodel/SessionViewModel.kt](app/src/main/kotlin/com/ontime/viewmodel/SessionViewModel.kt)** - State & logic

### Navigation
- **[ui/Navigation.kt](app/src/main/kotlin/com/ontime/ui/Navigation.kt)** - Screen routing

### Entry Point
- **[MainActivity.kt](app/src/main/kotlin/com/ontime/MainActivity.kt)** - App entry point

## 🎨 Design System

### Colors (High Contrast Dark Mode)
```
Background:     #000000 (Black)
Card Surface:   #1E1E1E (Dark Gray)
Primary:        #FFFFFF (White)
Secondary:      #808080 (Gray)
```

### Components
- **SessionCard** - Display focus sessions with time and blocked apps
- **AppChip** - Individual app badges
- **TimeBlockInput** - Show time range
- **TextField** - Input for title and reminder message
- **Button** - Material 3 styled actions

### Rounded Corners
- Cards: 20dp
- Buttons: 16dp
- Inputs: 12dp
- Chips: 8dp

## 🔄 Data Flow

```
User Action
    ↓
Screen Component
    ↓
ViewModel Method Called
    ↓
State Updated (StateFlow)
    ↓
Screen Recomposes
    ↓
Updated UI Displayed
```

### Example: Creating New Session
```
User taps "+ Add Session"
         ↓
viewModel.startAddingNewSession()
         ↓
Navigate to NewSession screen
         ↓
User fills form
         ↓
User taps "CREATE SESSION"
         ↓
viewModel.saveSession()
         ↓
Navigate back to Dashboard
         ↓
New session appears in list
```

## 📝 Core Components

### FocusSession Data Class
```kotlin
data class FocusSession(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val startTime: LocalTime = LocalTime.of(9, 0),
    val endTime: LocalTime = LocalTime.of(10, 0),
    val blockedApps: List<String> = emptyList(),
    val reminderMessage: String = ""
)
```

### SessionViewModel Methods
| Method | Purpose |
|--------|---------|
| `startAddingNewSession()` | Begin creating session |
| `startEditingSession(session)` | Edit existing session |
| `updateEditingSession(session)` | Update temporary state |
| `saveSession(session)` | Save to list |
| `cancelEditing()` | Discard changes |
| `addBlockedApp(app)` | Add app to block list |
| `removeBlockedApp(app)` | Remove from block list |

### Screens
1. **Dashboard** - Shows all sessions, "+ Add Session" button
2. **New Session** - Form to create session
3. **Edit Session** - Form to edit existing session

## 🎭 Navigation Routes
```
dashboard      → Home screen (default)
new_session    → Create new session screen
edit_session   → Edit session screen
```

## 💡 Usage Tips

### Adding a Session
1. Tap "+ Add Session" button
2. Fill in time, title, blocked apps, reminder
3. Tap "CREATE SESSION"
4. Session appears on dashboard

### Editing a Session
1. Tap on a SessionCard
2. Update desired fields
3. Tap "SAVE SESSION"
4. Changes reflected on dashboard

### Managing Blocked Apps
- Each app displays with an 'X' button to remove
- Tap '+' button to add more apps (future: implement app picker)

## 🔧 Configuration

### Build Configuration
All settings in `build.gradle.kts`:
- **Min SDK**: 24 (Android 7.0)
- **Compile SDK**: 34 (Android 14)
- **Target SDK**: 34

### Dependencies
- androidx.compose:compose-bom (Jetpack Compose)
- androidx.material3 (Material Design 3)
- androidx.navigation:navigation-compose (Navigation)
- androidx.lifecycle (ViewModel & StateFlow)

## 📚 Documentation Files

| File | Purpose |
|------|---------|
| [README.md](README.md) | Project overview |
| [ARCHITECTURE.md](ARCHITECTURE.md) | Detailed architecture & data flow |
| [COMPONENTS.md](COMPONENTS.md) | UI component reference |
| [IMPLEMENTATION.md](IMPLEMENTATION.md) | Best practices & troubleshooting |
| [QUICK_START.md](QUICK_START.md) | This file |

## 🧪 Testing

### Sample Data
The app initializes with test session:
```kotlin
FocusSession(
    title = "Doing Homework",
    startTime = LocalTime.of(17, 0),
    endTime = LocalTime.of(19, 0),
    blockedApps = listOf("TikTok", "Instagram")
)
```

This allows you to test UI without creating sessions manually.

## 🚨 Common Issues

### Issue: Gradle Sync Failed
**Solution**: `File → Invalidate Caches → Invalidate and Restart`

### Issue: Unresolved Reference Errors
**Solution**: `Build → Clean Project → Rebuild Project`

### Issue: App Doesn't Compile
**Solution**: Verify you have Android SDK 34 installed

See [IMPLEMENTATION.md](IMPLEMENTATION.md) for more troubleshooting.

## 🎯 Next Steps

### Phase 1 (Current) ✅
- ✅ UI/UX with Jetpack Compose
- ✅ State management with ViewModel
- ✅ Navigation between screens
- ✅ Material 3 dark theme

### Phase 2 (Recommended Next)
- [ ] App picker UI for selecting blocked apps
- [ ] Time picker dialog for setting session times
- [ ] Persistence with Room database
- [ ] Session notifications

### Phase 3 (Advanced)
- [ ] Actual app blocking (accessibility service)
- [ ] Focus analytics & reports
- [ ] Focus streak achievements
- [ ] Customizable themes

## 📖 Learning Resources

- **Jetpack Compose**: https://developer.android.com/jetpack/compose
- **Material 3 Design**: https://m3.material.io/
- **Navigation Compose**: https://developer.android.com/guide/navigation/navigation-compose
- **ViewModel & StateFlow**: https://developer.android.com/topic/architecture

## ✨ Key Features

✅ **Dark Mode** - High contrast, eye-friendly design
✅ **Material 3** - Modern Android design system
✅ **Jetpack Compose** - Declarative UI framework
✅ **State Management** - Clean MVVM architecture
✅ **Navigation** - Smooth screen transitions
✅ **Modular Code** - Easy to extend and maintain

## 🤝 Contributing

When extending the app:

1. **Create new screens** in `ui/screens/`
2. **Create new components** in `ui/components/`
3. **Add colors** to `ui/theme/Color.kt`
4. **Update ViewModel** for new state
5. **Add routes** to `Navigation.kt`

## 📱 Device Requirements

- **Min Android**: 7.0 (API 24)
- **Target Android**: 14 (API 34)
- **Screen Size**: Optimized for phones (600dp max width)

## 🔒 Current Limitations

- No actual app blocking (requires accessibility service)
- No persistent storage (in-memory only)
- No app picker - manage apps in UI
- No time picker - set times manually
- Dashboard only shows sessions (no detailed view)

## ⚠️ Important Notes

1. **Sample Data**: App starts with one test session for UI testing
2. **No Persistence**: Sessions lost on app restart (use Room database)
3. **UI-Only**: App doesn't actually block apps yet
4. **Material 3**: Full Material 3 support with dark theme only

## 🎓 Code Examples

### Display Sessions
```kotlin
LazyColumn {
    items(uiState.sessions) { session ->
        SessionCard(session = session)
    }
}
```

### Update State
```kotlin
Button(onClick = {
    viewModel.updateEditingSession(
        session.copy(title = "New Title")
    )
})
```

### Navigate
```kotlin
Button(onClick = {
    viewModel.startAddingNewSession()
    navController.navigate(Screen.NewSession.route)
})
```

## 📞 Getting Help

1. Check [IMPLEMENTATION.md](IMPLEMENTATION.md) for troubleshooting
2. Review [ARCHITECTURE.md](ARCHITECTURE.md) for data flow understanding
3. Check [COMPONENTS.md](COMPONENTS.md) for component usage
4. Refer to official Android documentation

---

**Happy coding! You're all set to develop the OnTime app. Start with the Dashboard screen and gradually add features. 🚀**

**Last Updated: March 2026**

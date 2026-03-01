# OnTime - Focus Session App

A productivity-focused Android app built with Jetpack Compose that helps users maintain focus by blocking distracting apps during scheduled study sessions.

## Project Structure

```
OnTime/
├── app/
│   ├── src/main/
│   │   ├── kotlin/com/ontime/
│   │   │   ├── MainActivity.kt              # Entry point
│   │   │   ├── data/
│   │   │   │   └── FocusSession.kt          # Data class for sessions
│   │   │   ├── ui/
│   │   │   │   ├── Navigation.kt            # Compose Navigation setup
│   │   │   │   ├── components/
│   │   │   │   │   ├── SessionCard.kt       # Session display card
│   │   │   │   │   └── TimeBlockInput.kt    # Time block display
│   │   │   │   ├── screens/
│   │   │   │   │   ├── DashboardScreen.kt   # Home/Dashboard screen
│   │   │   │   │   └── SessionEditScreen.kt # Edit/New session screen
│   │   │   │   └── theme/
│   │   │   │       ├── Color.kt             # Color definitions
│   │   │   │       └── Theme.kt             # Material 3 theme setup
│   │   │   └── viewmodel/
│   │   │       └── SessionViewModel.kt      # State management with StateFlow
│   │   ├── res/
│   │   │   ├── values/
│   │   │   │   ├── strings.xml              # App strings
│   │   │   │   └── themes.xml               # Android themes
│   │   │   └── AndroidManifest.xml
│   ├── build.gradle.kts                     # Module-level build config
│
├── build.gradle.kts                         # Project-level build config
├── settings.gradle.kts                      # Module includes
└── gradle.properties                        # Gradle properties
```

## Core Components

### 1. **Data Layer** (`FocusSession.kt`)
```kotlin
data class FocusSession(
    val id: String,                    // Unique identifier
    val title: String,                 // Session title (e.g., "Doing Homework")
    val startTime: LocalTime,          // Start time
    val endTime: LocalTime,            // End time
    val blockedApps: List<String>,     // List of blocked app names
    val reminderMessage: String        // Motivation message
)
```

### 2. **ViewModel** (`SessionViewModel.kt`)
Manages application state using Kotlin `StateFlow`:
- **Maintains a list of focus sessions**
- **Handles session CRUD operations** (Create, Read, Update, Delete)
- **Manages temporary editing state** for new/edited sessions
- **Tracks which screen is active** (Dashboard vs Edit Screen)

Key methods:
- `startAddingNewSession()` - Begin creating new session
- `startEditingSession(session)` - Edit existing session
- `updateEditingSession(session)` - Update temporary session
- `saveSession(session)` - Persist session to list
- `cancelEditing()` - Discard changes
- `addBlockedApp(app)` / `removeBlockedApp(app)` - Manage blocked apps list

### 3. **UI Layer**

#### Screens:
- **DashboardScreen**: Shows all focus sessions with "+ Add Session" button
- **SessionEditScreen**: Form to create/edit sessions (time, title, blocked apps, reminder)

#### Components:
- **SessionCard**: Displays a focus session with time range, title, and app tags
- **TimeBlockInput**: Shows start-end time in a card format
- **AppChip**: Individual blocked app badge

#### Navigation:
Routes:
- `dashboard` - Home screen
- `new_session` - Create new session
- `edit_session` - Edit existing session

### 4. **Theme** (`Color.kt` & `Theme.kt`)
- **Background**: `#000000` (Pure Black)
- **Card Surface**: `#1E1E1E` (Dark Gray)
- **Primary**: `#FFFFFF` (White) - Main text & accents
- **Secondary**: `#808080` (Light Gray) - Secondary text
- **Material 3**: Implemented with dark color scheme

## Design Features

✨ **Components with 16-24dp rounded corners**
✨ **High-contrast dark mode** for better focus
✨ **Smooth transitions** between screens with navigation
✨ **Responsive layout** with Column and LazyColumn composables

## Screens Overview

### 1. Dashboard Screen
```
┌─────────────────────────────┐
│ OnTime                       │
├─────────────────────────────┤
│ ┌───────────────────────┐   │
│ │ FOCUS SESSION         │   │
│ │ 05:00 PM – 07:00 PM   │   │
│ │ Doing Homework        │   │
│ │ [TikTok] [Instagram]  │   │
│ └───────────────────────┘   │
│                              │
│  ┌──────────────────────┐   │
│  │  + Add Session       │   │
│  └──────────────────────┘   │
└─────────────────────────────┘
```

### 2. New/Edit Session Screen
```
┌─────────────────────────────┐
│ ← NEW SESSION (or EDIT)      │
├─────────────────────────────┤
│ TIME BLOCK                   │
│ ┌───────────────────────┐   │
│ │ 09:00 AM — 10:00 AM   │   │
│ └───────────────────────┘   │
│                              │
│ TITLE                        │
│ ┌───────────────────────┐   │
│ │ e.g., Deep Work       │   │
│ └───────────────────────┘   │
│                              │
│ BLOCKED APPS                 │
│ ┌─────────────────────┐ ✕   │
│ │ TikTok              │     │
│ └─────────────────────┘     │
│ ┌─────────────────────┐ ✕   │
│ │ Instagram           │     │
│ └─────────────────────┘     │
│ ┌─────────────────────┐     │
│ │         +           │     │
│ └─────────────────────┘     │
│                              │
│ REMINDER MESSAGE             │
│ ┌───────────────────────┐   │
│ │ Write motivation here │   │
│ └───────────────────────┘   │
│                              │
│ ┌──────────────────────┐   │
│ │  CREATE SESSION      │   │
│ └──────────────────────┘   │
└─────────────────────────────┘
```

## Technologies Used

- **Kotlin** - Language
- **Jetpack Compose** - UI Framework
- **Material 3** - Design System
- **Navigation Compose** - Screen routing
- **ViewModel & StateFlow** - State management
- **Gradle KTS** - Build system

## Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24+ (Minimum target)
- Android SDK 34 (Recommended)

### Running the Project

1. **Open in Android Studio**:
   - File → Open → Select OnTime folder
   - Wait for Gradle sync to complete

2. **Build the project**:
   ```bash
   ./gradlew build
   ```

3. **Run on emulator/device**:
   - Click Run button or press `Shift + F10` in Android Studio

## Extension Points

### Adding Features:
1. **Add app selection UI** - Implement app picker to add blocked apps dynamically
2. **Time picker dialog** - Replace static time block with Material 3 TimePicker
3. **Persistence** - Integrate with Room database or DataStore
4. **Notifications** - Add reminder notifications at session start
5. **Blocking logic** - Implement actual app blocking mechanism via accessibility service
6. **Session analytics** - Track focus time and completion rates

### Customization:
- Modify colors in `Color.kt`
- Update typography in `Theme.kt`
- Add more screens in `Navigation.kt` and create new screen files
- Extend `FocusSession` with additional fields as needed

## Notes

- All screens use 20.dp horizontal padding for content
- Material 3 components with dark mode support
- Typography follows Material 3 guidelines
- State management uses unidirectional data flow
- Navigation handles back button properly with cancellation

---

**Built with ❤️ for focused productivity**

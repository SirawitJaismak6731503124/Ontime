# OnTime Implementation Guide

## Architecture Overview

The OnTime app follows **MVVM (Model-View-ViewModel)** architecture with **unidirectional data flow**.

```
┌──────────────────────────────────────────────────────┐
│                    Presentation Layer               │
│  (Screens & Components - Jetpack Compose UI)        │
│  ├── DashboardScreen                                │
│  ├── SessionEditScreen                              │
│  ├── SessionCard                                    │
│  └── TimeBlockInput                                 │
└──────────────────────────────────────────────────────┘
                          ▲
                          │ (collect state)
                          │
                          ▼
┌──────────────────────────────────────────────────────┐
│                   ViewModel Layer                    │
│  (SessionViewModel - State Management)               │
│  ├── StateFlow<SessionUiState>                      │
│  ├── List<FocusSession>                             │
│  └── editing session management                     │
└──────────────────────────────────────────────────────┘
                          ▲
                          │ (read/modify)
                          │
                          ▼
┌──────────────────────────────────────────────────────┐
│                     Data Layer                       │
│  (Models - FocusSession data class)                  │
│  ├── id: String                                     │
│  ├── title: String                                  │
│  ├── startTime: LocalTime                           │
│  ├── endTime: LocalTime                             │
│  ├── blockedApps: List<String>                      │
│  └── reminderMessage: String                        │
└──────────────────────────────────────────────────────┘
```

## Data Flow

### Creating a New Session:
```
User taps "+ Add Session"
         ↓
DashboardScreen calls viewModel.startAddingNewSession()
         ↓
ViewModel creates empty FocusSession and sets isAddingNewSession = true
         ↓
Navigation routes to SessionEditScreen
         ↓
SessionEditScreen shows form with empty fields
         ↓
User fills form and taps "CREATE SESSION"
         ↓
viewModel.saveSession(session) is called
         ↓
ViewModel adds session to list and clears editing state
         ↓
Navigation routes back to Dashboard
         ↓
Dashboard displays new session in list
```

### Editing an Session:
```
User taps on SessionCard
         ↓
onSessionClick callback triggers
         ↓
viewModel.startEditingSession(session) copies session to editing state
         ↓
Navigation routes to SessionEditScreen with isNewSession=false
         ↓
Form fields are populated with existing data
         ↓
User modifies fields (changes trigger updateEditingSession)
         ↓
User taps "SAVE SESSION"
         ↓
viewModel.saveSession updates existing session in list
         ↓
Returns to Dashboard with updated session
```

## State Management Details

### SessionUiState
```kotlin
data class SessionUiState(
    val sessions: List<FocusSession> = emptyList(),
    val editingSession: FocusSession? = null,
    val isAddingNewSession: Boolean = false
)
```

**Why StateFlow?**
- ✅ Hot flow - emits state updates automatically
- ✅ Thread-safe
- ✅ Observable - UI automatically updates when state changes
- ✅ Lifecycle-aware with compose

## Component Interactions

### DashboardScreen
```
┌─────────────────────────────────────────┐
│ DashboardScreen                         │
├─────────────────────────────────────────┤
│ Title: "OnTime"                         │
│                                         │
│ LazyColumn of SessionCards:             │
│  └─ SessionCard (each session)          │
│     ├─ Time Range                       │
│     ├─ Title                            │
│     └─ App Chips (blocked apps)         │
│                                         │
│ Button: "+ Add Session"                 │
│  └─ Calls onAddSessionClick()           │
│     └─ viewModel.startAddingNewSession()│
└─────────────────────────────────────────┘
```

### SessionEditScreen
```
┌──────────────────────────────────────────┐
│ SessionEditScreen                        │
├──────────────────────────────────────────┤
│ Back Button  [EDIT SESSION]              │
│                                          │
│ TIME BLOCK (Read-only display)          │
│ ┌────────────────────────────┐          │
│ │ 09:00 AM — 10:00 AM        │          │
│ └────────────────────────────┘          │
│                                          │
│ TITLE (TextField)                       │
│ ┌────────────────────────────┐          │
│ │ [e.g., Deep Work Session]  │          │
│ └────────────────────────────┘          │
│  └─ onValueChange → updateEditingSession│
│                                          │
│ BLOCKED APPS (LazyColumn)               │
│  └─ Each app in Row with delete button  │
│     └─ IconButton → removeBlockedApp()  │
│  └─ Add button with "+"                 │
│                                          │
│ REMINDER MESSAGE (TextField)             │
│ ┌────────────────────────────┐          │
│ │ [Write motivation...]      │          │
│ └────────────────────────────┘          │
│  └─ onValueChange → updateEditingSession│
│                                          │
│ CREATE/SAVE SESSION Button               │
│  └─ saveSession() → navigate back       │
└──────────────────────────────────────────┘
```

## Key Implementation Details

### Colors and Styling
All colors defined in `Color.kt`:
- `Background` (#000000) - Screen backgrounds
- `CardSurface` (#1E1E1E) - Card and input backgrounds
- `PrimaryWhite` (#FFFFFF) - Primary text and highlights
- `SecondaryGray` (#B0B0B0) - Secondary text
- `LightGray` (#808080) - Labels and hints

### Typography
- **Large Titles**: 36sp, Bold (OnTime)
- **Section Headers**: 12sp, SemiBold, Small caps (TIME BLOCK, TITLE)
- **Time Text**: 24sp, Bold
- **Task Title**: 14sp, Normal
- **Body Text**: 14sp, Medium/Normal

### Rounded Corners
- **Cards**: 16-20dp radius
- **Buttons**: 16dp radius
- **Input Fields**: 12dp radius
- **Chips**: 8dp radius

## Navigation Structure

```
Navigation Graph:
├── Dashboard (Start Destination)
│   ├── onAddSessionClick() → New Session
│   └── onSessionClick() → Edit Session
│
├── New Session
│   └── onBackClick() or saveSession() → Dashboard
│
└── Edit Session
    └── onBackClick() or saveSession() → Dashboard
```

## ViewModel Methods Reference

| Method | Purpose | Parameters | Returns |
|--------|---------|-----------|---------|
| `startEditingSession()` | Begin editing a session | `session: FocusSession` | Unit |
| `startAddingNewSession()` | Begin creating new session | - | Unit |
| `updateEditingSession()` | Update temporary editing state | `session: FocusSession` | Unit |
| `saveSession()` | Save session to list | `session: FocusSession` | Unit |
| `cancelEditing()` | Discard changes | - | Unit |
| `deleteSession()` | Remove session from list | `sessionId: String` | Unit |
| `addBlockedApp()` | Add app to blocked list | `app: String` | Unit |
| `removeBlockedApp()` | Remove from blocked list | `app: String` | Unit |

## Testing Considerations

The app initializes with sample data for testing:
```kotlin
init {
    _uiState.value = SessionUiState(
        sessions = listOf(
            FocusSession(
                title = "Doing Homework",
                startTime = LocalTime.of(17, 0),  // 5:00 PM
                endTime = LocalTime.of(19, 0),    // 7:00 PM
                blockedApps = listOf("TikTok", "Instagram")
            )
        )
    )
}
```

This allows you to:
- ✅ Test UI layouts from dashboard
- ✅ Test navigation to edit screen
- ✅ Test form population
- ✅ Test state updates

## Future Enhancements

### Phase 2 - App Blocking:
- Implement accessibility service for actual blocking
- Integrate with Android's App Standby features

### Phase 3 - Persistence:
- Add Room database for local storage
- Implement DataStore for user preferences

### Phase 4 - Notifications:
- Session start reminders
- Break time notifications
- Focus streak achievements

### Phase 5 - Analytics:
- Track focus sessions completed
- Measure actual focus time
- Generate productivity reports

---

**This architecture ensures scalability, testability, and maintainability of the OnTime app.**

# OnTime Architecture Guide

## System Overview Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                      OnTime Android App                      │
└─────────────────────────────────────────────────────────────┘

                          ┌──────────────┐
                          │  MainActivity│
                          │ (Orchestrator│
                          └──────┬───────┘
                                 │
                 ┌───────────────┼───────────────┐
                 │               │               │
        ┌────────▼────────┐   ┌──▼──────┐   ┌───▼─────────┐
        │ Dashboard       │   │ Edit    │   │ Reminder    │
        │ Screen          │   │ Screen  │   │ Screen      │
        └────────┬────────┘   └──┬──────┘   └───┬─────────┘
                 │               │             │
                 └───────────────┼─────────────┘
                                 │
                        ┌────────▼─────────┐
                        │  ViewModel       │
                        │ (State Manager)  │
                        └────────┬─────────┘
                                 │
                    ┌────────────┼────────────┐
                    │            │            │
            ┌───────▼────┐   ┌───▼────┐  ┌──▼──────────┐
            │ Room DB    │   │Firebase│  │ ForegroundSe│
            │ (Local)    │   │ Repo   │  │ rvice       │
            └────────────┘   └───┬────┘  └──┬──────────┘
                                 │          │
                            ┌────▼──────────▼───┐
                            │ Firestore Cloud   │
                            │ (Backup & Sync)   │
                            └───────────────────┘
                            
Notifications Layer (Separate Thread):
            
┌────────────────────────────────┐
│  FocusMonitorService            │
│  • Monitors every 1 second      │
│  • Checks UsageStatsManager     │
│  • Matches scheduled activities         │
│  • Triggers notifications       │
└────────────┬───────────────────┘
             │
    ┌────────▼──────────┐
    │ Notification      │
    │ Channel & Manager │
    └────────┬──────────┘
             │
    ┌────────▼──────────┐
    │ User Device       │
    │ Notification Bar  │
    │ (Heads-up)        │
    └───────────────────┘
```

---

## Data Model

```kotlin
FocusSession
├── id: String                    // Unique identifier
├── title: String                 // Session name
├── startTime: String             // HH:mm format
├── endTime: String               // HH:mm format
├── activityList: List<String>    // Package names
├── reminderMessage: String       // Motivational text
├── createdAt: Timestamp?         // Firebase timestamp
└── isActive: Boolean             // Active status
```

### Storage

```
Local (Room)              Cloud (Firestore)
    │                            │
    ├─► focus_sessions table      ├─► focus_sessions collection
    │   ├─  id (PK)              │   ├─  Document {
    │   ├─  title                │   │     id: <session_id>
    │   ├─  startTime            │   │     title: "..."
    │   ├─  endTime              │   │     startTime: "..."
    │   ├─  activityList (CSV)    │   │     endTime: "..."
    │   ├─  reminderMessage      │   │     activityList: [...]
    │   ├─  createdAt            │   │     reminderMessage: "..."
    │   └─  isActive             │   │     createdAt: timestamp
    │                             │   └─  }
    │                             │
100% Offline              Sync when online
```

---

## User Flow Diagram

### Creating a Session

```
1. Dashboard Screen
   │
   └─► User taps "+ Add Session"
       │
       └─► Navigate to Edit Screen
           │
           └─► Edit Session Screen
               ├─ Input title
               ├─ Set time
               ├─ Add scheduled activities
               └─ Write message
                  │
                  └─► User taps "SAVE"
                      │
                      ├─► ViewModel.createSession()
                      │   │
                      │   ├─► Room DB: Insert locally
                      │   └─► Firebase: Sync to cloud
                      │
                      └─► Navigate back to Dashboard
                          │
                          └─► Session appears in list
```

### Using a Session (Blocking Apps)

```
1. Session Active (time matches)
   │
   └─► FocusMonitorService runs in background
       │
       ├─► Every 1 second:
       │   ├─ Get foreground app (UsageStatsManager)
       │   ├─ Query active sessions (Room)
       │   └─ Check scheduled activities list
       │
       └─► User opens scheduled activity
           │
           └─► App package matches!
               │
               ├─► Create notification
               ├─ Set HIGH priority
               ├─ Add PendingIntent
               └─► Show heads-up notification
                   │
                   └─► User taps notification
                       │
                       └─► MainActivity opens
                           │
                           └─► Detect session_id intent extra
                               │
                               └─► Show Reminder Screen
                                   │
                                   ├─► Display message
                                   ├─► Show app name
                                   └─► Show "Got it!" button
                                       │
                                       └─► User taps button
                                           │
                                           └─► Back to app/home
```

---

## Backend Service Sequence

```
App Launch
    │
    ├─► MainActivity.onCreate()
    │   │
    │   ├─► Initialize Database
    │   ├─► Initialize ViewModel
    │   ├─► Initialize Firebase
    │   └─► startFocusMonitorService()
    │       │
    │       └─► Intent(MainActivity, FocusMonitorService)
    │
    └─► FocusMonitorService.onCreate()
        │
        ├─► Create NotificationChannel
        ├─► Setup NotificationManager
        ├─► Get UsageStatsManager
        └─► startMonitoring()
            │
            ├─► Coroutine: while(true)
            │   │
            │   ├─ Every 1000ms:
            │   │  ├─ getCurrentForegroundApp()
            │   │  │  │
            │   │  │  └─► UsageStatsManager.queryUsageStats()
            │   │  │      └─► Get package with latest lastTimeUsed
            │   │  │
            │   │  ├─ database.dao().getSessionsForTime()
            │   │  │  │
            │   │  │  └─► Room query with time condition
            │   │  │
            │   │  └─► For each session:
            │   │     │
            │   │     └─► if (currentApp in activityList)
            │   │         │
            │   │         └─► showInterruptionNotification()
            │   │
            │   └─ delay(1000)
            │
            └─► Runs until service killed
```

---

## State Management Flow

### ViewModel State

```
FocusSessionViewModel
├── allSessions: StateFlow<List<FocusSession>>
│   └─ Updates when DB changes
│      Observed by: Dashboard Screen
│
├── currentSession: StateFlow<FocusSession?>
│   └─ Stores session being edited
│      Observed by: Edit Screen
│
├── isLoading: StateFlow<Boolean>
│   └─ Tracks operation progress
│      Observed by: UI (show spinner)
│
└── errorMessage: StateFlow<String>
    └─ Error messages
       Observed by: UI (show error toast)
```

### Data Binding

```
ViewModel                   UI Screen
    │                           │
    ├─ allSessions ────────────► DashboardScreen
    │  │                         │
    │  └─ collect()              ├─ Recompose on change
    │                             └─ Update list
    │
    ├─ currentSession ──────────► EditSessionScreen
    │  │                         │
    │  └─ collect()              ├─ Populate fields
    │                             └─ Enable editing
    │
    └─ isLoading ───────────────► All Screens
       │                         │
       └─ collect()              └─ Show/hide spinner
```

---

## Database Schema

### Room Local Database

```sql
CREATE TABLE focus_sessions (
    id TEXT PRIMARY KEY,
    title TEXT NOT NULL,
    startTime TEXT NOT NULL,
    endTime TEXT NOT NULL,
    activityList TEXT,           -- Stored as CSV string
    reminderMessage TEXT,
    createdAt INTEGER,
    isActive INTEGER NOT NULL DEFAULT 1
);

-- Indexes (implicit on PK)
-- id is primary key for fast lookups
```

### Firestore Remote Collection

```
focus_sessions (Collection)
├── document_1
│   ├── id: "1234567890"
│   ├── title: "Homework"
│   ├── startTime: "05:00 PM"
│   ├── endTime: "07:00 PM"
│   ├── activityList: ["com.tiktok.android", "com.instagram.android"]
│   ├── reminderMessage: "Stay focused..."
│   ├── createdAt: Timestamp(123456789)
│   └── isActive: true
│
├── document_2
│   └─ ... (more sessions)
│
└─  ...
```

---

## Notification Flow

```
FocusMonitorService
    │
    ├─► Create PendingIntent
    │   │
    │   └─► Intent(MainActivity)
    │       ├─ Action: FLAG_ACTIVITY_NEW_TASK
    │       ├─ Action: FLAG_ACTIVITY_CLEAR_TASK
    │       ├─ Extra: session_id = <session_id>
    │       └─ Extra: show_reminder = true
    │
    ├─► Create Notification
    │   │
    │   ├─ NotificationCompat.Builder()
    │   ├─ setContentTitle("Stay Focused!")
    │   ├─ setContentText(session.title)
    │   ├─ setContentIntent(pendingIntent)
    │   ├─ setPriority(HIGH)
    │   └─ setCategory(ALARM)
    │
    └─► Show Notification
        │
        ├─► NotificationManager.notify(id, notification)
        │
        └─► User sees heads-up notification
            │
            └─► User taps notification
                │
                ├─► PendingIntent fires
                ├─► MainActivity receives intent extras
                └─► Show Reminder Screen for that session
```

---

## Sync Architecture

### Offline-First Pattern

```
User Action
    │
    ├─►  Room Database Write
    │    (Immediate, local)
    │    ✓ Works offline
    │
    ├─► Get response
    │    (Instant)
    │
    └─► In background:
        │
        ├─► Check internet connection
        ├─► If online:
        │   │
        │   └─► Firebase Sync
        │       ├─ Upload changes
        │       ├─ Download updates
        │       └─ Merge conflicts (if any)
        │
        └─► If offline:
            │
            └─► Queue for sync
                │
                └─► Auto-sync when online
```

### Data Consistency

```
Local DB                Firebase Firestore
(Source of Truth)       (Backup)
    │                        │
    ├─ Always writable       ├─ May be read-only
    │   even offline          │   until online
    │
    ├─ Fast operations       ├─ Async operations
    │
    └─ Sync thread monitors  └─ Syncs when online
       changes and uploads
```

---

## Security Considerations

### Permissions Model

```
Android Manifest
    │
    ├─ PACKAGE_USAGE_STATS
    │  └─ Required for getForeground app
    │     User must grant in Settings
    │
    ├─ INTERNET
    │  └─ Allow Firebase sync
    │     System grants automatically
    │
    ├─ POST_NOTIFICATIONS
    │  └─ Required for notifications
    │     User must grant on Android 13+
    │
    └─ FOREGROUND_SERVICE
       └─ Required to run background service
          User grants by using app
```

### Firebase Security

```
Firestore Rules
    │
    └─► match /focus_sessions/{document=**} {
           allow read, write: if request.auth != null;
           // User must be authenticated
        }
        
Authentication
    │
    └─► Firebase Auth (optional)
        ├─ Email/Password
        ├─ Google Sign-In
        └─ Phone Auth
```

---

## Performance Optimization

### Service Monitoring

```
Every Second Check:
    │
    ├─ ~5ms: Get foreground app
    │  └─ UsageStatsManager query
    │
    ├─ ~10ms: Query DB bounds check
    │  └─ Current time within session?
    │
    ├─ ~1ms: String matching
    │  └─ App matches active session rules?
    │
    ├─ ~5ms: (if match) Create notification
    │  └─ Usually no notification
    │
    └─ Total: ~20ms per cycle
       (Negligible impact on battery)
```

### Database Optimization

```
Room Queries:
    │
    ├─ Indexed on time for fast range queries
    ├─ Primary key on ID for lookups
    └─ Type converters cache complex types

Firestore Sync:
    │
    ├─ Async operations (don't block UI)
    ├─ Batch writes (efficient)
    └─ Only sync changed data
```

---

## Component Communication Diagram

```
┌─────────────────────────────────────────────────┐
│           Event Bus / State Flow                │
├─────────────────────────────────────────────────┤
│                                                 │
│  allSessions ◄──┐                      ┌──── Collection Observer
│  currentSession◄─┤                     │
│  isLoading     ◄─┤                     │
│  errorMessage  ◄─┴──► ViewModel ──────┼──► Database Changes
│                           ▲             │
│                           │             ├──► Firebase Events
│                           │             │
│                    UI Updates           └──► User Actions
│                           │
│                      Recomposition
│                           │
│                    Screens Update
│
└─────────────────────────────────────────────────┘
```

---

## Thread Model

```
Main Thread
├─ UI Rendering (Dashboard, Edit, Reminder)
├─ ViewModel state updates
├─ User interactions
└─ Activity lifecycle

IO Thread (Coroutines)
├─ Room Database operations
├─ Firebase Firestore sync
└─ Network calls

Default Thread (Coroutines)
├─ JSON parsing
├─ Calculations
└─ Data transformations

Service Thread
├─ FocusMonitorService runs
├─ UsageStatsManager queries
├─ Notification creation
└─ Every 1 second checks
```

---

## Lifecycle Diagram

```
App Lifecycle
    │
    ├─► onCreate()
    │   ├─ Initialize Database
    │   ├─ Initialize ViewModel
    │   └─ Start FocusMonitorService
    │
    ├─► onStart()
    │   └─ Start observing ViewModel changes
    │
    ├─► onResume()
    │   └─ UI visible and interactive
    │
    ├─► onPause()
    │   └─ User leaves app
    │
    ├─► onStop()
    │   └─ Activity hidden
    │
    ├─► onDestroy()
    │   └─ Activity destroyed
    │
    ║
    ║ Note: Service continues running
    ║ independently in background
    ║
    └─► Process may be killed by OS
        (but user data saved in Room)
```

---

## Summary

The OnTime app follows modern Android best practices:

✅ **MVVM Architecture** - Clear separation of concerns  
✅ **Offline-First** - Works without internet  
✅ **Cloud Sync** - Automatic Firebase backup  
✅ **Jetpack Compose** - Modern declarative UI  
✅ **Coroutines** - Efficient async operations  
✅ **Background Service** - Long-running monitoring  
✅ **Notifications** - High-priority interruptions  
✅ **Type Safety** - Kotlin for null safety  

---

For more details on specific components, see [README.md](./README.md)

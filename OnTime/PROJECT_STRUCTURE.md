# OnTime Project Structure

## File Tree

```
OnTime/
├── 📄 build.gradle.kts                          # Project-level build configuration
├── 📄 settings.gradle.kts                       # Gradle settings & module includes
├── 📄 gradle.properties                         # Gradle properties & JVM args
│
├── 📁 app/
│   ├── 📄 build.gradle.kts                      # Module-level build configuration
│   │
│   └── 📁 src/
│       └── 📁 main/
│           ├── 📄 AndroidManifest.xml           # App manifest
│           │
│           ├── 📁 kotlin/com/ontime/
│           │   ├── 📄 MainActivity.kt           # [ENTRY POINT] App entry point
│           │   │
│           │   ├── 📁 data/
│           │   │   └── 📄 FocusSession.kt       # Data class for focus sessions
│           │   │
│           │   ├── 📁 ui/
│           │   │   ├── 📄 Navigation.kt         # Compose navigation & routes
│           │   │   │
│           │   │   ├── 📁 components/
│           │   │   │   ├── 📄 SessionCard.kt    # SessionCard & AppChip composables
│           │   │   │   └── 📄 TimeBlockInput.kt # TimeBlockInput composable
│           │   │   │
│           │   │   ├── 📁 screens/
│           │   │   │   ├── 📄 DashboardScreen.kt       # [HOME] Session list + add button
│           │   │   │   └── 📄 SessionEditScreen.kt    # [FORM] Create/edit sessions
│           │   │   │
│           │   │   └── 📁 theme/
│           │   │       ├── 📄 Color.kt          # Color definitions
│           │   │       └── 📄 Theme.kt          # Material 3 theme setup
│           │   │
│           │   └── 📁 viewmodel/
│           │       └── 📄 SessionViewModel.kt   # [STATE] ViewModel & StateFlow
│           │
│           └── 📁 res/
│               └── 📁 values/
│                   ├── 📄 strings.xml           # String resources
│                   └── 📄 themes.xml            # Android themes
│
├── 📄 README.md                                 # Project overview
├── 📄 QUICK_START.md                            # Quick start guide
├── 📄 ARCHITECTURE.md                           # Architecture & data flow
├── 📄 COMPONENTS.md                             # UI component reference
├── 📄 IMPLEMENTATION.md                         # Best practices & troubleshooting
└── 📄 PROJECT_STRUCTURE.md                      # This file
```

## File Count Summary

```
Total Kotlin Files:      11
Total XML Files:         2
Total Config Files:      3
Total Documentation:     5
────────────────────────────
TOTAL:                   21 files
```

## Directory Breakdown

### Source Code (`app/src/main/kotlin/com/ontime/`)
```
com/ontime/
├── data/          (1 file)   - Data models
├── ui/            (12 files) - User interface
│   ├── components (2 files)  - Reusable UI components
│   ├── screens    (2 files)  - Full screens
│   └── theme      (2 files)  - Theming
├── viewmodel/     (1 file)   - State management
└── MainActivity   (1 file)   - Entry point
```

### Resources (`app/src/main/res/`)
```
res/
├── values/
│   ├── strings.xml           - App strings
│   └── themes.xml            - Android themes
└── (drawable/ mipmap/ etc.)  - [Future] Add icons & images here
```

### Build Configuration
```
Root/
├── build.gradle.kts          - Project-wide settings
├── settings.gradle.kts       - Module configuration
└── gradle.properties          - JVM & compilation settings
```

### Config Files
```
app/src/main/
└── AndroidManifest.xml       - App permissions & activities
```

## Code Organization by Layer

### 1️⃣ Data Layer
```
data/
└── FocusSession.kt
    ├── id: String
    ├── title: String
    ├── startTime: LocalTime
    ├── endTime: LocalTime
    ├── blockedApps: List<String>
    └── reminderMessage: String
```

### 2️⃣ ViewModel Layer
```
viewmodel/
└── SessionViewModel.kt
    ├── StateFlow<SessionUiState>
    ├── sessions management
    └── editing session logic
```

### 3️⃣ UI Components Layer
```
ui/components/
├── SessionCard.kt      - Session display card
├── AppChip.kt          - App badge display
└── TimeBlockInput.kt   - Time range display
```

### 4️⃣ Screen Layer
```
ui/screens/
├── DashboardScreen.kt     - Home screen (list view)
└── SessionEditScreen.kt   - Form screen (create/edit)
```

### 5️⃣ Navigation Layer
```
ui/
└── Navigation.kt
    ├── Screen sealed class
    └── OnTimeNavHost composable
```

### 6️⃣ Theme Layer
```
ui/theme/
├── Color.kt  - Color definitions
└── Theme.kt  - Material 3 theme setup
```

## File Dependencies

```
MainActivity.kt
    ├─ Navigation.kt
    │   ├── DashboardScreen.kt
    │   │   ├── SessionCard.kt
    │   │   │   └── AppChip.kt
    │   │   └── SessionViewModel.kt
    │   │       └── FocusSession.kt
    │   └── SessionEditScreen.kt
    │       ├── TimeBlockInput.kt
    │       └── SessionViewModel.kt
    └── Theme.kt
        └── Color.kt
```

## Size Reference

| Component | Lines of Code | Complexity |
|-----------|---------------|-----------|
| FocusSession.kt | ~20 | ⭐ Very Low |
| SessionViewModel.kt | ~90 | ⭐⭐ Low-Medium |
| SessionCard.kt | ~75 | ⭐⭐ Low-Medium |
| DashboardScreen.kt | ~65 | ⭐⭐ Low-Medium |
| SessionEditScreen.kt | ~200 | ⭐⭐⭐ Medium-High |
| Navigation.kt | ~55 | ⭐⭐ Low-Medium |
| Theme.kt | ~30 | ⭐ Very Low |
| Color.kt | ~15 | ⭐ Very Low |
| MainActivity.kt | ~30 | ⭐ Very Low |
| ────────────────── | ────────── | |
| **TOTAL** | **~580** | |

## Resource Files

### Strings (strings.xml)
```xml
<string name="app_name">OnTime</string>
```
*Future: Add more strings for internationalization*

### Themes (themes.xml)
```xml
<style name="Theme.OnTime" parent="android:Theme.Material.NoActionBar">
    <item name="android:colorBackground">#000000</item>
    <item name="android:colorForeground">#FFFFFF</item>
</style>
```

### Manifest (AndroidManifest.xml)
- Single activity: `MainActivity`
- No special permissions yet (for future: accessibility service)
- Theme applied globally

## Build Configuration Details

### Project-Level (build.gradle.kts)
```kotlin
plugins {
    id("com.android.application") version "8.1.2"
    kotlin("android") version "1.9.10"
}
```

### Module-Level (app/build.gradle.kts)
```kotlin
android {
    namespace = "com.ontime"
    compileSdk = 34
    minSdk = 24
    targetSdk = 34
}

dependencies {
    // Compose, Material 3, Navigation, ViewModel, etc.
}
```

## Adding New Files

### To Add a New Screen:
1. Create file in `ui/screens/NewScreen.kt`
2. Add route to `Navigation.kt`
3. Update `SessionViewModel.kt` if needed

### To Add a New Component:
1. Create file in `ui/components/NewComponent.kt`
2. Use in screens as needed

### To Add Theme Colors:
1. Define in `ui/theme/Color.kt`
2. Use in components via imports

## Gradle Wrapper

```gradle
distributionUrl=https://services.gradle.org/distributions/gradle-8.1-bin.zip
```
Located in: `gradle/wrapper/gradle-wrapper.properties`

## Package Structure

All code follows package structure:
```
com.ontime
├── data          - Models & data classes
├── ui            - User interface
│   ├── components
│   ├── screens
│   └── theme
├── viewmodel     - State management
└── MainActivity  - App entry point
```

## Naming Conventions

### Files
- Composable screens: `SomethingScreen.kt` (e.g., `DashboardScreen`)
- Composables: `SomethingCard.kt`, `Something.kt` (e.g., `SessionCard`)
- ViewModels: `SomethingViewModel.kt` (e.g., `SessionViewModel`)
- Data classes: `Something.kt` (e.g., `FocusSession`)

### Functions
- Composable functions: `@Composable fun SomethingScreen()`
- Regular functions: `camelCase()` (e.g., `startAddingNewSession()`)

### Classes & Objects
- PascalCase (e.g., `SessionCard`, `FocusSession`)

### Variables
- camelCase (e.g., `startTime`, `blockedApps`)

## Future File Additions

### Phase 2
- `database/` - Room database files
- `service/` - Accessibility service for blocking
- `ui/screens/AppPickerScreen.kt` - App picker UI

### Phase 3
- `analytics/` - Analytics & reporting
- `worker/` - WorkManager for notifications
- `repository/` - Repository pattern for data access

---

**Total Project: ~580 lines of Kotlin + configuration files**
**Architecture: MVVM with Jetpack Compose**
**Build System: Gradle Kotlin DSL**

# Implementation Tips & Troubleshooting

## Build Configuration Best Practices

### Gradle Configuration
The project uses:
- **Gradle 8.x** - Latest stable version
- **Kotlin 1.9.10** - Latest Kotlin version with full Compose support
- **Android Gradle Plugin 8.1.2** - Latest AGP
- **Compose BOM 2023.10.01** - Ensures all Compose versions are compatible

### Dependencies Overview

**Core Android**
- `androidx.core:core-ktx` - Kotlin extensions for Android APIs
- `androidx.lifecycle:lifecycle-runtime-ktx` - Lifecycle management

**Jetpack Compose**
- `androidx.activity:activity-compose` - Activity integration
- `androidx.compose.ui:ui` - Core UI components
- `androidx.compose.material3:material3` - Material 3 design components
- `androidx.compose.ui:ui-graphics` - Graphics support

**Navigation & State Management**
- `androidx.navigation:navigation-compose` - Compose-based navigation
- `androidx.lifecycle:lifecycle-viewmodel-compose` - ViewModel integration

## Common Setup Issues & Solutions

### Issue 1: Gradle Sync Failed
**Symptoms**: "Unable to resolve dependency"

**Solutions**:
1. Clear cache: `File → Invalidate Caches → Invalidate and Restart`
2. Remove .gradle folder: `rm -rf ~/.gradle`
3. Check internet connection
4. Update Gradle: `gradle/wrapper/gradle-wrapper.properties`

### Issue 2: Kotlin Version Mismatch
**Symptoms**: "Unresolved reference" errors in Kotlin files

**Solutions**:
1. Ensure `kotlinCompilerExtensionVersion = "1.5.4"` matches Kotlin 1.9.10
2. Update build.gradle.kts with matching versions:
   ```kotlin
   kotlin("android") version "1.9.10"
   kotlinCompilerExtensionVersion = "1.5.4"
   ```

### Issue 3: Compose Not Recognized
**Symptoms**: `@Composable` not recognized

**Solutions**:
1. Verify `buildFeatures { compose = true }` in build.gradle.kts
2. Check Compose BOM is in dependencies
3. Rebuild project: `Build → Clean Project → Rebuild Project`

### Issue 4: Navigation Issues
**Symptoms**: Screen doesn't navigate properly

**Solutions**:
1. Check route strings match exactly (case-sensitive)
2. Verify `rememberNavController()` is called once in MainActivity
3. Ensure NavHost wraps all screens

## Testing the App

### Unit Testing ViewModel
```kotlin
@Test
fun testStartAddingNewSession() {
    viewModel.startAddingNewSession()
    val state = viewModel.uiState.value
    assertTrue(state.isAddingNewSession)
    assertNotNull(state.editingSession)
}
```

### UI Testing Screens
```kotlin
@get:Rule
val composeTestRule = createComposeRule()

@Test
fun testDashboardDisplaysTitle() {
    composeTestRule.setContent {
        DashboardScreen(viewModel = viewModel, onAddSessionClick = {}, onSessionClick = {})
    }
    composeTestRule.onNodeWithText("OnTime").assertExists()
}
```

## Performance Tips

### 1. LazyColumn Over Column
```kotlin
// ✅ Good - Only composes visible items
LazyColumn {
    items(sessions) { session ->
        SessionCard(session)
    }
}

// ❌ Bad - Composes all items even if not visible
Column {
    sessions.forEach { session ->
        SessionCard(session)
    }
}
```

### 2. Remember for State
```kotlin
// ✅ Good - Remembers across recompositions
val textState = remember { mutableStateOf("") }

// ❌ Bad - Creates new state on every recomposition
val textState = mutableStateOf("")
```

### 3. Use collectAsState for StateFlow
```kotlin
// ✅ Good - Proper Compose integration
val uiState by viewModel.uiState.collectAsState()

// ❌ Bad - Lost recomposition potential
val uiState = viewModel.uiState.value
```

## Common Code Patterns

### Pattern 1: Updating Nested Objects
```kotlin
// Create immutable copy with updated field
val updatedSession = session.copy(
    title = "New Title",
    blockedApps = session.blockedApps + "NewApp"
)
viewModel.updateEditingSession(updatedSession)
```

### Pattern 2: Navigation with State
```kotlin
Button(onClick = {
    viewModel.startAddingNewSession()
    navController.navigate(Screen.NewSession.route)
})
```

### Pattern 3: Conditional Rendering
```kotlin
if (session.blockedApps.isNotEmpty()) {
    LazyRow {
        items(session.blockedApps) { app ->
            AppChip(appName = app)
        }
    }
}
```

## Styling Guidelines

### Spacing
```kotlin
// Define consistent spacing
Box(modifier = Modifier.padding(20.dp))  // Screen padding
Row(modifier = Modifier.fillMaxWidth().padding(16.dp))  // Content padding
Spacer(modifier = Modifier.height(12.dp))  // Between sections
```

### Colors
```kotlin
// Always import from theme
Text(color = PrimaryWhite)
Text(color = SecondaryGray)
Text(color = CardSurface)

// Never hardcode colors
Text(color = Color(0xFFFFFFFF))  // ❌ Avoid
```

### Rounded Corners
```kotlin
// Use RoundedCornerShape consistently
RoundedCornerShape(20.dp)   // Cards (20dp)
RoundedCornerShape(16.dp)   // Buttons (16dp)
RoundedCornerShape(12.dp)   // Inputs (12dp)
RoundedCornerShape(8.dp)    // Chips (8dp)
```

## Extension Examples

### Example 1: Add Time Picker
```kotlin
// In SessionEditScreen
TextField(
    value = session.startTime.toString(),
    onValueChange = {
        // Parse time and update
        val newTime = LocalTime.parse(it)
        viewModel.updateEditingSession(session.copy(startTime = newTime))
    }
)
```

### Example 2: Add App Picker
```kotlin
// Add to SessionEditScreen
Button(onClick = {
    // Show app selection dialog
    showAppPickerDialog = true
})

if (showAppPickerDialog) {
    // Launch intent to pick app
}
```

### Example 3: Save to Database
```kotlin
// Add to ViewModel
fun saveSessionToDb(session: FocusSession) {
    viewModel.saveSession(session)
    sessionDao.insertSession(session)  // Save to Room DB
}
```

## Debugging Tips

### 1. Checking State Changes
```kotlin
// Add debug logging
LaunchedEffect(uiState) {
    Log.d("SessionViewModel", "State changed: $uiState")
}
```

### 2. Compose Layout Debugging
```kotlin
// Use debugModifier
Box(modifier = Modifier.debug())

// Or enable Layout Inspector in Android Studio
// Tools → Layout Inspector
```

### 3. Navigation Debugging
```kotlin
// Add logs to navigation
navController.addOnDestinationChangedListener { _, destination, _ ->
    Log.d("Navigation", "Current destination: ${destination.route}")
}
```

## Performance Checklist

- [ ] All heavy operations in ViewModel, not Composables
- [ ] StateFlow used for state management
- [ ] LazyColumn/LazyRow for long lists
- [ ] Images optimized (if adding image assets)
- [ ] Unnecessary recompositions minimized with key()
- [ ] ProGuard/R8 minification enabled for release builds

## Release Build Checklist

Before deploying to production:

1. **Update version**:
   ```kotlin
   versionCode = 1
   versionName = "1.0.0"
   ```

2. **Enable minification**:
   ```kotlin
   buildTypes {
       release {
           isMinifyEnabled = true
           proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
       }
   }
   ```

3. **Test on multiple devices** (Android 7.0+)

4. **Check app size** with Android Studio Analyzer

5. **Sign APK** with release keystore

6. **Test on real device** before publishing

## Useful Links

- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Material 3 Design](https://m3.material.io/)
- [Android Navigation Compose](https://developer.android.com/guide/navigation/navigation-compose)
- [ViewModel StateFlow Pattern](https://developer.android.com/topic/architecture/ui-layer/state-holders)

---

**Happy coding! For more help, refer to official Android documentation.**

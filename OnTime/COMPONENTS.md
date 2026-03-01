# OnTime UI Component Reference

## Components Library

### 1. SessionCard
**Location**: `ui/components/SessionCard.kt`

**Purpose**: Display a focus session with time, title, and blocked apps

**Usage**:
```kotlin
SessionCard(
    session = focusSession,
    modifier = Modifier.fillMaxWidth()
)
```

**Props**:
- `session: FocusSession` - The session to display (required)
- `modifier: Modifier` - Layout modifier (optional)

**Displays**:
- "FOCUS SESSION" label (small caps, gray)
- Time range (bold, 24sp)
- Session title (14sp)
- Blocked app chips in horizontal scrolling row

**Example Output**:
```
┌─────────────────────────────┐
│ FOCUS SESSION               │
│ 05:00 PM – 07:00 PM         │
│ Doing Homework              │
│ [TikTok] [Instagram]        │
└─────────────────────────────┘
```

---

### 2. AppChip
**Location**: `ui/components/SessionCard.kt`

**Purpose**: Display individual blocked app as a badge

**Usage**:
```kotlin
AppChip(appName = "TikTok")
```

**Props**:
- `appName: String` - App name to display (required)
- `modifier: Modifier` - Layout modifier (optional)

**Style**:
- Container: Dark gray (#2A2A2A)
- Text: White, 12sp, medium weight
- Shape: 8dp rounded corners

---

### 3. TimeBlockInput
**Location**: `ui/components/TimeBlockInput.kt`

**Purpose**: Display start-end time in a card format

**Usage**:
```kotlin
TimeBlockInput(
    startTime = LocalTime.of(9, 0),
    endTime = LocalTime.of(10, 0)
)
```

**Props**:
- `startTime: LocalTime` - Session start time (required)
- `endTime: LocalTime` - Session end time (required)
- `modifier: Modifier` - Layout modifier (optional)

**Example Output**:
```
┌─────────────────────────┐
│ 09:00 AM — 10:00 AM     │
└─────────────────────────┘
```

---

## Screens Architecture

### DashboardScreen
**Location**: `ui/screens/DashboardScreen.kt`

**Props**:
```kotlin
@Composable
fun DashboardScreen(
    viewModel: SessionViewModel,
    onAddSessionClick: () -> Unit,
    onSessionClick: (String) -> Unit
)
```

**Key Elements**:
- Title: "OnTime" (36sp, bold)
- Session list: LazyColumn of SessionCards
- Add button: Outlined button with "+ Add Session"
- Spacing: 20dp horizontal padding

**State Management**:
- Collects `SessionUiState` from ViewModel
- Displays all sessions from `uiState.sessions`
- No direct editing here

---

### SessionEditScreen
**Location**: `ui/screens/SessionEditScreen.kt`

**Props**:
```kotlin
@Composable
fun SessionEditScreen(
    viewModel: SessionViewModel,
    onBackClick: () -> Unit,
    isNewSession: Boolean = false
)
```

**Sections**:

#### Header
- Back button (arrow icon)
- Title: "NEW SESSION" or "EDIT SESSION" (centered)

#### TIME BLOCK
- Read-only TimeBlockInput display
- No user interaction (time picker would be future enhancement)

#### TITLE
- TextField for session name
- Placeholder: "e.g., Deep Work Session"
- Background: CardSurface (#1E1E1E)
- Corner radius: 12dp

#### BLOCKED APPS
- LazyColumn of app rows
- Each app in Row with:
  - App name (left)
  - Circular X button (right) - removes app
- Add button: Outlined "+" for new apps

#### REMINDER MESSAGE
- Large TextField (120dp height)
- Placeholder: "Write your personal motivation here..."
- Multiline text input
- Background: CardSurface (#1E1E1E)

#### Bottom Action Button
- Full width button
- "CREATE SESSION" (new) or "SAVE SESSION" (edit)
- White background (#FFFFFF)
- Dark text for contrast
- Corner radius: 16dp

**State Management**:
- Uses `editingSession` from ViewModel
- All changes trigger `updateEditingSession()`
- Save button calls `saveSession()` and navigates back

---

## Theme & Colors

### Color Palette
```kotlin
// Primary Colors
val Background = Color(0xFF000000)          // Pure black
val CardSurface = Color(0xFF1E1E1E)         // Dark gray
val PrimaryWhite = Color(0xFFFFFFFF)        // White
val SecondaryGray = Color(0xFFB0B0B0)       // Light gray
val LightGray = Color(0xFF808080)           // Medium gray
```

### Color Usage
| Color | Usage |
|-------|-------|
| Background | Screen backgrounds, main page color |
| CardSurface | Cards, input fields, interactive elements |
| PrimaryWhite | Main text, headings, primary UI elements |
| SecondaryGray | Section labels, small text |
| LightGray | Placeholders, hints, secondary text |

---

## Typography System

### Text Styles

| Style | Size | Weight | Color | Use Case |
|-------|------|--------|-------|----------|
| App Title | 36sp | Bold | PrimaryWhite | "OnTime" header |
| Section Header | 12sp | SemiBold | SecondaryGray | "FOCUS SESSION", "TIME BLOCK" |
| Time Text | 24sp | Bold | PrimaryWhite | "05:00 PM – 07:00 PM" |
| Task Title | 14sp | Normal | LightGray | "Doing Homework" |
| Button Text | 16sp | SemiBold | PrimaryWhite/Background | Action buttons |
| Chip Text | 12sp | Medium | PrimaryWhite | App names in chips |
| Input Text | 14sp | Normal | PrimaryWhite | User input fields |

---

## Material 3 Components Used

### Cards
```kotlin
Card(
    modifier = modifier,
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = CardSurface),
    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
) {
    // Content
}
```

### Buttons
```kotlin
// Primary Action Button (white)
Button(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = ButtonDefaults.buttonColors(containerColor = PrimaryWhite)
) { }

// Secondary Action Button (outlined)
Button(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
    border = BorderStroke(2.dp, PrimaryWhite)
) { }
```

### TextFields
```kotlin
TextField(
    modifier = Modifier
        .fillMaxWidth()
        .height(48.dp),
    shape = RoundedCornerShape(12.dp),
    colors = TextFieldDefaults.colors(
        focusedContainerColor = CardSurface,
        unfocusedContainerColor = CardSurface,
        focusedTextColor = PrimaryWhite,
        focusedIndicatorColor = Color.Transparent
    )
)
```

### Icons
```kotlin
Icon(
    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
    contentDescription = "Back",
    tint = PrimaryWhite
)

Icon(
    imageVector = Icons.Filled.Close,
    contentDescription = "Remove",
    tint = PrimaryWhite
)
```

---

## Layout Patterns

### Full-Width Padded Content
```kotlin
Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
) {
    // Content with 20dp padding
}
```

### Horizontal Scrolling Items
```kotlin
LazyRow(
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    modifier = Modifier.fillMaxWidth()
) {
    items(items) { item ->
        // Item composable
    }
}
```

### Vertical Scrolling with Padding
```kotlin
LazyColumn(
    modifier = Modifier
        .fillMaxWidth()
        .weight(1f),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(vertical = 12.dp)
) {
    items(items) { item ->
        // Item composable
    }
}
```

### Header + Content + Footer
```kotlin
Column(modifier = Modifier.fillMaxSize()) {
    // Header (fixed height)
    Row(modifier = Modifier.height(56.dp)) { }
    
    // Content (flexible)
    LazyColumn(modifier = Modifier.weight(1f)) { }
    
    // Footer (fixed)
    Button(modifier = Modifier.height(54.dp)) { }
}
```

---

## Responsive Design

### Breakpoints (for future enhancement)
- **Phone Compact**: 348-599dp width
- **Phone Regular**: 600-839dp width
- **Tablet**: 840dp+ width

Current implementation targets **Phone** sizes with single-column layout suitable for most Android devices.

---

## Accessibility Features

### Current Implementations
- ✅ Sufficient color contrast (white on black/dark gray)
- ✅ Large text for section headers (12sp+)
- ✅ Content descriptions on all icons
- ✅ Touch targets: 48dp minimum (buttons)

### Future Improvements
- [ ] Add screen reader support labels
- [ ] Implement TalkBack compatibility
- [ ] Add haptic feedback on interactions
- [ ] Test with accessibility scanner

---

## Dark Mode Support

The app is **built for dark mode exclusively** with:
- High contrast for readability
- Reduced eye strain
- Better focus for users
- Optimized battery usage on OLED screens

All colors are optimized for dark theme - no light theme variant is implemented.

---

## Animation & Transitions

### Navigation Transitions
Default Compose navigation provides:
- Slide in from right → Slide out to right
- Default duration: 300ms
- Easing: Fast Out Slow In

### Future Enhancement Ideas
- Shared element transitions between screens
- Button ripple effects
- Content loading animations
- Fade effects on state changes

---

## Component Sizing Guide

### Standard Dimensions
```kotlin
// Padding
padding = 20.dp   // Screen horizontal
padding = 16.dp   // Content sections
padding = 12.dp   // Between elements
padding = 8.dp    // Small spacing

// Heights
Button height = 48dp (small), 54dp (large)
ChipHeight = 32-40dp
TextFieldHeight = 48-120dp (multiline)
IconButtonSize = 48dp

// Border Radius
Cards = 20dp
Buttons = 16dp
Inputs = 12dp
Chips = 8dp
```

---

## Best Practices for Extensions

When adding new components:

1. **Keep them in `ui/components/`**
   ```
   SessionCard.kt ✅
   SpecialDialog.kt ✅
   ```

2. **Follow naming conventions**
   - Composable functions: PascalCase (SessionCard)
   - Properties: camelCase (session, onDelete)

3. **Use theme colors consistently**
   ```kotlin
   Text(color = PrimaryWhite)      // ✅
   Text(color = Color(0xFFFFFFFF)) // ❌
   ```

4. **Provide clear composable signatures**
   ```kotlin
   @Composable
   fun MyComponent(
       requiredParam: String,
       optionalParam: String = "",
       modifier: Modifier = Modifier
   )
   ```

5. **Document with examples**
   ```kotlin
   /**
    * Displays a custom component
    * @param text The text to display
    * @param onClick Callback when clicked
    */
   @Composable
   fun MyComponent(text: String, onClick: () -> Unit)
   ```

---

**All components follow Material 3 design guidelines and OnTime's dark mode theme.**

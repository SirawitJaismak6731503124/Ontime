package com.ontime.ui.theme

import androidx.compose.foundation.isSystemInDarkMode
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryWhite,
    secondary = LightGray,
    tertiary = SecondaryGray,
    background = Background,
    surface = CardSurface,
    onBackground = PrimaryWhite,
    onSurface = PrimaryWhite,
    outline = SecondaryGray
)

@Composable
fun OnTimeTheme(
    darkTheme: Boolean = isSystemInDarkMode(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}

package com.ontime.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

private val darkColorScheme = darkColorScheme(
    primary = HighlightWhite,
    secondary = LightGrey,
    tertiary = SurfaceGrey,
    background = DeepBlack,
    surface = DarkGrey,
    error = ErrorRed,
    onBackground = WhiteText,
    onSurface = WhiteText,
    onPrimary = DeepBlack
)

@Composable
fun OnTimeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = darkColorScheme,
        typography = Typography(),
        content = content
    )
}

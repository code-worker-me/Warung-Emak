package com.example.pos_app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CustomLightGreen,
    onPrimary = Color.Black,
    primaryContainer = CustomOlive,
    secondary = CustomYellow,
    background = Color(0xFF1C1C16),
    surface = Color(0xFF24251E)
)

private val LightColorScheme = lightColorScheme(
    primary = CustomDarkOlive,          // #6A795D
    onPrimary = Color.White,
    primaryContainer = CustomLightGreen, // #B2BD7E
    secondary = CustomOlive,            // #8E9162
    tertiary = CustomYellow,            // #D9D28B
    background = Color(0xFFFCFCF8),
    surface = Color(0xFFFFFFFF)
)

@Composable
fun POSAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
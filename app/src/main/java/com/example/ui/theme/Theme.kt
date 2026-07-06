package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Emerald700,
    onPrimary = Color.White,
    primaryContainer = Emerald100,
    onPrimaryContainer = Emerald950,
    secondary = Emerald800,
    onSecondary = Color.White,
    secondaryContainer = Emerald50,
    onSecondaryContainer = Emerald900,
    tertiary = Emerald600,
    onTertiary = Color.White,
    background = AppBackground,
    onBackground = Slate900,
    surface = AppSurface,
    onSurface = Slate900,
    surfaceVariant = Color.White,
    onSurfaceVariant = Slate500,
    error = Color(0xFFEF4444),
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disabling dynamic color to ensure consistent polished theme
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = LightColorScheme // Force light theme for polished look

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

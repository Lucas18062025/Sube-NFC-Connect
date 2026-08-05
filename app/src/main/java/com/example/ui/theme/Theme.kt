package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

enum class ThemeMode(val displayName: String, val badgeText: String) {
    SYSTEM("Sistema", "Auto"),
    LIGHT("Modo Claro", "Día"),
    DARK("Modo Oscuro", "Noche"),
    OLED_BLACK("Modo OLED Negro", "Ahorro Batería")
}

private val DarkColorScheme = darkColorScheme(
    primary = SubeCyanAccent,
    onPrimary = Color.Black,
    primaryContainer = SubeBluePrimary,
    onPrimaryContainer = Color.White,
    secondary = SubeMintSuccess,
    onSecondary = Color.Black,
    background = SlateDarkBackground,
    onBackground = TextPrimaryDark,
    surface = SlateDarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = SlateDarkCard,
    onSurfaceVariant = TextSecondaryDark,
    error = DangerRed,
    onError = Color.White
)

private val OledColorScheme = darkColorScheme(
    primary = SubeCyanAccent,
    onPrimary = Color.Black,
    primaryContainer = SubeBluePrimary,
    onPrimaryContainer = Color.White,
    secondary = SubeMintSuccess,
    onSecondary = Color.Black,
    background = OledDarkBackground,
    onBackground = TextPrimaryDark,
    surface = OledDarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = OledDarkCard,
    onSurfaceVariant = TextSecondaryDark,
    error = DangerRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = SubeBluePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = SubeMintSuccess,
    onSecondary = Color.White,
    background = LightBackground,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightCardBg,
    onSurfaceVariant = TextSecondaryLight,
    error = DangerRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set false to preserve SUBE branded colors consistently
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        themeMode == ThemeMode.OLED_BLACK -> OledColorScheme
        themeMode == ThemeMode.DARK -> DarkColorScheme
        themeMode == ThemeMode.LIGHT -> LightColorScheme
        themeMode == ThemeMode.SYSTEM && darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

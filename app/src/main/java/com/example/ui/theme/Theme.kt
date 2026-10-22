package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = BrandDarkNavy,
    onPrimary = Color.White,
    primaryContainer = BrandNavyLight,
    onPrimaryContainer = Color.White,
    secondary = BrandSaffron,
    onSecondary = Color.White,
    secondaryContainer = BrandSaffronLight,
    onSecondaryContainer = BrandDarkNavy,
    tertiary = BrandGreen,
    onTertiary = Color.White,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondary,
    outline = DividerColor,
    error = ErrorRed,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = BrandSkyBlue,
    onPrimary = Color.Black,
    primaryContainer = BrandDarkNavy,
    onPrimaryContainer = Color.White,
    secondary = BrandSaffron,
    onSecondary = Color.White,
    tertiary = BrandGreenLight,
    onTertiary = Color.Black,
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF1F5F9),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF475569)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent CSC Indian branding
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

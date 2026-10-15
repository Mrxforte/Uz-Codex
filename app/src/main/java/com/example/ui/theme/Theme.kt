package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = UzLegalTeal,
    onPrimary = Color.White,
    primaryContainer = UzLegalTealSurface,
    onPrimaryContainer = UzLegalTealDark,
    secondary = UzLegalNavyHeadings,
    onSecondary = Color.White,
    secondaryContainer = UzLegalSurfaceVariantLight,
    onSecondaryContainer = UzLegalNavyHeadings,
    tertiary = UzLegalTealLight,
    background = UzLegalBgLight,
    onBackground = UzLegalNavyHeadings,
    surface = UzLegalSurfaceLight,
    onSurface = UzLegalNavyHeadings,
    surfaceVariant = UzLegalSurfaceVariantLight,
    onSurfaceVariant = UzLegalSlateBody,
    outline = UzLegalBorderLight,
    outlineVariant = UzLegalBorderLight.copy(alpha = 0.5f),
    error = UzLegalError,
    errorContainer = UzLegalErrorContainer,
    onError = Color.White,
    onErrorContainer = Color(0xFF991B1B)
)

private val DarkColorScheme = darkColorScheme(
    primary = UzLegalTealLight,
    onPrimary = UzLegalBgDark,
    primaryContainer = UzLegalTealDark.copy(alpha = 0.35f),
    onPrimaryContainer = UzLegalTealLight,
    secondary = Color(0xFF93C5FD),
    onSecondary = UzLegalBgDark,
    secondaryContainer = UzLegalSurfaceVariantDark,
    onSecondaryContainer = Color(0xFFE2E8F0),
    tertiary = UzLegalTeal,
    background = UzLegalBgDark,
    onBackground = Color(0xFFF8FAFC),
    surface = UzLegalSurfaceDark,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = UzLegalSurfaceVariantDark,
    onSurfaceVariant = UzLegalSlateMuted,
    outline = UzLegalBorderDark,
    outlineVariant = UzLegalBorderDark.copy(alpha = 0.5f),
    error = UzLegalError,
    errorContainer = UzLegalErrorContainer.copy(alpha = 0.2f),
    onError = Color.White,
    onErrorContainer = Color(0xFFFCA5A5)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

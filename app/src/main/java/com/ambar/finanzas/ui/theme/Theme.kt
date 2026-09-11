package com.ambar.finanzas.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Amber700, onPrimary = SurfaceLight,
    primaryContainer = Amber100, onPrimaryContainer = Amber900,
    secondary = Amber500, onSecondary = SurfaceLight,
    secondaryContainer = Amber50, onSecondaryContainer = Amber800,
    tertiary = InfoBlue,
    background = SurfaceLight, surface = SurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onBackground = OnSurfaceLight, onSurface = OnSurfaceLight,
    onSurfaceVariant = OnSurfaceVariantLight, error = ExpenseRed
)

private val DarkColorScheme = darkColorScheme(
    primary = Amber300, onPrimary = SurfaceDark,
    primaryContainer = Amber700, onPrimaryContainer = Amber100,
    secondary = Amber400, onSecondary = SurfaceDark,
    secondaryContainer = Amber600, onSecondaryContainer = Amber50,
    tertiary = InfoBlue,
    background = SurfaceDark, surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onBackground = OnSurfaceDark, onSurface = OnSurfaceDark,
    onSurfaceVariant = OnSurfaceVariantDark, error = ExpenseRed
)

@Composable
fun AmbarFinanzasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AmbarTypography,
        content = content
    )
}

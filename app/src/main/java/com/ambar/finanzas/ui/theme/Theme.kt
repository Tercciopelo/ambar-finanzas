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
    primary = Amber700,
    onPrimary = OnSurface,
    primaryContainer = Amber100,
    onPrimaryContainer = Amber900,
    secondary = Amber500,
    onSecondary = OnSurface,
    secondaryContainer = Amber50,
    onSecondaryContainer = Amber800,
    tertiary = InfoBlue,
    background = Surface,
    surface = Surface,
    surfaceVariant = SurfaceVariant,
    onBackground = OnSurface,
    onSurface = OnSurface,
    onSurfaceVariant = OnSurfaceVariant,
    error = ExpenseRed,
)

@Composable
fun AmbarFinanzasTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AmbarTypography,
        content = content
    )
}

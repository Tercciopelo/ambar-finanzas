package com.ambar.finanzas.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF006A70), onPrimary = Color.White,
    primaryContainer = Color(0xFFB0F1EF), onPrimaryContainer = Color(0xFF002F33),
    secondary = Color(0xFF426568), onSecondary = Color.White,
    secondaryContainer = Color(0xFFD7EBEB), onSecondaryContainer = Color(0xFF193638),
    tertiary = Color(0xFF625687),
    background = Color(0xFFF3FAF9), surface = Color(0xFFFAFDFC),
    surfaceVariant = Color(0xFFE4F0EF),
    onBackground = Color(0xFF142B2D), onSurface = Color(0xFF142B2D),
    onSurfaceVariant = Color(0xFF486264), error = Color(0xFFAE3439),
    outline = Color(0xFF6D8586)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6BDAD7), onPrimary = Color(0xFF003638),
    primaryContainer = Color(0xFF004F54), onPrimaryContainer = Color(0xFFB0F1EF),
    secondary = Color(0xFFA2CDCE), onSecondary = Color(0xFF133536),
    secondaryContainer = Color(0xFF284749), onSecondaryContainer = Color(0xFFD7EBEB),
    tertiary = Color(0xFFCEC1F2),
    background = Color(0xFF0B191B), surface = Color(0xFF102123),
    surfaceVariant = Color(0xFF213739),
    onBackground = Color(0xFFE1F0EF), onSurface = Color(0xFFE1F0EF),
    onSurfaceVariant = Color(0xFFB5CCCD), error = Color(0xFFFFB3B7),
    outline = Color(0xFF819B9C)
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
            window.navigationBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AmbarTypography,
        shapes = Shapes(small = RoundedCornerShape(12.dp), medium = RoundedCornerShape(20.dp), large = RoundedCornerShape(28.dp)),
        content = content
    )
}

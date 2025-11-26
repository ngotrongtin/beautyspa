package com.beautyspa.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Rose600,
    onPrimary = White,
    primaryContainer = Rose100,
    onPrimaryContainer = Rose900,
    secondary = Rose400,
    onSecondary = White,
    secondaryContainer = Rose100,
    onSecondaryContainer = Rose800,
    tertiary = Rose300,
    onTertiary = White,
    error = Rose700,
    onError = White,
    errorContainer = Rose100,
    onErrorContainer = Rose900,
    background = White,
    onBackground = Gray900,
    surface = White,
    onSurface = Gray900,
    surfaceVariant = Rose50,
    onSurfaceVariant = Gray700,
    outline = Gray300,
    outlineVariant = Gray200,
    scrim = Black
)

@Composable
fun BeautySpaTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current
    
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

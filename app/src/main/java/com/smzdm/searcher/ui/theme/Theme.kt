package com.smzdm.searcher.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Smzdm brand colors
val Orange500 = Color(0xFFFF6D00)
val Orange700 = Color(0xFFE65100)
val Orange200 = Color(0xFFFFCC80)

private val LightColorScheme = lightColorScheme(
    primary = Orange700,
    onPrimary = Color.White,
    primaryContainer = Orange200,
    secondary = Color(0xFF03A9F4),
    onSecondary = Color.White,
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1E),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1E),
    surfaceVariant = Color(0xFFFFF3E0),
    onSurfaceVariant = Color(0xFF49454F),
    error = Color(0xFFB3261E),
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = Orange200,
    onPrimary = Color(0xFF4E2600),
    primaryContainer = Orange700,
    secondary = Color(0xFF81D4FA),
    onSecondary = Color(0xFF003549),
    background = Color(0xFF1C1B1E),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1E),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF3E3A36),
    onSurfaceVariant = Color(0xFFCAC4D0),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410)
)

@Composable
fun SmzdmTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Use dynamic color on Android 12+
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}

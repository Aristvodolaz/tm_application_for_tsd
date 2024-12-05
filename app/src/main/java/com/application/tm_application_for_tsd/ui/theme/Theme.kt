package com.application.tm_application_for_tsd.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext


private val LightColors = lightColorScheme(
    primary = RedPrimary,
    onPrimary = White,
    secondary = GrayPrimary,
    onSecondary = White,
    background = LightGray,
    surface = White,
    onSurface = DarkGray
)

private val DarkColors = darkColorScheme(
    primary = RedPrimary,
    onPrimary = White,
    secondary = DarkGray,
    onSecondary = White,
    background = DarkGray,
    surface = GrayPrimary,
    onSurface = White
)

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,

    onSurface = Color(0xFF1C1B1F),
    */

@Composable
fun Tm_application_for_tsdTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
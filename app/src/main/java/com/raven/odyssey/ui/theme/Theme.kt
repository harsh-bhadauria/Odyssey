package com.raven.odyssey.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AppColorsDark.Purple,
    secondary = AppColorsDark.Teal,
    tertiary = AppColorsDark.Pink,
    background = AppColorsDark.Background,
    surface = AppColorsDark.Surface,
    onPrimary = AppColorsDark.White,
    onSecondary = AppColorsDark.White,
    onTertiary = AppColorsDark.White,
    onBackground = AppColorsDark.OnBackground,
    onSurface = AppColorsDark.OnSurface,
)

private val LightColorScheme = lightColorScheme(
    primary = AppColors.Purple,
    secondary = AppColors.Teal,
    tertiary = AppColors.Pink,
    background = AppColors.Background,
    surface = AppColors.White,
    onPrimary = AppColors.White,
    onSecondary = AppColors.White,
    onTertiary = AppColors.White,
    onBackground = AppColors.Black,
    onSurface = AppColors.Black,

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun OdysseyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme: ColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
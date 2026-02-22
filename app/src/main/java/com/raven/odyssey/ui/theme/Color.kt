package com.raven.odyssey.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

object AppColors {
    // The specific colors you extracted from the image
    val Purple = Color(0xFF813EE0)
    val Blue = Color(0xFF0080D5)
    val Teal = Color(0xFF00B3B2)
    val Yellow = Color(0xFFEF9A00)
    val Red = Color(0xFFE15265)
    val Pink = Color(0xFFE369BC)

    // Helper colors for backgrounds/text
    val Background = Color(0xFFECF3F6)
    val White = Color(0xFFFFFFFF)
    val Black = Color(0xFF000000)
}

/**
 * Dark palette for Odyssey.
 *
 * Keep accent colors consistent for brand, but adjust surfaces/background/text for dark UI.
 */
object AppColorsDark {
    // Accents (can be tweaked later, but safe defaults are same accents)
    val Purple = AppColors.Purple
    val Blue = AppColors.Blue
    val Teal = AppColors.Teal
    val Yellow = AppColors.Yellow
    val Red = AppColors.Red
    val Pink = AppColors.Pink

    // Surfaces / text
    val Background = Color(0xFF0F141A) // deep blue-gray
    val Surface = Color(0xFF151B22)    // slightly lighter for cards/sheets if needed
    val White = Color(0xFFFFFFFF)
    val Black = Color(0xFF000000)
    val OnBackground = Color(0xFFE8EEF2)
    val OnSurface = Color(0xFFE8EEF2)
}

package com.raven.odyssey.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.raven.odyssey.R

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)

val PixelSans = FontFamily(Font(R.font.pixel_sans))

val pixelSansStyle = TextStyle(
    fontFamily = PixelSans,
    fontWeight = FontWeight.Normal,
    fontSize = 28.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.5.sp
)


val Outfit = FontFamily(Font(R.font.outfit))
val OutfitRegular = FontFamily(Font(R.font.outfit_regular))
val DMSerifDisplay = FontFamily(Font(R.font.dm_serif_display))
val Inter = FontFamily(Font(R.font.inter))

object Typo {

    val Headline = TextStyle(
        fontFamily = DMSerifDisplay,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 40.sp
    )

    val Title = TextStyle(
        fontFamily = DMSerifDisplay,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp
    )

    val Subtitle = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        fontStyle = FontStyle.Italic,
        color = AppColors.Black.copy(alpha = 0.7f)
    )

    val Body = TextStyle(
        fontFamily = OutfitRegular,
        fontSize = 16.sp
    )

    val Time = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )

    val Number = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
    )
}
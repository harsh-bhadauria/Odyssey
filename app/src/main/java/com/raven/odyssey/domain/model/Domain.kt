package com.raven.odyssey.domain.model

import androidx.compose.ui.graphics.Color
import com.raven.odyssey.ui.theme.AppColors

enum class Domain(
    val color: Color
) {
    Intellect(AppColors.Blue),
    Vitality(AppColors.Red),
    Spirit(AppColors.Teal),
    Charisma(AppColors.Pink),
    Flow(AppColors.Purple),
    Order(AppColors.Yellow),
    Void(AppColors.Teal),
}
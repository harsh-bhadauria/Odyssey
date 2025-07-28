package com.raven.odyssey.ui.screens.habit.add

import com.raven.odyssey.domain.model.HabitFrequency
import com.raven.odyssey.domain.model.HabitType

data class HabitAddUiState(
    val name: String = "",
    val description: String = "",
    val isActive: Boolean = true,
    val frequency: HabitFrequency = HabitFrequency.Daily,
    val type: HabitType = HabitType.Binary,
    val hour: Int? = null,
    val minute: Int? = null,
    // For Measurable type
    val target: Int? = null,
    val unit: String? = null,
    // For Custom frequency
    val intervalDays: Int? = null,
)

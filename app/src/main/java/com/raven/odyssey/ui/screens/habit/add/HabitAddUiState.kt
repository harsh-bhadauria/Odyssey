package com.raven.odyssey.ui.screens.habit.add

import com.raven.odyssey.domain.model.Domain
import com.raven.odyssey.domain.model.HabitFrequency
import com.raven.odyssey.domain.model.HabitType

data class HabitAddUiState(
    val name: String = "",
    val description: String = "",
    val isActive: Boolean = true,
    val frequency: HabitFrequency = HabitFrequency.Daily,
    val type: HabitType = HabitType.Binary,
    val domain: Domain = Domain.Void,
    // For Measurable type
    val target: String = "",
    val unit: String = "",
    // For Custom frequency
    val intervalDays: Int? = null,
)

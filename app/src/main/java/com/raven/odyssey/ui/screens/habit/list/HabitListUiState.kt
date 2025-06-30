package com.raven.odyssey.ui.screens.habit.list

import com.raven.odyssey.data.entity.HabitEntity

data class HabitListUiState(
    val habits: List<HabitEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)


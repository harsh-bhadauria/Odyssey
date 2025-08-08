package com.raven.odyssey.ui.screens.habit.debug

import com.raven.odyssey.domain.model.Habit

data class HabitDebugUiState(
    val habits: List<Habit> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
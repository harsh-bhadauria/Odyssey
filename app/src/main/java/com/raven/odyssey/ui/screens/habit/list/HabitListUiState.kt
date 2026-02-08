package com.raven.odyssey.ui.screens.habit.list

import com.raven.odyssey.domain.model.Habit

data class HabitListUiState(
    val habits: List<Habit> = emptyList(),
    val completedHabits: List<Habit> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

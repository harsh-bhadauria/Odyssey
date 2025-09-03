package com.raven.odyssey.ui.screens.todo.add

import java.util.Calendar

data class TodoAddUiState(
    val title: String = "test",
    val description: String = "penguin",
    val selectedYear: Int,
    val selectedMonth: Int,
    val selectedDay: Int,
    val selectedHour: Int,
    val selectedMinute: Int,
    val isSaving: Boolean = false,
    val error: String? = null
) {
    companion object {
        fun default(): TodoAddUiState {
            val now = Calendar.getInstance()
            return TodoAddUiState(
                title = "",
                description = "",
                selectedYear = now.get(Calendar.YEAR),
                selectedMonth = now.get(Calendar.MONTH),
                selectedDay = now.get(Calendar.DAY_OF_MONTH),
                selectedHour = now.get(Calendar.HOUR_OF_DAY),
                selectedMinute = now.get(Calendar.MINUTE),
                isSaving = false,
                error = null
            )
        }
    }
}

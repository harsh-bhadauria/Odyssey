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
        fun default(initialDateMillis: Long? = null): TodoAddUiState {
            val now = Calendar.getInstance()
            val today = Calendar.getInstance()
            if (initialDateMillis != null) {
                now.timeInMillis = initialDateMillis
                // Check if initialDateMillis is today
                val isToday = now.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                        now.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
                if (isToday) {
                    // Use the nearest future hour from current time
                    val currentHour = today.get(Calendar.HOUR_OF_DAY)
                    val currentMinute = today.get(Calendar.MINUTE)
                    val hour = if (currentMinute > 0) currentHour + 1 else currentHour
                    now.set(Calendar.HOUR_OF_DAY, hour)
                    now.set(Calendar.MINUTE, 0)
                } else {
                    // Use 9:00 as default for other days
                    now.set(Calendar.HOUR_OF_DAY, 9)
                }
            } else {
                // No initial date, use nearest future hour from now
                val minute = now.get(Calendar.MINUTE)
                if (minute > 0) {
                    now.add(Calendar.HOUR_OF_DAY, 1)
                }
            }
            now.set(Calendar.MINUTE, 0)

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

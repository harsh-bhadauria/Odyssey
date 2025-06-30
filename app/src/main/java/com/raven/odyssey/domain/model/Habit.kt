package com.raven.odyssey.domain.model

sealed class HabitFrequency {
    data class Daily(val timesPerDay: Int) : HabitFrequency()
    data class Weekly(val timesPerWeek: Int) : HabitFrequency()
    data class Custom(val intervalDays: Int) : HabitFrequency()
}

data class Habit(
    val id: Long,
    val name: String,
    val description: String? = null,
    val isActive: Boolean = true,
    val frequency: HabitFrequency
)

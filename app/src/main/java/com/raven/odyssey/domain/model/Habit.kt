package com.raven.odyssey.domain.model

sealed class HabitFrequency {
    data object Daily : HabitFrequency()
    data object Weekly : HabitFrequency()
    data class Custom(val intervalDays: Int) : HabitFrequency()
}

sealed class HabitType{
    data object Binary : HabitType()
    data class Measurable(val target: Int, val unit: String, val progress: Int = 0) : HabitType()
}

data class Habit(
    val id: Long = 0,
    val name: String,
    val description: String? = null,
    val isActive: Boolean = true,
    val frequency: HabitFrequency = HabitFrequency.Daily,
    val type: HabitType = HabitType.Binary,
    val nextDue: Long,
)

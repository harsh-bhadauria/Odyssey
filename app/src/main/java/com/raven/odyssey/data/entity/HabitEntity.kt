package com.raven.odyssey.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.raven.odyssey.domain.model.Habit
import com.raven.odyssey.domain.model.HabitFrequency
import com.raven.odyssey.domain.model.HabitType

@Entity(tableName = "habits")
data class HabitEntity(

    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    val name: String,
    val description: String? = null,
    val isActive: Boolean = true,

    val frequencyType: String,
    val intervalDays: Int? = null,

    val habitType: String,
    val target: Int? = null,
    val unit: String? = null
)


fun Habit.toEntity(): HabitEntity {
    val (freqType, intervalDays) = when (frequency) {
        is HabitFrequency.Daily -> "Daily" to null
        is HabitFrequency.Weekly -> "Weekly" to null
        is HabitFrequency.Custom -> "Custom" to frequency.intervalDays
    }

    val (typeStr, unitStr, targetInt) = when (type) {
        is HabitType.Binary -> Triple("Binary", null, null)
        is HabitType.Measurable -> Triple("Measurable", type.unit, type.target)
    }

    return HabitEntity(
        name = name,
        description = description,
        isActive = isActive,
        frequencyType = freqType,
        intervalDays = intervalDays,
        habitType = typeStr,
        unit = unitStr,
        target = targetInt
    )
}

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
    val unit: String? = null,

    val nextDue: Long,
    val progress: Int? = null
)

// Helper for destructuring 4 values
private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

fun Habit.toEntity(): HabitEntity {
    val (freqType, intervalDays) = when (frequency) {
        is HabitFrequency.Daily -> "Daily" to null
        is HabitFrequency.Weekly -> "Weekly" to null
        is HabitFrequency.Custom -> "Custom" to frequency.intervalDays
    }

    val (typeStr, unitStr, targetInt, progressInt) = when (type) {
        is HabitType.Binary -> Quadruple("Binary", null, null, null)
        is HabitType.Measurable -> Quadruple("Measurable", type.unit, type.target, type.progress)
    }

    return HabitEntity(
        id = id,
        name = name,
        description = description,
        isActive = isActive,
        frequencyType = freqType,
        intervalDays = intervalDays,
        habitType = typeStr,
        unit = unitStr,
        target = targetInt,
        nextDue = nextDue,
        progress = progressInt
    )
}

fun HabitEntity.toDomain(): Habit {
    val domainFrequency = when (this.frequencyType) {
        "Daily" -> HabitFrequency.Daily
        "Weekly" -> HabitFrequency.Weekly
        "Custom" -> HabitFrequency.Custom(this.intervalDays ?: 1)
        else -> throw IllegalArgumentException("Unknown frequency type: ${this.frequencyType}")
    }
    val domainType = when (this.habitType) {
        "Binary" -> HabitType.Binary
        "Measurable" -> HabitType.Measurable(
            target = this.target ?: 0,
            unit = this.unit ?: "",
            progress = this.progress ?: 0
        )
        else -> throw IllegalArgumentException("Unknown habit type: ${this.habitType}")
    }
    return Habit(
        id = id,
        name = name,
        description = description,
        isActive = isActive,
        frequency = domainFrequency,
        type = domainType,
        nextDue = nextDue
    )
}

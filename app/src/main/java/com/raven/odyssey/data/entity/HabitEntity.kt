package com.raven.odyssey.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.raven.odyssey.domain.model.Habit
import com.raven.odyssey.domain.model.HabitFrequency

enum class HabitFrequencyType {
    DAILY,
    WEEKLY,
    CUSTOM
}

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String? = null,
    val isActive: Boolean = true,
    val frequencyType: HabitFrequencyType,
    val frequencyValue: Int = 1,
)

fun HabitEntity.toDomainModel(): Habit {
    val frequency = when (frequencyType) {
        HabitFrequencyType.DAILY -> HabitFrequency.Daily(frequencyValue)
        HabitFrequencyType.WEEKLY -> HabitFrequency.Weekly(frequencyValue)
        HabitFrequencyType.CUSTOM -> HabitFrequency.Custom(frequencyValue)
    }
    return Habit(id, name, description, isActive, frequency)
}

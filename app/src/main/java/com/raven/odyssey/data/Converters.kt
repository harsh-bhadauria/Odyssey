package com.raven.odyssey.data

import androidx.room.TypeConverter
import com.raven.odyssey.data.entity.HabitFrequencyType
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromHabitFrequencyType(type: HabitFrequencyType): String = type.name

    @TypeConverter
    fun toHabitFrequencyType(value: String): HabitFrequencyType =
        HabitFrequencyType.valueOf(value)

    @TypeConverter
    fun fromLocalDate(date: LocalDate): String = date.toString()

    @TypeConverter
    fun toLocalDate(value: String): LocalDate = LocalDate.parse(value)
}

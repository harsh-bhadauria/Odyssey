package com.raven.odyssey.domain.repository

import com.raven.odyssey.data.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun getAllHabits(): Flow<List<HabitEntity>>
    fun getHabitsForToday(time: Long): Flow<List<HabitEntity>>
    suspend fun insertHabit(habit: HabitEntity): Long
    suspend fun updateHabit(habit: HabitEntity)
    suspend fun deleteHabit(habit: HabitEntity)
    fun getDueHabits(currentTime: Long): Flow<List<HabitEntity>>
    fun getCompletedHabits(startOfDay: Long, endOfDay: Long): Flow<List<HabitEntity>>
    suspend fun logHabitCompletion(habitId: Long, timestamp: Long)
}
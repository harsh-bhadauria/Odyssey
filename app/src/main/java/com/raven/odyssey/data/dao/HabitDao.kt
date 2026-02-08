package com.raven.odyssey.data.dao

import androidx.room.*
import com.raven.odyssey.data.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity): Long

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    @Query("SELECT * FROM habits WHERE id = :id LIMIT 1")
    suspend fun getHabitById(id: Long): HabitEntity?

    @Query("SELECT * FROM habits WHERE (nextDue BETWEEN :startOfDay AND :endOfDay) OR nextDue IS NULL")
    fun getHabitsForToday(startOfDay: Long, endOfDay: Long): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE nextDue > :now")
    suspend fun getUpcomingHabits(now: Long): List<HabitEntity>

    @Query("SELECT * FROM habits WHERE nextDue <= :now")
    suspend fun getDueHabits(now: Long): List<HabitEntity>

    @Query("SELECT * FROM habits")
    suspend fun getAllHabitsList(): List<HabitEntity>

    @Query("SELECT h.* FROM habits h INNER JOIN habit_logs l ON h.id = l.habitId WHERE l.timestamp BETWEEN :startOfDay AND :endOfDay GROUP BY h.id ORDER BY MAX(l.timestamp) DESC")
    fun getCompletedHabits(startOfDay: Long, endOfDay: Long): Flow<List<HabitEntity>>
}

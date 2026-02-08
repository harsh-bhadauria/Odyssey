package com.raven.odyssey.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.raven.odyssey.data.entity.HabitLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: HabitLogEntity): Long

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId ORDER BY timestamp DESC")
    fun getLogsForHabit(habitId: Long): Flow<List<HabitLogEntity>>

    @Query("SELECT * FROM habit_logs WHERE timestamp BETWEEN :start AND :end")
    fun getLogsForDateRange(start: Long, end: Long): Flow<List<HabitLogEntity>>

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentLogs(habitId: Long, limit: Int): List<HabitLogEntity>

    @Query("DELETE FROM habit_logs WHERE id = :logId")
    suspend fun deleteLog(logId: Long)
}

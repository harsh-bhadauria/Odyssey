package com.raven.odyssey.data.repository

import com.raven.odyssey.data.dao.HabitDao
import com.raven.odyssey.data.entity.HabitEntity
import com.raven.odyssey.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject

class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao
) : HabitRepository {
    override fun getAllHabits(): Flow<List<HabitEntity>> = habitDao.getAllHabits()

    override suspend fun insertHabit(habit: HabitEntity): Long = habitDao.insertHabit(habit)

    override suspend fun updateHabit(habit: HabitEntity) = habitDao.updateHabit(habit)

    override suspend fun deleteHabit(habit: HabitEntity) = habitDao.deleteHabit(habit)

    override fun getHabitsForToday(time: Long): Flow<List<HabitEntity>> {
        val calendar = Calendar.getInstance().apply { timeInMillis = time }
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfDay = calendar.timeInMillis
        return habitDao.getHabitsForToday(startOfDay, endOfDay)
    }

    suspend fun getHabitById(id: Long): HabitEntity? {
        return habitDao.getHabitById(id)
    }

    override fun getDueHabits(currentTime: Long): Flow<List<HabitEntity>> {
        return habitDao.getAllHabits().map { entities ->
            entities.filter { entity ->
                entity.nextDue <= currentTime
            }
        }
    }
}

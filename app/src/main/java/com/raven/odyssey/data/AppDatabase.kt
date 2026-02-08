package com.raven.odyssey.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.raven.odyssey.data.dao.HabitDao
import com.raven.odyssey.data.dao.HabitLogDao
import com.raven.odyssey.data.dao.TodoDao
import com.raven.odyssey.data.entity.HabitEntity
import com.raven.odyssey.data.entity.HabitLogEntity
import com.raven.odyssey.data.entity.TodoEntity

@Database(
    entities = [HabitEntity::class, TodoEntity::class, HabitLogEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun todoDao(): TodoDao
    abstract fun habitLogDao(): HabitLogDao
}

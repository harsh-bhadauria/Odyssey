package com.raven.odyssey.di

import android.content.Context
import androidx.room.Room
import com.raven.odyssey.data.AppDatabase
import com.raven.odyssey.data.dao.HabitDao
import com.raven.odyssey.data.dao.TodoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase =
        Room.databaseBuilder(appContext, AppDatabase::class.java, "odyssey_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideHabitDao(db: AppDatabase): HabitDao = db.habitDao()

    @Provides
    fun provideTodoDao(db: AppDatabase): TodoDao = db.todoDao()

    @Provides
    fun provideHabitLogDao(db: AppDatabase): com.raven.odyssey.data.dao.HabitLogDao = db.habitLogDao()
}

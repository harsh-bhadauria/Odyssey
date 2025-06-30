package com.raven.odyssey.di

import com.raven.odyssey.domain.repository.HabitRepository
import com.raven.odyssey.data.repository.HabitRepositoryImpl
import com.raven.odyssey.domain.repository.TodoRepository
import com.raven.odyssey.data.repository.TodoRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindHabitRepository(
        impl: HabitRepositoryImpl
    ): HabitRepository

    @Binds
    @Singleton
    abstract fun bindTodoRepository(
        impl: TodoRepositoryImpl
    ): TodoRepository
}


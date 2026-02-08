package com.raven.odyssey.domain.repository

import com.raven.odyssey.data.entity.TodoEntity
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun getAllTodos(): Flow<List<TodoEntity>>
    fun getCompletedTodos(): Flow<List<TodoEntity>>
    suspend fun insertTodo(todo: TodoEntity): Long
    suspend fun updateTodo(todo: TodoEntity)
    suspend fun deleteTodo(todo: TodoEntity)
}
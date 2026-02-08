package com.raven.odyssey.data.repository

import com.raven.odyssey.data.dao.TodoDao
import com.raven.odyssey.data.entity.TodoEntity
import com.raven.odyssey.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TodoRepositoryImpl @Inject constructor(
    private val todoDao: TodoDao
) : TodoRepository {
    override fun getAllTodos(): Flow<List<TodoEntity>> = todoDao.getAllTodos()

    override fun getCompletedTodos(): Flow<List<TodoEntity>> = todoDao.getCompletedTodos()

    override suspend fun insertTodo(todo: TodoEntity): Long = todoDao.insertTodo(todo)

    override suspend fun updateTodo(todo: TodoEntity) = todoDao.updateTodo(todo)

    override suspend fun deleteTodo(todo: TodoEntity) = todoDao.deleteTodo(todo)

}

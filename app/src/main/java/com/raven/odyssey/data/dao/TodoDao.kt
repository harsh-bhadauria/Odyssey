package com.raven.odyssey.data.dao

import androidx.room.*
import com.raven.odyssey.data.entity.TodoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos")
    fun getAllTodos(): Flow<List<TodoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: TodoEntity): Long

    @Update
    suspend fun updateTodo(todo: TodoEntity)

    @Delete
    suspend fun deleteTodo(todo: TodoEntity)

    @Query("SELECT COUNT(*) FROM todos")
    suspend fun getTodoCount(): Int

    @Query("SELECT * FROM todos WHERE dueTime > :now")
    suspend fun getUpcomingTodos(now: Long): List<TodoEntity>

    @Query("SELECT * FROM todos WHERE isCompleted = 0")
    suspend fun getIncompleteTodos(): List<TodoEntity>
}

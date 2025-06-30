package com.raven.odyssey.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.raven.odyssey.domain.model.Todo

@Entity(tableName = "todos")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String? = null,
    val hour: Int? = null,
    val minute: Int? = null,
    val isCompleted: Boolean = false,
    val dueDate: Long? = null
)

fun TodoEntity.toDomain(): Todo = Todo(
    id = id,
    title = title,
    description = description,
    hour = hour,
    minute = minute,
    isCompleted = isCompleted,
    dueDate = dueDate
)

fun Todo.toEntity(): TodoEntity = TodoEntity(
    id = id,
    title = title,
    description = description,
    hour = hour,
    minute = minute,
    isCompleted = isCompleted,
    dueDate = dueDate
)

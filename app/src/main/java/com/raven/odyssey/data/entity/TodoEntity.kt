package com.raven.odyssey.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.raven.odyssey.domain.model.Domain
import com.raven.odyssey.domain.model.Todo

@Entity(tableName = "todos")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String? = null,
    val isCompleted: Boolean = false,
    @ColumnInfo(name = "dueTime")
    val dueTime: Long,
    val domain: String = Domain.Void.name,
)

fun TodoEntity.toDomain(): Todo = Todo(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted,
    dueTime = dueTime,
    domain = Domain.entries.firstOrNull { it.name.equals(domain, ignoreCase = true) } ?: Domain.Void,
)

fun Todo.toEntity(): TodoEntity = TodoEntity(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted,
    dueTime = dueTime,
    domain = domain.name,
)

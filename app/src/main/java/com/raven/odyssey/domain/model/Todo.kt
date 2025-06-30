package com.raven.odyssey.domain.model

data class Todo(
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val hour: Int? = null,
    val minute: Int? = null,
    val isCompleted: Boolean = false,
    val dueDate: Long? = null,
)

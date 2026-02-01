package com.raven.odyssey.domain.model

data class Todo(
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val isCompleted: Boolean = false,
    val dueTime: Long,
    val domain: Domain = Domain.Void,
)

package com.raven.odyssey.ui.screens.todo.list

import com.raven.odyssey.domain.model.Todo

data class TodoListUiState(
    val todos: List<Todo> = emptyList(),
    val overdueTodos: List<Todo> = emptyList(),
    val todayTodos: List<Todo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedDate: Long = 0L,
    val weekStart: Long = 0L
)

package com.raven.odyssey.ui.screens.todo.list

import com.raven.odyssey.data.entity.TodoEntity

data class TodoListUiState(
    val todos: List<TodoEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

package com.raven.odyssey.ui.screens.todo.list

import com.raven.odyssey.domain.model.Todo

data class TodoItemUiState(
    val id: Long,
    val title: String,
    val isCompleted: Boolean,
    val formattedTime: String?,
    val todo: Todo
)

data class TodoListUiState(
    val overdueTodos: List<TodoItemUiState> = emptyList(),
    val todayTodos: List<TodoItemUiState> = emptyList(),
    val completedTodos: List<TodoItemUiState> = emptyList(),
    val isOverdueExpanded: Boolean = true,
    val isTodayExpanded: Boolean = true,
    val isCompletedExpanded: Boolean = false,
    val greetingName: String = "Ravenous",
    val greetingSubtitle: String = "Top ~~ramen~~ o' the morning",
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedDate: Long = 0L,
    val weekStart: Long = 0L
) {
    val isAllEmpty: Boolean
        get() = overdueTodos.isEmpty() && todayTodos.isEmpty() && completedTodos.isEmpty()

    val showTodayEmptyMessage: Boolean
        get() = todayTodos.isEmpty() && overdueTodos.isNotEmpty()
}

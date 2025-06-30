package com.raven.odyssey.ui.screens.todo.add

data class TodoAddUiState(
    val title: String = "test",
    val description: String = "penguin",
    val dueDate: String = "",
    val hour: Int? = null,
    val minute: Int? = null,
    val isSaving: Boolean = false,
    val error: String? = null
)


package com.raven.odyssey.ui.screens.todo.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raven.odyssey.data.entity.toEntity
import com.raven.odyssey.domain.model.Todo
import com.raven.odyssey.domain.notification.NotificationScheduler
import com.raven.odyssey.domain.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoAddViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(TodoAddUiState())
    val uiState: StateFlow<TodoAddUiState> = _uiState.asStateFlow()

    fun updateUiState(
        title: String? = null,
        description: String? = null,
        hour: Int? = null,
        minute: Int? = null,
        dueDate: String? = null,
        isSaving: Boolean? = null,
        error: String? = null,
    ) {
        _uiState.update {
            it.copy(
                title = title ?: it.title,
                description = description ?: it.description,
                hour = hour ?: it.hour,
                minute = minute ?: it.minute,
                dueDate = dueDate ?: it.dueDate,
                isSaving = isSaving ?: it.isSaving,
                error = error
            )
        }
    }

    fun addTodo(onAdded: () -> Unit) {
        val state = _uiState.value

        if (state.title.isBlank()) {
            updateUiState(error = "Title cannot be empty")
            return
        }

        updateUiState(isSaving = true, error = null)

        viewModelScope.launch {
            try {
                val todo = Todo(
                    title = state.title,
                    description = state.description.takeIf { it.isNotBlank() },
                    hour = state.hour,
                    minute = state.minute,
                    dueDate = state.dueDate.toLongOrNull()
                )
                val id = todoRepository.insertTodo(todo.toEntity())
                val todoWithId = todo.copy(id = id)
                notificationScheduler.scheduleNotification(todoWithId)
                _uiState.value = TodoAddUiState()
                onAdded()

            } catch (e: Exception) {
                updateUiState(isSaving = false, error = e.message)
            }
        }
    }
}
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

    private val _uiState = MutableStateFlow(TodoAddUiState.default())
    val uiState: StateFlow<TodoAddUiState> = _uiState.asStateFlow()

    fun updateUiState(
        title: String? = null,
        description: String? = null,
        selectedYear: Int? = null,
        selectedMonth: Int? = null,
        selectedDay: Int? = null,
        selectedHour: Int? = null,
        selectedMinute: Int? = null,
        isSaving: Boolean? = null,
        error: String? = null,
    ) {
        _uiState.update {
            it.copy(
                title = title ?: it.title,
                description = description ?: it.description,
                selectedYear = selectedYear ?: it.selectedYear,
                selectedMonth = selectedMonth ?: it.selectedMonth,
                selectedDay = selectedDay ?: it.selectedDay,
                selectedHour = selectedHour ?: it.selectedHour,
                selectedMinute = selectedMinute ?: it.selectedMinute,
                isSaving = isSaving ?: it.isSaving,
                error = error ?: it.error
            )
        }
    }

    private fun getDueTimeMillis(): Long {
        val state = _uiState.value
        val calendar = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.YEAR, state.selectedYear)
            set(java.util.Calendar.MONTH, state.selectedMonth)
            set(java.util.Calendar.DAY_OF_MONTH, state.selectedDay)
            set(java.util.Calendar.HOUR_OF_DAY, state.selectedHour)
            set(java.util.Calendar.MINUTE, state.selectedMinute)
        }
        return calendar.timeInMillis
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
                    dueTime = getDueTimeMillis()
                )
                val id = todoRepository.insertTodo(todo.toEntity())
                val todoWithId = todo.copy(id = id)
                notificationScheduler.scheduleNotification(todoWithId)
                _uiState.value = TodoAddUiState.default()
                onAdded()

            } catch (e: Exception) {
                updateUiState(isSaving = false, error = e.message)
            }
        }
    }
}
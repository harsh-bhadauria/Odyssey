package com.raven.odyssey.ui.screens.todo.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raven.odyssey.data.entity.toDomain
import com.raven.odyssey.data.entity.toEntity
import com.raven.odyssey.domain.model.Todo
import com.raven.odyssey.domain.notification.NotificationScheduler
import com.raven.odyssey.domain.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

fun getStartOfDay(timeInMillis: Long): Long {
    return Calendar.getInstance().apply {
        setTimeInMillis(timeInMillis)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

fun getStartOfWeek(timeInMillis: Long): Long {
    return Calendar.getInstance().apply {
        setTimeInMillis(timeInMillis)
        set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {
    private val today = System.currentTimeMillis()
    private val initialSelectedDate = getStartOfDay(today)
    private val initialWeekStart = getStartOfWeek(today)
    private val _uiState = MutableStateFlow(
        TodoListUiState(
            isLoading = true,
            selectedDate = initialSelectedDate,
            weekStart = initialWeekStart
        )
    )
    val uiState: StateFlow<TodoListUiState> = _uiState.asStateFlow()

    init {
        loadTodos()
    }

    fun updateUiState(
        selectedDate: Long = uiState.value.selectedDate,
        weekStart: Long = uiState.value.weekStart
    ) {
        _uiState.update { it.copy(selectedDate = selectedDate, weekStart = weekStart) }
    }

    fun loadTodos() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                todoRepository.getAllTodos().map {
                    it.map { todoEntity ->
                        todoEntity.toDomain()
                    }
                }.collect { todos ->
                    _uiState.update { it.copy(todos = todos, isLoading = false, error = null) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            try {
                todoRepository.deleteTodo(todo.toEntity())
                notificationScheduler.cancelNotification(todo.id)
            } catch (e: Exception) {
            }
        }
    }
}
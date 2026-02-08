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
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private fun startOfDay(timeInMillis: Long): Long {
    return Calendar.getInstance().apply {
        this.timeInMillis = timeInMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

fun getStartOfWeek(timeInMillis: Long): Long {
    return Calendar.getInstance().apply {
        this.timeInMillis = timeInMillis
        set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

private fun startOfNextDay(timeInMillis: Long): Long {
    val start = startOfDay(timeInMillis)
    return start + TimeUnit.DAYS.toMillis(1)
}

@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {
    private val today = System.currentTimeMillis()
    private val initialSelectedDate = startOfDay(today)
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

    fun toggleOverdueExpanded() {
        _uiState.update { it.copy(isOverdueExpanded = !it.isOverdueExpanded) }
    }

    fun toggleTodayExpanded() {
        _uiState.update { it.copy(isTodayExpanded = !it.isTodayExpanded) }
    }

    fun toggleCompletedExpanded() {
        _uiState.update { it.copy(isCompletedExpanded = !it.isCompletedExpanded) }
    }

    fun loadTodos() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                todoRepository.getAllTodos().map { entities ->
                    entities.map { it.toDomain() }
                }.collect { todos ->
                    val now = System.currentTimeMillis()
                    val todayStart = startOfDay(now)
                    val tomorrowStart = startOfNextDay(now)

                    val overdue = todos
                        .filter { !it.isCompleted }
                        .filter { it.dueTime in 1 until todayStart }
                        .sortedBy { it.dueTime }
                        .map (::toUiState)

                    val today = todos
                        .filter { !it.isCompleted }
                        .filter { it.dueTime in todayStart until tomorrowStart }
                        .sortedBy { it.dueTime }
                        .map (::toUiState)

                    val completed = todos
                        .filter { it.isCompleted }
                        .sortedByDescending { it.dueTime }
                        .map (::toUiState)

                    _uiState.update {
                        it.copy(
                            overdueTodos = overdue,
                            todayTodos = today,
                            completedTodos = completed,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun toUiState(todo: Todo): TodoItemUiState {
        val formattedTime = if (todo.dueTime > 0L) {
            val cal = Calendar.getInstance().apply { timeInMillis = todo.dueTime }
            String.format(
                Locale.getDefault(),
                "%02d:%02d",
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE)
            )
        } else null

        return TodoItemUiState(
            id = todo.id,
            title = todo.title,
            isCompleted = todo.isCompleted,
            formattedTime = formattedTime,
            todo = todo
        )
    }

    fun toggleTodo(todo: Todo) {
        viewModelScope.launch {
            try {
                val updatedTodo = todo.copy(isCompleted = !todo.isCompleted)
                todoRepository.updateTodo(updatedTodo.toEntity())
                if (updatedTodo.isCompleted) {
                    notificationScheduler.cancelNotification(todo.id)
                }
            } catch (e: Exception) {
                // Handle error
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
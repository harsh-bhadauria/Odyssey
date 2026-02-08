package com.raven.odyssey.ui.screens.habit.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raven.odyssey.data.entity.toDomain
import com.raven.odyssey.data.entity.toEntity
import com.raven.odyssey.domain.model.Habit
import com.raven.odyssey.domain.model.HabitFrequency
import com.raven.odyssey.domain.model.HabitType
import com.raven.odyssey.domain.notification.HabitNotificationScheduler
import com.raven.odyssey.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HabitListViewModel @Inject constructor(
    private val habitRepository: HabitRepository,
    private val notificationScheduler: HabitNotificationScheduler
) : ViewModel() {
    private val _uiState = MutableStateFlow(HabitListUiState(isLoading = true))
    val uiState: StateFlow<HabitListUiState> = _uiState

    init {
        loadHabits()
    }

    private fun loadHabits() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            calendar.set(Calendar.MILLISECOND, 999)
            val endOfDay = calendar.timeInMillis

            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startOfDay = calendar.timeInMillis

            val dueHabitsFlow = habitRepository.getDueHabits(endOfDay)
                .map { habitsList ->
                    habitsList
                        .map { habitEntity -> habitEntity.toDomain() }
                        .sortedWith(
                            compareBy<Habit> { habit ->
                                when (habit.type) {
                                    is HabitType.Binary -> 0
                                    is HabitType.Measurable -> 1
                                }
                            }.thenBy { it.nextDue }
                        )
                }

            val completedHabitsFlow = habitRepository.getCompletedHabits(startOfDay, endOfDay)
                .map { habitsList ->
                    habitsList.map { it.toDomain() }
                }

            combine(dueHabitsFlow, completedHabitsFlow) { due, completed ->
                HabitListUiState(
                    habits = due,
                    completedHabits = completed,
                    isLoading = false,
                    error = null
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun completeHabit(habit: Habit) {
        viewModelScope.launch {
            // Normalize "Today" to midnight
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val todayMidnight = calendar.timeInMillis

            // For Weekly habits, the "Start of Week" is Monday.

            val nextDueMillis = when (habit.frequency) {
                is HabitFrequency.Daily -> {
                    // Next due is Tomorrow (Today + 1 day)
                    calendar.timeInMillis = todayMidnight
                    calendar.add(Calendar.DAY_OF_YEAR, 1)
                    calendar.timeInMillis
                }

                is HabitFrequency.Weekly -> {
                    // Next occurrence of MONDAY, strictly after today.
                    calendar.timeInMillis = todayMidnight
                    val currentDoW = calendar.get(Calendar.DAY_OF_WEEK)
                    val targetDoW = Calendar.MONDAY

                    val daysUntilNext = (targetDoW - currentDoW + 7) % 7
                    // If daysUntilNext is 0 (today is Monday), we want next week (7 days)
                    val daysToAdd = if (daysUntilNext == 0) 7 else daysUntilNext

                    calendar.add(Calendar.DAY_OF_YEAR, daysToAdd)
                    calendar.timeInMillis
                }

                is HabitFrequency.Custom -> {
                    // Next due is Today + Interval
                    calendar.timeInMillis = todayMidnight
                    calendar.add(Calendar.DAY_OF_YEAR, habit.frequency.intervalDays)
                    calendar.timeInMillis
                }
            }

            // Reset progress for measurable habits
            val updatedType = if (habit.type is HabitType.Measurable) {
                (habit.type).copy(progress = 0)
            } else habit.type
            val updatedHabit = habit.copy(nextDue = nextDueMillis, type = updatedType)

            habitRepository.updateHabit(updatedHabit.toEntity())
            habitRepository.logHabitCompletion(habit.id, System.currentTimeMillis())
            notificationScheduler.cancelNotification(habit.id)
            notificationScheduler.scheduleNotification(updatedHabit)
        }
    }

    fun incrementProgress(habit: Habit) {
        val measurable = habit.type
        if (measurable is HabitType.Measurable) {
            val newProgress = (measurable.progress + 1).coerceAtMost(measurable.target)
            val updatedHabit = habit.copy(type = measurable.copy(progress = newProgress))
            viewModelScope.launch {
                habitRepository.updateHabit(updatedHabit.toEntity())
                // Update UI state
                _uiState.value = _uiState.value.copy(
                    habits = _uiState.value.habits.map {
                        if (it.id == habit.id) updatedHabit else it
                    }
                )
                if (newProgress == measurable.target) {
                    completeHabit(updatedHabit)
                }
            }
        }
    }

    fun decrementProgress(habit: Habit) {
        val measurable = habit.type
        if (measurable is HabitType.Measurable) {
            val newProgress = (measurable.progress - 1).coerceAtLeast(0)
            val updatedHabit = habit.copy(type = measurable.copy(progress = newProgress))
            viewModelScope.launch {
                habitRepository.updateHabit(updatedHabit.toEntity())
                // Update UI state
                _uiState.value = _uiState.value.copy(
                    habits = _uiState.value.habits.map {
                        if (it.id == habit.id) updatedHabit else it
                    }
                )
            }
        }
    }
}

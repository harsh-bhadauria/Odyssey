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
            habitRepository.getHabitsForToday(System.currentTimeMillis()).map { habitsList ->
                habitsList.map { habitEntity ->
                    habitEntity.toDomain()
                }
            }.collect { habits ->
                _uiState.value = HabitListUiState(
                    habits = habits,
                    isLoading = false
                )
            }
        }
    }

    fun completeHabit(habit: Habit) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val dueCal = Calendar.getInstance().apply { timeInMillis = habit.nextDue }
            val hour = dueCal.get(Calendar.HOUR_OF_DAY)
            val minute = dueCal.get(Calendar.MINUTE)
            when (habit.frequency) {
                is HabitFrequency.Daily -> {
                    // Advance to same time tomorrow
                    dueCal.timeInMillis = habit.nextDue
                    do {
                        dueCal.add(Calendar.DATE, 1)
                    } while (dueCal.timeInMillis <= now)
                }

                is HabitFrequency.Weekly -> {
                    // Advance to same time next week on the scheduled weekday
                    dueCal.timeInMillis = habit.nextDue
                    val dayOfWeek = dueCal.get(Calendar.DAY_OF_WEEK)
                    val nowCal = Calendar.getInstance().apply { timeInMillis = now }
                    val daysUntilNext = (dayOfWeek - nowCal.get(Calendar.DAY_OF_WEEK) + 7) % 7
                    dueCal.timeInMillis = now
                    dueCal.add(Calendar.DATE, if (daysUntilNext == 0) 7 else daysUntilNext)
                }

                is HabitFrequency.Custom -> {
                    // Advance to same time after interval
                    dueCal.timeInMillis = habit.nextDue
                    do {
                        dueCal.add(Calendar.DATE, habit.frequency.intervalDays)
                    } while (dueCal.timeInMillis <= now)
                }
            }

            dueCal.set(Calendar.HOUR_OF_DAY, hour)
            dueCal.set(Calendar.MINUTE, minute)

            // Reset progress for measurable habits
            val updatedType = if (habit.type is HabitType.Measurable) {
                (habit.type).copy(progress = 0)
            } else habit.type
            val updatedHabit = habit.copy(nextDue = dueCal.timeInMillis, type = updatedType)

            habitRepository.updateHabit(updatedHabit.toEntity())
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

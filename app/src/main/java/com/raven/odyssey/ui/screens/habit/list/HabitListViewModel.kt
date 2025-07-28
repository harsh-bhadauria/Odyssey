package com.raven.odyssey.ui.screens.habit.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raven.odyssey.data.entity.toDomain
import com.raven.odyssey.data.entity.toEntity
import com.raven.odyssey.domain.model.Habit
import com.raven.odyssey.domain.notification.HabitNotificationScheduler
import com.raven.odyssey.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
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
                val now = System.currentTimeMillis()
                habitsList.map { habitEntity ->
                    val habit = habitEntity.toDomain()
                    val oneDayMillis = 24 * 60 * 60 * 1000
                    if (habit.nextDue - now > oneDayMillis) {
                        val oldDue =
                            java.util.Calendar.getInstance().apply { timeInMillis = habit.nextDue }
                        val newDue = java.util.Calendar.getInstance().apply {
                            timeInMillis = now
                            add(java.util.Calendar.DAY_OF_YEAR, 1)
                            set(
                                java.util.Calendar.HOUR_OF_DAY,
                                oldDue.get(java.util.Calendar.HOUR_OF_DAY)
                            )
                            set(java.util.Calendar.MINUTE, oldDue.get(java.util.Calendar.MINUTE))
                            set(java.util.Calendar.SECOND, 0)
                            set(java.util.Calendar.MILLISECOND, 0)
                        }
                        habit.copy(nextDue = newDue.timeInMillis)
                    } else {
                        habit
                    }
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
            val currentDueDate = habit.nextDue
            val calendar = java.util.Calendar.getInstance()
            calendar.timeInMillis = currentDueDate
            calendar.add(java.util.Calendar.DATE, 1)
            val newDueDate = calendar.timeInMillis
            val updatedHabit = habit.copy(nextDue = newDueDate)
            habitRepository.updateHabit(updatedHabit.toEntity())
            notificationScheduler.cancelNotification(habit.id)
            notificationScheduler.scheduleNotification(updatedHabit)
        }
    }
}

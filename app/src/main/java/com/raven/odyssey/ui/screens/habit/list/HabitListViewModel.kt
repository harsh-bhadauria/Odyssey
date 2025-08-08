package com.raven.odyssey.ui.screens.habit.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raven.odyssey.data.entity.toDomain
import com.raven.odyssey.data.entity.toEntity
import com.raven.odyssey.domain.model.Habit
import com.raven.odyssey.domain.model.HabitFrequency
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
            habitRepository.getDueHabits(System.currentTimeMillis()).map { habitsList ->
                val now = System.currentTimeMillis()
                habitsList.map { habitEntity ->
                    val habit = habitEntity.toDomain()
                    val oneDayMillis = 24 * 60 * 60 * 1000
                    if (habit.nextDue - now > oneDayMillis) {
                        val oldDue =
                            Calendar.getInstance().apply { timeInMillis = habit.nextDue }
                        val newDue = Calendar.getInstance().apply {
                            timeInMillis = now
                            add(Calendar.DAY_OF_YEAR, 1)
                            set(
                                Calendar.HOUR_OF_DAY,
                                oldDue.get(Calendar.HOUR_OF_DAY)
                            )
                            set(Calendar.MINUTE, oldDue.get(Calendar.MINUTE))
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
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
            val now = System.currentTimeMillis()
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = now
            when (habit.frequency) {
                is HabitFrequency.Daily -> {
                    // Add 1 day until in the future
                    val dueCal =
                        Calendar.getInstance().apply { timeInMillis = habit.nextDue }
                    while (dueCal.timeInMillis <= now) {
                        dueCal.add(Calendar.DATE, 1)
                    }
                    val updatedHabit = habit.copy(nextDue = dueCal.timeInMillis)
                    habitRepository.updateHabit(updatedHabit.toEntity())
                    notificationScheduler.cancelNotification(habit.id)
                    notificationScheduler.scheduleNotification(updatedHabit)
                }

                is HabitFrequency.Weekly -> {
                    // Set to next Monday at 00:00
                    val dueCal = Calendar.getInstance().apply { timeInMillis = now }
                    dueCal.set(Calendar.HOUR_OF_DAY, 0)
                    dueCal.set(Calendar.MINUTE, 0)
                    dueCal.set(Calendar.SECOND, 0)
                    dueCal.set(Calendar.MILLISECOND, 0)
                    val dayOfWeek = dueCal.get(Calendar.DAY_OF_WEEK)
                    val daysUntilMonday = (Calendar.MONDAY - dayOfWeek + 7) % 7
                    if (daysUntilMonday == 0 && dueCal.timeInMillis > now) {
                        // Today is Monday and time is in the future
                    } else {
                        dueCal.add(Calendar.DATE, if (daysUntilMonday == 0) 7 else daysUntilMonday)
                    }
                    val updatedHabit = habit.copy(nextDue = dueCal.timeInMillis)
                    habitRepository.updateHabit(updatedHabit.toEntity())
                    notificationScheduler.cancelNotification(habit.id)
                    notificationScheduler.scheduleNotification(updatedHabit)
                }

                is HabitFrequency.Custom -> {
                    val dueCal =
                        Calendar.getInstance().apply { timeInMillis = habit.nextDue }
                    while (dueCal.timeInMillis <= now) {
                        dueCal.add(Calendar.DATE, habit.frequency.intervalDays)
                    }
                    val updatedHabit = habit.copy(nextDue = dueCal.timeInMillis)
                    habitRepository.updateHabit(updatedHabit.toEntity())
                    notificationScheduler.cancelNotification(habit.id)
                    notificationScheduler.scheduleNotification(updatedHabit)
                }
            }
        }
    }
}

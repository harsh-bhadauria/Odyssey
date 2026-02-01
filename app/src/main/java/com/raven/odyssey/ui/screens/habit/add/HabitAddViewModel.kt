package com.raven.odyssey.ui.screens.habit.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raven.odyssey.data.entity.toEntity
import com.raven.odyssey.domain.model.Habit
import com.raven.odyssey.domain.model.Domain
import com.raven.odyssey.domain.model.HabitFrequency
import com.raven.odyssey.domain.model.HabitType
import com.raven.odyssey.domain.notification.HabitNotificationScheduler
import com.raven.odyssey.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel

class HabitAddViewModel @Inject constructor(
    private val habitRepository: HabitRepository,
    private val notificationScheduler: HabitNotificationScheduler
) : ViewModel() {
    private val _uiState = MutableStateFlow(HabitAddUiState())
    val uiState: StateFlow<HabitAddUiState> = _uiState

    fun updateUiState(
        name: String? = null,
        description: String? = null,
        hour: Int? = null,
        minute: Int? = null,
        frequency: HabitFrequency? = null,
        type: HabitType? = null,
        domain: Domain? = null,
        target: Int? = null,
        unit: String? = null,
        intervalDays: Int? = null,
    ) {
        _uiState.value = _uiState.value.copy(
            name = name ?: _uiState.value.name,
            description = description ?: _uiState.value.description,
            hour = hour ?: _uiState.value.hour,
            minute = minute ?: _uiState.value.minute,
            frequency = frequency ?: _uiState.value.frequency,
            type = type ?: _uiState.value.type,
            domain = domain ?: _uiState.value.domain,
            target = target ?: _uiState.value.target,
            unit = unit ?: _uiState.value.unit,
            intervalDays = intervalDays ?: _uiState.value.intervalDays,
        )
    }

    fun addHabit(onAdded: () -> Unit) {
        if (_uiState.value.name.isBlank()) return
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, _uiState.value.hour ?: 0)
            calendar.set(Calendar.MINUTE, _uiState.value.minute ?: 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            val nextDue = if (_uiState.value.frequency == HabitFrequency.Weekly) {
                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                val daysSinceMonday = (dayOfWeek + 5) % 7 // Monday = 2, so (2+5)%7=0
                calendar.add(Calendar.DAY_OF_MONTH, -daysSinceMonday)
                calendar.timeInMillis
            } else {
                calendar.timeInMillis
            }

            val frequency = when (_uiState.value.frequency) {
                is HabitFrequency.Custom -> HabitFrequency.Custom(_uiState.value.intervalDays ?: 1)
                else -> _uiState.value.frequency
            }
            val type = when (_uiState.value.type) {
                is HabitType.Measurable -> HabitType.Measurable(
                    target = _uiState.value.target ?: 1,
                    unit = _uiState.value.unit ?: ""
                )

                else -> _uiState.value.type
            }
            val habit = Habit(
                name = _uiState.value.name,
                description = _uiState.value.description,
                isActive = true,
                frequency = frequency,
                type = type,
                domain = _uiState.value.domain,
                nextDue = nextDue
            )
            val id = habitRepository.insertHabit(habit.toEntity())
            val habitWithId = habit.copy(id = id)
            notificationScheduler.scheduleNotification(habitWithId)
            onAdded()
        }
    }
}

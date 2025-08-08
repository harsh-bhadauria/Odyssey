package com.raven.odyssey.ui.screens.habit.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raven.odyssey.data.entity.toDomain
import com.raven.odyssey.data.entity.toEntity
import com.raven.odyssey.domain.model.Habit
import com.raven.odyssey.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitDebugViewModel @Inject constructor(
    private val habitRepository: HabitRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HabitDebugUiState(isLoading = true))
    val uiState: StateFlow<HabitDebugUiState> = _uiState

    init {
        loadAllHabits()
    }

    private fun loadAllHabits() {
        viewModelScope.launch {
            habitRepository.getAllHabits().collect { entities ->
                _uiState.value = HabitDebugUiState(
                    habits = entities.map { it.toDomain() },
                    isLoading = false
                )
            }

        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            habitRepository.deleteHabit(habit.toEntity())
            loadAllHabits()
        }
    }
}


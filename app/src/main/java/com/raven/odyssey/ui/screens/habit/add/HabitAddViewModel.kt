package com.raven.odyssey.ui.screens.habit.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raven.odyssey.data.entity.HabitEntity
import com.raven.odyssey.data.entity.HabitFrequencyType
import com.raven.odyssey.domain.model.HabitFrequency
import com.raven.odyssey.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel

class HabitAddViewModel @Inject constructor(
    private val habitRepository: HabitRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HabitAddUiState())
    val uiState: StateFlow<HabitAddUiState> = _uiState

    fun updateUiState(
        name: String? = null,
        description: String? = null,
        isSaving: Boolean? = null
    ) {
        _uiState.value = _uiState.value.copy(
            name = name ?: _uiState.value.name,
            description = description ?: _uiState.value.description,
            isSaving = isSaving ?: _uiState.value.isSaving
        )
    }

    fun saveHabit(onSaved: () -> Unit) {
        if (_uiState.value.name.isBlank()) return
        updateUiState(isSaving = true)
        viewModelScope.launch {
            habitRepository.insertHabit(
                HabitEntity(name = _uiState.value.name, frequencyType = HabitFrequencyType.DAILY,
                    description = _uiState.value.description)
            )
            updateUiState(isSaving = false)
            onSaved()
        }
    }
}

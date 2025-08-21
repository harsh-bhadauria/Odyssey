package com.raven.odyssey.ui.screens.habit.add

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitAddScreen(
    viewModel: HabitAddViewModel = hiltViewModel(),
    onHabitAdded: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val timePickerDialogVisible = remember { mutableStateOf(false) }
    val calendar = Calendar.getInstance()
    val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
    val currentMinute = calendar.get(Calendar.MINUTE) + 1
    val timePickerState = remember { TimePickerState(currentHour, currentMinute, false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = uiState.name,
            onValueChange = { viewModel.updateUiState(name = it) },
            label = { Text("Habit Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = uiState.description,
            onValueChange = { viewModel.updateUiState(description = it) },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Frequency selection
        var freqExpanded by remember { mutableStateOf(false) }
        Box {
            OutlinedButton(onClick = { freqExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(
                    when (uiState.frequency) {
                        is com.raven.odyssey.domain.model.HabitFrequency.Daily -> "Daily"
                        is com.raven.odyssey.domain.model.HabitFrequency.Weekly -> "Weekly"
                        is com.raven.odyssey.domain.model.HabitFrequency.Custom -> "Custom"
                    }
                )
            }
            DropdownMenu(expanded = freqExpanded, onDismissRequest = { freqExpanded = false }) {
                DropdownMenuItem(text = { Text("Daily") }, onClick = {
                    viewModel.updateUiState(
                        frequency = com.raven.odyssey.domain.model.HabitFrequency.Daily,
                        intervalDays = null
                    )
                    freqExpanded = false
                })
                DropdownMenuItem(text = { Text("Weekly") }, onClick = {
                    viewModel.updateUiState(
                        frequency = com.raven.odyssey.domain.model.HabitFrequency.Weekly,
                        intervalDays = null,
                        hour = null,
                        minute = null
                    )
                    freqExpanded = false
                })
                DropdownMenuItem(text = { Text("Custom") }, onClick = {
                    viewModel.updateUiState(
                        frequency = com.raven.odyssey.domain.model.HabitFrequency.Custom(
                            uiState.intervalDays ?: 1
                        )
                    )
                    freqExpanded = false
                })
            }
        }
        if (uiState.frequency is com.raven.odyssey.domain.model.HabitFrequency.Custom) {
            OutlinedTextField(
                value = uiState.intervalDays?.toString() ?: "",
                onValueChange = { value ->
                    val intVal = value.toIntOrNull() ?: 1
                    viewModel.updateUiState(
                        intervalDays = intVal,
                        frequency = com.raven.odyssey.domain.model.HabitFrequency.Custom(intVal)
                    )
                },
                label = { Text("Interval Days") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Only show time picker for Daily and Custom
        if (uiState.frequency !is com.raven.odyssey.domain.model.HabitFrequency.Weekly) {
            if (timePickerDialogVisible.value) {
                AlertDialog(
                    onDismissRequest = { timePickerDialogVisible.value = false },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.updateUiState(
                                hour = timePickerState.hour,
                                minute = timePickerState.minute
                            )
                            timePickerDialogVisible.value = false
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { timePickerDialogVisible.value = false }) {
                            Text("Cancel")
                        }
                    },
                    title = { Text("Pick Time") },
                    text = {
                        TimePicker(state = timePickerState)
                    }
                )
            }

            Button(
                onClick = { timePickerDialogVisible.value = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (uiState.hour != null && uiState.minute != null)
                        String.format(Locale.getDefault(),"Time: %02d:%02d", uiState.hour, uiState.minute)
                    else
                        "Pick Time"
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Type selection
        var typeExpanded by remember { mutableStateOf(false) }
        Box {
            OutlinedButton(onClick = { typeExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(
                    when (uiState.type) {
                        is com.raven.odyssey.domain.model.HabitType.Binary -> "Binary"
                        is com.raven.odyssey.domain.model.HabitType.Measurable -> "Measurable"
                    }
                )
            }
            DropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                DropdownMenuItem(text = { Text("Binary") }, onClick = {
                    viewModel.updateUiState(
                        type = com.raven.odyssey.domain.model.HabitType.Binary,
                        target = null,
                        unit = null
                    )
                    typeExpanded = false
                })
                DropdownMenuItem(text = { Text("Measurable") }, onClick = {
                    viewModel.updateUiState(
                        type = com.raven.odyssey.domain.model.HabitType.Measurable(
                            uiState.target ?: 1,
                            uiState.unit ?: ""
                        )
                    )
                    typeExpanded = false
                })
            }
        }
        if (uiState.type is com.raven.odyssey.domain.model.HabitType.Measurable) {
            OutlinedTextField(
                value = uiState.target?.toString() ?: "",
                onValueChange = { value ->
                    val intVal = value.toIntOrNull() ?: 1
                    viewModel.updateUiState(
                        target = intVal,
                        type = com.raven.odyssey.domain.model.HabitType.Measurable(
                            intVal,
                            uiState.unit ?: ""
                        )
                    )
                },
                label = { Text("Target") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.unit ?: "",
                onValueChange = { value ->
                    viewModel.updateUiState(
                        unit = value,
                        type = com.raven.odyssey.domain.model.HabitType.Measurable(
                            uiState.target ?: 1, value
                        )
                    )
                },
                label = { Text("Unit") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                viewModel.addHabit {
                    onHabitAdded?.invoke()
                }
            },
            enabled = uiState.name.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Habit")
        }
    }
}
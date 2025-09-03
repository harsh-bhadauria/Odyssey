package com.raven.odyssey.ui.screens.todo.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoAddScreen(
    onTodoAdded: () -> Unit,
    viewModel: TodoAddViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val datePickerDialogVisible = remember { mutableStateOf(false) }
    val timePickerDialogVisible = remember { mutableStateOf(false) }

    fun getSelectedDateMillis(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, uiState.selectedYear)
            set(Calendar.MONTH, uiState.selectedMonth)
            set(Calendar.DAY_OF_MONTH, uiState.selectedDay)
        }.timeInMillis
    }

    val timePickerState = remember {
        TimePickerState(uiState.selectedHour, uiState.selectedMinute, false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = uiState.title,
            onValueChange = { viewModel.updateUiState(title = it) },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isSaving
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.description,
            onValueChange = { viewModel.updateUiState(description = it) },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isSaving
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Date Picker Dialog
        if (datePickerDialogVisible.value) {
            val datePickerState =
                rememberDatePickerState(initialSelectedDateMillis = getSelectedDateMillis())
            DatePickerDialog(
                onDismissRequest = { datePickerDialogVisible.value = false },
                confirmButton = {
                    TextButton(onClick = {
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            val cal = Calendar.getInstance().apply { timeInMillis = millis }
                            viewModel.updateUiState(
                                selectedYear = cal.get(Calendar.YEAR),
                                selectedMonth = cal.get(Calendar.MONTH),
                                selectedDay = cal.get(Calendar.DAY_OF_MONTH)
                            )
                        }
                        datePickerDialogVisible.value = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { datePickerDialogVisible.value = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState,
                    showModeToggle = false,
                )
            }
        }

        // Time Picker Dialog
        if (timePickerDialogVisible.value) {
            AlertDialog(
                onDismissRequest = { timePickerDialogVisible.value = false },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.updateUiState(
                            selectedHour = timePickerState.hour,
                            selectedMinute = timePickerState.minute
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
            onClick = {
                timePickerDialogVisible.value = false
                datePickerDialogVisible.value = true
            },
            enabled = !uiState.isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                String.format(
                    Locale.getDefault(),
                    "Date: %04d-%02d-%02d",
                    uiState.selectedYear,
                    uiState.selectedMonth + 1,
                    uiState.selectedDay
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                datePickerDialogVisible.value = false
                timePickerDialogVisible.value = true
            },
            enabled = !uiState.isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                String.format(
                    Locale.getDefault(),
                    "Time: %02d:%02d",
                    uiState.selectedHour,
                    uiState.selectedMinute
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.addTodo(onTodoAdded) },
            enabled = !uiState.isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Todo")
        }

        if (uiState.error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(uiState.error ?: "", color = MaterialTheme.colorScheme.error)
        }
    }
}

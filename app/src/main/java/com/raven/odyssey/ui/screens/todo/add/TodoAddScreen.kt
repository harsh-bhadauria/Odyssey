package com.raven.odyssey.ui.screens.todo.add

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.util.Calendar

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoAddScreen(
    onTodoAdded: () -> Unit,
    viewModel: TodoAddViewModel = hiltViewModel()
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
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = uiState.title,
            onValueChange = { it -> viewModel.updateUiState(title = it) },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isSaving
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.description,
            onValueChange = { it -> viewModel.updateUiState(description = it) },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isSaving
        )

        Spacer(modifier = Modifier.height(8.dp))

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
            enabled = !uiState.isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (uiState.hour != null && uiState.minute != null)
                    String.format("Time: %02d:%02d", uiState.hour, uiState.minute)
                else
                    "Pick Time"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.error != null) {

            Text(text = uiState.error ?: "Unknown error", color = MaterialTheme.colorScheme.error)

            Spacer(modifier = Modifier.height(8.dp))

        }

        Button(
            onClick = { viewModel.addTodo(onTodoAdded) },
            enabled = uiState.title.isNotBlank() && !uiState.isSaving
        ) {
            Text(if (uiState.isSaving) "Saving..." else "Add Todo")
        }

    }
}

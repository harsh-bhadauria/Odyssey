package com.raven.odyssey.ui.screens.habit.debug

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.raven.odyssey.domain.model.Habit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HabitDebugScreen(
    viewModel: HabitDebugViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    when {
        uiState.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        uiState.error != null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = uiState.error ?: "Unknown error",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        else -> {
            LazyColumn(Modifier.fillMaxSize()) {
                items(uiState.habits) { habit ->
                    HabitDebugCard(habit, onLongPress = { viewModel.deleteHabit(habit) })
                }
            }
        }
    }
}

@Composable
fun HabitDebugCard(habit: Habit, onLongPress: (Habit) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = {},
                onLongClick = { onLongPress(habit) }
            )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(0.8f)
            ) {
                Text(
                    text = habit.name,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                if (!habit.description.isNullOrEmpty()) {
                    Text(
                        text = habit.description,
                        fontSize = 12.sp
                    )
                }
                Text(
                    text = "${habit.type}",
                    fontSize = 12.sp
                )
                Text(
                    text = "${habit.frequency}",
                    fontSize = 12.sp
                )
            }
            Text(
                text = SimpleDateFormat("MMM-dd HH:mm", Locale.getDefault()).format(Date(habit.nextDue)),
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

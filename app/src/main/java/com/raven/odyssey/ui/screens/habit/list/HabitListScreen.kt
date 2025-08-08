package com.raven.odyssey.ui.screens.habit.list

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.raven.odyssey.domain.model.Habit
import com.raven.odyssey.domain.model.HabitFrequency
import com.raven.odyssey.domain.model.HabitType
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun HabitListScreen(
    onHabitDebugClicked: () -> Unit,
    viewModel: HabitListViewModel = viewModel()
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
            HabitListUI(
                uiState, onLongPress = { habit -> viewModel.completeHabit(habit) },
                onHabitDebugClicked = onHabitDebugClicked
            )
        }
    }
}

@Composable
fun HabitListUI(
    uiState: HabitListUiState,
    onLongPress: (Habit) -> Unit,
    onHabitDebugClicked: () -> Unit
) {
    Column {
        Button(onClick = onHabitDebugClicked) {
            Text("Habit Debug")
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(uiState.habits) { habit ->
                HabitCard(habit, onLongPress)
            }
        }
    }

}

@Composable
fun HabitCard(habit: Habit, onLongPress: (Habit) -> Unit) {
    when (habit.type) {
        is HabitType.Binary -> BinaryHabitCard(habit, onLongPress)
        is HabitType.Measurable -> MeasurableHabitCard(habit, onLongPress)
    }
}

@Composable
fun BinaryHabitCard(habit: Habit, onLongPress: (Habit) -> Unit) {
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
            }
            Text(
                text = SimpleDateFormat("HH:mm").format(Date(habit.nextDue)),
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
        }

    }
}

@Composable
fun MeasurableHabitCard(habit: Habit, onLongPress: (Habit) -> Unit) {
    val measurable = habit.type as HabitType.Measurable
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = {},
                onLongClick = { onLongPress(habit) }
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                text = "Target: ${measurable.target} ${measurable.unit}",
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Preview
@Composable
fun HabitCardPreview() {
    HabitCard(
        habit = Habit(
            id = 1,
            name = "Drink Water",
            description = "Stay hydrated throughout the day",
            isActive = true,
            frequency = HabitFrequency.Daily,
            type = HabitType.Binary,
            nextDue = System.currentTimeMillis() + 1000
        ),
        onLongPress = {}
    )
}
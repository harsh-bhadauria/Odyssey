package com.raven.odyssey.ui.screens.habit.list

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
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
import java.util.Locale

@Composable
fun HabitListScreenOld(
    onHabitDebugClicked: () -> Unit,
    viewModel: HabitListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
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

            uiState.habits.isEmpty() -> {
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Button(onClick = onHabitDebugClicked) {
                        Text("Habit Debug")
                    }
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "All done for today! \uD83C\uDF89",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                }
            }

            else -> {
                HabitListUI(
                    uiState, onLongPress = { habit -> viewModel.completeHabit(habit) },
                    onHabitDebugClicked = onHabitDebugClicked,
                    onIncrement = { viewModel.incrementProgress(it) },
                    onDecrement = { viewModel.decrementProgress(it) }
                )
            }
        }
    }
}


@Composable
fun HabitListUI(
    uiState: HabitListUiState,
    onLongPress: (Habit) -> Unit,
    onHabitDebugClicked: () -> Unit,
    onIncrement: (Habit) -> Unit = {},
    onDecrement: (Habit) -> Unit = {}
) {
    Column {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(uiState.habits) { habit ->
                HabitCard(habit, onLongPress, onIncrement, onDecrement)
            }
            item {
                Button(onClick = onHabitDebugClicked) {
                    Text("Habit Debug")
                }
            }
        }
    }
}

@Composable
fun HabitCard(
    habit: Habit,
    onLongPress: (Habit) -> Unit,
    onIncrement: (Habit) -> Unit,
    onDecrement: (Habit) -> Unit
) {
    when (habit.type) {
        is HabitType.Binary -> BinaryHabitCard(habit, onLongPress)
        is HabitType.Measurable -> MeasurableHabitCard(habit, onLongPress, onIncrement, onDecrement)
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
                Text(
                    text = SimpleDateFormat("MMM dd, yyyy, HH:mm", Locale.getDefault()).format(
                        Date(
                            habit.nextDue
                        )
                    )
                )
            }
            Text(
                text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(habit.nextDue)),
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun MeasurableHabitCard(
    habit: Habit,
    onLongPress: (Habit) -> Unit,
    onIncrement: (Habit) -> Unit,
    onDecrement: (Habit) -> Unit
) {
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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { onDecrement(habit) }) {
                    Text("-")
                }
                Text(
                    text = "${measurable.progress} / ${measurable.target} ${measurable.unit}",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Button(onClick = { onIncrement(habit) }) {
                    Text("+")
                }
            }
            val progress = measurable.progress.toFloat() / measurable.target.toFloat()
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                color = ProgressIndicatorDefaults.linearColor,
                trackColor = ProgressIndicatorDefaults.linearTrackColor,
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HabitCardPreview() {
    HabitCard(
        habit = Habit(
            id = 1,
            name = "Drink Water",
            description = "Stay hydrated throughout the day",
            isActive = true,
            frequency = HabitFrequency.Daily,
            type = HabitType.Measurable(
                target = 8,
                unit = "cups",
                progress = 5
            ),
            nextDue = System.currentTimeMillis() + 1000
        ),
        onLongPress = {},
        onIncrement = {},
        onDecrement = {}
    )
}
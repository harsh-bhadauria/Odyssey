package com.raven.odyssey.ui.screens.habit.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.raven.odyssey.domain.model.Habit
import com.raven.odyssey.domain.model.HabitType
import com.raven.odyssey.ui.theme.AppColors
import com.raven.odyssey.ui.theme.Typo


@Composable
fun HabitListScreen(
    viewModel: HabitListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        HabitHeader()

        Spacer(modifier = Modifier.height(24.dp))

        // "This Week" Section
        Text(
            text = "This Week",
            style = Typo.Title,
            color = AppColors.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        // List of Weekly Habits
        uiState.habits.forEach { habit ->
            HabitCard(
                habit = habit,
                onIncrement = { viewModel.incrementProgress(habit) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // "Today" Section
        Text(
            text = "Today",
            style = Typo.Title,
            color = AppColors.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        // List of Daily Habits
        uiState.habits.forEach { habit ->
            HabitCard(
                habit = habit,
                onIncrement = { viewModel.incrementProgress(habit) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(100.dp)) // Space for FAB
    }

}

@Composable
fun HabitHeader() {
    Column(
        modifier = Modifier.padding(horizontal = 12.dp)
    ) {
        Text(
            text = "Habit Tracker",
            style = Typo.Headline
        )
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Another day, another day, another day",
            style = Typo.Subtitle
        )
    }
}

@Composable
fun HabitCard(
    habit: Habit,
    onIncrement: () -> Unit
) {
    val measurable = habit.type as HabitType.Measurable
    val themeColor = habit.domain.color

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min) // Ensures the button matches the card height
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {

            // Title and Counter Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = habit.name,
                    style = Typo.Body,
                    color = AppColors.Black
                )

                Text(
                    text = "${measurable.progress} / ${measurable.target} ${measurable.unit}",
                    style = Typo.Number,
                    color = AppColors.Black
                )
            }

            Spacer(modifier = Modifier.height(12.dp))


            // Custom Progress Bar
            LinearProgressIndicator(
                progress = {
                    (measurable.progress.toFloat() / measurable.target.toFloat()).coerceIn(
                        0f,
                        1f
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = themeColor,
                trackColor = AppColors.Background
            )
        }

        Box(
            modifier = Modifier
                .width(60.dp)
                .fillMaxHeight()
                .background(themeColor)
                .clickable(onClick = onIncrement),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increment",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
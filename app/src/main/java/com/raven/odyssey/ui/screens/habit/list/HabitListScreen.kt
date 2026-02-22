package com.raven.odyssey.ui.screens.habit.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.raven.odyssey.domain.model.Habit
import com.raven.odyssey.domain.model.HabitFrequency
import com.raven.odyssey.domain.model.HabitType
import com.raven.odyssey.ui.theme.Typo


@Composable
fun HabitListScreen(
    viewModel: HabitListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val weeklyHabits = uiState.habits.filter { it.frequency is HabitFrequency.Weekly }
    val todayHabits = uiState.habits.filter { it.frequency is HabitFrequency.Daily || it.frequency is HabitFrequency.Custom }
    val completedHabits = uiState.completedHabits

    val weeklyBinaryCount = weeklyHabits.count { it.type is HabitType.Binary }
    val todayBinaryCount = todayHabits.count { it.type is HabitType.Binary }

    val weeklyLastBinaryIndex = weeklyHabits.indexOfLast { it.type is HabitType.Binary }
    val todayLastBinaryIndex = todayHabits.indexOfLast { it.type is HabitType.Binary }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 100.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalItemSpacing = 12.dp
        ) {
            item(span = StaggeredGridItemSpan.FullLine) {
                HabitHeader()
            }

            if (weeklyHabits.isNotEmpty()) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
                item(span = StaggeredGridItemSpan.FullLine) {
                    Text(
                        text = "This Week",
                        style = Typo.Title,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                itemsIndexed(
                    items = weeklyHabits,
                    key = { _, habit -> habit.id },
                    span = { index, habit ->
                        when (habit.type) {
                            is HabitType.Measurable -> StaggeredGridItemSpan.FullLine
                            is HabitType.Binary -> {
                                val isLastBinary = index == weeklyLastBinaryIndex
                                if (weeklyBinaryCount % 2 == 1 && isLastBinary) {
                                    StaggeredGridItemSpan.FullLine
                                } else {
                                    StaggeredGridItemSpan.SingleLane
                                }
                            }
                        }
                    }
                ) { _, habit ->
                    HabitListItemCard(
                        habit = habit,
                        onAction = {
                            when (habit.type) {
                                is HabitType.Measurable -> viewModel.incrementProgress(habit)
                                is HabitType.Binary -> viewModel.completeHabit(habit)
                            }
                        }
                    )
                }

                item(span = StaggeredGridItemSpan.FullLine) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            if (todayHabits.isNotEmpty()) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Text(
                        text = "Today",
                        style = Typo.Title,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }

                itemsIndexed(
                    items = todayHabits,
                    key = { _, habit -> habit.id },
                    span = { index, habit ->
                        when (habit.type) {
                            is HabitType.Measurable -> StaggeredGridItemSpan.FullLine
                            is HabitType.Binary -> {
                                val isLastBinary = index == todayLastBinaryIndex
                                if (todayBinaryCount % 2 == 1 && isLastBinary) {
                                    StaggeredGridItemSpan.FullLine
                                } else {
                                    StaggeredGridItemSpan.SingleLane
                                }
                            }
                        }
                    }
                ) { _, habit ->
                    HabitListItemCard(
                        habit = habit,
                        onAction = {
                            when (habit.type) {
                                is HabitType.Measurable -> viewModel.incrementProgress(habit)
                                is HabitType.Binary -> viewModel.completeHabit(habit)
                            }
                        }
                    )
                }
            }

            if (completedHabits.isNotEmpty()) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Completed",
                        style = Typo.Title,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                items(
                    items = completedHabits,
                    key = { it.id },
                    span = { StaggeredGridItemSpan.FullLine }
                ) { habit ->
                    // Render explicitly as completed state (e.g., lower opacity or checkmark)
                    // For measurable habits, we want to show full progress visually
                    val displayHabit = if (habit.type is HabitType.Measurable) {
                         habit.copy(type = habit.type.copy(progress = habit.type.target))
                    } else {
                        habit
                    }

                    Box(modifier = Modifier.alpha(0.6f)) {
                         HabitListItemCard(
                            habit = displayHabit,
                            onAction = { } // No-op for now, or implement undo
                        )
                    }
                }
            }
        }

        // Truly centered empty-state when there are no habits at all.
        if (weeklyHabits.isEmpty() && todayHabits.isEmpty() && completedHabits.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No habits due right now",
                        style = Typo.Title,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You're all caught up!",
                        style = Typo.Subtitle,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

@Composable
private fun HabitListItemCard(
    habit: Habit,
    onAction: () -> Unit
) {
    when (habit.type) {
        is HabitType.Measurable -> HabitCard(habit = habit, onIncrement = onAction)
        is HabitType.Binary -> BinaryHabitGridCard(
            habit = habit,
            onToggleComplete = onAction
        )
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
            style = Typo.Subtitle,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
        )

        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun BinaryHabitGridCard(
    habit: Habit,
    onToggleComplete: () -> Unit
) {
    val themeColor = habit.domain.color

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onToggleComplete),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = habit.name,
                style = Typo.Body,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(themeColor.copy(alpha = 1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Complete",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun HabitCard(
    habit: Habit,
    onIncrement: () -> Unit
) {
    val themeColor = habit.domain.color

    val progressText = when (val type = habit.type) {
        is HabitType.Measurable -> "${type.progress} / ${type.target} ${type.unit}"
        is HabitType.Binary -> ""
    }

    val progressFraction = when (val type = habit.type) {
        is HabitType.Measurable ->
            if (type.target <= 0) 0f else (type.progress.toFloat() / type.target.toFloat()).coerceIn(0f, 1f)

        is HabitType.Binary -> 0f
    }

    val actionIcon = when (habit.type) {
        is HabitType.Measurable -> Icons.Default.Add
        is HabitType.Binary -> Icons.Default.Check
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
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
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = progressText,
                    style = Typo.Number,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Custom Progress Bar
            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = themeColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
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
                imageVector = actionIcon,
                contentDescription = when (habit.type) {
                    is HabitType.Measurable -> "Increment"
                    is HabitType.Binary -> "Complete"
                },
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
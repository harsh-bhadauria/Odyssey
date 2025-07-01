package com.raven.odyssey.ui.screens.habit.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.raven.odyssey.data.entity.HabitEntity

@Composable
fun HabitListScreen(
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
            HabitListUI(uiState)
        }
    }
}

@Composable
fun HabitListUI(uiState: HabitListUiState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(uiState.habits) { habit ->
            HabitCard(habit)
        }
    }
}

@Composable
fun HabitCard(habit: HabitEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
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
        }
    }
}
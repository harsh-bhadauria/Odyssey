package com.raven.odyssey.ui.navigation

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    current: Screen,
) {
    val title = when (current) {
        is Screen.TodoList -> "Odyssey"
        is Screen.TodoAdd -> "Add Todo"
        is Screen.HabitList -> "Odyssey"
        is Screen.HabitAdd -> "Add Habit"
    }
    CenterAlignedTopAppBar(
        title = {
            Text(title)
        }
    )
}

@Preview
@Composable
fun AppTopBarPreview() {
    AppTopBar(current = Screen.TodoList)
}

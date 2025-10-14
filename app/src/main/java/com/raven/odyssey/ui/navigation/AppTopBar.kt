package com.raven.odyssey.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.raven.odyssey.ui.theme.pixelSansStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    current: Screen,
    onOpenDrawer: () -> Unit
) {
    val title = when (current) {
        is Screen.TodoList -> "Odyssey"
        is Screen.TodoAdd -> "Add Todo"
        is Screen.HabitList -> "Odyssey"
        is Screen.HabitAdd -> "Add Habit"
        else -> "Odyssey"
    }
    CenterAlignedTopAppBar(
        title = {
            Text(title, style = pixelSansStyle)
        },
        navigationIcon = {
            IconButton(onClick = onOpenDrawer) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = null
                )
            }
        },
    )
}

@Preview
@Composable
fun AppTopBarPreview() {
    AppTopBar(current = Screen.TodoList, onOpenDrawer = {})
}

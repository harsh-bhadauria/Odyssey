package com.raven.odyssey.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: @Composable () -> Unit
)

val bottomNavItems = listOf(
    BottomNavItem(
        screen = Screen.TodoList,
        label = "Todos",
        icon = { Icon(Icons.Filled.Checklist,null) }
    ),
    BottomNavItem(
        screen = Screen.HabitList,
        label = "Habits",
        icon = { Icon(Icons.Filled.Bolt,null) }
    )
)

@Composable
fun BottomNavigationBar(current: Screen, onSelect: (Screen) -> Unit) {
    NavigationBar {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = current == item.screen,
                onClick = { onSelect(item.screen) },
                icon = item.icon,
                label = { Text(item.label) },
            )
        }
    }
}

@Preview
@Composable
fun BottomNavigationBarPreview() {
    var current: Screen by remember { mutableStateOf(Screen.TodoList) }
    BottomNavigationBar(
        current = current,
        onSelect = { screen -> current = screen }
    )
}

package com.raven.odyssey.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.raven.odyssey.ui.screens.habit.add.HabitAddScreen
import com.raven.odyssey.ui.screens.habit.list.HabitListScreen
import com.raven.odyssey.ui.screens.todo.add.TodoAddScreen
import com.raven.odyssey.ui.screens.todo.list.TodoListScreen

@Composable
fun AppNavDisplay() {

    val backstack = remember { mutableStateListOf<Screen>(Screen.TodoList) }
    val current = backstack.lastOrNull() ?: Screen.TodoList

    val showBottomBar = current is Screen.TodoList || current is Screen.HabitList
    val showFab = current is Screen.TodoList || current is Screen.HabitList

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(current = current) { selected ->
                    if (current != selected) {
                        backstack.removeLastOrNull()
                        backstack.add(selected)
                    }

                }
            }
        },
        floatingActionButton = {
            if (showFab) {
                FloatingActionButton(
                    onClick = {
                        when (current) {
                            is Screen.TodoList -> backstack.add(Screen.TodoAdd)
                            is Screen.HabitList -> backstack.add(Screen.HabitAdd)
                            else -> {}
                        }
                    },
                ) { Icon(Icons.Default.Add, null) }
            }
        },
        topBar = {
            AppTopBar(current = current)
        }
    ) {

        NavDisplay(
            backStack = backstack,
            onBack = { backstack.removeLastOrNull() },
            modifier = Modifier.padding(it),
            entryProvider = entryProvider {
                entry<Screen.TodoList> { TodoListScreen() }
                entry<Screen.HabitList> { HabitListScreen() }
                entry<Screen.TodoAdd> {
                    TodoAddScreen(
                        onTodoAdded = { backstack.removeLastOrNull() }
                    )
                }
                entry<Screen.HabitAdd> {
                   HabitAddScreen(
                       onHabitAdded = { backstack.removeLastOrNull() }
                   )
                }
            }
        )
    }
}
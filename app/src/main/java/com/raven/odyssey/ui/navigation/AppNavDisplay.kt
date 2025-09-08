package com.raven.odyssey.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.raven.odyssey.ui.screens.habit.add.HabitAddScreen
import com.raven.odyssey.ui.screens.habit.debug.HabitDebugScreen
import com.raven.odyssey.ui.screens.habit.list.HabitListScreen
import com.raven.odyssey.ui.screens.todo.add.TodoAddScreen
import com.raven.odyssey.ui.screens.todo.list.TodoListScreen
import com.raven.odyssey.ui.screens.todo.list.TodoListViewModel

@Composable
fun AppNavDisplay() {

    val backstack = remember { mutableStateListOf<Screen>(Screen.TodoList) }
    val current = backstack.lastOrNull() ?: Screen.TodoList

    val showBottomBar = current is Screen.TodoList || current is Screen.HabitList
    val showFab = current is Screen.TodoList || current is Screen.HabitList

    // Obtain the TodoListViewModel to access selectedDate
    val todoListViewModel: TodoListViewModel = hiltViewModel()
    val uiState by todoListViewModel.uiState.collectAsState()

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
            if (current is Screen.TodoList) {
                FloatingActionButton(
                    onClick = {
                        val initialDateMillis = if (uiState.selectedDate != 0L) {
                            uiState.selectedDate
                        } else {
                            null
                        }
                        backstack.add(Screen.TodoAdd(initialDateMillis))
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
                entry<Screen.TodoList> {
                    TodoListScreen()
                }
                entry<Screen.HabitList> {
                    HabitListScreen(
                        onHabitDebugClicked = { backstack.add(Screen.HabitDebug) }
                    )
                }
                entry<Screen.TodoAdd> { screen ->
                    val selectedDateMillis = screen.selectedDateMillis
                    TodoAddScreen(
                        onTodoAdded = { backstack.removeLastOrNull() },
                        initialDateMillis = selectedDateMillis
                    )
                }
                entry<Screen.HabitAdd> {
                    HabitAddScreen(
                        onHabitAdded = { backstack.removeLastOrNull() }
                    )
                }

                entry<Screen.HabitDebug> { HabitDebugScreen() }
            }
        )
    }
}
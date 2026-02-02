package com.raven.odyssey.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.raven.odyssey.ui.screens.habit.add.HabitAddMenu
import com.raven.odyssey.ui.screens.habit.debug.HabitDebugScreen
import com.raven.odyssey.ui.screens.habit.list.HabitListScreen
import com.raven.odyssey.ui.screens.todo.add.TodoAddMenu
import com.raven.odyssey.ui.screens.todo.list.TodoListScreen
import com.raven.odyssey.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavDisplay() {

    val backstack = remember { mutableStateListOf<Screen>(Screen.TodoList) }

    val top = backstack.lastOrNull() ?: Screen.TodoList

    val isOverlayTop = top is Screen.TodoAdd || top is Screen.HabitAdd

    val current = if (isOverlayTop) {
        backstack.getOrNull(backstack.lastIndex - 1) ?: Screen.TodoList
    } else {
        top
    }

    val showBottomBar = current is Screen.TodoList || current is Screen.HabitList

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val contentBackstack = remember(backstack.toList()) {
        backstack.filterNot { it is Screen.TodoAdd || it is Screen.HabitAdd }
    }

    if (isOverlayTop) {
        BackHandler {
            backstack.removeLastOrNull()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                if (current is Screen.TodoList || current is Screen.HabitList) {
                    FloatingActionButton(
                        onClick = {
                            when (current) {
                                is Screen.TodoList -> backstack.add(Screen.TodoAdd)
                                is Screen.HabitList -> backstack.add(Screen.HabitAdd)
                                else -> Unit
                            }
                        },
                        containerColor = AppColors.Teal,
                        shape = CircleShape,
                        modifier = Modifier.size(64.dp)
                    ) { Icon(Icons.Default.Add, null, tint = AppColors.White) }
                }
            }
        ) {

            NavDisplay(
                backStack = contentBackstack,
                onBack = {
                    // Pop from the real backstack (which may include overlays).
                    backstack.removeLastOrNull()
                },
                modifier = Modifier.padding(it),
                entryProvider = entryProvider {
                    entry<Screen.TodoList> {
                        TodoListScreen()
                    }
                    entry<Screen.HabitList> {
                        HabitListScreen()
                    }
                    entry<Screen.HabitDebug> { HabitDebugScreen() }
                }
            )
        }

        // Overlays
        when (top) {
            is Screen.TodoAdd -> {
                ModalBottomSheet(
                    sheetState = sheetState,
                    onDismissRequest = { backstack.removeLastOrNull() },
                    dragHandle = {}
                ) {
                    TodoAddMenu(
                        onDismiss = { backstack.removeLastOrNull() }
                    )
                }
            }

            is Screen.HabitAdd -> {
                ModalBottomSheet(
                    sheetState = sheetState,
                    onDismissRequest = { backstack.removeLastOrNull() },
                    dragHandle = {}
                ) {
                    val habitAddViewModel: com.raven.odyssey.ui.screens.habit.add.HabitAddViewModel =
                        androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel()

                    HabitAddMenu(
                        onDismiss = {
                            habitAddViewModel.resetUiState()
                            backstack.removeLastOrNull()
                        },
                        viewModel = habitAddViewModel,
                    )

                    // Also reset when the sheet is dismissed by gesture.
                    androidx.compose.runtime.DisposableEffect(Unit) {
                        onDispose {
                            habitAddViewModel.resetUiState()
                        }
                    }
                }
            }

            else -> Unit
        }
    }
}
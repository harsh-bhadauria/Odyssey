package com.raven.odyssey.ui.navigation

sealed class Screen {
    object HabitList : Screen()
    object HabitAdd : Screen()
    object TodoList : Screen()
    data class TodoAdd(val selectedDateMillis: Long? = null) : Screen()
    object HabitDebug : Screen()

    // Overlay route used to show the task add menu as a bottom sheet.
    object TaskAddSheet : Screen()
}
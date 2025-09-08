package com.raven.odyssey.ui.navigation

sealed class Screen {
    object HabitList : Screen()
    object HabitAdd : Screen()
    object TodoList : Screen()
    data class TodoAdd(val selectedDateMillis: Long? = null) : Screen()
    object HabitDebug : Screen()
}
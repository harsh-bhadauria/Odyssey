package com.raven.odyssey.ui.navigation

sealed class Screen {
    object HabitList : Screen()
    object HabitAdd : Screen()
    object TodoAdd : Screen()
    object TodoList : Screen()
    object HabitDebug : Screen()
}
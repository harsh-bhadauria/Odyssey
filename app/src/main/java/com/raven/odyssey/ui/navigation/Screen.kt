package com.raven.odyssey.ui.navigation

sealed class Screen {
    object HabitList : Screen()
    object HabitAdd : Screen()
    object TodoList : Screen()
    object TodoAdd : Screen()
}
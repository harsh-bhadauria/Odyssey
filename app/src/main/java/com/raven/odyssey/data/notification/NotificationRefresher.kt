package com.raven.odyssey.data.notification

import android.util.Log
import com.raven.odyssey.data.dao.HabitDao
import com.raven.odyssey.data.dao.TodoDao
import com.raven.odyssey.data.entity.toDomain
import com.raven.odyssey.domain.model.Habit
import com.raven.odyssey.domain.notification.HabitNotificationScheduler
import com.raven.odyssey.domain.notification.NotificationScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class NotificationRefresher @Inject constructor(
    private val habitDao: HabitDao,
    private val todoDao: TodoDao,
    private val habitNotificationScheduler: HabitNotificationScheduler,
    private val todoNotificationScheduler: NotificationScheduler
) {
    fun refreshAllNotifications() {
        CoroutineScope(Dispatchers.IO).launch {
            val now = System.currentTimeMillis()
            val dueHabits = habitDao.getDueHabits(now)
            Log.d("NotificationRefresher", "Due habits count: "+dueHabits.size)
            for (habitEntity in dueHabits) {
                val habit = Habit(
                    id = habitEntity.id,
                    name = habitEntity.name,
                    description = habitEntity.description,
                    nextDue = habitEntity.nextDue,
                )
                habitNotificationScheduler.scheduleNotification(habit)
            }

            val incompleteTodos = todoDao.getIncompleteTodos()
            Log.d("NotificationRefresher", "Incomplete todos count: ${incompleteTodos.size}")
            for (todoEntity in incompleteTodos) {
                val todo = todoEntity.toDomain()
                todoNotificationScheduler.scheduleNotification(todo)
            }
            Log.d(
                "NotificationRefresher",
                "Refreshed notifications for ${dueHabits.size} habits and ${incompleteTodos.size} todos."
            )
        }
    }
}

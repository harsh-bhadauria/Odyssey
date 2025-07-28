package com.raven.odyssey.domain.notification

import com.raven.odyssey.domain.model.Habit

interface HabitNotificationScheduler {
    fun scheduleNotification(habit: Habit)
    fun cancelNotification(habitId: Long)
}


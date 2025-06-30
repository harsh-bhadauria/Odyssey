package com.raven.odyssey.domain.notification

import com.raven.odyssey.domain.model.Todo

interface NotificationScheduler {
    fun scheduleNotification(todo: Todo)
    fun cancelNotification(todoId: Long)
}
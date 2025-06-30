package com.raven.odyssey.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.provider.Settings

object NotificationChannelUtil {
    const val CHANNEL_ID = "todo_reminder_channel"
    private const val CHANNEL_NAME = "Todo Reminders"
    private const val CHANNEL_DESC = "Notifications for scheduled todos"

    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = CHANNEL_DESC
            enableLights(true)
            enableVibration(true)
            setSound(Settings.System.DEFAULT_NOTIFICATION_URI, null)
        }
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}


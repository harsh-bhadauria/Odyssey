package com.raven.odyssey.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.provider.Settings

object NotificationChannelUtil {
    /**
     * Legacy channel id used by older versions of the app.
     * Kept to preserve users' channel settings after upgrading.
     */
    const val CHANNEL_ID = "todo_reminder_channel"

    const val TODO_CHANNEL_ID = CHANNEL_ID
    const val HABIT_CHANNEL_ID = "habit_reminder_channel"

    private const val TODO_CHANNEL_NAME = "Todo Reminders"
    private const val TODO_CHANNEL_DESC = "Notifications for scheduled todos"

    private const val HABIT_CHANNEL_NAME = "Habit Reminders"
    private const val HABIT_CHANNEL_DESC = "Notifications for scheduled habits"

    fun createNotificationChannels(context: Context) {

        val todoChannel = NotificationChannel(
            TODO_CHANNEL_ID,
            TODO_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = TODO_CHANNEL_DESC
            enableLights(true)
            enableVibration(true)
            setSound(Settings.System.DEFAULT_NOTIFICATION_URI, null)
        }

        val habitChannel = NotificationChannel(
            HABIT_CHANNEL_ID,
            HABIT_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = HABIT_CHANNEL_DESC
            enableLights(true)
            enableVibration(true)
            setSound(Settings.System.DEFAULT_NOTIFICATION_URI, null)
        }

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(todoChannel)
        manager.createNotificationChannel(habitChannel)
    }

    @Deprecated(
        message = "Use createNotificationChannels(Context) to create both Todo + Habit channels",
        replaceWith = ReplaceWith("createNotificationChannels(context)")
    )
    fun createNotificationChannel(context: Context) = createNotificationChannels(context)
}

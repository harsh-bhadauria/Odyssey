package com.raven.odyssey.data.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.raven.odyssey.MainActivity
import com.raven.odyssey.R

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val notificationId = intent.getLongExtra("notification_id", 0L)

        val notification = buildNotification(context, intent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                NotificationManagerCompat.from(context).notify(notificationId.toInt(), notification)
            }
        } else {
            // Pre-33 doesn't require POST_NOTIFICATIONS runtime permission.
            NotificationManagerCompat.from(context).notify(notificationId.toInt(), notification)
        }

    }

    private fun buildNotification(
        context: Context,
        intent: Intent
    ): Notification {

        val notificationId = intent.getLongExtra("notification_id", 0L)
        val title = intent.getStringExtra("title") ?: "Todo Reminder"
        val description = intent.getStringExtra("description") ?: "You have a scheduled todo."

        val channelId = when (intent.getStringExtra(EXTRA_NOTIFICATION_TYPE)) {
            TYPE_HABIT -> NotificationChannelUtil.HABIT_CHANNEL_ID
            TYPE_TODO -> NotificationChannelUtil.TODO_CHANNEL_ID
            else -> NotificationChannelUtil.TODO_CHANNEL_ID // backward compat for older scheduled alarms
        }

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("notification_id", notificationId)
        }

        val tapPendingIntent = PendingIntent.getActivity(
            context,
            notificationId.toInt(),
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val deleteIntent = Intent(context, NotificationReceiver::class.java).apply {
            action = "ACTION_DELETE_NOTIFICATION"
            putExtra("notification_id", notificationId)
            putExtra("title", title)
            putExtra("description", description)
            putExtra(EXTRA_NOTIFICATION_TYPE, intent.getStringExtra(EXTRA_NOTIFICATION_TYPE))
        }

        val deletePendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId.toInt(),
            deleteIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.raven_notif)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .setAutoCancel(false)
            .setContentIntent(tapPendingIntent)
            .setDeleteIntent(deletePendingIntent)
            .setOngoing(true)
            .build()
    }

    companion object {
        const val EXTRA_NOTIFICATION_TYPE = "notification_type"
        const val TYPE_TODO = "todo"
        const val TYPE_HABIT = "habit"

        /**
         * Keep todo/habit alarms and notifications from colliding.
         * Request codes & notification ids only need to be stable per-item per-type.
         */
        private const val TODO_OFFSET = 1_000_000
        private const val HABIT_OFFSET = 2_000_000

        fun todoRequestCode(todoId: Long): Int = (TODO_OFFSET + todoId).toInt()
        fun habitRequestCode(habitId: Long): Int = (HABIT_OFFSET + habitId).toInt()

        fun todoNotificationId(todoId: Long): Long = TODO_OFFSET + todoId
        fun habitNotificationId(habitId: Long): Long = HABIT_OFFSET + habitId
    }
}
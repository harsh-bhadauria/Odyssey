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
        }

    }

    private fun buildNotification(
        context: Context,
        intent: Intent
    ): Notification {

        val notificationId = intent.getLongExtra("notification_id", 0L)
        val title = intent.getStringExtra("title") ?: "Todo Reminder"
        val description = intent.getStringExtra("description") ?: "You have a scheduled todo."

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
        }

        val deletePendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId.toInt(),
            deleteIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        return NotificationCompat.Builder(context, NotificationChannelUtil.CHANNEL_ID)
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
}
package com.raven.odyssey.data.notification

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationManagerCompat
import com.raven.odyssey.domain.model.Todo
import com.raven.odyssey.domain.notification.NotificationScheduler
import java.util.Calendar

class NotificationSchedulerImpl(private val context: Context) : NotificationScheduler {
    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    override fun scheduleNotification(todo: Todo) {
        val hour = todo.hour ?: return
        val minute = todo.minute ?: return
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("notification_id", todo.id)
            putExtra("title", todo.title)
            putExtra("description", todo.description)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            todo.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    override fun cancelNotification(todoId: Long) {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            todoId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)

        NotificationManagerCompat.from(context).cancel(todoId.toInt())
    }

}

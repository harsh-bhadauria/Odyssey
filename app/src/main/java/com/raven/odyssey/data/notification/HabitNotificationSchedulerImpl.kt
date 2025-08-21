package com.raven.odyssey.data.notification

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationManagerCompat
import com.raven.odyssey.domain.model.Habit
import com.raven.odyssey.domain.notification.HabitNotificationScheduler
import java.util.Calendar

class HabitNotificationSchedulerImpl(
    private val context: Context
) : HabitNotificationScheduler {
    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    override fun scheduleNotification(habit: Habit) {
        val nextDue = habit.nextDue
        val calendar = Calendar.getInstance().apply {
            timeInMillis = nextDue
        }
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("notification_id", habit.id)
            putExtra("title", habit.name)
            putExtra("description", habit.description)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            habit.id.toInt(),
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

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    override fun cancelNotification(habitId: Long) {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            habitId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)

        NotificationManagerCompat.from(context).cancel(habitId.toInt())
    }
}

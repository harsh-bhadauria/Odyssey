package com.raven.odyssey

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.raven.odyssey.data.notification.HabitDueUpdater
import com.raven.odyssey.data.notification.NotificationChannelUtil
import com.raven.odyssey.data.notification.NotificationRefresher
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class OdysseyApp : Application() {

    @Inject
    lateinit var notificationRefresher: NotificationRefresher
    @Inject
    lateinit var habitDueUpdater: HabitDueUpdater

    override fun onCreate() {
        super.onCreate()
        NotificationChannelUtil.createNotificationChannel(this)
        ProcessLifecycleOwner.get().lifecycleScope.launch {
            habitDueUpdater.updateHabitsNextDueToFuture()
            notificationRefresher.refreshAllNotifications()
        }
    }


}
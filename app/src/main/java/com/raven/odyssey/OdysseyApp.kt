package com.raven.odyssey

import android.app.Application
import com.raven.odyssey.data.notification.NotificationChannelUtil
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class OdysseyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationChannelUtil.createNotificationChannel(this)
    }
}
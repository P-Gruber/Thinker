package org.thinker.thinker.infrastructure.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import org.thinker.thinker.R
import org.thinker.thinker.application.TaskManager
import org.thinker.thinker.infrastructure.AndroidTaskManager
import org.thinker.thinker.infrastructure.kextensions.ContextExtensions.getAppName
import org.thinker.thinker.infrastructure.osevents.AndroidEvents

class MainService : Service()
{
    private val systemEvents = AndroidEvents()
    private val androidTaskManager: TaskManager = AndroidTaskManager(systemEvents)
    override fun onBind(intent: Intent) = null

    override fun onCreate()
    {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int
    {
        startForeground(NOTIFICATION_ID, createNotification())
        return START_NOT_STICKY
    }

    private fun createNotification(): Notification
    {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel()
    {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "Tasks Notifications", // TODO: Localize it
            NotificationManager.IMPORTANCE_DEFAULT
        )

        val manager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(serviceChannel)
    }

    companion object
    {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "Main"
    }
}
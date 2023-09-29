package org.thinker.thinker.infrastructure.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import org.thinker.thinker.R
import org.thinker.thinker.application.TaskManager
import org.thinker.thinker.domain.Task
import org.thinker.thinker.infrastructure.AndroidTaskManager

class MainService : Service()
{
    private lateinit var taskManager: TaskManager
    private val binder = LocalBinder()

    inner class LocalBinder : Binder()
    {
        fun getService(): MainService = this@MainService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate()
    {
        super.onCreate()
        createNotificationChannel()
        taskManager = AndroidTaskManager(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int
    {
        startForeground(NOTIFICATION_ID, createNotification())
        return START_STICKY
    }

    override fun onDestroy()
    {
        super.onDestroy()
        taskManager.onDestroy()
    }

    fun addTask(task: Task)
    {
        taskManager.addTask(task)
    }

    fun removeTask(task: Task)
    {
        taskManager.removeTask(task)
    }

    fun updateTask(task: Task)
    {
        taskManager.updateTask(task)
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
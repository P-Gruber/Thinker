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
import org.thinker.thinker.domain.AITask
import org.thinker.thinker.domain.Task
import org.thinker.thinker.domain.dataretriever.DataSourceName
import org.thinker.thinker.domain.osevents.Event
import org.thinker.thinker.domain.restrictioncheker.RestrictionName
import org.thinker.thinker.infrastructure.core.AndroidTaskManager
import org.thinker.thinker.infrastructure.core.dataretrieving.AndroidDataRetriever
import org.thinker.thinker.infrastructure.core.localization.AndroidLocalizedStrings
import org.thinker.thinker.infrastructure.core.restrictions.AndroidRestrictionChecker
import org.thinker.thinker.infrastructure.core.shell.AndroidShell
import org.thinker.thinker.infrastructure.data.ChatGPTRepo
import org.thinker.thinker.infrastructure.data.remote.ChatGptApi

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
        taskManager = AndroidTaskManager(this)

        // TODO: Remove test
        val aiTask = AITask(
            AndroidShell(this),
            AndroidRestrictionChecker(this),
            AndroidDataRetriever(this),
            ChatGPTRepo(ChatGptApi()),
            AndroidLocalizedStrings(this),
            "Remind me my next task only 1 hour before. You will do it using the pattern \"Remember to <task name>\".",
            setOf(Event.Screen.TurnedOn()),
//            setOf(DataSourceName.FileContent("/storage/emulated/0/Documents/mobile/Domingos.md")),
            setOf(
                DataSourceName.DailyFileContent(
                    "/storage/emulated/0/Documents/mobile/daily-notes/",
                    "yyyy-MM-dd",
                    "md"
                ),
                DataSourceName.TimeOfDay()
            ),
            setOf(RestrictionName.BatteryLowerThan(20)),
            setOf("notifier", "do-nothing")
        )
        addTask(aiTask)
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
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setDefaults(0)
            .build()
    }

    private fun createNotificationChannel()
    {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.tasks_notifications),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            setSound(null, null)
            enableLights(false)
            enableVibration(false)
        }

        val manager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(serviceChannel)
    }

    companion object
    {
        private const val NOTIFICATION_ID = 564234544
        private const val CHANNEL_ID = "Main"
    }
}
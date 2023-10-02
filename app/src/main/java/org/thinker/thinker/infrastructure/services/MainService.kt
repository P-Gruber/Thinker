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
import org.thinker.thinker.domain.nlp.NLPModel
import org.thinker.thinker.domain.osevents.Event
import org.thinker.thinker.domain.restrictioncheker.RestrictionName
import org.thinker.thinker.infrastructure.AndroidTaskManager
import org.thinker.thinker.infrastructure.dataretrieving.AndroidDataRetriever
import org.thinker.thinker.infrastructure.localization.AndroidLocalizedStrings
import org.thinker.thinker.infrastructure.restrictions.AndroidRestrictionChecker
import org.thinker.thinker.infrastructure.shell.AndroidShell

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
            object : NLPModel
            {
                override fun submitPrompt(prompt: String): String
                {
                    return "toast -t \"Faked response\""
                }
            },
            AndroidLocalizedStrings(this),
            "prompt",
            setOf(Event.Screen.TurnedOn()),
            setOf(DataSourceName.FileContent("/storage/emulated/0/Documents/mobile/Domingos.md")),
            setOf(RestrictionName.BatteryLowerThan(44)),
            setOf("toast")
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
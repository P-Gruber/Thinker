package org.thinker.thinker.infrastructure.services

import android.app.Service
import android.content.Intent
import org.thinker.thinker.application.TaskManager
import org.thinker.thinker.infrastructure.osevents.AndroidEvents
import org.thinker.thinker.infrastructure.AndroidTaskManager

class MainService : Service()
{
    private val systemEvents = AndroidEvents()
    private val androidTaskManager: TaskManager = AndroidTaskManager(systemEvents)
    override fun onBind(intent: Intent) = null


}
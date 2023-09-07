package org.thinker.thinker.infrastructure.services

import android.app.Service
import android.content.Intent
import org.thinker.thinker.application.TaskManager
import org.thinker.thinker.domain.Task
import org.thinker.thinker.domain.osevents.Event

class AndroidTaskManagerService : Service(), TaskManager
{

    override fun onBind(intent: Intent) = null

    override fun addTask(task: Task)
    {
        TODO("Not yet implemented")
    }

    override fun removeTask(task: Task)
    {
        TODO("Not yet implemented")
    }

    override fun updateTask(task: Task)
    {
        TODO("Not yet implemented")
    }

    override fun update(event: Event)
    {
        TODO("Not yet implemented")
    }
}
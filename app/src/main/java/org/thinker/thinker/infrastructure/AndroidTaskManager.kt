package org.thinker.thinker.infrastructure

import org.thinker.thinker.application.TaskManager
import org.thinker.thinker.domain.Task
import org.thinker.thinker.domain.osevents.Event
import org.thinker.thinker.domain.osevents.SystemEvents

class AndroidTaskManager(androidEvents: SystemEvents) : TaskManager
{
    private val tasks = mutableListOf<Task>()

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
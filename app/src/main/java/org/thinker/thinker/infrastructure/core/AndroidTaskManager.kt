package org.thinker.thinker.infrastructure.core

import android.content.Context
import org.thinker.thinker.application.TaskManager
import org.thinker.thinker.domain.Task
import org.thinker.thinker.domain.osevents.Event
import org.thinker.thinker.infrastructure.core.osevents.AndroidEvents

class AndroidTaskManager(context: Context) : TaskManager
{
    private val androidEvents = AndroidEvents(context, this)

    private val tasks = mutableListOf<Task>()

    override fun addTask(task: Task)
    {
        tasks.add(task)
        task.getTriggeringEvents().forEach {
            androidEvents.subscribeTo(it)
        }
    }

    override fun removeTask(task: Task)
    {
        tasks.remove(task)
        task.getTriggeringEvents().forEach {
            androidEvents.unsubscribeFrom(it)
        }
    }

    override fun updateTask(task: Task)
    {
        removeTask(task)
        addTask(task)
    }

    override fun onDestroy()
    {
        androidEvents.onDestroy()
    }

    override fun update(event: Event)
    {
        tasks.forEach { task ->
            task.getTriggeringEvents().forEach triggers@{ taskEvent ->
                if (taskEvent == event)
                {
                    task.execute()
                    return@triggers
                }
            }
        }
    }
}
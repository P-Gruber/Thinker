package org.thinker.thinker.application

import org.thinker.thinker.domain.Task
import org.thinker.thinker.domain.osevents.SystemEventObserver

interface TaskManager: SystemEventObserver
{
    fun addTask(task: Task)

    fun removeTask(task: Task)

    fun updateTask(task: Task)
}
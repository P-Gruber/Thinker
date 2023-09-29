package org.thinker.thinker.infrastructure.osevents

import android.content.Context
import org.thinker.thinker.domain.osevents.Event
import org.thinker.thinker.domain.osevents.SystemEvent
import org.thinker.thinker.domain.osevents.SystemEventObserver
import org.thinker.thinker.domain.osevents.SystemEvents
import org.thinker.thinker.domain.utils.Either
import org.thinker.thinker.infrastructure.AndroidTaskManager
import org.thinker.thinker.infrastructure.osevents.events.ScreenEvent

class AndroidEvents(private val context: Context, private val observer: AndroidTaskManager) :
    SystemEvents, SystemEventObserver
{
    private val observedAndroidEvents = mutableMapOf<Event, SystemEvent?>()

    override fun subscribeTo(event: Event): Either<Exception, Boolean>
    {
        val systemEvent = observedAndroidEvents.computeIfAbsent(event) {
            when (event)
            {
                is Event.Screen -> ScreenEvent(context, this)
            }
        }
        return systemEvent?.subscribeTo(event) ?: Either.Left(Exception())
    }


    override fun unsubscribeFrom(event: Event): Either<Exception, Boolean>
    {
        val systemEvent = observedAndroidEvents[event]
        val response = systemEvent?.unsubscribeFrom(event)
        response?.fold({}) { wasLastOne ->
            if (wasLastOne) observedAndroidEvents.remove(event)
        }
        return response ?: Either.Left(Exception())
    }

    override fun notifyObserver(event: Event)
    {
        observer.update(event)
    }

    override fun onDestroy()
    {
        observedAndroidEvents.values.forEach {
            it?.onDestroy()
        }
    }

    override fun update(event: Event)
    {
        notifyObserver(event)
    }

}

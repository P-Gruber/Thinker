package org.thinker.thinker.infrastructure.osevents

import org.thinker.thinker.domain.osevents.Event
import org.thinker.thinker.domain.osevents.SystemEventObserver
import org.thinker.thinker.domain.osevents.SystemEvents

class AndroidEvents: SystemEvents
{
    private var observer: SystemEventObserver? = null

    private val observedEvents = mutableSetOf<Event>()

    private val activeAndroidEvents = mutableSetOf<SystemEvents>()

    override fun subscribeTo(event: Event)
    {
        TODO("Not yet implemented")
    }

    override fun unsubscribeFrom(event: Event)
    {
        TODO("Not yet implemented")
    }

    override fun subscribe(systemEventObserver: SystemEventObserver)
    {
        TODO("Not yet implemented")
    }

    override fun unsubscribe(systemEventObserver: SystemEventObserver)
    {
        TODO("Not yet implemented")
    }

    override fun notifyObservers(event: Event)
    {
        TODO("Not yet implemented")
    }

}

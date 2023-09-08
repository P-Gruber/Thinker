package org.thinker.thinker.domain.osevents

interface SystemEvents
{
    fun subscribeTo(event: Event)

    fun unsubscribeFrom(event: Event)

    fun subscribe(systemEventObserver: SystemEventObserver)

    fun unsubscribe(systemEventObserver: SystemEventObserver)

    fun notifyObservers(event: Event)
}

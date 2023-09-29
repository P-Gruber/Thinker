package org.thinker.thinker.domain.osevents

import org.thinker.thinker.domain.utils.Either

interface SystemEvent
{

    /** @return true if the observer was successfully subscribed to the event,
     *  false if it was already subscribed */
    fun subscribeTo(event: Event): Either<Exception, Boolean>

    /** @return true if no event is being observed anymore */
    fun unsubscribeFrom(event: Event): Either<Exception, Boolean>

    fun notifyObserver(event: Event)

    fun onDestroy()
}
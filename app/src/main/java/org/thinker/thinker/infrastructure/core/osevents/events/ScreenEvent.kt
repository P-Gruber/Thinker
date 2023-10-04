package org.thinker.thinker.infrastructure.core.osevents.events

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import org.thinker.thinker.domain.osevents.Event
import org.thinker.thinker.domain.osevents.SystemEvent
import org.thinker.thinker.domain.utils.Either
import org.thinker.thinker.infrastructure.core.osevents.AndroidEvents
import org.thinker.thinker.infrastructure.core.osevents.EventReceiver.Companion.makeReceiverForEvent

class ScreenEvent(private var context: Context, private val observer: AndroidEvents) : SystemEvent
{
    private val observedEvents = mutableSetOf<Event.Screen>()

    private val eventToReceiver = mapOf(
        makeReceiverForEvent(
            Event.Screen.TurnedOn(),
            Intent.ACTION_SCREEN_ON,
            ::notifyObserver
        )
    )

    override fun subscribeTo(event: Event): Either<Exception, Boolean>
    {
        if (event !is Event.Screen) return Either.Left(Exception())
        val eventIsNotAlreadyContained = observedEvents.add(event)
        return if (eventIsNotAlreadyContained)
        {
            registerReceiverFor(event)
            Either.Right(true)
        } else Either.Right(false)
    }

    override fun unsubscribeFrom(event: Event): Either<Exception, Boolean>
    {
        if (event !is Event.Screen) return Either.Left(Exception())
        observedEvents.remove(event)
        unregisterReceiverFor(event)
        return Either.Right(observedEvents.isEmpty())
    }

    override fun notifyObserver(event: Event)
    {
        observer.update(event)
    }

    override fun onDestroy()
    {
        observedEvents.forEach {
            unsubscribeFrom(it)
        }
    }

    private fun registerReceiverFor(event: Event)
    {
        val eventReceiver = eventToReceiver[event] ?: return
        context.registerReceiver(eventReceiver, IntentFilter(Intent.ACTION_SCREEN_ON))
    }

    private fun unregisterReceiverFor(event: Event)
    {
        context.unregisterReceiver(eventToReceiver[event])
    }
}
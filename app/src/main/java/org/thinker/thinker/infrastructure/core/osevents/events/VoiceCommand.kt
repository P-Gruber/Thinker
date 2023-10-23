package org.thinker.thinker.infrastructure.core.osevents.events

import android.app.Activity
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import org.thinker.thinker.domain.osevents.Event
import org.thinker.thinker.domain.osevents.SystemEvent
import org.thinker.thinker.domain.utils.Either
import org.thinker.thinker.infrastructure.core.osevents.AndroidEvents
import org.thinker.thinker.infrastructure.utils.kextensions.logIfDebugging

class VoiceCommand(private val observer: AndroidEvents) : SystemEvent
{
    private val eventBusObserver = Observer<Event.Command.Voice>(::notifyObserver)

    override fun subscribeTo(event: Event): Either<Exception, Boolean>
    {
        EVENT_BUS.observeForever(eventBusObserver)
        return Either.Right(true)
    }

    override fun unsubscribeFrom(event: Event): Either<Exception, Boolean>
    {
        EVENT_BUS.removeObserver(eventBusObserver)
        return Either.Right(true)
    }

    override fun notifyObserver(event: Event)
    {
        observer.update(event)
    }

    override fun onDestroy()
    {
        EVENT_BUS.removeObserver(eventBusObserver)
    }

    class VoiceCommandActivity : Activity()
    {
        override fun onCreate(savedInstanceState: Bundle?)
        {
            super.onCreate(savedInstanceState)
            "Voice Command Action Executed".logIfDebugging()
            EVENT_BUS.postValue(Event.Command.Voice())
            finish()
        }
    }

    companion object
    {
        private val EVENT_BUS = MutableLiveData<Event.Command.Voice>()
    }
}
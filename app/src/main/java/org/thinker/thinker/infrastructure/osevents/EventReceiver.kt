package org.thinker.thinker.infrastructure.osevents

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.thinker.thinker.domain.osevents.Event


data class EventReceiver(
    val event: Event, val action: String, val onReceive: (Event) -> Unit
) : BroadcastReceiver()
{
    override fun onReceive(context: Context?, intent: Intent?)
    {
        if (intent?.action == action) onReceive(event)
    }

    companion object
    {
        fun makeReceiverForEvent(
            event: Event,
            action: String,
            onReceive: (Event) -> Unit
        ) =
            Pair(event, EventReceiver(event, action, onReceive))
    }
}
package org.thinker.thinker.domain.osevents

interface SystemEventObserver
{
    fun update(event: Event)
}
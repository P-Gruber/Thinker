package org.thinker.thinker.domain

import org.thinker.thinker.domain.dataretriever.DataSourceName
import org.thinker.thinker.domain.osevents.Event
import org.thinker.thinker.domain.restrictioncheker.RestrictionName


abstract class Task()
{
    protected abstract val triggers: Set<Event>
    protected abstract val dataSourceNames: Set<DataSourceName>
    protected abstract val restrictionNames: Set<RestrictionName>
    protected abstract val actions: Set<String> // Set<ProgramName>

    abstract suspend fun execute()

    abstract fun getTriggeringEvents(): Set<Event>
}

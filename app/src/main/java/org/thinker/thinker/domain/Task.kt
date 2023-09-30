package org.thinker.thinker.domain

import org.thinker.thinker.domain.dataretriever.DataRetriever
import org.thinker.thinker.domain.dataretriever.DataSourceName
import org.thinker.thinker.domain.osevents.Event
import org.thinker.thinker.domain.restrictioncheker.RestrictionChecker
import org.thinker.thinker.domain.restrictioncheker.RestrictionName
import org.thinker.thinker.domain.shell.Shell


abstract class Task(
    private val shell: Shell,
    private val restrictionChecker: RestrictionChecker,
    private val dataRetriever: DataRetriever
)
{
    protected abstract val triggers: Set<Event>
    protected abstract val dataSourceNames: Set<DataSourceName>
    protected abstract val restrictionNames: Set<RestrictionName>
    protected abstract val actions: Set<String> // Set<ProgramName>

    abstract fun execute()

    abstract fun getTriggeringEvents(): Set<Event>
}

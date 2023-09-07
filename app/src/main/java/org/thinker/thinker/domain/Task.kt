package org.thinker.thinker.domain

import org.thinker.thinker.domain.dataretriever.DataRetriever
import org.thinker.thinker.domain.dataretriever.DataSourceName
import org.thinker.thinker.domain.osevents.Event
import org.thinker.thinker.domain.restrictioncheker.RestrictionChecker
import org.thinker.thinker.domain.shell.Shell


abstract class Task(
    private val shell: Shell,
    private val restrictionChecker: RestrictionChecker,
    private val dataRetriever: DataRetriever
)
{
    protected val triggers = listOf<Event>()
    protected val dataSourceNames = listOf<DataSourceName>()
    protected val actions = listOf<String>() // List<ProgramName>

    abstract fun execute()
}

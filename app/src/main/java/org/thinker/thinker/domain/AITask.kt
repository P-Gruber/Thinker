package org.thinker.thinker.domain

import org.thinker.thinker.domain.dataretriever.DataRetriever
import org.thinker.thinker.domain.dataretriever.DataSourceName
import org.thinker.thinker.domain.osevents.Event
import org.thinker.thinker.domain.restrictioncheker.RestrictionChecker
import org.thinker.thinker.domain.shell.Shell

class AITask(
    private val shell: Shell,
    private val restrictionChecker: RestrictionChecker,
    private val dataRetriever: DataRetriever,
    override val triggers: Set<Event>,
    override val dataSourceNames: Set<DataSourceName>,
    override val actions: Set<String>
) : Task(shell, restrictionChecker, dataRetriever)
{

    override fun execute()
    {
        // TODO: "Not yet implemented"
        shell.interpretInput("test", {}, {}, {})
    }

    override fun getTriggeringEvents(): Set<Event>
    {
        return triggers
    }
}
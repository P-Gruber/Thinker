package org.thinker.thinker.domain

import org.thinker.thinker.domain.dataretriever.DataRetriever
import org.thinker.thinker.domain.dataretriever.DataSourceName
import org.thinker.thinker.domain.nlp.NLPModel
import org.thinker.thinker.domain.osevents.Event
import org.thinker.thinker.domain.restrictioncheker.RestrictionChecker
import org.thinker.thinker.domain.restrictioncheker.RestrictionName
import org.thinker.thinker.domain.shell.Shell

class AITask(
    private val shell: Shell,
    private val restrictionChecker: RestrictionChecker,
    private val dataRetriever: DataRetriever,
    private val nlpModel: NLPModel,
    private val prompt: String,
    override val triggers: Set<Event>,
    override val dataSourceNames: Set<DataSourceName>,
    override val restrictionNames: Set<RestrictionName>,
    override val actions: Set<String>
) : Task(shell, restrictionChecker, dataRetriever)
{

    override fun execute()
    {
        if (canNotExecute()) return
        val finalPrompt = resolvePrompt()
        val response = nlpModel.submitPrompt(finalPrompt)
        shell.interpretInput(response, {}, {}, { })
        // TODO: handle failure scenario (also response is not a command)
    }

    override fun getTriggeringEvents(): Set<Event>
    {
        return triggers
    }

    private fun canNotExecute(): Boolean
    {
        return restrictionNames.any { restriction ->
            restrictionChecker.check(restriction)
        }
    }


    private fun resolvePrompt(): String
    {
        val data = fetchData()
        val programsInstructions = getProgramsInstructions()
        return "data: $data\n\n" +
                "Programs Instructions: $programsInstructions\n\n" +
                prompt
    }

    private fun fetchData(): String
    {
        return dataSourceNames.joinToString("\n\n") { dataSource ->
            dataRetriever.retrieveData(dataSource)
        }
    }

    private fun getProgramsInstructions(): String
    {
        return actions.joinToString("\n\n") { program ->
            var instruction = ""
            shell.interpretInput("$program --help",
                {},
                stdout = { output -> instruction = output },
                {}
            )
            instruction
        }
    }

}
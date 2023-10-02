package org.thinker.thinker.domain

import org.thinker.thinker.domain.dataretriever.DataRetriever
import org.thinker.thinker.domain.dataretriever.DataSourceName
import org.thinker.thinker.domain.dataretriever.PermissionNotGrantedException
import org.thinker.thinker.domain.localization.LocalizedStrings
import org.thinker.thinker.domain.nlp.NLPModel
import org.thinker.thinker.domain.osevents.Event
import org.thinker.thinker.domain.restrictioncheker.RestrictionChecker
import org.thinker.thinker.domain.restrictioncheker.RestrictionName
import org.thinker.thinker.domain.shell.Shell
import org.thinker.thinker.domain.utils.Either
import org.thinker.thinker.infrastructure.presentation.XXPermissionsActivity
import org.thinker.thinker.infrastructure.shell.builtin.Notifier

class AITask(
    private val shell: Shell,
    private val restrictionChecker: RestrictionChecker,
    private val dataRetriever: DataRetriever,
    private val nlpModel: NLPModel,
    private val localizedStrings: LocalizedStrings,
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
        if (finalPrompt.isLeft)
        {
            shell.interpretInput(
                "toast -t \"An error has occurred: ${finalPrompt.getLeftValue().message}\"",
                {}, {}, {}
            )
            return
        }
//        val response = nlpModel.submitPrompt(finalPrompt)
        var response = ":("
        finalPrompt.fold({}) {
            response = "toast -t \"$it\""
        }
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


    private fun resolvePrompt(): Either<Exception, String>
    {
        val data = retrieveData().also {
            if (it.isLeft)
            {
                if (it.getLeftValue() is PermissionNotGrantedException)
                {
                    val permission = (it.getLeftValue() as PermissionNotGrantedException).permission
                    val title = localizedStrings.taskNeedsPermissionToContinue
                    val message = localizedStrings.touchNotificationToProceed
                    val intentClass =
                        "org.thinker.thinker.infrastructure.presentation.XXPermissionsActivity"
                    val bundle = "[\n" +
                            "    {\n" +
                            "        \"type\": \"string\",\n" +
                            "        \"key\": \"${XXPermissionsActivity.PERMISSION_KEY}\",\n" +
                            "        \"value\": \"$permission\"\n" +
                            "    }\n" +
                            "]\n"
                    shell.interpretInput(
                        "${Notifier.NAME} " +
                                "--notification-id 1 " +
                                "--title \"$title\" " +
                                "--message \"$message\" " +
                                "--intent-class \"$intentClass\" " +
                                "--bundle \"$bundle\"",
                        {}, {}, {}
                    )
                }
                return it
            }
        }
        val programsInstructions = getProgramsInstructions()
        return Either.Right(
            "data: ${data.getRightValue()}\n\n" +
                    "Programs Instructions: $programsInstructions\n\n" +
                    prompt
        )
    }

    private fun retrieveData(): Either<Exception, String>
    {
        val stringBuilder = StringBuilder()

        for (dataSource in dataSourceNames)
        {
            when (val data = dataRetriever.retrieveData(dataSource))
            {
                is Either.Right -> stringBuilder.append(data.value).append("\n\n")
                is Either.Left -> return data
            }
        }

        // Remove the last "\n\n" for cleaner output.
        if (stringBuilder.isNotEmpty())
        {
            stringBuilder.setLength(stringBuilder.length - 2)
        }

        return Either.Right(stringBuilder.toString())
    }


    private fun getProgramsInstructions(): String
    {
        return actions.joinToString("\n\n") { program ->
            var instruction = ""
            shell.interpretInput(
                "$program --help",
                {},
                stdout = { output -> instruction = output },
                {}
            )
            instruction
        }
    }

}
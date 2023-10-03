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
        if (mustAbort()) return

        val finalPrompt = resolvePrompt().rightOrNullAndRun {
            when (value)
            {
                is PermissionNotGrantedException -> notifyPermissionProblem(value.permission)
                else -> notifyPromptProblem() // TODO: be more specific: program not found, etc
            }
        } ?: return

        val input = nlpModel.submitPrompt(finalPrompt).rightOrNullAndRun {
            notifyPromptProblem() // TODO: be more specific: internet, parsing, etc
        } ?: return

        val exitCode = shell.interpretInput(input, {}, {}, { })
        if (exitCode != Shell.ExitCodes.SUCCESS)
        {
            notifyPromptProblem() // TODO: be more specific: program not found, etc
        }
    }

    private fun mustAbort(): Boolean
    {
        val aRestrictionIsActive = isAnyRestrictionActive().rightOrNullAndRun {
            notifyRestrictionProblem()
            return true
        }
        if (aRestrictionIsActive == true) return true
        return false
    }

    private fun notifyPromptProblem()
    {
        // TODO: Show the task name, so that the user knows from where the problem comes
        val title = localizedStrings.problemOccurredWhileMakingPrompt
        val message = localizedStrings.taskWillNotExecute
        shell.interpretInput(
            "${Notifier.NAME} " +
                    "--notification-id 47318 " +
                    "--title \"$title\" " +
                    "--message \"$message\"",
            {}, {}, {}
        )
    }

    override fun getTriggeringEvents(): Set<Event>
    {
        return triggers
    }

    private fun isAnyRestrictionActive(): Either<Exception, Boolean>
    {
        restrictionNames.forEach {
            restrictionChecker.check(it).fold(
                { exception ->
                    return Either.Left(exception)
                },
                { isActive ->
                    if (isActive) return Either.Right(true)
                }
            )
        }
        return Either.Right(false)
    }

    private fun notifyRestrictionProblem()
    {
        // TODO: Show the task name, so that the user knows from where the problem comes
        val title = localizedStrings.problemOccurredWhileVerifyingConstraint
        val message = localizedStrings.taskWillNotExecute
        shell.interpretInput(
            "${Notifier.NAME} " +
                    "--notification-id 865424 " +
                    "--title \"$title\" " +
                    "--message \"$message\"",
            {}, {}, {}
        )
    }


    private fun resolvePrompt(): Either<Exception, String>
    {
        val data = retrieveData().rightOrNullAndRun {
            return this
        }

        val programsInstructions = getProgramsInstructions().rightOrNullAndRun {
            return this
        }

        return Either.Right(
            "data: ${data}\n\n" +
                    "Programs Instructions: $programsInstructions\n\n" +
                    prompt
        )
    }

    private fun notifyPermissionProblem(permission: String)
    {
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
                    "--notification-id 45442 " +
                    "--title \"$title\" " +
                    "--message \"$message\" " +
                    "--intent-class \"$intentClass\" " +
                    "--bundle \"$bundle\"",
            {}, {}, {}
        )
    }

    private fun retrieveData(): Either<Exception, String>
    {
        val stringBuilder = StringBuilder()

        for (dataSource in dataSourceNames)
        {
            val data = dataRetriever.retrieveData(dataSource).rightOrNullAndRun {
                return this
            }
            stringBuilder.append(data).append("\n\n")
        }

        // Remove the last "\n\n" for cleaner output.
        if (stringBuilder.isNotEmpty())
        {
            stringBuilder.setLength(stringBuilder.length - 2)
        }

        return Either.Right(stringBuilder.toString())
    }


    private fun getProgramsInstructions(): Either<Exception, String>
    {
        val instructions = mutableListOf<String>()

        for (program in actions)
        {
            var instruction = ""
            val exitCode = shell.interpretInput(
                "$program --help",
                {},
                stdout = { output -> instruction = output },
                {}
            )
            if (exitCode == Shell.ExitCodes.SUCCESS) instructions.add(instruction)
            else return Either.Left(Exception())
        }

        return Either.Right(instructions.joinToString("\n\n"))
    }

}
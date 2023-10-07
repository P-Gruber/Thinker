package org.thinker.thinker.domain

import org.thinker.thinker.domain.dataretriever.DataRetriever
import org.thinker.thinker.domain.dataretriever.DataSourceName
import org.thinker.thinker.domain.dataretriever.PermissionNotGrantedException
import org.thinker.thinker.domain.localization.LocalizedStrings
import org.thinker.thinker.domain.osevents.Event
import org.thinker.thinker.domain.repository.NLPModelException
import org.thinker.thinker.domain.repository.NLPModelRepo
import org.thinker.thinker.domain.restrictioncheker.RestrictionChecker
import org.thinker.thinker.domain.restrictioncheker.RestrictionName
import org.thinker.thinker.domain.shell.Shell
import org.thinker.thinker.domain.utils.Either
import org.thinker.thinker.infrastructure.core.shell.builtin.Notifier
import org.thinker.thinker.infrastructure.presentation.XXPermissionsActivity

class AITask(
    private val shell: Shell,
    private val restrictionChecker: RestrictionChecker,
    private val dataRetriever: DataRetriever,
    private val nlpModelRepo: NLPModelRepo,
    private val localizedStrings: LocalizedStrings,
    private val prompt: String,
    override val triggers: Set<Event>,
    override val dataSourceNames: Set<DataSourceName>,
    override val restrictionNames: Set<RestrictionName>,
    override val actions: Set<String>
) : Task()
{

    override fun getTriggeringEvents(): Set<Event>
    {
        return triggers
    }

    override suspend fun execute()
    {
        if (mustAbort()) return

        val finalPrompt = resolvePrompt().rightOrNullAndRun {
            when (value)
            {
                is PermissionNotGrantedException -> notifyPermissionProblem(value.permission)
                // TODO: be more specific: program not found, etc
                // TODO: Show the task name, so that the user knows from where the problem comes
                else -> notifyMessage(
                    237954,
                    localizedStrings.problemOccurredWhileMakingPrompt,
                    localizedStrings.taskWillNotExecute
                )
            }
        } ?: return

        val input = nlpModelRepo.getResponse(finalPrompt).rightOrNullAndRun {
            notifyNLPModelProblem(value)
        } ?: return

        val exitCode = shell.interpretInput(input, {}, {}, {})
        if (exitCode != Shell.ExitCodes.SUCCESS)
        {
            notifyShellProblem(exitCode)
        }
    }

    private fun mustAbort(): Boolean
    {
        val aRestrictionIsActive = isAnyRestrictionActive().rightOrNullAndRun {
            notifyMessage(
                865424,
                localizedStrings.problemOccurredWhileVerifyingConstraint,
                localizedStrings.taskWillNotExecute
            )
            return true
        }
        if (aRestrictionIsActive == true) return true
        return false
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


    private fun resolvePrompt(): Either<Exception, String>
    {
        // TODO: Ensure the size does not exceed the limit
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

    private fun notifyShellProblem(exitCode: Int)
    {
        val message = when (exitCode)
        {
            1 -> localizedStrings.unexpectedProblem
            2 -> localizedStrings.misuseOfShellBuiltinOrIncorrectCommandUsage
            3 -> localizedStrings.parsingError
            126 -> localizedStrings.commandInvokedCannotExecute
            127 -> localizedStrings.commandNotFound
            128 -> localizedStrings.invalidExitArgument
            else -> localizedStrings.unexpectedProblem
        }
        notifyMessage(237954, localizedStrings.problemOccurredWhileGeneratingResponse, message)
    }

    private fun notifyNLPModelProblem(exception: NLPModelException)
    {
        val message = when (exception)
        {
            is NLPModelException.NoInternet -> localizedStrings.noInternet
            is NLPModelException.Client -> localizedStrings.clientSideIssue
            is NLPModelException.Forbidden -> localizedStrings.clientSideIssue
            is NLPModelException.TooManyRequests -> localizedStrings.tooManyRequests
            is NLPModelException.Server -> localizedStrings.serverSideIssue
            is NLPModelException.Unauthorized -> localizedStrings.apiKeyNotProvidedOrInvalid
            is NLPModelException.Unexpected -> localizedStrings.unexpectedProblem
        }
        notifyMessage(237954, localizedStrings.problemOccurredWhileGeneratingResponse, message)
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

    private fun notifyMessage(id: Int, title: String, message: String)
    {
        shell.interpretInput(
            "${Notifier.NAME} " +
                    "--notification-id $id " +
                    "--title \"$title\" " +
                    "--message \"$message\"",
            {}, {}, {}
        )
    }

}
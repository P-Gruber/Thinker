package org.thinker.thinker.domain

import org.thinker.thinker.domain.dataretriever.DataSourceName
import org.thinker.thinker.domain.localization.LocalizedStrings
import org.thinker.thinker.domain.osevents.Event
import org.thinker.thinker.domain.restrictioncheker.RestrictionChecker
import org.thinker.thinker.domain.restrictioncheker.RestrictionName
import org.thinker.thinker.domain.shell.Shell
import org.thinker.thinker.domain.utils.Either
import org.thinker.thinker.infrastructure.core.shell.builtin.Notifier

/**
 * A macro task represents an automated action or sequence of actions.
 * When specific conditions or triggers are met, this task will be executed to automate user-defined
 * behaviors on the device.
 */
class MacroTask(
    private val shell: Shell,
    private val restrictionChecker: RestrictionChecker,
    private val localizedStrings: LocalizedStrings,
    override val triggers: Set<Event>,
    override val dataSourceNames: Set<DataSourceName>,
    override val restrictionNames: Set<RestrictionName>,
    override val actions: Set<String>
) : Task()
{
    override suspend fun execute()
    {
        if (mustAbort()) return

        actions.forEach { input ->
            val exitCode = shell.interpretInput(input, {}, {}, {})
            if (exitCode != Shell.ExitCodes.SUCCESS)
            {
                notifyShellProblem(exitCode, input)
            }
        }
    }

    override fun getTriggeringEvents(): Set<Event>
    {
        return triggers
    }

    private fun mustAbort(): Boolean
    {
        val aRestrictionIsActive = isAnyRestrictionActive().rightOrNullAndRun {
            notifyMessage(
                (10000..99999).random(),
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


    private fun notifyShellProblem(exitCode: Int, input: String)
    {
        val message = when (exitCode)
        {
            1 -> localizedStrings.unexpectedProblem
            2 -> localizedStrings.misuseOfShellBuiltinOrIncorrectCommandUsage
            3 -> localizedStrings.parsingError
            126 -> localizedStrings.commandInvokedCannotExecute
            127 -> "${localizedStrings.commandNotFound}: \"$input\""
            128 -> localizedStrings.invalidExitArgument
            else -> localizedStrings.unexpectedProblem
        }
        notifyMessage(
            (10000..99999).random(),
            localizedStrings.problemOccurredWhileGeneratingResponse,
            message
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
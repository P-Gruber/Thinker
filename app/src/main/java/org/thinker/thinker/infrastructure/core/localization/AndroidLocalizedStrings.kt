package org.thinker.thinker.infrastructure.core.localization

import android.content.Context
import org.thinker.thinker.R
import org.thinker.thinker.domain.localization.LocalizedStrings

class AndroidLocalizedStrings(private val context: Context) : LocalizedStrings()
{
    override val taskNeedsPermissionToContinue: String
        get() = context.getString(R.string.task_needs_permission_to_continue)

    override val touchNotificationToProceed: String
        get() = context.getString(R.string.touch_notification_to_proceed)
    override val problemOccurredWhileVerifyingConstraint: String
        get() = context.getString(R.string.problem_occurred_while_verifying_constraint)
    override val taskWillNotExecute: String
        get() = context.getString(R.string.task_will_not_execute)

    override val problemOccurredWhileMakingPrompt: String
        get() = context.getString(R.string.problem_occurred_while_making_prompt)

    override val problemOccurredWhileGeneratingResponse: String
        get() = context.getString(R.string.problem_occurred_while_generating_response)

    override val clientSideIssue: String
        get() = context.getString(R.string.client_side_issue)

    override val unexpectedProblem: String
        get() = context.getString(R.string.unexpected_problem)

    override val apiKeyNotProvidedOrInvalid: String
        get() = context.getString(R.string.api_key_not_provided_or_invalid)

    override val serverSideIssue: String
        get() = context.getString(R.string.server_side_issue)

    override val noInternet: String
        get() = context.getString(R.string.no_internet)

    override val invalidExitArgument: String
        get() = context.getString(R.string.invalid_exit_argument)

    override val commandNotFound: String
        get() = context.getString(R.string.command_not_found)

    override val commandInvokedCannotExecute: String
        get() = context.getString(R.string.command_invoked_cannot_execute)

    override val parsingError: String
        get() = context.getString(R.string.parsing_error)

    override val misuseOfShellBuiltinOrIncorrectCommandUsage: String
        get() = context.getString(R.string.misuse_of_shell_builtin_or_incorrect_command_usage)

    override val tooManyRequests: String
        get() = context.getString(R.string.too_many_requests)

}
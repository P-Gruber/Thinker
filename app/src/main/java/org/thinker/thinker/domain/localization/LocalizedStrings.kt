package org.thinker.thinker.domain.localization

abstract class LocalizedStrings
{
    abstract val taskNeedsPermissionToContinue: String

    abstract val touchNotificationToProceed: String

    abstract val problemOccurredWhileVerifyingConstraint: String

    abstract val taskWillNotExecute: String

    abstract val problemOccurredWhileMakingPrompt: String

    abstract val problemOccurredWhileGeneratingResponse: String

    abstract val clientSideIssue: String

    abstract val unexpectedProblem: String

    abstract val apiKeyNotProvidedOrInvalid: String

    abstract val serverSideIssue: String

    abstract val noInternet: String

    abstract val invalidExitArgument: String

    abstract val commandNotFound: String

    abstract val commandInvokedCannotExecute: String

    abstract val parsingError: String

    abstract val misuseOfShellBuiltinOrIncorrectCommandUsage: String

    abstract val tooManyRequests: String
}
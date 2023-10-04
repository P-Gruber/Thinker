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
}
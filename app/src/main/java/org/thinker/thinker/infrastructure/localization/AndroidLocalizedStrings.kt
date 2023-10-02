package org.thinker.thinker.infrastructure.localization

import android.content.Context
import org.thinker.thinker.R
import org.thinker.thinker.domain.localization.LocalizedStrings

class AndroidLocalizedStrings(private val context: Context) : LocalizedStrings()
{
    override val taskNeedsPermissionToContinue: String
        get() = context.getString(R.string.task_needs_permission_to_continue)

    override val touchNotificationToProceed: String
        get() = context.getString(R.string.touch_notification_to_proceed)
}
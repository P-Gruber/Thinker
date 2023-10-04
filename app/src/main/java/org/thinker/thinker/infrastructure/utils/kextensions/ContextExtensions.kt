package org.thinker.thinker.infrastructure.utils.kextensions

import android.content.Context

object ContextExtensions
{
    fun Context.getAppName(): String {
        return this.applicationInfo.loadLabel(this.packageManager).toString()
    }
}
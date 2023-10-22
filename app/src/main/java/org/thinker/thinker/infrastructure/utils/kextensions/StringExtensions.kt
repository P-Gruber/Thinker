package org.thinker.thinker.infrastructure.utils.kextensions

import android.util.Log
import org.thinker.thinker.infrastructure.presentation.MainActivity.Companion.DEBUGGING

fun String.logIfDebugging(tag: String = "Thinker", priority: Int = Log.DEBUG)
{
    if (DEBUGGING) Log.println(priority, tag, this)
}
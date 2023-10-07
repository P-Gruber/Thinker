package org.thinker.thinker.infrastructure.utils.kextensions

import org.thinker.thinker.infrastructure.presentation.MainActivity.Companion.DEBUGGING

fun Throwable.printStackTraceIfDebugging()
{
    if (DEBUGGING) this.printStackTrace()
}
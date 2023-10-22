package org.thinker.thinker.domain.utils

import java.util.Calendar

fun todayTimeToMillis(timeOfDay: String): Long
{
    val (hour, minute) = timeOfDay.split(":").map { it.toInt() }
    return Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

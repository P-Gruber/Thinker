package org.thinker.thinker.infrastructure.core.osevents.events

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import org.thinker.thinker.domain.osevents.Event
import org.thinker.thinker.domain.osevents.SystemEvent
import org.thinker.thinker.domain.utils.Either
import org.thinker.thinker.domain.utils.todayTimeToMillis
import org.thinker.thinker.infrastructure.core.osevents.AndroidEvents
import org.thinker.thinker.infrastructure.utils.kextensions.logIfDebugging
import org.thinker.thinker.infrastructure.utils.kextensions.printStackTraceIfDebugging
import kotlin.time.Duration.Companion.hours

class DailyTime(
    private val context: Context,
    private val observer: AndroidEvents,
    private val timeOfDay: String
) : SystemEvent
{
    private val eventBusObserver = Observer<Event.DateTime.DailyTime>(::notifyObserver)
    override fun subscribeTo(event: Event): Either<Exception, Boolean>
    {
        EVENT_BUS.observeForever(eventBusObserver)
        scheduleDailyAlarm(context, timeOfDay, todayTimeToMillis(timeOfDay))
        return Either.Right(true)
    }

    override fun unsubscribeFrom(event: Event): Either<Exception, Boolean>
    {
        EVENT_BUS.removeObserver(eventBusObserver)
        // TODO: cancel alarm
        return Either.Right(false)
    }

    override fun notifyObserver(event: Event)
    {
        observer.update(event)
    }

    override fun onDestroy()
    {
        EVENT_BUS.removeObserver(eventBusObserver)
        // TODO: cancel alarm?
    }

    class DailyTimeReceiver : BroadcastReceiver()
    {
        override fun onReceive(context: Context?, intent: Intent?)
        {
            val timeOfDay = intent?.action ?: return
            EVENT_BUS.postValue(Event.DateTime.DailyTime(timeOfDay))
            "A Daily Time Receiver was invoked at $timeOfDay".logIfDebugging()

            // Re-schedule
            context?.let {
                val intervalMillis = 24.hours.inWholeMilliseconds
                val newTimeOfDay = todayTimeToMillis(timeOfDay) + intervalMillis
                scheduleDailyAlarm(it, timeOfDay, newTimeOfDay)
            }
        }
    }

    companion object
    {
        private val EVENT_BUS = MutableLiveData<Event.DateTime.DailyTime>()

        private fun scheduleDailyAlarm(context: Context, timeOfDay: String, time: Long)
        {
            val alarmManager = context.getSystemService(AlarmManager::class.java)
            val intent = Intent(context, DailyTimeReceiver::class.java).apply {
                action = timeOfDay
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent,
                PendingIntent.FLAG_MUTABLE
            )
            try
            {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
            } catch (e: SecurityException)
            {
                e.printStackTraceIfDebugging()
            }
        }
    }
}

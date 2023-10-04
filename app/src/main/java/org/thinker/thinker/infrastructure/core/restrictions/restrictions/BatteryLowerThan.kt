package org.thinker.thinker.infrastructure.core.restrictions.restrictions

import android.content.Context
import android.os.BatteryManager
import org.thinker.thinker.domain.restrictioncheker.Restriction
import org.thinker.thinker.domain.utils.Either

class BatteryLowerThan(private val context: Context, private val value: Int) : Restriction
{
    override fun check(): Either<Exception, Boolean>
    {
        return runCatching {
            val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            val batteryLevel = batteryManager.getIntProperty(
                BatteryManager.BATTERY_PROPERTY_CAPACITY
            )
            Either.Right(batteryLevel < value)
        }.getOrElse {
            Either.Left(Exception(it))
        }
    }
}
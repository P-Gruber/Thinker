package org.thinker.thinker.infrastructure.core.restrictions

import android.content.Context
import org.thinker.thinker.domain.restrictioncheker.Restriction
import org.thinker.thinker.domain.restrictioncheker.RestrictionChecker
import org.thinker.thinker.domain.restrictioncheker.RestrictionName
import org.thinker.thinker.domain.utils.Either
import org.thinker.thinker.infrastructure.core.restrictions.restrictions.BatteryLowerThan

class AndroidRestrictionChecker(private val context: Context) : RestrictionChecker
{
    override fun check(restriction: RestrictionName): Either<Exception, Boolean>
    {
        return getRestriction(restriction).check()
    }

    private fun getRestriction(restriction: RestrictionName): Restriction
    {
        return when (restriction)
        {
            is RestrictionName.BatteryLowerThan -> BatteryLowerThan(context, restriction.value)
        }
    }
}
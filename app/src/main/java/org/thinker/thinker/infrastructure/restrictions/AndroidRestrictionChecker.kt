package org.thinker.thinker.infrastructure.restrictions

import org.thinker.thinker.domain.restrictioncheker.RestrictionChecker
import org.thinker.thinker.domain.restrictioncheker.RestrictionName

class AndroidRestrictionChecker : RestrictionChecker
{
    override fun check(restriction: RestrictionName): Boolean
    {
        // TODO: Not yet implemented
        return false
    }
}
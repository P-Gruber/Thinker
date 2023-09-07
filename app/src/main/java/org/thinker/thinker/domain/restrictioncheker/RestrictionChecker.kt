package org.thinker.thinker.domain.restrictioncheker

interface RestrictionChecker
{
    fun check(restriction: RestrictionName): Boolean
}

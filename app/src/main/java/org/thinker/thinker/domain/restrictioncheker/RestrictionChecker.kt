package org.thinker.thinker.domain.restrictioncheker

import org.thinker.thinker.domain.utils.Either

interface RestrictionChecker
{
    fun check(restriction: RestrictionName): Either<Exception, Boolean>
}

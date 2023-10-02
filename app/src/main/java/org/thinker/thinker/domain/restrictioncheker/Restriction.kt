package org.thinker.thinker.domain.restrictioncheker

import org.thinker.thinker.domain.utils.Either

interface Restriction
{
    fun check(): Either<Exception, Boolean>
}
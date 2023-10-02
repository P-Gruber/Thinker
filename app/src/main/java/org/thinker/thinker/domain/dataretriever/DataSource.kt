package org.thinker.thinker.domain.dataretriever

import org.thinker.thinker.domain.utils.Either

interface DataSource
{
    fun retrieveData(): Either<Exception, String>
}
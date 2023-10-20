package org.thinker.thinker.domain.dataretriever

import org.thinker.thinker.domain.utils.Either

interface DataRetriever
{
    fun retrieveData(dataSourceName: DataSourceName): Either<DataRetrieverException, String>
}

package org.thinker.thinker.domain.dataretriever

interface DataRetriever
{
    fun retrieveData(dataSourceName: DataSourceName): String
}

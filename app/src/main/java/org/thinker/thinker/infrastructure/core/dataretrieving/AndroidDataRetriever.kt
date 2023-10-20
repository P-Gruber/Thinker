package org.thinker.thinker.infrastructure.core.dataretrieving

import android.content.Context
import org.thinker.thinker.domain.dataretriever.DataRetriever
import org.thinker.thinker.domain.dataretriever.DataRetrieverException
import org.thinker.thinker.domain.dataretriever.DataSource
import org.thinker.thinker.domain.dataretriever.DataSourceName
import org.thinker.thinker.domain.utils.Either
import org.thinker.thinker.infrastructure.core.dataretrieving.datasources.DailyFileContent
import org.thinker.thinker.infrastructure.core.dataretrieving.datasources.FileContent
import org.thinker.thinker.infrastructure.core.dataretrieving.datasources.TimeOfDay

class AndroidDataRetriever(private val context: Context) : DataRetriever
{
    override fun retrieveData(dataSourceName: DataSourceName): Either<DataRetrieverException, String>
    {
        return getDataSource(dataSourceName).retrieveData()
    }

    private fun getDataSource(dataSourceName: DataSourceName): DataSource
    {
        return when (dataSourceName)
        {
            is DataSourceName.FileContent -> FileContent(context, dataSourceName.filePath)
            is DataSourceName.DailyFileContent -> DailyFileContent(
                context,
                dataSourceName.folder,
                dataSourceName.dateTimeFormat,
                dataSourceName.fileExtension
            )

            is DataSourceName.TimeOfDay -> TimeOfDay()
        }
    }
}
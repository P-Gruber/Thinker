package org.thinker.thinker.domain.dataretriever

sealed class DataSourceName(val keyName: String)
{
    class FileContent(val filePath: String) : DataSourceName("FileContent")

    class DailyFileContent(
        val folder: String, val dateTimeFormat: String, val fileExtension: String
    ) : DataSourceName("DailyFileContent")

    class TimeOfDay : DataSourceName("TimeOfDay")

}

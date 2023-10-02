package org.thinker.thinker.domain.dataretriever

sealed class DataSourceName(val keyName: String)
{
    class FileContent(val filePath: String) : DataSourceName("FileContent")
}

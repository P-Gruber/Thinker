package org.thinker.thinker.infrastructure.core.dataretrieving.datasources

import android.content.Context
import com.hjq.permissions.Permission
import org.thinker.thinker.domain.dataretriever.DataRetrieverException
import org.thinker.thinker.domain.dataretriever.DataSource
import org.thinker.thinker.domain.dataretriever.FileNotFound
import org.thinker.thinker.domain.dataretriever.PermissionNotGranted
import org.thinker.thinker.domain.dataretriever.Unexpected
import org.thinker.thinker.domain.utils.Either
import org.thinker.thinker.infrastructure.presentation.XXPermissionsActivity
import java.io.File
import java.io.FileNotFoundException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DailyFileContent(
    private val context: Context,
    private val folder: String,
    private val dateTimeFormat: String,
    private val fileExtension: String
) : DataSource
{
    override fun retrieveData(): Either<DataRetrieverException, String>
    {
        val permission = Permission.MANAGE_EXTERNAL_STORAGE
        if (XXPermissionsActivity.isNotGranted(context, permission))
        {
            return Either.Left(PermissionNotGranted(permission))
        }

        val dateTimeFormatted = runCatching {
            val dateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern(dateTimeFormat)
            dateTime.format(formatter)
        }.getOrDefault(dateTimeFormat)

        val filePath = "$folder$dateTimeFormatted.$fileExtension"

        return try
        {
            val content = File(filePath).readText(Charsets.UTF_8)
            Either.Right("File content: \n$content")
        } catch (e: Exception)
        {
            val exception = when (e)
            {
                is FileNotFoundException -> FileNotFound(filePath)
                else -> Unexpected()
            }
            Either.Left(exception)
        }
    }
}
package org.thinker.thinker.infrastructure.dataretrieving.datasources

import android.content.Context
import com.hjq.permissions.Permission
import org.thinker.thinker.domain.dataretriever.DataSource
import org.thinker.thinker.domain.dataretriever.PermissionNotGrantedException
import org.thinker.thinker.domain.utils.Either
import org.thinker.thinker.infrastructure.presentation.XXPermissionsActivity
import java.io.File

class FileContent(private val context: Context, private val filePath: String) : DataSource
{
    override fun retrieveData(): Either<Exception, String>
    {
        val permission = Permission.MANAGE_EXTERNAL_STORAGE
        if (XXPermissionsActivity.isNotGranted(context, permission))
        {
            return Either.Left(PermissionNotGrantedException(permission))
        }

        return try
        {
            val content = File(filePath).readText(Charsets.UTF_8)
            Either.Right(content)
        } catch (e: Exception)
        {
            Either.Left(e)
        }
    }
}
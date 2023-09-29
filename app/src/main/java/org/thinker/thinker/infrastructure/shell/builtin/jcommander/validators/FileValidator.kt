package org.thinker.thinker.infrastructure.shell.builtin.jcommander.validators

import com.beust.jcommander.IParameterValidator
import com.beust.jcommander.ParameterException
import java.io.File

class FileValidator : IParameterValidator
{
    override fun validate(name: String?, value: String?)
    {
        runCatching {
            if (isValidFile(value!!).not()) throw Exception()
        }.onFailure {
            throw ParameterException("Parameter $name should be a valid absolute path (found \"$value\")")
        }
    }

    private fun isValidFile(filePath: String): Boolean
    {
        val file = File(filePath)
        return file.exists() && file.isFile && file.canRead()
    }
}
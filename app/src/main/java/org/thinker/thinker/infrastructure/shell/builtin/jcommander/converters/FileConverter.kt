package org.thinker.thinker.infrastructure.shell.builtin.jcommander.converters

import com.beust.jcommander.IStringConverter
import java.io.File

class FileConverter : IStringConverter<File>
{
    override fun convert(value: String?): File
    {
        return File(value!!)
    }
}
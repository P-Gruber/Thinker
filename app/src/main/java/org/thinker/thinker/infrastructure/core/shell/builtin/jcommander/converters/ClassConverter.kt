package org.thinker.thinker.infrastructure.core.shell.builtin.jcommander.converters

import com.beust.jcommander.IStringConverter

class ClassConverter : IStringConverter<Class<*>>
{
    override fun convert(value: String?): Class<*>
    {
        return Class.forName(value!!)
    }
}
package org.thinker.thinker.infrastructure.core.shell.builtin.jcommander.validators

import com.beust.jcommander.IParameterValidator
import com.beust.jcommander.ParameterException

class GravityValidator : IParameterValidator
{
    override fun validate(name: String?, value: String?)
    {
        runCatching {
            if (isValidGravity(value!!).not()) throw Exception()
        }.onFailure {
            throw ParameterException("Parameter $name should be a valid gravity (found \"$value\")")
        }
    }

    private fun isValidGravity(value: String): Boolean
    {
        return value == "bottom" ||
                value == "top" ||
                value == "left" ||
                value == "right" ||
                value == "center"
    }
}
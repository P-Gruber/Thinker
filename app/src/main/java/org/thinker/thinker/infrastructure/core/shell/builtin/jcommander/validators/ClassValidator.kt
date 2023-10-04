package org.thinker.thinker.infrastructure.core.shell.builtin.jcommander.validators

import com.beust.jcommander.IParameterValidator
import com.beust.jcommander.ParameterException

class ClassValidator : IParameterValidator
{
    override fun validate(name: String?, value: String?)
    {
        runCatching {
            Class.forName(value!!)
        }.onFailure {
            throw ParameterException("Parameter $name should be a valid class (found \"$value\")")
        }
    }
}
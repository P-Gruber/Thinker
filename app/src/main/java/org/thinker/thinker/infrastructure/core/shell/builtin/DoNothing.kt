package org.thinker.thinker.infrastructure.core.shell.builtin

import org.thinker.thinker.domain.shell.Program
import org.thinker.thinker.domain.shell.Shell

class DoNothing : Program
{
    override fun execute(
        args: List<String>,
        stdin: (String) -> Unit,
        stdout: (String) -> Unit,
        stderr: (String) -> Unit
    ): Int
    {
        return Shell.ExitCodes.SUCCESS
    }

    companion object
    {
        const val NAME = "do-nothing"
    }
}
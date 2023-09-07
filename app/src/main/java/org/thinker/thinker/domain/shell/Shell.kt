package org.thinker.thinker.domain.shell

interface Shell
{
    fun interpretInput(
        input: String,
        stdin: (String) -> Unit,
        stdout: (String) -> Unit,
        stderr: (String) -> Unit
    ): Int
}

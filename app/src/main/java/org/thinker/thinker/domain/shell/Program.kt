package org.thinker.thinker.domain.shell

interface Program
{
    fun execute(
        args: List<String>,
        stdin: (String) -> Unit,
        stdout: (String) -> Unit,
        stderr: (String) -> Unit
    ): Int
}
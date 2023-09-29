package org.thinker.thinker.infrastructure.shell

import android.content.Context
import android.widget.Toast
import org.thinker.thinker.domain.shell.Shell

class AndroidShell(private val context: Context) : Shell
{
    override fun interpretInput(
        input: String,
        stdin: (String) -> Unit,
        stdout: (String) -> Unit,
        stderr: (String) -> Unit
    ): Int
    {
        // TODO: Not Yet Implemented
        if (input == "test") Toast.makeText(context, "test", Toast.LENGTH_SHORT).show()
        return 0
    }
}
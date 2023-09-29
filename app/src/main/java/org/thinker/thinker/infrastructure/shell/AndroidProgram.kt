package org.thinker.thinker.infrastructure.shell

import android.content.Context
import android.content.Intent
import org.thinker.thinker.domain.shell.Program
import org.thinker.thinker.domain.shell.Shell

/* Uses app package as path, akin to PATH in Unix-like systems */
class AndroidProgram(private val appPackage: String, private val context: Context) : Program
{
    override fun execute(
        args: List<String>,
        stdin: (String) -> Unit,
        stdout: (String) -> Unit,
        stderr: (String) -> Unit
    ): Int
    {
        // TODO: handle exit codes, exceptions, etc
        val intent = Intent(AndroidShell.EXECUTE_COMMAND_ACTION)
        intent.setPackage(appPackage)
        intent.putExtra("args", args.toTypedArray())
        context.startForegroundService(intent)
        return Shell.ExitCodes.SUCCESS
    }
}
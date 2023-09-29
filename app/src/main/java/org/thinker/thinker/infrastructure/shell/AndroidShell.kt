package org.thinker.thinker.infrastructure.shell

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import org.thinker.thinker.domain.shell.Program
import org.thinker.thinker.domain.shell.Shell
import org.thinker.thinker.infrastructure.shell.builtin.ToastProgram

class AndroidShell(private val context: Context) : Shell
{
    private val packageManager: PackageManager by lazy { context.packageManager }

    private val nameToBuiltinProgram = mutableMapOf<String, Program>()
    private val nameToAndroidProgramCache = mutableMapOf<String, AndroidProgram>()

    override fun interpretInput(
        input: String,
        stdin: (String) -> Unit,
        stdout: (String) -> Unit,
        stderr: (String) -> Unit
    ): Int
    {
        if (input.isBlank()) return Shell.ExitCodes.SUCCESS
        val parsedInput = parseCommand(input)

        val programName = parsedInput.first
        val args = parsedInput.second

        val program = loadProgram(programName)
            ?: return Shell.ExitCodes.COMMAND_NOT_FOUND

        return executeProgram(program, args, stdin, stdout, stderr)
    }

    private fun parseCommand(input: String): Pair<String, List<String>>
    {
        val parts = input.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)".toRegex()).map {
            it.replace("\"", "")
        }.filter { it.isNotBlank() }
        val programName = parts[0]
        val args = parts.drop(1)
        return programName to args
    }

    /**
     * Attempts to find a program by its name in the following order:
     * 1. Searches within built programs.
     * 2. If not found, checks the cached Android programs.
     * 3. If still not found, searches for the Android program by name and caches it if found.
     *
     * @param programName The name of the program to search for.
     * @return The found Program or null if not found.
     */
    private fun loadProgram(programName: String): Program?
    {
        val program = findBuiltinProgramByName(programName)
            ?: findCachedAndroidProgramByName(programName)
            ?: findAndroidProgramByName(programName)?.also {
                cacheAndroidProgram(
                    it,
                    programName
                )
            }
        return program
    }

    private fun findBuiltinProgramByName(programName: String): Program?
    {
        val builtin = nameToBuiltinProgram[programName]
        if (builtin != null) return builtin

        return when (programName)
        {
            ToastProgram.NAME -> ToastProgram(context)
            else -> null
        }?.also {
            nameToBuiltinProgram[programName] = it
        }
    }

    private fun findCachedAndroidProgramByName(programName: String): AndroidProgram?
    {
        return nameToAndroidProgramCache[programName]
    }

    private fun findAndroidProgramByName(appName: String): AndroidProgram?
    {
        val intent = Intent(EXECUTE_COMMAND_ACTION)
        val resolveInfoList =
            packageManager.queryIntentServices(intent, PackageManager.MATCH_ALL)

        val appPackage = resolveInfoList.find {
            val currentAppName = it.loadLabel(packageManager).toString()
            appName == currentAppName
        }?.serviceInfo?.packageName

        return appPackage?.let { AndroidProgram(it, context) }
    }

    private fun cacheAndroidProgram(androidProgram: AndroidProgram, appName: String)
    {
        nameToAndroidProgramCache[appName] = androidProgram
    }

    private fun executeProgram(
        program: Program,
        args: List<String>,
        stdin: (String) -> Unit,
        stdout: (String) -> Unit,
        stderr: (String) -> Unit
    ): Int
    {
        // TODO: Handle errors etc
        val exitCode = program.execute(args, stdin, stdout, stderr)
        return exitCode
    }

    companion object
    {
        const val EXECUTE_COMMAND_ACTION = "org.thinker.EXECUTE_COMMAND"
    }
}
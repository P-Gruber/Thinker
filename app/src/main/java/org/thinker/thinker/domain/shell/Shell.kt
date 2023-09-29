package org.thinker.thinker.domain.shell

interface Shell
{
    fun interpretInput(
        input: String,
        stdin: (String) -> Unit,
        stdout: (String) -> Unit,
        stderr: (String) -> Unit
    ): Int

    object ExitCodes
    {
        // Successful completion of a program.
        const val SUCCESS = 0

        // General errors or unspecified errors
        const val GENERAL_ERROR = 1

        // Misuse of shell built-ins or incorrect command usage
        const val MISUSE = 2

        // General errors or unspecified errors
        const val PARSING_ERROR = 3

        // Command invoked cannot execute (possibly permission problems)
        const val CANNOT_EXECUTE = 126

        // Command not found
        const val COMMAND_NOT_FOUND = 127

        // Invalid exit argument, value greater than 255
        const val INVALID_EXIT = 128
    }
}

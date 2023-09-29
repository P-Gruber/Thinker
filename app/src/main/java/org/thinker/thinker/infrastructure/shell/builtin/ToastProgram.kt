package org.thinker.thinker.infrastructure.shell.builtin

import android.content.Context
import android.widget.Toast
import com.beust.jcommander.IParameterValidator
import com.beust.jcommander.IStringConverter
import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import com.beust.jcommander.ParameterException
import org.thinker.thinker.domain.shell.Program
import org.thinker.thinker.domain.shell.Shell

class ToastProgram(private val context: Context) : Program
{
    override fun execute(
        args: List<String>,
        stdin: (String) -> Unit,
        stdout: (String) -> Unit,
        stderr: (String) -> Unit
    ): Int
    {
        val arguments = Args()
        val jCommander = JCommander.newBuilder()
            .programName(NAME)
            .addObject(arguments)
            .build()
        runCatching {
            jCommander.parse(*args.toTypedArray())
        }.onFailure {
            it.message?.let(stderr)
            return Shell.ExitCodes.PARSING_ERROR
        }

        return arguments.run {
            when
            {
                help ->
                {
                    printHelp(jCommander, stdout)
                    Shell.ExitCodes.SUCCESS
                }

                text != null -> showToast(text!!, duration)

                else -> Shell.ExitCodes.GENERAL_ERROR
            }
        }
    }

    private fun printHelp(
        jCommander: JCommander,
        stdout: (String) -> Unit
    )
    {
        val stringBuilder = StringBuilder()
        jCommander.usageFormatter.usage(stringBuilder)
        stdout(stringBuilder.toString())
    }

    private fun showToast(text: String, duration: Int): Int
    {
        Toast.makeText(context, text, duration).show()
        return Shell.ExitCodes.SUCCESS
    }


    class Args
    {
        @Parameter(names = ["-h", "--help"], description = "Show help", help = true)
        var help = false

        @Parameter(names = ["-t", "--text"], description = "The text of the toast", required = true)
        var text: String? = null

        @Parameter(
            names = ["-d", "--duration"],
            description = "Duration of the toast. \nValid options are: \"SHORT\", \"LONG\"",
            validateWith = [ToastDurationValidator::class],
            converter = ToastDurationConverter::class,
        )
        var duration: Int = Toast.LENGTH_LONG
    }

    class ToastDurationValidator : IParameterValidator
    {
        override fun validate(name: String?, value: String?)
        {
            runCatching {
                if (isValidDuration(value!!).not()) throw Exception()
            }.onFailure {
                throw ParameterException("Parameter $name should be a valid duration (found \"$value\")")
            }
        }

        private fun isValidDuration(value: String): Boolean
        {
            return value == "SHORT" || value == "LONG"
        }

    }

    class ToastDurationConverter : IStringConverter<Int>
    {
        override fun convert(value: String?): Int
        {
            return when (value!!.lowercase())
            {
                "short" -> Toast.LENGTH_SHORT
                else -> Toast.LENGTH_LONG
            }
        }
    }

    companion object
    {
        const val NAME = "toast"
    }
}
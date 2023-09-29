package org.thinker.thinker.infrastructure.shell.builtin

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import com.beust.jcommander.IParameterValidator
import com.beust.jcommander.IStringConverter
import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import com.beust.jcommander.ParameterException
import org.thinker.thinker.domain.shell.Program
import org.thinker.thinker.domain.shell.Shell
import org.thinker.thinker.infrastructure.shell.builtin.jcommander.converters.GravityConverter
import org.thinker.thinker.infrastructure.shell.builtin.jcommander.validators.GravityValidator

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

                text != null && xOffset != null && yOffset != null ->
                    showToast(text!!, duration, gravity, xOffset!!, yOffset!!)

                text != null -> showToast(text!!, duration, gravity, 0, 0)

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

    private fun showToast(
        text: String, duration: Int, gravity: Int, xOffset: Int, yOffset: Int
    ): Int
    {
        val toast = Toast.makeText(context, text, duration)
        toast.setGravity(gravity, xOffset, yOffset)
        toast.show()

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

        @Parameter(
            names = ["-g", "--gravity"],
            description = "Gravity for the toast position.\n" +
                    "Valid options are: \n" +
                    "   \"top\"\n" +
                    "   \"bottom\"\n" +
                    "   \"left\"\n" +
                    "   \"right\"\n" +
                    "   \"center\"\n" +
                    "   \"top-left\"\n" +
                    "   \"top-center\"\n" +
                    "   \"top-right\"\n" +
                    "   \"bottom-left\"\n" +
                    "   \"bottom-right\"\n" +
                    "   \"bottom-center\"\n" +
                    "   \"center-left\"\n" +
                    "   \"center-right\"",
            validateWith = [GravityValidator::class],
            converter = GravityConverter::class
        )
        var gravity: Int = Gravity.BOTTOM or Gravity.CENTER

        @Parameter(names = ["-x"], description = "X-offset for toast position")
        var xOffset: Int? = null

        @Parameter(names = ["-y"], description = "Y-offset for toast position")
        var yOffset: Int? = null
    }

    private class ToastDurationValidator : IParameterValidator
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

    private class ToastDurationConverter : IStringConverter<Int>
    {
        override fun convert(value: String?): Int
        {
            return when (value!!.lowercase())
            {
                "SHORT" -> Toast.LENGTH_SHORT
                else -> Toast.LENGTH_LONG
            }
        }
    }

    companion object
    {
        const val NAME = "toast"
    }
}
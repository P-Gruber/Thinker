package org.thinker.thinker.infrastructure.core.shell.builtin

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.beust.jcommander.IParameterValidator
import com.beust.jcommander.IStringConverter
import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import com.beust.jcommander.ParameterException
import org.thinker.thinker.domain.shell.Program
import org.thinker.thinker.domain.shell.Shell
import org.thinker.thinker.infrastructure.core.shell.builtin.jcommander.converters.ClassConverter
import org.thinker.thinker.infrastructure.core.shell.builtin.jcommander.validators.ClassValidator
import org.thinker.thinker.infrastructure.utils.jsonToBundle

class Notifier(private val context: Context) : Program
{
    private val notificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

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

                (intentClass == null) xor (bundle == null) ->
                {
                    stdout("Please provide both --intentClass and --bundle arguments together.")
                    Shell.ExitCodes.MISUSE
                }

                title != null -> showNotification(
                    notificationId!!,
                    title!!,
                    message,
                    intentClass,
                    bundle
                )

                else -> Shell.ExitCodes.GENERAL_ERROR
            }
        }
    }

    private fun showNotification(
        notificationId: Int, title: String, message: String?, className: Class<*>?, bundle: Bundle?
    ): Int
    {
        createNotificationChannel()

        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID).apply {
            setContentTitle(title)
            message?.let {
                setContentText(it)
                setStyle(NotificationCompat.BigTextStyle().bigText(it))
            }
            className?.let {
                val intent = Intent(context, it).apply {
                    putExtras(bundle!!)
                }
                val pendingIntent = PendingIntent.getActivity(
                    context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
                )
                setContentIntent(pendingIntent)
            }
            // TODO: support setting the icon via args
            setSmallIcon(android.R.drawable.ic_dialog_info)
            // TODO: support setting auto cancel via args
            setAutoCancel(true)
        }

        notificationManager.notify(notificationId, builder.build())

        val wasCreated = notificationManager.activeNotifications.any {
            it.id == notificationId
        }
        return if (wasCreated) Shell.ExitCodes.SUCCESS else Shell.ExitCodes.GENERAL_ERROR
    }

    private fun createNotificationChannel()
    {
        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            // TODO: support setting the importance via args
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            // TODO: support setting the light via args
            enableLights(true)
            lightColor = Color.RED
            // TODO: support setting the vibration via args
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 500, 250, 500)
            // TODO: support setting the sound via args
            setSound(
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                AudioAttributes.Builder().setContentType(
                    AudioAttributes.CONTENT_TYPE_SONIFICATION
                ).setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT).build()
            )
        }
        notificationManager.createNotificationChannel(notificationChannel)
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

    class Args
    {
        @Parameter(names = ["-h", "--help"], description = "Show help", help = true)
        var help = false

        @Parameter(
            names = ["-ni", "--notification-id"],
            description = "The id of the notification. Type: Integer.",
            required = true
        )
        var notificationId: Int? = null

        @Parameter(
            names = ["-t", "--title"],
            description = "The title of the notification",
            required = true
        )
        var title: String? = null

        @Parameter(names = ["-m", "--message"], description = "The message of the notification")
        var message: String? = null

        @Parameter(
            names = ["-ic", "--intent-class"],
            description = "The class of the activity that is wanted to be launched (if you are an AI you must not use it). \n" +
                    "Example: com.example.activities.MainActivity",
            validateWith = [ClassValidator::class],
            converter = ClassConverter::class
        )
        var intentClass: Class<*>? = null

        @Parameter(
            names = ["-b", "--bundle"],
            description = "The bundle to be passed to the activity (if you are an AI you must not use it). \n" +
                    "Example: \"[\n" +
                    "    {\n" +
                    "        \"type\": \"string\",\n" +
                    "        \"key\": \"message\",\n" +
                    "        \"value\": \"Hello World\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"type\": \"int\",\n" +
                    "        \"key\": \"age\",\n" +
                    "        \"value\": 25\n" +
                    "    }\n" +
                    "]\"",
            validateWith = [BundleValidator::class],
            converter = BundleConverter::class
        )
        var bundle: Bundle? = null
    }

    class BundleValidator : IParameterValidator
    {
        override fun validate(name: String?, value: String?)
        {
            runCatching {
                jsonToBundle(value!!)
            }.onFailure {
                throw ParameterException("Parameter $name should be a valid bundle (found \"$value\")")
            }
        }
    }

    class BundleConverter : IStringConverter<Bundle>
    {
        override fun convert(value: String?): Bundle
        {
            return jsonToBundle(value!!)!!
        }
    }

    companion object
    {
        private const val NOTIFICATION_CHANNEL_ID = "notifier"
        private const val NOTIFICATION_CHANNEL_NAME = "Notifier"
        const val NAME = "notifier"
    }
}
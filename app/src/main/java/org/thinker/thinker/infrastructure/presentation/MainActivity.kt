package org.thinker.thinker.infrastructure.presentation

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.thinker.thinker.infrastructure.services.MainService
import org.thinker.thinker.infrastructure.ui.theme.ThinkerTheme

class MainActivity : ComponentActivity()
{
    private lateinit var mainService: MainService
    private var isServiceBound = false

    private val serviceConnection = object : ServiceConnection
    {
        override fun onServiceConnected(className: ComponentName, service: IBinder)
        {
            val binder = service as MainService.LocalBinder
            mainService = binder.getService()
            isServiceBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName)
        {
            isServiceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContent {
            ThinkerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
        startService(Intent(this, MainService::class.java))
    }

    override fun onStart()
    {
        super.onStart()
        bindService(
            Intent(this, MainService::class.java),
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onDestroy()
    {
        super.onDestroy()
        if (isServiceBound)
        {
            unbindService(serviceConnection)
            isServiceBound = false
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier)
{
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview()
{
    ThinkerTheme {
        Greeting("Android")
    }
}
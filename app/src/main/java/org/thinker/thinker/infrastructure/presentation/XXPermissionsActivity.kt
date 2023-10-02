package org.thinker.thinker.infrastructure.presentation

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import org.thinker.thinker.infrastructure.presentation.ui.theme.ThinkerTheme
import org.thinker.thinker.infrastructure.utils.lazyExtra

class XXPermissionsActivity : ComponentActivity()
{
    private val permission by lazyExtra<String>(PERMISSION_KEY)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContent()
        // TODO: instead of automatically asking for permission,
        //  show some explanation and a button to request for the permission
        requestPermission(permission)
    }

    private fun setContent()
    {
        setContent {
            ThinkerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting2("Android")
                }
            }
        }
    }

    private fun requestPermission(permission: String)
    {
        XXPermissions.with(this).permission(permission).request(object : OnPermissionCallback
        {
            override fun onGranted(permissions: MutableList<String>, allGranted: Boolean)
            {
//                finish()
            }

            override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean)
            {
                // Do nothing, the user may read the explanation again
            }
        })
    }

    companion object
    {
        const val PERMISSION_KEY = "PERMISSION"

        fun isNotGranted(context: Context, permission: String): Boolean
        {
            return XXPermissions.isGranted(context, permission).not()
        }
    }
}

@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier)
{
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2()
{
    ThinkerTheme {
        Greeting2("Android")
    }
}
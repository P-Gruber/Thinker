@file:Suppress("unused")

package org.thinker.thinker.infrastructure.utils

import android.app.Activity
import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import android.os.PersistableBundle
import android.util.Log
import android.util.Size
import android.util.SizeF
import org.json.JSONArray
import java.io.Serializable

/** Lazy initialize with intent extra [key] */
inline fun <reified T : Any?> Activity.lazyExtra(key: String) =
    lazy { intent.extras?.get(key) as T }

/** Lazy initialize with intent extra [key] */
//inline fun <reified T : Any?> Fragment.lazyArg(key: String) = lazy { arguments?.get(key) as T }

/*inline fun <reified T : Any?> Fragment.mutableLazyArg(key: String) = object : ReadWriteProperty<Any?, T>
{
    private val lazyValue by lazy { arguments?.get(key) as T }
    private var newValue: T? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>) = newValue ?: lazyValue

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T)
    {
        newValue = value
    }
}*/

/** Create bundle from [extras] */
fun Bundle.putExtras(extras: Array<out Pair<String, Any?>> = emptyArray()) = apply {
    for ((key, value) in extras)
    {
        when (value)
        {
            null -> Unit // poor mans null safety
            is IBinder -> putBinder(key, value)
            is Boolean -> putBoolean(key, value)
            is Bundle -> putBundle(key, value)
            is Byte -> putByte(key, value)
            is Char -> putChar(key, value)
            is CharSequence -> putCharSequence(key, value)
            is Double -> putDouble(key, value)
            is Float -> putFloat(key, value)
            is Int -> putInt(key, value)
            is Long -> putLong(key, value)
            is Parcelable -> putParcelable(key, value)
            is Short -> putShort(key, value)
            is Size -> putSize(key, value)
            is SizeF -> putSizeF(key, value)
            is String -> putString(key, value)
            is Serializable ->
            {
                Log.w(
                    "BundleHelper.kt",
                    "Warning: using Serializable for bundling value of class ${value.javaClass}"
                )
                putSerializable(key, value)
            }

            is PersistableBundle -> putAll(value)
            else -> throw IllegalArgumentException("Cannot put to bundle, unsupported type: ${value.javaClass}")
        }
    }
}

fun jsonToBundle(json: String): Bundle?
{
    return runCatching {
        val jsonArray = JSONArray(json)
        val bundle = Bundle()
        for (i in 0 until jsonArray.length())
        {
            val jsonObject = jsonArray.getJSONObject(i)
            when (jsonObject.getString("type"))
            {
                "string" -> bundle.putString(
                    jsonObject.getString("key"),
                    jsonObject.getString("value")
                )

                "int" -> bundle.putInt(jsonObject.getString("key"), jsonObject.getInt("value"))
                // Add more types as needed
            }
        }
        bundle
    }.getOrNull()
}

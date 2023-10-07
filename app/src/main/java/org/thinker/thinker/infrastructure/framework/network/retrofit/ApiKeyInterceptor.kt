package org.thinker.thinker.infrastructure.framework.network.retrofit

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor(private val context: Context) : Interceptor
{
    override fun intercept(chain: Interceptor.Chain): Response
    {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val sharedPreferences = EncryptedSharedPreferences.create(
            context,
            "secret_shared_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        // TODO: Ask for the api key in runtime (ensure we deactivate this token before the code is publicised)
        val apiKey = sharedPreferences.getString(
            "API_KEY",
            "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"
        )

        val requestWithApiKey = chain.request().newBuilder()
            .header("Authorization", "Bearer $apiKey")
            .build()

        return chain.proceed(requestWithApiKey)
    }
}

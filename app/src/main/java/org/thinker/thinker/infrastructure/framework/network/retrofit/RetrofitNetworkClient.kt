package org.thinker.thinker.infrastructure.framework.network.retrofit

import okhttp3.OkHttpClient
import org.thinker.thinker.MyApplication
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitNetworkClient
{
    private const val TIMEOUT_IN_SECONDS = 10L

    private var retrofit: Retrofit? = null

    val retrofitClient: Retrofit
        get()
        {
            if (retrofit == null)
            {
                val okHttpClientBuilder = OkHttpClient.Builder()
                    .addInterceptor(ApiKeyInterceptor(MyApplication.applicationContext()))
                    .connectTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                    .build()
                retrofit = Retrofit.Builder()
                    .baseUrl("https://api.openai.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClientBuilder)
                    .build()
            }
            return retrofit!!
        }
}
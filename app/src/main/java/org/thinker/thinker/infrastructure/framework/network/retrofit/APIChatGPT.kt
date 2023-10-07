package org.thinker.thinker.infrastructure.framework.network.retrofit

import org.thinker.thinker.infrastructure.data.models.GPTRequest
import org.thinker.thinker.infrastructure.data.models.GPTResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface APIChatGPT
{
    @POST("https://api.openai.com/v1/completions")
    suspend fun getGPTResponse(@Body request: GPTRequest): Response<GPTResponse>

}
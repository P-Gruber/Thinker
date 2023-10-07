package org.thinker.thinker.infrastructure.data.remote

import org.thinker.thinker.domain.repository.NLPModelException
import org.thinker.thinker.domain.utils.Either
import org.thinker.thinker.infrastructure.data.ChatGPTDataSource
import org.thinker.thinker.infrastructure.data.models.GPTRequest
import org.thinker.thinker.infrastructure.data.models.GPTResponse
import org.thinker.thinker.infrastructure.framework.network.retrofit.APIChatGPT
import org.thinker.thinker.infrastructure.framework.network.retrofit.RetrofitNetworkClient
import org.thinker.thinker.infrastructure.utils.kextensions.printStackTraceIfDebugging
import retrofit2.Response

class ChatGptApi : ChatGPTDataSource
{
    private val api by lazy { RetrofitNetworkClient.retrofitClient.create(APIChatGPT::class.java) }
    override suspend fun getResponse(prompt: String): Either<NLPModelException, String>
    {
        return runCatching {
            val response = api.getGPTResponse(GPTRequest(prompt))
            val text = response.body()?.choices?.first()?.text?.trim()
            if (response.isSuccessful.not())
            {
                Throwable(response.toString()).printStackTraceIfDebugging()
                response.mapErrorCode()
            } else if (text == null) Either.Left(NLPModelException.Unexpected())
            else Either.Right(text)
        }.getOrElse {
            it.printStackTraceIfDebugging()
            Either.Left(NLPModelException.NoInternet())
        }
    }

    private fun Response<GPTResponse>.mapErrorCode(): Either<NLPModelException, String>
    {
        return Either.Left(
            when (this.code())
            {
                401 -> NLPModelException.Unauthorized()
                403 -> NLPModelException.Forbidden()
                429 -> NLPModelException.TooManyRequests()
                in 400..499 -> NLPModelException.Client()
                in 500..599 -> NLPModelException.Server()
                else -> NLPModelException.Unexpected()
            }
        )
    }
}
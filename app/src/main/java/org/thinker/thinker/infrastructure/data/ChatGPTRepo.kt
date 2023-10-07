package org.thinker.thinker.infrastructure.data

import org.thinker.thinker.domain.repository.NLPModelException
import org.thinker.thinker.domain.repository.NLPModelRepo
import org.thinker.thinker.domain.utils.Either

class ChatGPTRepo(private val chatGPTDataSource: ChatGPTDataSource) : NLPModelRepo
{
    override suspend fun getResponse(prompt: String): Either<NLPModelException, String>
    {
        return chatGPTDataSource.getResponse(prompt)
    }
}
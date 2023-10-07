package org.thinker.thinker.infrastructure.data

import org.thinker.thinker.domain.repository.NLPModelException
import org.thinker.thinker.domain.utils.Either

interface ChatGPTDataSource
{
    suspend fun getResponse(prompt: String): Either<NLPModelException, String>
}

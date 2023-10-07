package org.thinker.thinker.domain.repository

import org.thinker.thinker.domain.utils.Either

interface NLPModelRepo
{
    suspend fun getResponse(prompt: String): Either<NLPModelException, String>
}
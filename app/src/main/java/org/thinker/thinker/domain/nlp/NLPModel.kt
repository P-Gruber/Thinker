package org.thinker.thinker.domain.nlp

import org.thinker.thinker.domain.utils.Either

interface NLPModel
{
    fun submitPrompt(prompt: String): Either<Exception, String>
}
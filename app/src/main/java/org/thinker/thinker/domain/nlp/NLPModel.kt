package org.thinker.thinker.domain.nlp

interface NLPModel
{
    fun submitPrompt(prompt: String): String
}
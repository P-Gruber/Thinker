package org.thinker.thinker.infrastructure.data.models

data class GPTRequest(
    val prompt: String,
    val model: String = "gpt-3.5-turbo-instruct",
    val temperature: Int = 0,
    val max_tokens: Int = 150
)

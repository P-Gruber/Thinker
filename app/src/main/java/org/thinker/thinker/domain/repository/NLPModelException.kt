package org.thinker.thinker.domain.repository

sealed class NLPModelException : Exception()
{
    class NoInternet : NLPModelException()

    //  This happens when there's no API key provided, or the provided API key is invalid.
    class Unauthorized : NLPModelException()

    // It indicates that you've reached your rate limit or usage limit for the API.
    class Forbidden : NLPModelException()

    // It indicates that you've reached your rate limit or usage limit for the API.
    class TooManyRequests : NLPModelException()

    /** This often indicates an issue with the input data. It could mean:
    - The request is missing a model name.
    - The content is too long.
    - The temperature value is out of range.
    - The specified model does not exist.
    - The request size exceeds the limit.
    ... among other input issues. */
    class Client : NLPModelException()

    class Server : NLPModelException()

    class Unexpected : NLPModelException()

}

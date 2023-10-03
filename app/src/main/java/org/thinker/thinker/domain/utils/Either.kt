package org.thinker.thinker.domain.utils

sealed class Either<out L, out R>
{
    data class Left<out L>(val value: L) : Either<L, Nothing>()
    data class Right<out R>(val value: R) : Either<Nothing, R>()
    {
        override fun getRightValue() = value
    }

    inline fun <T> fold(onLeft: (L) -> T, onRight: (R) -> T): T = when (this)
    {
        is Left -> onLeft(value)
        is Right -> onRight(value)
    }

    fun getLeftValue() = (this as? Left)?.value
    open fun getRightValue() = (this as? Right)?.value

    /** @return null if it is Left, so that kotlin "?:" elvis operator can be chained. */
    inline fun rightOrNullAndRun(block: Left<L>.() -> Unit): R?
    {
        return if (this is Left)
        {
            block.invoke(this)
            null
        } else (this as Right).value
    }

    val isLeft: Boolean get() = this is Left
    val isRight: Boolean get() = this is Right
}
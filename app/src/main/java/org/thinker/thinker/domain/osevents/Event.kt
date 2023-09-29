package org.thinker.thinker.domain.osevents

sealed class Event(keyName: String)
{
    sealed class Screen(keyName: String) : Event(keyName)
    {
        class TurnedOn : Screen("ScreenTurnedOn")
        {
            override fun equals(other: Any?): Boolean
            {
                return other is TurnedOn
            }

            override fun hashCode(): Int
            {
                return javaClass.hashCode()
            }
        }
    }
}

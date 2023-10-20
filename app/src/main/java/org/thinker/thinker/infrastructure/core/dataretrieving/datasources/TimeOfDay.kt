package org.thinker.thinker.infrastructure.core.dataretrieving.datasources

import org.thinker.thinker.domain.dataretriever.DataRetrieverException
import org.thinker.thinker.domain.dataretriever.DataSource
import org.thinker.thinker.domain.utils.Either
import java.time.LocalTime

class TimeOfDay : DataSource
{
    override fun retrieveData(): Either<DataRetrieverException, String>
    {
        val currentTime = LocalTime.now()
        val currentHour = currentTime.hour
        val currentMinute = currentTime.minute
        val currentSecond = currentTime.second
        val timeOfDay = "$currentHour:$currentMinute:$currentSecond"
        return Either.Right("Current time: $timeOfDay")
    }
}
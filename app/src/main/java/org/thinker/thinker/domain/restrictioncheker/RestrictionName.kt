package org.thinker.thinker.domain.restrictioncheker

sealed class RestrictionName(val keyName: String)
{
    data class BatteryLowerThan(val value: Int) : RestrictionName("BatteryLowerThan")
}

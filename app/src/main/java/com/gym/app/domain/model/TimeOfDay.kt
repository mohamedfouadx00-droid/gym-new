package com.gym.app.domain.model

/**
 * PHASE 01D — CORE USER MODELS
 *
 * A minimal, platform-independent representation of a wall-clock time of
 * day (hour + minute), used for preferences such as preferred workout time,
 * sleep time, and wake time.
 *
 * This intentionally avoids any Android or java.time dependency inside the
 * domain layer, since the domain must stay independent from
 * Room/DataStore/Android framework types. [hour] and [minute] are plain
 * Kotlin [Int]s, validated at construction time.
 */
data class TimeOfDay(
    val hour: Int,
    val minute: Int
) {
    init {
        require(hour in 0..23) { "Hour must be between 0 and 23, was $hour" }
        require(minute in 0..59) { "Minute must be between 0 and 59, was $minute" }
    }

    companion object {
        /** Creates a [TimeOfDay] only if [hour] and [minute] are valid, otherwise null. */
        fun ofOrNull(hour: Int, minute: Int): TimeOfDay? =
            if (hour in 0..23 && minute in 0..59) TimeOfDay(hour, minute) else null
    }
}

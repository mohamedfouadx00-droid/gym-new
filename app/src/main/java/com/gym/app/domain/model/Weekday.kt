package com.gym.app.domain.model

/**
 * PHASE 01D — CORE USER MODELS
 *
 * Days of the week, used by [UserPreferences] for preferred workout days
 * and rest days. A dedicated domain enum avoids depending on any
 * Android/JVM date-time framework type inside the domain layer.
 */
enum class Weekday {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY
}

package com.gym.app.domain.model

/**
 * PHASE 01D — CORE USER MODELS
 *
 * Preferred measurement system for displaying weight/height/etc. Purely a
 * user preference — does not change how domain values are stored
 * internally (always metric, see [UserProfile]).
 */
enum class UnitSystem {
    METRIC,
    IMPERIAL
}

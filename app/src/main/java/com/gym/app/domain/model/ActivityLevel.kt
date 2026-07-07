package com.gym.app.domain.model

/**
 * PHASE 01D — CORE USER MODELS
 *
 * General daily activity level outside of structured training, commonly
 * used later as an input for energy expenditure calculations (e.g. TDEE).
 */
enum class ActivityLevel {
    SEDENTARY,
    LIGHTLY_ACTIVE,
    MODERATELY_ACTIVE,
    VERY_ACTIVE,
    EXTRA_ACTIVE
}

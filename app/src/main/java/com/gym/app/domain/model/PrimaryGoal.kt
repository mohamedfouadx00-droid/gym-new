package com.gym.app.domain.model

/**
 * PHASE 01D — CORE USER MODELS
 *
 * The main goal a user is pursuing. Drives which future feature logic
 * (workout, nutrition, etc.) applies — no such logic is implemented yet.
 */
enum class PrimaryGoal {
    WEIGHT_GAIN,
    WEIGHT_LOSS,
    MUSCLE_BUILDING,
    STRENGTH,
    MAINTENANCE,
    GENERAL_FITNESS
}

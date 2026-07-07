package com.gym.app.domain.model

/**
 * PHASE 01D — CORE USER MODELS
 *
 * Central domain model describing a single user's physical profile. This
 * is part of the core product model (User Profile + Goal + Preferences)
 * that every future feature (Workout, Nutrition, Recovery, Home, ...) must
 * read from instead of hardcoding or duplicating user-specific data.
 *
 * Design notes:
 * - Immutable ([data class] + `val` only).
 * - No Room/DataStore annotations, no Android framework types.
 * - No persistence and no default "fake" personal values — every value
 *   must be supplied explicitly by whoever constructs this model.
 * - [userId] exists so the app can later support multiple/local profiles,
 *   login, and cloud sync without redesigning this model.
 * - Height and weight are stored in metric units (centimeters, kilograms)
 *   as the single internal source of truth; [UserPreferences.unitSystem]
 *   only affects how values are *displayed*, not how they are stored here.
 *
 * @property userId Stable identifier of the user this profile belongs to.
 * @property name Display name of the user.
 * @property age Age in whole years.
 * @property gender Biological sex, used for calculation purposes only.
 *   Nullable because it is only needed for certain calculations and a user
 *   may choose not to provide it.
 * @property heightCm Height in centimeters.
 * @property currentWeightKg Current body weight in kilograms.
 * @property experienceLevel General training experience level.
 * @property activityLevel General daily activity level (outside training).
 */
data class UserProfile(
    val userId: String,
    val name: String,
    val age: Int,
    val gender: Gender?,
    val heightCm: Double,
    val currentWeightKg: Double,
    val experienceLevel: ExperienceLevel,
    val activityLevel: ActivityLevel
) {
    init {
        require(userId.isNotBlank()) { "userId must not be blank" }
        require(name.isNotBlank()) { "name must not be blank" }
        require(isValidAge(age)) { "age must be between $MIN_AGE and $MAX_AGE, was $age" }
        require(isValidHeight(heightCm)) { "heightCm must be positive and realistic, was $heightCm" }
        require(isValidWeight(currentWeightKg)) { "currentWeightKg must be positive and realistic, was $currentWeightKg" }
    }

    companion object {
        const val MIN_AGE = 10
        const val MAX_AGE = 100
        const val MIN_HEIGHT_CM = 50.0
        const val MAX_HEIGHT_CM = 260.0
        const val MIN_WEIGHT_KG = 20.0
        const val MAX_WEIGHT_KG = 400.0

        /** Whether [age] is a plausible age for this application's audience. */
        fun isValidAge(age: Int): Boolean = age in MIN_AGE..MAX_AGE

        /** Whether [heightCm] is a positive, realistic human height. */
        fun isValidHeight(heightCm: Double): Boolean = heightCm in MIN_HEIGHT_CM..MAX_HEIGHT_CM

        /** Whether [weightKg] is a positive, realistic human body weight. */
        fun isValidWeight(weightKg: Double): Boolean = weightKg in MIN_WEIGHT_KG..MAX_WEIGHT_KG
    }
}

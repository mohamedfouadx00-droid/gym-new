package com.gym.app.domain.model

/**
 * PHASE 01D — CORE USER MODELS
 *
 * Central domain model describing a single user's active goal. Part of the
 * core product model (User Profile + Goal + Preferences).
 *
 * Design notes:
 * - Immutable ([data class] + `val` only).
 * - No Room/DataStore annotations, no Android framework types.
 * - Dates are represented as epoch-day [Long] values (days since
 *   1970-01-01), a simple platform-independent representation that avoids
 *   pulling java.time or Android date types into the domain layer while
 *   still being trivially convertible to/from them at the edges later.
 * - [targetWeightKg] only makes sense for certain goals (e.g. Weight Gain,
 *   Weight Loss); [isTargetWeightApplicable] documents that rule explicitly
 *   rather than leaving it implicit.
 *
 * @property goalId Stable identifier of this goal.
 * @property userId Identifier of the [UserProfile] this goal belongs to.
 * @property primaryGoal The main goal being pursued.
 * @property targetWeightKg Target body weight in kilograms, when applicable
 *   to [primaryGoal] (see [isTargetWeightApplicable]). Null otherwise.
 * @property goalStartDate Epoch day the goal was started.
 * @property targetDate Optional epoch day the user is aiming to reach the
 *   goal by.
 * @property secondaryGoals Additional, non-primary goals the user is also
 *   pursuing alongside [primaryGoal].
 */
data class Goal(
    val goalId: String,
    val userId: String,
    val primaryGoal: PrimaryGoal,
    val targetWeightKg: Double?,
    val goalStartDate: Long,
    val targetDate: Long?,
    val secondaryGoals: List<PrimaryGoal> = emptyList()
) {
    init {
        require(goalId.isNotBlank()) { "goalId must not be blank" }
        require(userId.isNotBlank()) { "userId must not be blank" }
        if (targetWeightKg != null) {
            require(isValidTargetWeight(targetWeightKg)) {
                "targetWeightKg must be positive and realistic, was $targetWeightKg"
            }
        }
        if (targetDate != null) {
            require(targetDate >= goalStartDate) {
                "targetDate ($targetDate) must not be before goalStartDate ($goalStartDate)"
            }
        }
        require(primaryGoal !in secondaryGoals) {
            "primaryGoal must not be duplicated inside secondaryGoals"
        }
    }

    /**
     * Whether [targetWeightKg] is meaningful for this goal's [primaryGoal].
     * A target weight naturally applies to weight-oriented goals; goals
     * like Strength or General Fitness don't require one.
     */
    fun isTargetWeightApplicable(): Boolean = isTargetWeightApplicable(primaryGoal)

    companion object {
        const val MIN_TARGET_WEIGHT_KG = 20.0
        const val MAX_TARGET_WEIGHT_KG = 400.0

        private val TARGET_WEIGHT_APPLICABLE_GOALS = setOf(
            PrimaryGoal.WEIGHT_GAIN,
            PrimaryGoal.WEIGHT_LOSS,
            PrimaryGoal.MUSCLE_BUILDING
        )

        /** Whether a target weight is naturally meaningful for [goal]. */
        fun isTargetWeightApplicable(goal: PrimaryGoal): Boolean =
            goal in TARGET_WEIGHT_APPLICABLE_GOALS

        /** Whether [targetWeightKg] is a positive, realistic body weight. */
        fun isValidTargetWeight(targetWeightKg: Double): Boolean =
            targetWeightKg in MIN_TARGET_WEIGHT_KG..MAX_TARGET_WEIGHT_KG
    }
}

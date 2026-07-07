package com.gym.app.domain.model

/**
 * PHASE 01D — CORE USER MODELS
 *
 * Central domain model describing a single user's preferences. Part of the
 * core product model (User Profile + Goal + Preferences).
 *
 * Design notes:
 * - Immutable ([data class] + `val` only).
 * - No Room/DataStore annotations, no Android framework types.
 * - Only fields that are meaningful at the domain level are included;
 *   things like UI theme or language are deliberately excluded (the app is
 *   Arabic-only with a single fixed layout direction, so there is no
 *   domain-level language preference).
 * - [reminderPreferences] is a small dedicated domain value rather than a
 *   generic Boolean/Int bag, since "which reminders are enabled" is a
 *   genuinely structured concept, not primitive obsession.
 *
 * @property userId Identifier of the [UserProfile] these preferences
 *   belong to.
 * @property workoutLocation Where the user primarily trains.
 * @property preferredWorkoutDays Days of the week the user prefers to train.
 * @property restDays Days of the week the user prefers to rest.
 * @property preferredWorkoutTime Preferred time of day to train, if any.
 * @property availableEquipment Free-form list of equipment names the user
 *   has access to. Kept as simple strings at the domain level since a full
 *   equipment catalog/enum is a future-feature concern (Workout), not part
 *   of this foundational phase.
 * @property preferredMealCount Preferred number of meals per day.
 * @property sleepTime Preferred/typical bedtime, if known.
 * @property wakeTime Preferred/typical wake-up time, if known.
 * @property budgetLevel General budget preference.
 * @property enabledSupplements Free-form list of supplement names the user
 *   is open to/currently using. Kept as simple strings for the same reason
 *   as [availableEquipment].
 * @property reminderPreferences Which categories of reminders are enabled.
 * @property unitSystem Preferred unit system for displaying values.
 */
data class UserPreferences(
    val userId: String,
    val workoutLocation: WorkoutLocation,
    val preferredWorkoutDays: Set<Weekday> = emptySet(),
    val restDays: Set<Weekday> = emptySet(),
    val preferredWorkoutTime: TimeOfDay?,
    val availableEquipment: List<String> = emptyList(),
    val preferredMealCount: Int,
    val sleepTime: TimeOfDay?,
    val wakeTime: TimeOfDay?,
    val budgetLevel: BudgetLevel,
    val enabledSupplements: List<String> = emptyList(),
    val reminderPreferences: ReminderPreferences = ReminderPreferences(),
    val unitSystem: UnitSystem
) {
    init {
        require(userId.isNotBlank()) { "userId must not be blank" }
        require(preferredWorkoutDays.intersect(restDays).isEmpty()) {
            "A day cannot be both a preferred workout day and a rest day: " +
                "${preferredWorkoutDays.intersect(restDays)}"
        }
        require(isValidMealCount(preferredMealCount)) {
            "preferredMealCount must be between $MIN_MEAL_COUNT and $MAX_MEAL_COUNT, " +
                "was $preferredMealCount"
        }
    }

    companion object {
        const val MIN_MEAL_COUNT = 1
        const val MAX_MEAL_COUNT = 10

        /** Whether [mealCount] is a realistic number of meals per day. */
        fun isValidMealCount(mealCount: Int): Boolean = mealCount in MIN_MEAL_COUNT..MAX_MEAL_COUNT
    }
}

/**
 * Which categories of reminders are enabled for the user. Deliberately
 * small and flat — this is a preference, not a scheduling/alarm system
 * (alarms/notifications are explicitly out of scope for this phase).
 */
data class ReminderPreferences(
    val workoutReminders: Boolean = false,
    val mealReminders: Boolean = false,
    val sleepReminders: Boolean = false,
    val hydrationReminders: Boolean = false
)

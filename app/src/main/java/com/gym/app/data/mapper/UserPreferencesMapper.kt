package com.gym.app.data.mapper

import com.gym.app.data.local.entity.UserPreferencesEntity
import com.gym.app.domain.model.BudgetLevel
import com.gym.app.domain.model.ReminderPreferences
import com.gym.app.domain.model.TimeOfDay
import com.gym.app.domain.model.UnitSystem
import com.gym.app.domain.model.UserPreferences
import com.gym.app.domain.model.Weekday
import com.gym.app.domain.model.WorkoutLocation

/**
 * PHASE 01E — ROOM DATABASE FOUNDATION
 *
 * Pure mapping functions between [UserPreferences] (domain, which also
 * embeds [ReminderPreferences]) and [UserPreferencesEntity] (Room
 * persistence).
 *
 * [Weekday] sets and free-form string lists are stored as a single
 * delimited [String] column on the entity (see [UserPreferencesEntity]);
 * the encode/decode helpers live here rather than as Room
 * [androidx.room.TypeConverter]s because they only apply within this
 * single mapper and are simpler kept local.
 */

private const val DELIMITER = ","

private fun Set<Weekday>.encode(): String = joinToString(separator = DELIMITER) { it.name }

private fun String.decodeWeekdays(): Set<Weekday> =
    if (isEmpty()) emptySet() else split(DELIMITER).map { Weekday.valueOf(it) }.toSet()

private fun List<String>.encode(): String = joinToString(separator = DELIMITER)

private fun String.decodeStringList(): List<String> =
    if (isEmpty()) emptyList() else split(DELIMITER)

/** Converts this domain [UserPreferences] into its Room [UserPreferencesEntity] representation. */
fun UserPreferences.toEntity(): UserPreferencesEntity = UserPreferencesEntity(
    userId = userId,
    workoutLocation = workoutLocation.name,
    preferredWorkoutDays = preferredWorkoutDays.encode(),
    restDays = restDays.encode(),
    preferredWorkoutTimeHour = preferredWorkoutTime?.hour,
    preferredWorkoutTimeMinute = preferredWorkoutTime?.minute,
    availableEquipment = availableEquipment.encode(),
    preferredMealCount = preferredMealCount,
    sleepTimeHour = sleepTime?.hour,
    sleepTimeMinute = sleepTime?.minute,
    wakeTimeHour = wakeTime?.hour,
    wakeTimeMinute = wakeTime?.minute,
    budgetLevel = budgetLevel.name,
    enabledSupplements = enabledSupplements.encode(),
    workoutReminders = reminderPreferences.workoutReminders,
    mealReminders = reminderPreferences.mealReminders,
    sleepReminders = reminderPreferences.sleepReminders,
    hydrationReminders = reminderPreferences.hydrationReminders,
    unitSystem = unitSystem.name
)

/** Converts this Room [UserPreferencesEntity] back into the domain [UserPreferences] model. */
fun UserPreferencesEntity.toDomain(): UserPreferences {
    val preferredWorkoutTime = TimeOfDay.ofOrNull(
        preferredWorkoutTimeHour ?: -1,
        preferredWorkoutTimeMinute ?: -1
    )
    val sleepTime = TimeOfDay.ofOrNull(sleepTimeHour ?: -1, sleepTimeMinute ?: -1)
    val wakeTime = TimeOfDay.ofOrNull(wakeTimeHour ?: -1, wakeTimeMinute ?: -1)

    return UserPreferences(
        userId = userId,
        workoutLocation = WorkoutLocation.valueOf(workoutLocation),
        preferredWorkoutDays = preferredWorkoutDays.decodeWeekdays(),
        restDays = restDays.decodeWeekdays(),
        preferredWorkoutTime = preferredWorkoutTime,
        availableEquipment = availableEquipment.decodeStringList(),
        preferredMealCount = preferredMealCount,
        sleepTime = sleepTime,
        wakeTime = wakeTime,
        budgetLevel = BudgetLevel.valueOf(budgetLevel),
        enabledSupplements = enabledSupplements.decodeStringList(),
        reminderPreferences = ReminderPreferences(
            workoutReminders = workoutReminders,
            mealReminders = mealReminders,
            sleepReminders = sleepReminders,
            hydrationReminders = hydrationReminders
        ),
        unitSystem = UnitSystem.valueOf(unitSystem)
    )
}

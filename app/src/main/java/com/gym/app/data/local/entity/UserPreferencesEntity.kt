package com.gym.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * PHASE 01E — ROOM DATABASE FOUNDATION
 *
 * Room persistence model for [com.gym.app.domain.model.UserPreferences]
 * (which also embeds [com.gym.app.domain.model.ReminderPreferences]).
 *
 * [userId] is the primary key: this foundational phase models preferences
 * as a single row per user (a 1:1 relationship with [UserProfileEntity]),
 * which matches how the domain model is used today. Nothing here prevents
 * evolving this later if a real multi-preference-set feature emerges.
 *
 * Collections ([preferredWorkoutDays], [restDays], [availableEquipment],
 * [enabledSupplements]) are stored as single delimited [String] columns via
 * `UserPreferencesTypeConverters`. They are simple, small, unordered-enough
 * collections with no independent identity per element, so a delimited
 * string is a deliberately simple, stable choice for this foundational
 * phase rather than introducing extra join tables.
 *
 * `preferredWorkoutTime`, `sleepTime`, and `wakeTime` are each flattened
 * into a nullable `...Hour`/`...Minute` [Int] pair rather than a single
 * encoded string, so the stored values stay simple primitives that are
 * trivially queryable/sortable if ever needed, without any custom encoding.
 *
 * `reminderPreferences` (a small, fixed-shape nested value in the domain
 * model) is flattened into four plain [Boolean] columns rather than stored
 * as JSON, since it has a fixed, known set of fields today and flattening
 * keeps the schema simple and directly queryable.
 */
@Entity(tableName = "user_preferences")
data class UserPreferencesEntity(
    @PrimaryKey
    val userId: String,
    val workoutLocation: String,
    val preferredWorkoutDays: String,
    val restDays: String,
    val preferredWorkoutTimeHour: Int?,
    val preferredWorkoutTimeMinute: Int?,
    val availableEquipment: String,
    val preferredMealCount: Int,
    val sleepTimeHour: Int?,
    val sleepTimeMinute: Int?,
    val wakeTimeHour: Int?,
    val wakeTimeMinute: Int?,
    val budgetLevel: String,
    val enabledSupplements: String,
    val workoutReminders: Boolean,
    val mealReminders: Boolean,
    val sleepReminders: Boolean,
    val hydrationReminders: Boolean,
    val unitSystem: String
)

package com.gym.app.data.local.converters

import androidx.room.TypeConverter

/**
 * PHASE 01E — ROOM DATABASE FOUNDATION
 *
 * Converts [com.gym.app.data.local.entity.GoalEntity.secondaryGoals]
 * between a `List<String>` (enum names) and a single delimited [String]
 * column, since Room cannot persist a `List<String>` column directly.
 *
 * A comma is safe as a delimiter here because every stored value is an
 * enum `name` (`PrimaryGoal.name`), which by Kotlin's rules can never
 * contain a comma.
 */
class GoalTypeConverters {

    @TypeConverter
    fun fromSecondaryGoals(secondaryGoals: List<String>): String =
        secondaryGoals.joinToString(separator = DELIMITER)

    @TypeConverter
    fun toSecondaryGoals(value: String): List<String> =
        if (value.isEmpty()) emptyList() else value.split(DELIMITER)

    companion object {
        private const val DELIMITER = ","
    }
}

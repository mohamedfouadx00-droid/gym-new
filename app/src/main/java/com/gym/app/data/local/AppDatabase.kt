package com.gym.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gym.app.data.local.converters.GoalTypeConverters
import com.gym.app.data.local.dao.GoalDao
import com.gym.app.data.local.dao.UserPreferencesDao
import com.gym.app.data.local.dao.UserProfileDao
import com.gym.app.data.local.entity.GoalEntity
import com.gym.app.data.local.entity.UserPreferencesEntity
import com.gym.app.data.local.entity.UserProfileEntity

/**
 * PHASE 01E — ROOM DATABASE FOUNDATION
 *
 * The single Room database for the app, covering exactly the three core
 * product models: User Profile, Goal, and User Preferences. No other
 * entities belong here yet — future features (Workout history, Sleep
 * logs, Pain logs, etc., per PROJECT_HANDOFF.md) will add their own
 * entities in later phases, not this one.
 *
 * `version = 1` since this is the first schema. `exportSchema = false` is
 * a deliberate, explicit choice for this foundational phase (no schema
 * history location has been set up yet); this can be revisited once real
 * migrations are needed.
 */
@Database(
    entities = [
        UserProfileEntity::class,
        GoalEntity::class,
        UserPreferencesEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(GoalTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userProfileDao(): UserProfileDao
    abstract fun goalDao(): GoalDao
    abstract fun userPreferencesDao(): UserPreferencesDao

    companion object {
        const val DATABASE_NAME = "gym_database"
    }
}

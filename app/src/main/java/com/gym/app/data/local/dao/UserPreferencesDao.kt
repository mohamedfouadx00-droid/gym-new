package com.gym.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.gym.app.data.local.entity.UserPreferencesEntity
import kotlinx.coroutines.flow.Flow

/**
 * PHASE 01E — ROOM DATABASE FOUNDATION
 *
 * Basic persistence operations for [UserPreferencesEntity]. This
 * foundational phase models one preferences row per user.
 */
@Dao
interface UserPreferencesDao {

    /** Inserts new preferences or replaces the existing row for the same [UserPreferencesEntity.userId]. */
    @Upsert
    suspend fun upsert(userPreferences: UserPreferencesEntity)

    /** Returns the preferences for [userId], or null if none exist yet. */
    @Query("SELECT * FROM user_preferences WHERE userId = :userId LIMIT 1")
    suspend fun getByUserId(userId: String): UserPreferencesEntity?

    /** Observes the preferences for [userId] as they change over time. */
    @Query("SELECT * FROM user_preferences WHERE userId = :userId LIMIT 1")
    fun observeByUserId(userId: String): Flow<UserPreferencesEntity?>

    /** Deletes the preferences for [userId], if any exist. */
    @Query("DELETE FROM user_preferences WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String)
}

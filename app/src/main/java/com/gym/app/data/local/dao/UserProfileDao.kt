package com.gym.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.gym.app.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

/**
 * PHASE 01E — ROOM DATABASE FOUNDATION
 *
 * Basic persistence operations for [UserProfileEntity]. Deliberately
 * minimal: only what the database foundation itself needs to prove
 * correct persistence, not a full feature repository.
 */
@Dao
interface UserProfileDao {

    /** Inserts a new profile or replaces the existing one for the same [UserProfileEntity.userId]. */
    @Upsert
    suspend fun upsert(userProfile: UserProfileEntity)

    /** Returns the profile for [userId], or null if none exists yet. */
    @Query("SELECT * FROM user_profiles WHERE userId = :userId LIMIT 1")
    suspend fun getByUserId(userId: String): UserProfileEntity?

    /** Observes the profile for [userId] as it changes over time. */
    @Query("SELECT * FROM user_profiles WHERE userId = :userId LIMIT 1")
    fun observeByUserId(userId: String): Flow<UserProfileEntity?>

    /** Deletes the profile for [userId], if one exists. */
    @Query("DELETE FROM user_profiles WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String)
}

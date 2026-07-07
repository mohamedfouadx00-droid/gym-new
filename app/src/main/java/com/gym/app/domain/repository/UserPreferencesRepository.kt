package com.gym.app.domain.repository

import com.gym.app.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

/**
 * PHASE 01F — USER REPOSITORIES
 *
 * Domain-facing contract for reading and writing a user's
 * [UserPreferences]. Exposes domain models only — no Room entities.
 * Implementations (Phase 01F: [com.gym.app.data.repository
 * .UserPreferencesRepositoryImpl]) translate to/from the Room persistence
 * layer added in Phase 01E.
 *
 * This foundational phase models one preferences row per user, matching
 * [com.gym.app.data.local.dao.UserPreferencesDao].
 */
interface UserPreferencesRepository {

    /** Observes the preferences for [userId] as they change over time, or null if none exist. */
    fun observeByUserId(userId: String): Flow<UserPreferences?>

    /** Returns the preferences for [userId] once, or null if none exist yet. */
    suspend fun getByUserId(userId: String): UserPreferences?

    /** Saves (inserts or replaces) [userPreferences]. */
    suspend fun save(userPreferences: UserPreferences)

    /** Deletes the preferences for [userId], if any exist. */
    suspend fun deleteByUserId(userId: String)
}

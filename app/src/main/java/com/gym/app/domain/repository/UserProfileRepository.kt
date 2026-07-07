package com.gym.app.domain.repository

import com.gym.app.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * PHASE 01F — USER REPOSITORIES
 *
 * Domain-facing contract for reading and writing a user's [UserProfile].
 * Exposes domain models only — no Room entities, no Android framework
 * types. Implementations (Phase 01F: [com.gym.app.data.repository
 * .UserProfileRepositoryImpl]) are responsible for translating to/from
 * the Room persistence layer added in Phase 01E.
 *
 * Deliberately minimal: only the operations meaningful for a single-row
 * per-user profile foundation. No use cases, no feature-specific logic.
 */
interface UserProfileRepository {

    /** Observes the profile for [userId] as it changes over time, or null if none exists. */
    fun observeByUserId(userId: String): Flow<UserProfile?>

    /** Returns the profile for [userId] once, or null if none exists yet. */
    suspend fun getByUserId(userId: String): UserProfile?

    /** Saves (inserts or replaces) [userProfile]. */
    suspend fun save(userProfile: UserProfile)

    /** Deletes the profile for [userId], if one exists. */
    suspend fun deleteByUserId(userId: String)
}

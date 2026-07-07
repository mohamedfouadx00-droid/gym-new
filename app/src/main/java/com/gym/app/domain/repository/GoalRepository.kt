package com.gym.app.domain.repository

import com.gym.app.domain.model.Goal
import kotlinx.coroutines.flow.Flow

/**
 * PHASE 01F — USER REPOSITORIES
 *
 * Domain-facing contract for reading and writing a user's [Goal] records.
 * A user may have more than one goal recorded over time (the domain layer
 * does not assume a single fixed goal — see [com.gym.app.data.local.dao
 * .GoalDao]), so this contract mirrors that: lookups by [Goal.goalId] for
 * a single goal, and by [Goal.userId] for all of a user's goals.
 *
 * Exposes domain models only — no Room entities. Implementations
 * (Phase 01F: [com.gym.app.data.repository.GoalRepositoryImpl]) translate
 * to/from the Room persistence layer added in Phase 01E.
 */
interface GoalRepository {

    /** Observes every goal recorded for [userId] as they change over time. */
    fun observeByUserId(userId: String): Flow<List<Goal>>

    /** Returns every goal recorded for [userId] once. */
    suspend fun getByUserId(userId: String): List<Goal>

    /** Returns the goal for [goalId], or null if none exists yet. */
    suspend fun getByGoalId(goalId: String): Goal?

    /** Saves (inserts or replaces) [goal]. */
    suspend fun save(goal: Goal)

    /** Deletes the goal identified by [goalId], if it exists. */
    suspend fun deleteByGoalId(goalId: String)
}

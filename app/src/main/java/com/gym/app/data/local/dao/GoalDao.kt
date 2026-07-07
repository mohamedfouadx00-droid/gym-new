package com.gym.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.gym.app.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

/**
 * PHASE 01E — ROOM DATABASE FOUNDATION
 *
 * Basic persistence operations for [GoalEntity]. A user may have more than
 * one goal recorded over time (the domain layer does not assume a single
 * fixed goal), so lookups are by [GoalEntity.goalId] where a single row is
 * needed, and by [GoalEntity.userId] where all of a user's goals are
 * needed.
 */
@Dao
interface GoalDao {

    /** Inserts a new goal or replaces the existing one for the same [GoalEntity.goalId]. */
    @Upsert
    suspend fun upsert(goal: GoalEntity)

    /** Returns the goal for [goalId], or null if none exists yet. */
    @Query("SELECT * FROM goals WHERE goalId = :goalId LIMIT 1")
    suspend fun getByGoalId(goalId: String): GoalEntity?

    /** Returns every goal recorded for [userId]. */
    @Query("SELECT * FROM goals WHERE userId = :userId")
    suspend fun getByUserId(userId: String): List<GoalEntity>

    /** Observes every goal recorded for [userId] as they change over time. */
    @Query("SELECT * FROM goals WHERE userId = :userId")
    fun observeByUserId(userId: String): Flow<List<GoalEntity>>

    /** Deletes the goal identified by [goalId], if it exists. */
    @Query("DELETE FROM goals WHERE goalId = :goalId")
    suspend fun deleteByGoalId(goalId: String)
}

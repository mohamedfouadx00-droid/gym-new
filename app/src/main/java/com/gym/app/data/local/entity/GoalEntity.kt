package com.gym.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * PHASE 01E — ROOM DATABASE FOUNDATION
 *
 * Room persistence model for [com.gym.app.domain.model.Goal].
 *
 * [goalId] remains the primary key, matching the domain model's own stable
 * identity. [userId] is indexed (not a foreign key with cascading
 * constraints yet — that would assume a specific one-goal-per-user or
 * strict-ownership rule this foundational phase does not need to enforce)
 * so lookups/observations "by userId" stay efficient as data grows.
 *
 * `primaryGoal` and each entry of `secondaryGoals` are stored as the enum's
 * `name` (String), for the same forward-compatibility reason as
 * [UserProfileEntity.gender]. `secondaryGoals` itself is stored as a single
 * delimited [String] column via `GoalTypeConverters` rather than a separate
 * table, since it is a small, unordered-enough list of enum values with no
 * independent identity of its own — a full join table would be
 * over-engineering for this foundational phase.
 */
@Entity(
    tableName = "goals",
    indices = [Index(value = ["userId"])]
)
data class GoalEntity(
    @PrimaryKey
    val goalId: String,
    val userId: String,
    val primaryGoal: String,
    val targetWeightKg: Double?,
    val goalStartDate: Long,
    val targetDate: Long?,
    val secondaryGoals: List<String>
)

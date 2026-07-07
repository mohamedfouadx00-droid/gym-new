package com.gym.app.data.mapper

import com.gym.app.data.local.entity.GoalEntity
import com.gym.app.domain.model.Goal
import com.gym.app.domain.model.PrimaryGoal

/**
 * PHASE 01E — ROOM DATABASE FOUNDATION
 *
 * Pure mapping functions between [Goal] (domain) and [GoalEntity] (Room
 * persistence).
 */

/** Converts this domain [Goal] into its Room [GoalEntity] representation. */
fun Goal.toEntity(): GoalEntity = GoalEntity(
    goalId = goalId,
    userId = userId,
    primaryGoal = primaryGoal.name,
    targetWeightKg = targetWeightKg,
    goalStartDate = goalStartDate,
    targetDate = targetDate,
    secondaryGoals = secondaryGoals.map { it.name }
)

/** Converts this Room [GoalEntity] back into the domain [Goal] model. */
fun GoalEntity.toDomain(): Goal = Goal(
    goalId = goalId,
    userId = userId,
    primaryGoal = PrimaryGoal.valueOf(primaryGoal),
    targetWeightKg = targetWeightKg,
    goalStartDate = goalStartDate,
    targetDate = targetDate,
    secondaryGoals = secondaryGoals.map { PrimaryGoal.valueOf(it) }
)

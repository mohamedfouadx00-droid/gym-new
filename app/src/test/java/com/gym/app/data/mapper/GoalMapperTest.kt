package com.gym.app.data.mapper

import com.gym.app.domain.model.Goal
import com.gym.app.domain.model.PrimaryGoal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * PHASE 01E — ROOM DATABASE FOUNDATION
 *
 * Verifies Goal <-> GoalEntity mapping correctness, including
 * [Goal.secondaryGoals] (a list encoded as a delimited string on the
 * entity) and nullable target fields.
 */
class GoalMapperTest {

    @Test
    fun `domain to entity to domain preserves all fields including secondary goals`() {
        val original = Goal(
            goalId = "goal-1",
            userId = "user-1",
            primaryGoal = PrimaryGoal.WEIGHT_LOSS,
            targetWeightKg = 75.0,
            goalStartDate = 19000L,
            targetDate = 19100L,
            secondaryGoals = listOf(PrimaryGoal.GENERAL_FITNESS, PrimaryGoal.STRENGTH)
        )

        val roundTripped = original.toEntity().toDomain()

        assertEquals(original, roundTripped)
    }

    @Test
    fun `empty secondary goals round trip as empty list`() {
        val original = Goal(
            goalId = "goal-2",
            userId = "user-1",
            primaryGoal = PrimaryGoal.MAINTENANCE,
            targetWeightKg = null,
            goalStartDate = 19000L,
            targetDate = null,
            secondaryGoals = emptyList()
        )

        val entity = original.toEntity()
        assertEquals("", entity.secondaryGoals.joinToString(","))

        val roundTripped = entity.toDomain()
        assertTrue(roundTripped.secondaryGoals.isEmpty())
        assertEquals(original, roundTripped)
    }

    @Test
    fun `nullable target fields are preserved through entity round trip`() {
        val original = Goal(
            goalId = "goal-3",
            userId = "user-2",
            primaryGoal = PrimaryGoal.STRENGTH,
            targetWeightKg = null,
            goalStartDate = 18500L,
            targetDate = null
        )

        val entity = original.toEntity()
        assertEquals(null, entity.targetWeightKg)
        assertEquals(null, entity.targetDate)

        assertEquals(original, entity.toDomain())
    }
}

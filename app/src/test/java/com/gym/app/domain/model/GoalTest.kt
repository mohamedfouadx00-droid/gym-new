package com.gym.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * PHASE 01D — CORE USER MODELS
 *
 * Unit tests for [Goal]'s validation rules, target-weight applicability,
 * and immutable behavior.
 */
class GoalTest {

    private fun validGoal(
        primaryGoal: PrimaryGoal = PrimaryGoal.WEIGHT_LOSS,
        targetWeightKg: Double? = 75.0,
        goalStartDate: Long = 19_000L,
        targetDate: Long? = 19_100L,
        secondaryGoals: List<PrimaryGoal> = emptyList()
    ) = Goal(
        goalId = "goal-1",
        userId = "user-1",
        primaryGoal = primaryGoal,
        targetWeightKg = targetWeightKg,
        goalStartDate = goalStartDate,
        targetDate = targetDate,
        secondaryGoals = secondaryGoals
    )

    @Test
    fun `valid goal is constructed successfully`() {
        val goal = validGoal()
        assertEquals("goal-1", goal.goalId)
        assertEquals(PrimaryGoal.WEIGHT_LOSS, goal.primaryGoal)
    }

    @Test
    fun `blank goalId is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            validGoal().copy(goalId = " ")
        }
    }

    @Test
    fun `blank userId is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            validGoal().copy(userId = "")
        }
    }

    @Test
    fun `target date before start date is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            validGoal(goalStartDate = 20_000L, targetDate = 19_999L)
        }
    }

    @Test
    fun `target date equal to start date is accepted`() {
        val goal = validGoal(goalStartDate = 20_000L, targetDate = 20_000L)
        assertEquals(20_000L, goal.targetDate)
    }

    @Test
    fun `null target date is accepted`() {
        val goal = validGoal(targetDate = null)
        assertEquals(null, goal.targetDate)
    }

    @Test
    fun `invalid target weight is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            validGoal(targetWeightKg = -1.0)
        }
    }

    @Test
    fun `null target weight is accepted for goals where it does not apply`() {
        val goal = validGoal(primaryGoal = PrimaryGoal.STRENGTH, targetWeightKg = null)
        assertEquals(null, goal.targetWeightKg)
    }

    @Test
    fun `primaryGoal duplicated in secondaryGoals is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            validGoal(
                primaryGoal = PrimaryGoal.STRENGTH,
                targetWeightKg = null,
                secondaryGoals = listOf(PrimaryGoal.STRENGTH)
            )
        }
    }

    @Test
    fun `weight oriented goals report target weight as applicable`() {
        assertTrue(Goal.isTargetWeightApplicable(PrimaryGoal.WEIGHT_GAIN))
        assertTrue(Goal.isTargetWeightApplicable(PrimaryGoal.WEIGHT_LOSS))
        assertTrue(Goal.isTargetWeightApplicable(PrimaryGoal.MUSCLE_BUILDING))
    }

    @Test
    fun `non weight oriented goals report target weight as not applicable`() {
        assertFalse(Goal.isTargetWeightApplicable(PrimaryGoal.STRENGTH))
        assertFalse(Goal.isTargetWeightApplicable(PrimaryGoal.MAINTENANCE))
        assertFalse(Goal.isTargetWeightApplicable(PrimaryGoal.GENERAL_FITNESS))
    }

    @Test
    fun `instance method matches companion helper for its own primaryGoal`() {
        val goal = validGoal(primaryGoal = PrimaryGoal.MUSCLE_BUILDING, targetWeightKg = 82.0)
        assertEquals(Goal.isTargetWeightApplicable(goal.primaryGoal), goal.isTargetWeightApplicable())
    }

    @Test
    fun `copy produces a new immutable instance without mutating the original`() {
        val original = validGoal(targetWeightKg = 75.0)
        val updated = original.copy(targetWeightKg = 72.0)

        assertEquals(75.0, original.targetWeightKg)
        assertEquals(72.0, updated.targetWeightKg)
    }
}

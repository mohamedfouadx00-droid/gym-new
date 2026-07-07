package com.gym.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

/**
 * PHASE 01D — CORE USER MODELS
 *
 * Unit tests for [UserPreferences]'s validation rules and immutable
 * behavior.
 */
class UserPreferencesTest {

    private fun validPreferences(
        preferredWorkoutDays: Set<Weekday> = setOf(Weekday.MONDAY, Weekday.WEDNESDAY),
        restDays: Set<Weekday> = setOf(Weekday.SUNDAY),
        preferredMealCount: Int = 4
    ) = UserPreferences(
        userId = "user-1",
        workoutLocation = WorkoutLocation.GYM,
        preferredWorkoutDays = preferredWorkoutDays,
        restDays = restDays,
        preferredWorkoutTime = TimeOfDay(18, 30),
        availableEquipment = listOf("dumbbells", "barbell"),
        preferredMealCount = preferredMealCount,
        sleepTime = TimeOfDay(23, 0),
        wakeTime = TimeOfDay(6, 30),
        budgetLevel = BudgetLevel.MEDIUM,
        enabledSupplements = listOf("whey protein"),
        unitSystem = UnitSystem.METRIC
    )

    @Test
    fun `valid preferences are constructed successfully`() {
        val prefs = validPreferences()
        assertEquals("user-1", prefs.userId)
        assertEquals(WorkoutLocation.GYM, prefs.workoutLocation)
    }

    @Test
    fun `blank userId is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            validPreferences().copy(userId = "")
        }
    }

    @Test
    fun `overlapping workout and rest days are rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            validPreferences(
                preferredWorkoutDays = setOf(Weekday.MONDAY),
                restDays = setOf(Weekday.MONDAY)
            )
        }
    }

    @Test
    fun `disjoint workout and rest days are accepted`() {
        val prefs = validPreferences(
            preferredWorkoutDays = setOf(Weekday.MONDAY, Weekday.TUESDAY),
            restDays = setOf(Weekday.SUNDAY)
        )
        assertEquals(setOf(Weekday.MONDAY, Weekday.TUESDAY), prefs.preferredWorkoutDays)
    }

    @Test
    fun `meal count below minimum is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            validPreferences(preferredMealCount = UserPreferences.MIN_MEAL_COUNT - 1)
        }
    }

    @Test
    fun `meal count above maximum is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            validPreferences(preferredMealCount = UserPreferences.MAX_MEAL_COUNT + 1)
        }
    }

    @Test
    fun `null time preferences are accepted`() {
        val prefs = validPreferences().copy(
            preferredWorkoutTime = null,
            sleepTime = null,
            wakeTime = null
        )
        assertEquals(null, prefs.preferredWorkoutTime)
    }

    @Test
    fun `default reminder preferences are all disabled`() {
        val prefs = validPreferences()
        assertEquals(ReminderPreferences(), prefs.reminderPreferences)
        assertEquals(false, prefs.reminderPreferences.workoutReminders)
    }

    @Test
    fun `copy produces a new immutable instance without mutating the original`() {
        val original = validPreferences()
        val updated = original.copy(budgetLevel = BudgetLevel.HIGH)

        assertEquals(BudgetLevel.MEDIUM, original.budgetLevel)
        assertEquals(BudgetLevel.HIGH, updated.budgetLevel)
    }
}

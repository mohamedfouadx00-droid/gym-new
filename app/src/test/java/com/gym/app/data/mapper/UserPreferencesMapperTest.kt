package com.gym.app.data.mapper

import com.gym.app.domain.model.BudgetLevel
import com.gym.app.domain.model.ReminderPreferences
import com.gym.app.domain.model.TimeOfDay
import com.gym.app.domain.model.UnitSystem
import com.gym.app.domain.model.UserPreferences
import com.gym.app.domain.model.Weekday
import com.gym.app.domain.model.WorkoutLocation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * PHASE 01E — ROOM DATABASE FOUNDATION
 *
 * Verifies UserPreferences <-> UserPreferencesEntity mapping correctness,
 * including [Weekday] sets, free-form string lists, nullable [TimeOfDay]
 * fields, and the flattened [ReminderPreferences].
 */
class UserPreferencesMapperTest {

    @Test
    fun `domain to entity to domain preserves all fields with full data`() {
        val original = UserPreferences(
            userId = "user-1",
            workoutLocation = WorkoutLocation.GYM,
            preferredWorkoutDays = setOf(Weekday.MONDAY, Weekday.WEDNESDAY, Weekday.FRIDAY),
            restDays = setOf(Weekday.SUNDAY),
            preferredWorkoutTime = TimeOfDay(18, 30),
            availableEquipment = listOf("dumbbells", "barbell"),
            preferredMealCount = 4,
            sleepTime = TimeOfDay(23, 0),
            wakeTime = TimeOfDay(6, 30),
            budgetLevel = BudgetLevel.MEDIUM,
            enabledSupplements = listOf("creatine", "whey protein"),
            reminderPreferences = ReminderPreferences(
                workoutReminders = true,
                mealReminders = false,
                sleepReminders = true,
                hydrationReminders = false
            ),
            unitSystem = UnitSystem.METRIC
        )

        val roundTripped = original.toEntity().toDomain()

        assertEquals(original, roundTripped)
    }

    @Test
    fun `null time-of-day fields and empty collections round trip correctly`() {
        val original = UserPreferences(
            userId = "user-2",
            workoutLocation = WorkoutLocation.HOME,
            preferredWorkoutDays = emptySet(),
            restDays = emptySet(),
            preferredWorkoutTime = null,
            availableEquipment = emptyList(),
            preferredMealCount = 3,
            sleepTime = null,
            wakeTime = null,
            budgetLevel = BudgetLevel.LOW,
            enabledSupplements = emptyList(),
            unitSystem = UnitSystem.IMPERIAL
        )

        val entity = original.toEntity()
        assertNull(entity.preferredWorkoutTimeHour)
        assertNull(entity.sleepTimeHour)
        assertNull(entity.wakeTimeHour)

        val roundTripped = entity.toDomain()
        assertEquals(original, roundTripped)
    }

    @Test
    fun `preferred workout days and rest days do not collide after round trip`() {
        val original = UserPreferences(
            userId = "user-3",
            workoutLocation = WorkoutLocation.OUTDOOR,
            preferredWorkoutDays = setOf(Weekday.TUESDAY, Weekday.THURSDAY),
            restDays = setOf(Weekday.SATURDAY, Weekday.SUNDAY),
            preferredWorkoutTime = TimeOfDay(7, 0),
            preferredMealCount = 5,
            sleepTime = null,
            wakeTime = null,
            budgetLevel = BudgetLevel.HIGH,
            unitSystem = UnitSystem.METRIC
        )

        val roundTripped = original.toEntity().toDomain()

        assertEquals(original.preferredWorkoutDays, roundTripped.preferredWorkoutDays)
        assertEquals(original.restDays, roundTripped.restDays)
        assertEquals(
            emptySet<Weekday>(),
            roundTripped.preferredWorkoutDays.intersect(roundTripped.restDays)
        )
    }
}

package com.gym.app.data.repository

import com.gym.app.data.local.fake.FakeUserPreferencesDao
import com.gym.app.domain.model.BudgetLevel
import com.gym.app.domain.model.TimeOfDay
import com.gym.app.domain.model.UnitSystem
import com.gym.app.domain.model.UserPreferences
import com.gym.app.domain.model.Weekday
import com.gym.app.domain.model.WorkoutLocation
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

/**
 * PHASE 01F — USER REPOSITORIES
 *
 * Verifies [UserPreferencesRepositoryImpl] correctly delegates to its DAO
 * and round-trips through the existing Phase 01E mapper, using an
 * in-memory fake DAO ([FakeUserPreferencesDao]).
 */
class UserPreferencesRepositoryImplTest {

    private lateinit var dao: FakeUserPreferencesDao
    private lateinit var repository: UserPreferencesRepositoryImpl

    @Before
    fun setUp() {
        dao = FakeUserPreferencesDao()
        repository = UserPreferencesRepositoryImpl(dao)
    }

    private fun samplePreferences(userId: String = "user-1") = UserPreferences(
        userId = userId,
        workoutLocation = WorkoutLocation.GYM,
        preferredWorkoutDays = setOf(Weekday.SATURDAY, Weekday.MONDAY),
        restDays = setOf(Weekday.FRIDAY),
        preferredWorkoutTime = TimeOfDay(18, 30),
        availableEquipment = listOf("dumbbells", "barbell"),
        preferredMealCount = 4,
        sleepTime = TimeOfDay(23, 0),
        wakeTime = TimeOfDay(6, 30),
        budgetLevel = BudgetLevel.MEDIUM,
        enabledSupplements = listOf("creatine"),
        unitSystem = UnitSystem.METRIC
    )

    @Test
    fun `getByUserId returns null when no preferences have been saved`() = runTest {
        assertNull(repository.getByUserId("missing-user"))
    }

    @Test
    fun `save then getByUserId returns the same domain preferences`() = runTest {
        val preferences = samplePreferences()

        repository.save(preferences)

        assertEquals(preferences, repository.getByUserId(preferences.userId))
    }

    @Test
    fun `save replaces the existing preferences row for the same userId`() = runTest {
        val original = samplePreferences()
        val updated = original.copy(preferredMealCount = 5, budgetLevel = BudgetLevel.HIGH)

        repository.save(original)
        repository.save(updated)

        assertEquals(updated, repository.getByUserId(original.userId))
    }

    @Test
    fun `observeByUserId emits the latest saved preferences`() = runTest {
        val preferences = samplePreferences()

        repository.save(preferences)

        assertEquals(preferences, repository.observeByUserId(preferences.userId).first())
    }

    @Test
    fun `deleteByUserId removes the preferences`() = runTest {
        val preferences = samplePreferences()
        repository.save(preferences)

        repository.deleteByUserId(preferences.userId)

        assertNull(repository.getByUserId(preferences.userId))
    }
}

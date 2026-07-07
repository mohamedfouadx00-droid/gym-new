package com.gym.app.data.local

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.gym.app.data.local.entity.GoalEntity
import com.gym.app.data.local.entity.UserPreferencesEntity
import com.gym.app.data.local.entity.UserProfileEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * PHASE 01E — ROOM DATABASE FOUNDATION
 *
 * Instrumented tests exercising the real Room database (in-memory, but a
 * genuine SQLite instance via the Android test runner) to verify the
 * persistence behavior this phase is actually about: saving/reading each
 * entity and preserving userId relationships across tables.
 */
@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {

    private lateinit var db: AppDatabase

    @Before
    fun createDatabase() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        db.close()
    }

    @Test
    fun saveAndReadUserProfile() = runTest {
        val profile = UserProfileEntity(
            userId = "user-1",
            name = "Ahmed",
            age = 28,
            gender = "MALE",
            heightCm = 178.0,
            currentWeightKg = 82.5,
            experienceLevel = "INTERMEDIATE",
            activityLevel = "MODERATELY_ACTIVE"
        )

        db.userProfileDao().upsert(profile)
        val loaded = db.userProfileDao().getByUserId("user-1")

        assertEquals(profile, loaded)
    }

    @Test
    fun readingMissingUserProfileReturnsNull() = runTest {
        val loaded = db.userProfileDao().getByUserId("does-not-exist")
        assertNull(loaded)
    }

    @Test
    fun saveAndReadGoal() = runTest {
        val goal = GoalEntity(
            goalId = "goal-1",
            userId = "user-1",
            primaryGoal = "WEIGHT_LOSS",
            targetWeightKg = 75.0,
            goalStartDate = 19000L,
            targetDate = 19100L,
            secondaryGoals = listOf("GENERAL_FITNESS")
        )

        db.goalDao().upsert(goal)
        val loaded = db.goalDao().getByGoalId("goal-1")

        assertEquals(goal, loaded)
    }

    @Test
    fun saveAndReadUserPreferences() = runTest {
        val preferences = UserPreferencesEntity(
            userId = "user-1",
            workoutLocation = "GYM",
            preferredWorkoutDays = "MONDAY,WEDNESDAY",
            restDays = "SUNDAY",
            preferredWorkoutTimeHour = 18,
            preferredWorkoutTimeMinute = 30,
            availableEquipment = "dumbbells,barbell",
            preferredMealCount = 4,
            sleepTimeHour = 23,
            sleepTimeMinute = 0,
            wakeTimeHour = 6,
            wakeTimeMinute = 30,
            budgetLevel = "MEDIUM",
            enabledSupplements = "creatine",
            workoutReminders = true,
            mealReminders = false,
            sleepReminders = true,
            hydrationReminders = false,
            unitSystem = "METRIC"
        )

        db.userPreferencesDao().upsert(preferences)
        val loaded = db.userPreferencesDao().getByUserId("user-1")

        assertEquals(preferences, loaded)
    }

    @Test
    fun userIdRelationshipIsPreservedAcrossAllThreeTables() = runTest {
        val userId = "user-shared"

        db.userProfileDao().upsert(
            UserProfileEntity(
                userId = userId,
                name = "Sara",
                age = 30,
                gender = "FEMALE",
                heightCm = 165.0,
                currentWeightKg = 60.0,
                experienceLevel = "BEGINNER",
                activityLevel = "SEDENTARY"
            )
        )
        db.goalDao().upsert(
            GoalEntity(
                goalId = "goal-shared",
                userId = userId,
                primaryGoal = "MUSCLE_BUILDING",
                targetWeightKg = 65.0,
                goalStartDate = 19000L,
                targetDate = null,
                secondaryGoals = emptyList()
            )
        )
        db.userPreferencesDao().upsert(
            UserPreferencesEntity(
                userId = userId,
                workoutLocation = "HOME",
                preferredWorkoutDays = "",
                restDays = "",
                preferredWorkoutTimeHour = null,
                preferredWorkoutTimeMinute = null,
                availableEquipment = "",
                preferredMealCount = 3,
                sleepTimeHour = null,
                sleepTimeMinute = null,
                wakeTimeHour = null,
                wakeTimeMinute = null,
                budgetLevel = "LOW",
                enabledSupplements = "",
                workoutReminders = false,
                mealReminders = false,
                sleepReminders = false,
                hydrationReminders = false,
                unitSystem = "METRIC"
            )
        )

        val profile = db.userProfileDao().getByUserId(userId)
        val goals = db.goalDao().getByUserId(userId)
        val preferences = db.userPreferencesDao().getByUserId(userId)

        assertEquals(userId, profile?.userId)
        assertEquals(1, goals.size)
        assertEquals(userId, goals.first().userId)
        assertEquals(userId, preferences?.userId)
    }

    @Test
    fun upsertReplacesExistingRowForSameUserId() = runTest {
        val original = UserProfileEntity(
            userId = "user-2",
            name = "Omar",
            age = 40,
            gender = "MALE",
            heightCm = 180.0,
            currentWeightKg = 90.0,
            experienceLevel = "ADVANCED",
            activityLevel = "VERY_ACTIVE"
        )
        db.userProfileDao().upsert(original)

        val updated = original.copy(currentWeightKg = 88.0)
        db.userProfileDao().upsert(updated)

        val loaded = db.userProfileDao().getByUserId("user-2")
        assertEquals(88.0, loaded?.currentWeightKg)
    }

    @Test
    fun deletingUserProfileRemovesIt() = runTest {
        val profile = UserProfileEntity(
            userId = "user-3",
            name = "Layla",
            age = 25,
            gender = "FEMALE",
            heightCm = 160.0,
            currentWeightKg = 55.0,
            experienceLevel = "BEGINNER",
            activityLevel = "LIGHTLY_ACTIVE"
        )
        db.userProfileDao().upsert(profile)
        db.userProfileDao().deleteByUserId("user-3")

        assertNull(db.userProfileDao().getByUserId("user-3"))
    }
}

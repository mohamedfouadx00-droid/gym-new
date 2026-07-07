package com.gym.app.data.mapper

import com.gym.app.domain.model.ActivityLevel
import com.gym.app.domain.model.ExperienceLevel
import com.gym.app.domain.model.Gender
import com.gym.app.domain.model.UserProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * PHASE 01E — ROOM DATABASE FOUNDATION
 *
 * Verifies UserProfile <-> UserProfileEntity mapping correctness,
 * including the nullable [Gender] round trip.
 */
class UserProfileMapperTest {

    @Test
    fun `domain to entity to domain preserves all fields with non-null gender`() {
        val original = UserProfile(
            userId = "user-1",
            name = "Ahmed",
            age = 28,
            gender = Gender.MALE,
            heightCm = 178.0,
            currentWeightKg = 82.5,
            experienceLevel = ExperienceLevel.INTERMEDIATE,
            activityLevel = ActivityLevel.MODERATELY_ACTIVE
        )

        val roundTripped = original.toEntity().toDomain()

        assertEquals(original, roundTripped)
    }

    @Test
    fun `null gender is preserved through entity round trip`() {
        val original = UserProfile(
            userId = "user-2",
            name = "Sara",
            age = 34,
            gender = null,
            heightCm = 165.0,
            currentWeightKg = 60.0,
            experienceLevel = ExperienceLevel.BEGINNER,
            activityLevel = ActivityLevel.SEDENTARY
        )

        val entity = original.toEntity()
        assertNull(entity.gender)

        val roundTripped = entity.toDomain()
        assertEquals(original, roundTripped)
    }

    @Test
    fun `enum fields are stored as their name for forward compatibility`() {
        val original = UserProfile(
            userId = "user-3",
            name = "Omar",
            age = 40,
            gender = Gender.MALE,
            heightCm = 180.0,
            currentWeightKg = 90.0,
            experienceLevel = ExperienceLevel.ADVANCED,
            activityLevel = ActivityLevel.VERY_ACTIVE
        )

        val entity = original.toEntity()

        assertEquals("ADVANCED", entity.experienceLevel)
        assertEquals("VERY_ACTIVE", entity.activityLevel)
        assertEquals("MALE", entity.gender)
    }
}

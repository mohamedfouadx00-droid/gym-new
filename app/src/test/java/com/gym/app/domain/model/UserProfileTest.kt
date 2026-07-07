package com.gym.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * PHASE 01D — CORE USER MODELS
 *
 * Unit tests for [UserProfile]'s validation rules and immutability.
 */
class UserProfileTest {

    private fun validProfile(
        age: Int = 30,
        heightCm: Double = 175.0,
        currentWeightKg: Double = 80.0
    ) = UserProfile(
        userId = "user-1",
        name = "Test User",
        age = age,
        gender = Gender.MALE,
        heightCm = heightCm,
        currentWeightKg = currentWeightKg,
        experienceLevel = ExperienceLevel.INTERMEDIATE,
        activityLevel = ActivityLevel.MODERATELY_ACTIVE
    )

    @Test
    fun `valid profile is constructed successfully`() {
        val profile = validProfile()
        assertEquals("user-1", profile.userId)
        assertEquals(30, profile.age)
    }

    @Test
    fun `blank userId is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            validProfile().copy(userId = "   ")
        }
    }

    @Test
    fun `blank name is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            validProfile().copy(name = "")
        }
    }

    @Test
    fun `age below minimum is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            validProfile(age = UserProfile.MIN_AGE - 1)
        }
    }

    @Test
    fun `age above maximum is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            validProfile(age = UserProfile.MAX_AGE + 1)
        }
    }

    @Test
    fun `age at boundaries is accepted`() {
        validProfile(age = UserProfile.MIN_AGE)
        validProfile(age = UserProfile.MAX_AGE)
    }

    @Test
    fun `non positive height is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            validProfile(heightCm = 0.0)
        }
        assertThrows(IllegalArgumentException::class.java) {
            validProfile(heightCm = -10.0)
        }
    }

    @Test
    fun `unrealistically large height is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            validProfile(heightCm = UserProfile.MAX_HEIGHT_CM + 1)
        }
    }

    @Test
    fun `non positive weight is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            validProfile(currentWeightKg = 0.0)
        }
        assertThrows(IllegalArgumentException::class.java) {
            validProfile(currentWeightKg = -5.0)
        }
    }

    @Test
    fun `gender is optional and may be null`() {
        val profile = validProfile().copy(gender = null)
        assertEquals(null, profile.gender)
    }

    @Test
    fun `isValidAge helper matches constructor validation`() {
        assertTrue(UserProfile.isValidAge(UserProfile.MIN_AGE))
        assertTrue(UserProfile.isValidAge(UserProfile.MAX_AGE))
        assertFalse(UserProfile.isValidAge(UserProfile.MIN_AGE - 1))
        assertFalse(UserProfile.isValidAge(UserProfile.MAX_AGE + 1))
    }

    @Test
    fun `copy produces a new immutable instance without mutating the original`() {
        val original = validProfile(currentWeightKg = 80.0)
        val updated = original.copy(currentWeightKg = 78.0)

        assertEquals(80.0, original.currentWeightKg, 0.0)
        assertEquals(78.0, updated.currentWeightKg, 0.0)
    }
}

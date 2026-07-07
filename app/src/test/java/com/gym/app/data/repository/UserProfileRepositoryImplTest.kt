package com.gym.app.data.repository

import com.gym.app.data.local.fake.FakeUserProfileDao
import com.gym.app.domain.model.ActivityLevel
import com.gym.app.domain.model.ExperienceLevel
import com.gym.app.domain.model.Gender
import com.gym.app.domain.model.UserProfile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

/**
 * PHASE 01F — USER REPOSITORIES
 *
 * Verifies [UserProfileRepositoryImpl] correctly delegates to its DAO and
 * round-trips through the existing Phase 01E mapper, using an in-memory
 * fake DAO ([FakeUserProfileDao]) rather than a real Room database.
 */
class UserProfileRepositoryImplTest {

    private lateinit var dao: FakeUserProfileDao
    private lateinit var repository: UserProfileRepositoryImpl

    @Before
    fun setUp() {
        dao = FakeUserProfileDao()
        repository = UserProfileRepositoryImpl(dao)
    }

    private fun sampleProfile(userId: String = "user-1") = UserProfile(
        userId = userId,
        name = "Layla",
        age = 30,
        gender = Gender.FEMALE,
        heightCm = 165.0,
        currentWeightKg = 63.0,
        experienceLevel = ExperienceLevel.INTERMEDIATE,
        activityLevel = ActivityLevel.MODERATELY_ACTIVE
    )

    @Test
    fun `getByUserId returns null when no profile has been saved`() = runTest {
        assertNull(repository.getByUserId("missing-user"))
    }

    @Test
    fun `save then getByUserId returns the same domain profile`() = runTest {
        val profile = sampleProfile()

        repository.save(profile)

        assertEquals(profile, repository.getByUserId(profile.userId))
    }

    @Test
    fun `save replaces the existing profile for the same userId`() = runTest {
        val original = sampleProfile()
        val updated = original.copy(currentWeightKg = 65.5, age = 31)

        repository.save(original)
        repository.save(updated)

        assertEquals(updated, repository.getByUserId(original.userId))
    }

    @Test
    fun `observeByUserId emits the latest saved profile`() = runTest {
        val profile = sampleProfile()

        repository.save(profile)

        assertEquals(profile, repository.observeByUserId(profile.userId).first())
    }

    @Test
    fun `deleteByUserId removes the profile`() = runTest {
        val profile = sampleProfile()
        repository.save(profile)

        repository.deleteByUserId(profile.userId)

        assertNull(repository.getByUserId(profile.userId))
    }
}

package com.gym.app.data.repository

import com.gym.app.data.local.fake.FakeGoalDao
import com.gym.app.domain.model.Goal
import com.gym.app.domain.model.PrimaryGoal
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * PHASE 01F — USER REPOSITORIES
 *
 * Verifies [GoalRepositoryImpl] correctly delegates to its DAO, including
 * the multi-goal-per-user shape (lookup by [Goal.goalId] vs by
 * [Goal.userId]), using an in-memory fake DAO ([FakeGoalDao]).
 */
class GoalRepositoryImplTest {

    private lateinit var dao: FakeGoalDao
    private lateinit var repository: GoalRepositoryImpl

    @Before
    fun setUp() {
        dao = FakeGoalDao()
        repository = GoalRepositoryImpl(dao)
    }

    private fun sampleGoal(
        goalId: String = "goal-1",
        userId: String = "user-1"
    ) = Goal(
        goalId = goalId,
        userId = userId,
        primaryGoal = PrimaryGoal.WEIGHT_LOSS,
        targetWeightKg = 70.0,
        goalStartDate = 19_000L,
        targetDate = 19_200L,
        secondaryGoals = listOf(PrimaryGoal.GENERAL_FITNESS)
    )

    @Test
    fun `getByGoalId returns null when no goal has been saved`() = runTest {
        assertNull(repository.getByGoalId("missing-goal"))
    }

    @Test
    fun `save then getByGoalId returns the same domain goal`() = runTest {
        val goal = sampleGoal()

        repository.save(goal)

        assertEquals(goal, repository.getByGoalId(goal.goalId))
    }

    @Test
    fun `getByUserId returns every goal for that user only`() = runTest {
        val userGoal = sampleGoal(goalId = "goal-1", userId = "user-1")
        val otherUserGoal = sampleGoal(goalId = "goal-2", userId = "user-2")

        repository.save(userGoal)
        repository.save(otherUserGoal)

        val result = repository.getByUserId("user-1")

        assertEquals(listOf(userGoal), result)
    }

    @Test
    fun `observeByUserId emits every saved goal for that user`() = runTest {
        val firstGoal = sampleGoal(goalId = "goal-1", userId = "user-1")
        val secondGoal = sampleGoal(
            goalId = "goal-2",
            userId = "user-1"
        ).copy(primaryGoal = PrimaryGoal.STRENGTH, targetWeightKg = null, secondaryGoals = emptyList())

        repository.save(firstGoal)
        repository.save(secondGoal)

        val result = repository.observeByUserId("user-1").first()

        assertEquals(2, result.size)
        assertTrue(result.containsAll(listOf(firstGoal, secondGoal)))
    }

    @Test
    fun `deleteByGoalId removes only that goal`() = runTest {
        val goal = sampleGoal()
        repository.save(goal)

        repository.deleteByGoalId(goal.goalId)

        assertNull(repository.getByGoalId(goal.goalId))
        assertEquals(emptyList<Goal>(), repository.getByUserId(goal.userId))
    }
}

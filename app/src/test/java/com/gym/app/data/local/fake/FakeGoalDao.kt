package com.gym.app.data.local.fake

import com.gym.app.data.local.dao.GoalDao
import com.gym.app.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * PHASE 01F — USER REPOSITORIES
 *
 * Simple in-memory fake of [GoalDao] used only by repository unit tests.
 * Keyed by [GoalEntity.goalId], with [GoalEntity.userId] filtered at read
 * time — mirroring the real DAO's query shapes without needing SQLite.
 */
class FakeGoalDao : GoalDao {

    private val state = MutableStateFlow<Map<String, GoalEntity>>(emptyMap())

    override suspend fun upsert(goal: GoalEntity) {
        state.value = state.value + (goal.goalId to goal)
    }

    override suspend fun getByGoalId(goalId: String): GoalEntity? =
        state.value[goalId]

    override suspend fun getByUserId(userId: String): List<GoalEntity> =
        state.value.values.filter { it.userId == userId }

    override fun observeByUserId(userId: String): Flow<List<GoalEntity>> =
        state.map { goals -> goals.values.filter { it.userId == userId } }

    override suspend fun deleteByGoalId(goalId: String) {
        state.value = state.value - goalId
    }
}

package com.gym.app.data.repository

import com.gym.app.data.local.dao.GoalDao
import com.gym.app.data.mapper.toDomain
import com.gym.app.data.mapper.toEntity
import com.gym.app.domain.model.Goal
import com.gym.app.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * PHASE 01F — USER REPOSITORIES
 *
 * Default [GoalRepository] implementation backed by [GoalDao]
 * (Phase 01E). Translates between the domain [Goal] model and its Room
 * entity exclusively through the existing [com.gym.app.data.mapper]
 * extension functions — no mapping logic lives here.
 */
class GoalRepositoryImpl @Inject constructor(
    private val goalDao: GoalDao
) : GoalRepository {

    override fun observeByUserId(userId: String): Flow<List<Goal>> =
        goalDao.observeByUserId(userId).map { entities -> entities.map { it.toDomain() } }

    override suspend fun getByUserId(userId: String): List<Goal> =
        goalDao.getByUserId(userId).map { it.toDomain() }

    override suspend fun getByGoalId(goalId: String): Goal? =
        goalDao.getByGoalId(goalId)?.toDomain()

    override suspend fun save(goal: Goal) {
        goalDao.upsert(goal.toEntity())
    }

    override suspend fun deleteByGoalId(goalId: String) {
        goalDao.deleteByGoalId(goalId)
    }
}

package com.gym.app.data.repository

import com.gym.app.data.local.dao.UserPreferencesDao
import com.gym.app.data.mapper.toDomain
import com.gym.app.data.mapper.toEntity
import com.gym.app.domain.model.UserPreferences
import com.gym.app.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * PHASE 01F — USER REPOSITORIES
 *
 * Default [UserPreferencesRepository] implementation backed by
 * [UserPreferencesDao] (Phase 01E). Translates between the domain
 * [UserPreferences] model and its Room entity exclusively through the
 * existing [com.gym.app.data.mapper] extension functions — no mapping
 * logic lives here.
 */
class UserPreferencesRepositoryImpl @Inject constructor(
    private val userPreferencesDao: UserPreferencesDao
) : UserPreferencesRepository {

    override fun observeByUserId(userId: String): Flow<UserPreferences?> =
        userPreferencesDao.observeByUserId(userId).map { entity -> entity?.toDomain() }

    override suspend fun getByUserId(userId: String): UserPreferences? =
        userPreferencesDao.getByUserId(userId)?.toDomain()

    override suspend fun save(userPreferences: UserPreferences) {
        userPreferencesDao.upsert(userPreferences.toEntity())
    }

    override suspend fun deleteByUserId(userId: String) {
        userPreferencesDao.deleteByUserId(userId)
    }
}

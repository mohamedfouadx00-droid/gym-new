package com.gym.app.data.repository

import com.gym.app.data.local.dao.UserProfileDao
import com.gym.app.data.mapper.toDomain
import com.gym.app.data.mapper.toEntity
import com.gym.app.domain.model.UserProfile
import com.gym.app.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * PHASE 01F — USER REPOSITORIES
 *
 * Default [UserProfileRepository] implementation backed by [UserProfileDao]
 * (Phase 01E). Translates between the domain [UserProfile] model and its
 * Room entity exclusively through the existing [com.gym.app.data.mapper]
 * extension functions — no mapping logic lives here.
 */
class UserProfileRepositoryImpl @Inject constructor(
    private val userProfileDao: UserProfileDao
) : UserProfileRepository {

    override fun observeByUserId(userId: String): Flow<UserProfile?> =
        userProfileDao.observeByUserId(userId).map { entity -> entity?.toDomain() }

    override suspend fun getByUserId(userId: String): UserProfile? =
        userProfileDao.getByUserId(userId)?.toDomain()

    override suspend fun save(userProfile: UserProfile) {
        userProfileDao.upsert(userProfile.toEntity())
    }

    override suspend fun deleteByUserId(userId: String) {
        userProfileDao.deleteByUserId(userId)
    }
}

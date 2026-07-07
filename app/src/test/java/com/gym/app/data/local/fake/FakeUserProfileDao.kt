package com.gym.app.data.local.fake

import com.gym.app.data.local.dao.UserProfileDao
import com.gym.app.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * PHASE 01F — USER REPOSITORIES
 *
 * Simple in-memory fake of [UserProfileDao] used only by repository unit
 * tests. Avoids pulling in Room/Robolectric/instrumented tests just to
 * verify that [com.gym.app.data.repository.UserProfileRepositoryImpl]
 * delegates to its DAO and maps correctly — the real DAO's SQL behavior is
 * already covered by the instrumented `AppDatabaseTest` from Phase 01E.
 */
class FakeUserProfileDao : UserProfileDao {

    private val state = MutableStateFlow<Map<String, UserProfileEntity>>(emptyMap())

    override suspend fun upsert(userProfile: UserProfileEntity) {
        state.value = state.value + (userProfile.userId to userProfile)
    }

    override suspend fun getByUserId(userId: String): UserProfileEntity? =
        state.value[userId]

    override fun observeByUserId(userId: String): Flow<UserProfileEntity?> =
        state.map { it[userId] }

    override suspend fun deleteByUserId(userId: String) {
        state.value = state.value - userId
    }
}

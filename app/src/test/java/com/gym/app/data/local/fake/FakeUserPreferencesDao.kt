package com.gym.app.data.local.fake

import com.gym.app.data.local.dao.UserPreferencesDao
import com.gym.app.data.local.entity.UserPreferencesEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * PHASE 01F — USER REPOSITORIES
 *
 * Simple in-memory fake of [UserPreferencesDao] used only by repository
 * unit tests.
 */
class FakeUserPreferencesDao : UserPreferencesDao {

    private val state = MutableStateFlow<Map<String, UserPreferencesEntity>>(emptyMap())

    override suspend fun upsert(userPreferences: UserPreferencesEntity) {
        state.value = state.value + (userPreferences.userId to userPreferences)
    }

    override suspend fun getByUserId(userId: String): UserPreferencesEntity? =
        state.value[userId]

    override fun observeByUserId(userId: String): Flow<UserPreferencesEntity?> =
        state.map { it[userId] }

    override suspend fun deleteByUserId(userId: String) {
        state.value = state.value - userId
    }
}

package com.gym.app.data.appstate

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.gym.app.domain.appstate.AppState
import com.gym.app.domain.appstate.AppStateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * PHASE 01G — DATASTORE FOUNDATION
 *
 * Default [AppStateRepository] implementation backed by a Preferences
 * [DataStore] (see [com.gym.app.core.di.DataStoreModule] for how the
 * instance itself reaches the Hilt graph). Translates between the domain
 * [AppState] model and raw [Preferences] keys exclusively in this file —
 * no DataStore types leak into [AppStateRepository] or [AppState].
 *
 * Kept entirely separate from the Room-backed repositories (Phase 01F):
 * this class never touches [com.gym.app.data.local.AppDatabase] or any
 * DAO, and stores nothing that duplicates
 * [com.gym.app.domain.model.UserProfile], [com.gym.app.domain.model.Goal],
 * or [com.gym.app.domain.model.UserPreferences].
 */
class AppStateRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : AppStateRepository {

    private object Keys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val ACTIVE_USER_ID = stringPreferencesKey("active_user_id")
    }

    override val appState: Flow<AppState> =
        dataStore.data.map { preferences ->
            AppState(
                onboardingCompleted = preferences[Keys.ONBOARDING_COMPLETED] ?: false,
                activeUserId = preferences[Keys.ACTIVE_USER_ID]
            )
        }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.ONBOARDING_COMPLETED] = completed
        }
    }

    override suspend fun setActiveUserId(userId: String?) {
        dataStore.edit { preferences ->
            if (userId == null) {
                preferences.remove(Keys.ACTIVE_USER_ID)
            } else {
                preferences[Keys.ACTIVE_USER_ID] = userId
            }
        }
    }
}

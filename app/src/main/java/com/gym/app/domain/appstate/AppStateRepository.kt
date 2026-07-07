package com.gym.app.domain.appstate

import kotlinx.coroutines.flow.Flow

/**
 * PHASE 01G — DATASTORE FOUNDATION
 *
 * Domain-facing contract for reading and writing simple, application-level
 * [AppState] — kept intentionally separate from the Room-backed
 * repositories added in Phase 01F ([com.gym.app.domain.repository
 * .UserProfileRepository], [com.gym.app.domain.repository.GoalRepository],
 * [com.gym.app.domain.repository.UserPreferencesRepository]), which remain
 * the source of truth for [com.gym.app.domain.model.UserProfile],
 * [com.gym.app.domain.model.Goal], and [com.gym.app.domain.model
 * .UserPreferences].
 *
 * Exposes only the domain [AppState] type and Kotlin Flow/suspend — no
 * DataStore, Preferences, or Android framework types appear in this
 * contract. Implementations (Phase 01G:
 * [com.gym.app.data.appstate.AppStateRepositoryImpl]) are responsible for
 * translating to/from Preferences DataStore.
 *
 * Deliberately minimal: only the two fields defined on [AppState], with
 * one suspend updater per field plus a reactive [Flow]. No use cases, no
 * feature-specific logic, no onboarding/app-start logic.
 */
interface AppStateRepository {

    /** Observes [AppState] as it changes over time. Never completes. */
    val appState: Flow<AppState>

    /** Updates whether onboarding has been completed. */
    suspend fun setOnboardingCompleted(completed: Boolean)

    /** Updates the currently active user id, or clears it by passing null. */
    suspend fun setActiveUserId(userId: String?)
}

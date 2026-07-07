package com.gym.app.domain.appstate

/**
 * PHASE 01G — DATASTORE FOUNDATION
 *
 * Simple, application-level state — as opposed to per-user domain data
 * (see [com.gym.app.domain.model.UserProfile], [com.gym.app.domain.model
 * .Goal], [com.gym.app.domain.model.UserPreferences]), which is stored in
 * Room (Phase 01E) and accessed through the repositories added in
 * Phase 01F.
 *
 * [AppState] answers only "what should the app do/show right now at
 * launch", not "what does the user want/need" — that distinction is what
 * keeps this out of Room:
 *
 * - [onboardingCompleted] — whether the one-time onboarding flow has been
 *   finished. This is app flow state, not user profile/goal/preference
 *   data, and has no meaning per-row in a Room table.
 * - [activeUserId] — which locally stored user (by `userId`, matching
 *   [com.gym.app.domain.model.UserProfile.userId]) is currently active.
 *   This is a pointer to a Room-stored profile, not a duplicate of it.
 *
 * Deliberately excluded: a units setting. [com.gym.app.domain.model
 * .UnitSystem] already exists and is clearly defined, but it belongs to
 * [com.gym.app.domain.model.UserPreferences] (a per-user Room row) — it
 * is not simple, user-independent app state, so mirroring it here would
 * duplicate Room data rather than complement it.
 *
 * This is a plain, immutable Kotlin type: no DataStore/Android/Hilt types
 * appear here. Translation to/from the DataStore Preferences layer is the
 * responsibility of [com.gym.app.data.appstate.AppStateRepositoryImpl]
 * only.
 */
data class AppState(
    val onboardingCompleted: Boolean,
    val activeUserId: String?
) {
    companion object {
        /** The state of a fresh install, before onboarding has ever run. */
        val INITIAL = AppState(
            onboardingCompleted = false,
            activeUserId = null
        )
    }
}

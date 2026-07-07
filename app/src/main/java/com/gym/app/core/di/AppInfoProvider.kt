package com.gym.app.core.di

import javax.inject.Inject
import javax.inject.Singleton

/**
 * PHASE 01C — DEPENDENCY INJECTION FOUNDATION
 *
 * A minimal, purely technical infrastructure dependency. Its only purpose
 * in this phase is to prove that Hilt constructor injection works end to
 * end (Application → Activity → ViewModel).
 *
 * It exposes technical/internal labels only — never user data, never a
 * fixed weight/goal/profile, never anything from the future
 * "User Profile + Goal + Preferences" product model. That model is
 * explicitly out of scope for this phase (see PROJECT_HANDOFF.md).
 *
 * No [dagger.Module] is used here on purpose: since this is a concrete
 * class with an [Inject]-annotated constructor and no interface to bind,
 * Hilt can construct it directly. Adding a module for this would be an
 * unnecessary abstraction.
 */
@Singleton
class AppInfoProvider @Inject constructor() {

    /** Technical application name (not user-facing profile data). */
    fun getApplicationName(): String = "GYM"

    /** Internal technical label identifying this DI foundation phase. */
    fun getTechnicalLabel(): String = "phase-01c-di-foundation"
}

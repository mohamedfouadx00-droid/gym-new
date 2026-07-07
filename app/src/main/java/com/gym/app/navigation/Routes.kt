package com.gym.app.navigation

/**
 * PHASE 01B — NAVIGATION FOUNDATION
 *
 * Centralized route definitions for the entire navigation graph.
 *
 * Every navigation call in the app must reference [Routes] instead of raw
 * string literals, so route strings are never duplicated or mistyped in
 * more than one place.
 *
 * This is intentionally a flat, simple structure for now (five temporary
 * navigation-test destinations). It is designed to be extended later with
 * real feature destinations (Onboarding, Profile, Goals, Preferences,
 * Workout Logging, Nutrition, etc.) without breaking existing call sites.
 */
sealed class Routes(val route: String) {
    data object Start : Routes("start")
    data object Home : Routes("home")
    data object Workout : Routes("workout")
    data object Progress : Routes("progress")
    data object Settings : Routes("settings")

    /**
     * PHASE 01H-02B — STARTUP & BASIC ONBOARDING
     *
     * Dedicated onboarding sub-graph. Kept as its own nested set of routes
     * (rather than flat top-level routes) so future onboarding steps (Goal
     * Setup, Workout Preferences, Lifestyle Preferences, completion) can be
     * added without touching [Routes.Start] or the main feature
     * destinations above.
     */
    sealed class Onboarding(route: String) : Routes(route) {
        /** Graph route used to nest the onboarding flow inside [AppNavHost]. */
        data object Graph : Onboarding("onboarding_graph")

        /** First real onboarding screen: basic profile input. */
        data object BasicProfile : Onboarding("onboarding_basic_profile")

        /**
         * Placeholder destination reached after a valid basic profile is
         * saved. Reserved for future Goal Setup (Phase 02C). Implements no
         * feature logic itself.
         */
        data object NextPlaceholder : Onboarding("onboarding_next_placeholder")
    }
}

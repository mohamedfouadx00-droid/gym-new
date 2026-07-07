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
}

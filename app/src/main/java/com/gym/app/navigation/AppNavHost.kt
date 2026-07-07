package com.gym.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.gym.app.ui.screens.HomeScreen
import com.gym.app.ui.screens.OnboardingBasicProfileScreen
import com.gym.app.ui.screens.OnboardingCompleteScreen
import com.gym.app.ui.screens.OnboardingGoalSetupScreen
import com.gym.app.ui.screens.OnboardingLifestylePreferencesScreen
import com.gym.app.ui.screens.OnboardingWorkoutPreferencesScreen
import com.gym.app.ui.screens.ProfileScreen
import com.gym.app.ui.screens.ProgressScreen
import com.gym.app.ui.screens.SettingsScreen
import com.gym.app.ui.screens.StartScreen
import com.gym.app.ui.screens.WorkoutScreen

/**
 * PHASE 01B — NAVIGATION FOUNDATION
 * PHASE 01H-02B — STARTUP & BASIC ONBOARDING
 *
 * The single navigation host for the whole app. Wires together the
 * navigation-test screens using the centralized [Routes], plus (as of
 * Phase 01H-02B) the onboarding sub-graph ([Routes.Onboarding]). No
 * feature logic, no data models, and no business rules live here — this
 * file only declares the graph shape.
 */
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Start.route
    ) {
        composable(Routes.Start.route) {
            StartScreen(navController = navController)
        }
        composable(Routes.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Routes.Workout.route) {
            WorkoutScreen(navController = navController)
        }
        composable(Routes.Progress.route) {
            ProgressScreen(navController = navController)
        }
        composable(Routes.Settings.route) {
            SettingsScreen(navController = navController)
        }
        composable(Routes.Profile.route) {
            ProfileScreen(navController = navController)
        }

        // PHASE 01H-02B / 02C-02F — Onboarding sub-graph. Nested so the
        // whole onboarding flow (Basic Profile, Goal Setup, Workout
        // Preferences, Lifestyle Preferences, completion) stays inside
        // this graph only, without touching the top-level destinations
        // above.
        navigation(
            route = Routes.Onboarding.Graph.route,
            startDestination = Routes.Onboarding.BasicProfile.route
        ) {
            composable(Routes.Onboarding.BasicProfile.route) {
                OnboardingBasicProfileScreen(navController = navController)
            }
            composable(Routes.Onboarding.GoalSetup.route) {
                OnboardingGoalSetupScreen(navController = navController)
            }
            composable(Routes.Onboarding.WorkoutPreferences.route) {
                OnboardingWorkoutPreferencesScreen(navController = navController)
            }
            composable(Routes.Onboarding.LifestylePreferences.route) {
                OnboardingLifestylePreferencesScreen(navController = navController)
            }
            composable(Routes.Onboarding.Complete.route) {
                OnboardingCompleteScreen(navController = navController)
            }
        }
    }
}

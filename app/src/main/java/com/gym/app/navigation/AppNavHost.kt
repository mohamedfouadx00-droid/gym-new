package com.gym.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gym.app.ui.screens.HomeScreen
import com.gym.app.ui.screens.ProgressScreen
import com.gym.app.ui.screens.SettingsScreen
import com.gym.app.ui.screens.StartScreen
import com.gym.app.ui.screens.WorkoutScreen

/**
 * PHASE 01B — NAVIGATION FOUNDATION
 *
 * The single navigation host for the whole app. It only wires together
 * temporary navigation-test screens using the centralized [Routes]. No
 * feature logic, no data models, and no business rules live here.
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
    }
}

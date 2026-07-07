package com.gym.app.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.gym.app.R

/**
 * PHASE 01B — NAVIGATION FOUNDATION
 *
 * Temporary navigation-test screen only. Future phases will replace this
 * with the real Workout feature, which will depend on User Profile, Goal,
 * and Preferences.
 */
@Composable
fun WorkoutScreen(navController: NavHostController) {
    NavTestScreenContent(
        title = stringResource(id = R.string.workout_title),
        navController = navController
    )
}

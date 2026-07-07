package com.gym.app.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.gym.app.R

/**
 * PHASE 01B — NAVIGATION FOUNDATION
 *
 * Temporary navigation-test screen only. Future phases will replace this
 * with the real Home feature, which will aggregate information from other
 * features for the active user (based on User Profile + Goal + Preferences).
 */
@Composable
fun HomeScreen(navController: NavHostController) {
    NavTestScreenContent(
        title = stringResource(id = R.string.home_title),
        navController = navController
    )
}

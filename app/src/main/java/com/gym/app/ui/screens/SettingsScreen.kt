package com.gym.app.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.gym.app.R

/**
 * PHASE 01B — NAVIGATION FOUNDATION
 *
 * Temporary navigation-test screen only. Future phases will replace this
 * with the real Settings feature, which will read/update Preferences.
 */
@Composable
fun SettingsScreen(navController: NavHostController) {
    NavTestScreenContent(
        title = stringResource(id = R.string.settings_title),
        navController = navController
    )
}

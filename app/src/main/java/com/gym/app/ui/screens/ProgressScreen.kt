package com.gym.app.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.gym.app.R

/**
 * PHASE 01B — NAVIGATION FOUNDATION
 *
 * Temporary navigation-test screen only. Future phases will replace this
 * with the real Progress feature.
 */
@Composable
fun ProgressScreen(navController: NavHostController) {
    NavTestScreenContent(
        title = stringResource(id = R.string.progress_title),
        navController = navController
    )
}

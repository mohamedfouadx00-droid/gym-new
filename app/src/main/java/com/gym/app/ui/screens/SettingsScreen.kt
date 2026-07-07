package com.gym.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.gym.app.R
import com.gym.app.navigation.Routes

/**
 * PHASE 01B — NAVIGATION FOUNDATION
 * PHASE 02G — PROFILE SCREEN
 *
 * Still mostly a navigation-test screen; the real Settings feature will
 * read/update Preferences later. Now also offers a way to reach the real
 * Profile screen ([ProfileScreen], Phase 02G).
 */
@Composable
fun SettingsScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.settings_title),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = stringResource(id = R.string.nav_test_screen_body),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 16.dp)
        )
        Button(
            modifier = Modifier.padding(top = 32.dp),
            onClick = { navController.navigate(Routes.Profile.route) }
        ) {
            Text(text = stringResource(id = R.string.settings_go_profile))
        }
        Button(
            modifier = Modifier.padding(top = 12.dp),
            onClick = { navController.popBackStack() }
        ) {
            Text(text = stringResource(id = R.string.nav_back))
        }
    }
}

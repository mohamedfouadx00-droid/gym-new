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

/**
 * PHASE 01B — NAVIGATION FOUNDATION
 *
 * Shared body for the temporary navigation-test destinations (Home, Workout,
 * Progress, Settings). These are technical screens only, meant to prove that
 * navigation and back-navigation work correctly — they do not implement any
 * real feature yet.
 *
 * All visible text is Arabic and comes from string resources.
 */
@Composable
internal fun NavTestScreenContent(
    title: String,
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = stringResource(id = R.string.nav_test_screen_body),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 16.dp)
        )

        Button(
            modifier = Modifier.padding(top = 32.dp),
            onClick = { navController.popBackStack() }
        ) {
            Text(text = stringResource(id = R.string.nav_back))
        }
    }
}

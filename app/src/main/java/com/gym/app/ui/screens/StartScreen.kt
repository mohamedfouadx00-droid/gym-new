package com.gym.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.gym.app.R
import com.gym.app.navigation.Routes

/**
 * PHASE 01B — NAVIGATION FOUNDATION
 * PHASE 01C — DEPENDENCY INJECTION FOUNDATION
 *
 * Start destination of the navigation graph. This is a temporary technical
 * screen only, used to prove that navigation to the other four destinations
 * works correctly. It implements no real feature.
 *
 * As of Phase 01C it is also temporarily wired to [StartViewModel] (via
 * hiltViewModel()) purely to prove that Hilt ViewModel injection works end
 * to end. The small technical status line rendered below the title carries
 * no feature meaning.
 */
@Composable
fun StartScreen(
    navController: NavHostController,
    viewModel: StartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.start_title),
            style = MaterialTheme.typography.headlineMedium
        )

        if (uiState.isDependencyInjectionReady) {
            Text(
                text = stringResource(
                    id = R.string.start_di_status,
                    uiState.applicationName
                ),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            onClick = { navController.navigate(Routes.Home.route) }
        ) {
            Text(text = stringResource(id = R.string.nav_go_home))
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            onClick = { navController.navigate(Routes.Workout.route) }
        ) {
            Text(text = stringResource(id = R.string.nav_go_workout))
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            onClick = { navController.navigate(Routes.Progress.route) }
        ) {
            Text(text = stringResource(id = R.string.nav_go_progress))
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            onClick = { navController.navigate(Routes.Settings.route) }
        ) {
            Text(text = stringResource(id = R.string.nav_go_settings))
        }
    }
}

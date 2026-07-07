package com.gym.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
 * PHASE 01H — APP START LOGIC
 *
 * Start destination of the navigation graph. Purely a routing screen now:
 * all startup decision logic lives in [StartViewModel] (see
 * [StartDestination]); this composable only observes the resulting
 * [StartDestination] and performs the corresponding one-time navigation
 * once it is known, replacing itself in the back stack so the user can
 * never navigate back to this transient screen.
 *
 * While [StartDestination.Undetermined] (briefly, while the DataStore read
 * is in flight on a background coroutine), a minimal loading indicator is
 * shown — no feature UI, no blocking of the main thread.
 */
@Composable
fun StartScreen(
    navController: NavHostController,
    viewModel: StartViewModel = hiltViewModel()
) {
    val destination by viewModel.destination.collectAsStateWithLifecycle()

    LaunchedEffect(destination) {
        when (destination) {
            is StartDestination.Home -> {
                navController.navigate(Routes.Home.route) {
                    popUpTo(Routes.Start.route) { inclusive = true }
                }
            }

            is StartDestination.Onboarding -> {
                navController.navigate(Routes.Onboarding.Graph.route) {
                    popUpTo(Routes.Start.route) { inclusive = true }
                }
            }

            is StartDestination.Undetermined -> {
                // Still resolving app state; do nothing yet.
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Text(
            text = stringResource(id = R.string.start_loading),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

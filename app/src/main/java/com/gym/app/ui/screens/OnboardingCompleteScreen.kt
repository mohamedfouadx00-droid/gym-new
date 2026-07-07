package com.gym.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
 * PHASE 02F — COMPLETE ONBOARDING
 *
 * Final onboarding screen. Purely a routing/status screen: all validation
 * and completion logic lives in [OnboardingCompleteViewModel]; this
 * composable only observes [OnboardingCompleteResult] and reacts to it.
 *
 * On [OnboardingCompleteResult.Success], navigates to Home, clearing the
 * whole onboarding back stack. On
 * [OnboardingCompleteResult.Incomplete], shows an explanatory message and
 * a retry action instead of ever faking success.
 */
@Composable
fun OnboardingCompleteScreen(
    navController: NavHostController,
    viewModel: OnboardingCompleteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.result) {
        if (uiState.result is OnboardingCompleteResult.Success) {
            navController.navigate(Routes.Home.route) {
                popUpTo(Routes.Onboarding.Graph.route) { inclusive = true }
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
        when (uiState.result) {
            is OnboardingCompleteResult.Checking -> {
                CircularProgressIndicator()
                Text(
                    text = stringResource(id = R.string.onboarding_complete_checking),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            is OnboardingCompleteResult.Incomplete -> {
                Text(
                    text = stringResource(id = R.string.onboarding_complete_incomplete_title),
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = stringResource(id = R.string.onboarding_complete_incomplete_body),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Button(
                    onClick = viewModel::verifyAndComplete,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                ) {
                    Text(text = stringResource(id = R.string.onboarding_complete_retry))
                }
            }

            is OnboardingCompleteResult.Success -> {
                CircularProgressIndicator()
                Text(
                    text = stringResource(id = R.string.onboarding_complete_success),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

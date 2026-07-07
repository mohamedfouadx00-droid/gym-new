package com.gym.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
 * PHASE 02B — BASIC PROFILE INPUT
 *
 * Placeholder destination reached after a valid basic profile has been
 * saved. Reserved for future Goal Setup (Phase 02C) — implements no
 * feature logic, no Goal Setup, no onboarding completion. Does not
 * navigate anywhere else itself; the next onboarding step (02C) will
 * replace this screen.
 */
@Composable
fun OnboardingNextPlaceholderScreen(
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
            text = stringResource(id = R.string.onboarding_next_placeholder_title),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = stringResource(id = R.string.onboarding_next_placeholder_body),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

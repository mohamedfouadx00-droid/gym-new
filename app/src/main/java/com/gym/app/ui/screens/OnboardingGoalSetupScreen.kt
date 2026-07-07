package com.gym.app.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.gym.app.R
import com.gym.app.domain.model.Goal
import com.gym.app.domain.model.PrimaryGoal
import com.gym.app.navigation.Routes

/**
 * PHASE 02C — GOAL SETUP
 *
 * Second onboarding screen. Collects only fields that already exist on
 * [Goal] (primary goal, optional target weight, secondary goals). All UI
 * state lives in [OnboardingGoalSetupViewModel]; this composable only
 * renders it and forwards input events — no persistence logic here.
 *
 * Arabic-only, RTL (inherited from [com.gym.app.ui.theme.GymTheme]). All
 * visible text comes from string resources.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingGoalSetupScreen(
    navController: NavHostController,
    viewModel: OnboardingGoalSetupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.saveComplete) {
        if (uiState.saveComplete) {
            navController.navigate(Routes.Onboarding.WorkoutPreferences.route) {
                popUpTo(Routes.Onboarding.GoalSetup.route) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.onboarding_goal_setup_title),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = stringResource(id = R.string.onboarding_goal_setup_subtitle),
            style = MaterialTheme.typography.bodyMedium
        )

        PrimaryGoalDropdown(
            selected = uiState.primaryGoal,
            onSelected = viewModel::onPrimaryGoalChanged
        )

        if (Goal.isTargetWeightApplicable(uiState.primaryGoal)) {
            OutlinedTextField(
                value = uiState.targetWeightKg,
                onValueChange = viewModel::onTargetWeightChanged,
                label = { Text(stringResource(id = R.string.onboarding_field_target_weight)) },
                isError = uiState.targetWeightError,
                supportingText = {
                    if (uiState.targetWeightError) {
                        Text(stringResource(id = R.string.onboarding_field_target_weight_error))
                    }
                },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        SecondaryGoalsSelector(
            primaryGoal = uiState.primaryGoal,
            selected = uiState.secondaryGoals,
            onToggle = viewModel::onSecondaryGoalToggled
        )

        if (uiState.errorNoActiveUser) {
            Text(
                text = stringResource(id = R.string.onboarding_error_no_active_user),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Button(
            onClick = viewModel::onSaveClicked,
            enabled = !uiState.isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isSaving) {
                CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
            }
            Text(text = stringResource(id = R.string.onboarding_save_and_continue))
        }
    }
}

private fun labelForPrimaryGoal(goal: PrimaryGoal): Int = when (goal) {
    PrimaryGoal.WEIGHT_GAIN -> R.string.primary_goal_weight_gain
    PrimaryGoal.WEIGHT_LOSS -> R.string.primary_goal_weight_loss
    PrimaryGoal.MUSCLE_BUILDING -> R.string.primary_goal_muscle_building
    PrimaryGoal.STRENGTH -> R.string.primary_goal_strength
    PrimaryGoal.MAINTENANCE -> R.string.primary_goal_maintenance
    PrimaryGoal.GENERAL_FITNESS -> R.string.primary_goal_general_fitness
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrimaryGoalDropdown(
    selected: PrimaryGoal,
    onSelected: (PrimaryGoal) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = stringResource(id = labelForPrimaryGoal(selected)),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(id = R.string.onboarding_field_primary_goal)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            PrimaryGoal.entries.forEach { goal ->
                DropdownMenuItem(
                    text = { Text(stringResource(id = labelForPrimaryGoal(goal))) },
                    onClick = {
                        onSelected(goal)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun SecondaryGoalsSelector(
    primaryGoal: PrimaryGoal,
    selected: Set<PrimaryGoal>,
    onToggle: (PrimaryGoal) -> Unit
) {
    Column {
        Text(
            text = stringResource(id = R.string.onboarding_field_secondary_goals),
            style = MaterialTheme.typography.labelLarge
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(top = 4.dp)
                .horizontalScroll(rememberScrollState())
        ) {
            PrimaryGoal.entries
                .filter { it != primaryGoal }
                .forEach { goal ->
                    FilterChip(
                        selected = goal in selected,
                        onClick = { onToggle(goal) },
                        label = { Text(stringResource(id = labelForPrimaryGoal(goal))) }
                    )
                }
        }
    }
}

package com.gym.app.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
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
import com.gym.app.domain.model.Weekday
import com.gym.app.domain.model.WorkoutLocation
import com.gym.app.navigation.Routes

/**
 * PHASE 02D — WORKOUT PREFERENCES
 *
 * Third onboarding screen. Collects only the workout-related subset of
 * [com.gym.app.domain.model.UserPreferences] fields. All UI state lives
 * in [OnboardingWorkoutPreferencesViewModel]; this composable only
 * renders it and forwards input events.
 *
 * Arabic-only, RTL. All visible text comes from string resources.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingWorkoutPreferencesScreen(
    navController: NavHostController,
    viewModel: OnboardingWorkoutPreferencesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.saveComplete) {
        if (uiState.saveComplete) {
            navController.navigate(Routes.Onboarding.LifestylePreferences.route) {
                popUpTo(Routes.Onboarding.WorkoutPreferences.route) { inclusive = true }
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
            text = stringResource(id = R.string.onboarding_workout_preferences_title),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = stringResource(id = R.string.onboarding_workout_preferences_subtitle),
            style = MaterialTheme.typography.bodyMedium
        )

        WorkoutLocationDropdown(
            selected = uiState.workoutLocation,
            onSelected = viewModel::onWorkoutLocationChanged
        )

        WeekdaySelector(
            titleRes = R.string.onboarding_field_preferred_workout_days,
            selected = uiState.preferredWorkoutDays,
            onToggle = viewModel::onWorkoutDayToggled
        )

        WeekdaySelector(
            titleRes = R.string.onboarding_field_rest_days,
            selected = uiState.restDays,
            onToggle = viewModel::onRestDayToggled
        )

        if (uiState.daysOverlapError) {
            Text(
                text = stringResource(id = R.string.onboarding_field_days_overlap_error),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Text(
            text = stringResource(id = R.string.onboarding_field_preferred_workout_time),
            style = MaterialTheme.typography.labelLarge
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = uiState.preferredWorkoutHour,
                onValueChange = viewModel::onPreferredHourChanged,
                label = { Text(stringResource(id = R.string.onboarding_field_hour)) },
                isError = uiState.timeError,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = uiState.preferredWorkoutMinute,
                onValueChange = viewModel::onPreferredMinuteChanged,
                label = { Text(stringResource(id = R.string.onboarding_field_minute)) },
                isError = uiState.timeError,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.weight(1f)
            )
        }
        if (uiState.timeError) {
            Text(
                text = stringResource(id = R.string.onboarding_field_time_error),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

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

private fun labelForWeekday(day: Weekday): Int = when (day) {
    Weekday.MONDAY -> R.string.weekday_monday
    Weekday.TUESDAY -> R.string.weekday_tuesday
    Weekday.WEDNESDAY -> R.string.weekday_wednesday
    Weekday.THURSDAY -> R.string.weekday_thursday
    Weekday.FRIDAY -> R.string.weekday_friday
    Weekday.SATURDAY -> R.string.weekday_saturday
    Weekday.SUNDAY -> R.string.weekday_sunday
}

@Composable
private fun WeekdaySelector(
    titleRes: Int,
    selected: Set<Weekday>,
    onToggle: (Weekday) -> Unit
) {
    Column {
        Text(
            text = stringResource(id = titleRes),
            style = MaterialTheme.typography.labelLarge
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(top = 4.dp)
                .horizontalScroll(rememberScrollState())
        ) {
            Weekday.entries.forEach { day ->
                FilterChip(
                    selected = day in selected,
                    onClick = { onToggle(day) },
                    label = { Text(stringResource(id = labelForWeekday(day))) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorkoutLocationDropdown(
    selected: WorkoutLocation,
    onSelected: (WorkoutLocation) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val labelFor: (WorkoutLocation) -> Int = {
        when (it) {
            WorkoutLocation.HOME -> R.string.workout_location_home
            WorkoutLocation.GYM -> R.string.workout_location_gym
            WorkoutLocation.OUTDOOR -> R.string.workout_location_outdoor
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = stringResource(id = labelFor(selected)),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(id = R.string.onboarding_field_workout_location)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            WorkoutLocation.entries.forEach { location ->
                DropdownMenuItem(
                    text = { Text(stringResource(id = labelFor(location))) },
                    onClick = {
                        onSelected(location)
                        expanded = false
                    }
                )
            }
        }
    }
}

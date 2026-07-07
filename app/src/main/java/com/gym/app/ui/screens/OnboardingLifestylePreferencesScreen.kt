package com.gym.app.ui.screens

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.gym.app.R
import com.gym.app.domain.model.BudgetLevel
import com.gym.app.domain.model.UnitSystem
import com.gym.app.navigation.Routes

/**
 * PHASE 02E — LIFESTYLE PREFERENCES
 *
 * Fourth onboarding screen. Collects only the lifestyle-related subset of
 * [com.gym.app.domain.model.UserPreferences] fields: meal count,
 * sleep/wake time, budget level, unit system, and reminders. All UI state
 * lives in [OnboardingLifestylePreferencesViewModel]; this composable only
 * renders it and forwards input events.
 *
 * Arabic-only, RTL. All visible text comes from string resources.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingLifestylePreferencesScreen(
    navController: NavHostController,
    viewModel: OnboardingLifestylePreferencesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.saveComplete) {
        if (uiState.saveComplete) {
            navController.navigate(Routes.Onboarding.Complete.route) {
                popUpTo(Routes.Onboarding.LifestylePreferences.route) { inclusive = true }
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
            text = stringResource(id = R.string.onboarding_lifestyle_preferences_title),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = stringResource(id = R.string.onboarding_lifestyle_preferences_subtitle),
            style = MaterialTheme.typography.bodyMedium
        )

        OutlinedTextField(
            value = uiState.preferredMealCount,
            onValueChange = viewModel::onMealCountChanged,
            label = { Text(stringResource(id = R.string.onboarding_field_meal_count)) },
            isError = uiState.mealCountError,
            supportingText = {
                if (uiState.mealCountError) {
                    Text(stringResource(id = R.string.onboarding_field_meal_count_error))
                }
            },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = stringResource(id = R.string.onboarding_field_sleep_time),
            style = MaterialTheme.typography.labelLarge
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = uiState.sleepHour,
                onValueChange = viewModel::onSleepHourChanged,
                label = { Text(stringResource(id = R.string.onboarding_field_hour)) },
                isError = uiState.timeError,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = uiState.sleepMinute,
                onValueChange = viewModel::onSleepMinuteChanged,
                label = { Text(stringResource(id = R.string.onboarding_field_minute)) },
                isError = uiState.timeError,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = stringResource(id = R.string.onboarding_field_wake_time),
            style = MaterialTheme.typography.labelLarge
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = uiState.wakeHour,
                onValueChange = viewModel::onWakeHourChanged,
                label = { Text(stringResource(id = R.string.onboarding_field_hour)) },
                isError = uiState.timeError,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = uiState.wakeMinute,
                onValueChange = viewModel::onWakeMinuteChanged,
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

        BudgetLevelDropdown(
            selected = uiState.budgetLevel,
            onSelected = viewModel::onBudgetLevelChanged
        )

        UnitSystemDropdown(
            selected = uiState.unitSystem,
            onSelected = viewModel::onUnitSystemChanged
        )

        Text(
            text = stringResource(id = R.string.onboarding_field_reminders),
            style = MaterialTheme.typography.labelLarge
        )
        ReminderRow(
            labelRes = R.string.reminder_workout,
            checked = uiState.reminderPreferences.workoutReminders,
            onCheckedChange = viewModel::onWorkoutRemindersToggled
        )
        ReminderRow(
            labelRes = R.string.reminder_meal,
            checked = uiState.reminderPreferences.mealReminders,
            onCheckedChange = viewModel::onMealRemindersToggled
        )
        ReminderRow(
            labelRes = R.string.reminder_sleep,
            checked = uiState.reminderPreferences.sleepReminders,
            onCheckedChange = viewModel::onSleepRemindersToggled
        )
        ReminderRow(
            labelRes = R.string.reminder_hydration,
            checked = uiState.reminderPreferences.hydrationReminders,
            onCheckedChange = viewModel::onHydrationRemindersToggled
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

@Composable
private fun ReminderRow(
    labelRes: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(id = labelRes), style = MaterialTheme.typography.bodyMedium)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BudgetLevelDropdown(
    selected: BudgetLevel,
    onSelected: (BudgetLevel) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val labelFor: (BudgetLevel) -> Int = {
        when (it) {
            BudgetLevel.LOW -> R.string.budget_level_low
            BudgetLevel.MEDIUM -> R.string.budget_level_medium
            BudgetLevel.HIGH -> R.string.budget_level_high
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
            label = { Text(stringResource(id = R.string.onboarding_field_budget_level)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            BudgetLevel.entries.forEach { level ->
                DropdownMenuItem(
                    text = { Text(stringResource(id = labelFor(level))) },
                    onClick = {
                        onSelected(level)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitSystemDropdown(
    selected: UnitSystem,
    onSelected: (UnitSystem) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val labelFor: (UnitSystem) -> Int = {
        when (it) {
            UnitSystem.METRIC -> R.string.unit_system_metric
            UnitSystem.IMPERIAL -> R.string.unit_system_imperial
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
            label = { Text(stringResource(id = R.string.onboarding_field_unit_system)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            UnitSystem.entries.forEach { unit ->
                DropdownMenuItem(
                    text = { Text(stringResource(id = labelFor(unit))) },
                    onClick = {
                        onSelected(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.gym.app.domain.model.ActivityLevel
import com.gym.app.domain.model.BudgetLevel
import com.gym.app.domain.model.ExperienceLevel
import com.gym.app.domain.model.Gender
import com.gym.app.domain.model.Goal
import com.gym.app.domain.model.PrimaryGoal
import com.gym.app.domain.model.UnitSystem
import com.gym.app.domain.model.WorkoutLocation

/**
 * PHASE 02G — PROFILE SCREEN
 *
 * Lets the active user view and edit their saved [com.gym.app.domain.model
 * .UserProfile], [Goal], and [com.gym.app.domain.model.UserPreferences] in
 * one place. All data comes from [ProfileViewModel] via the existing
 * repositories only — no direct DAO access, no fake/hardcoded data. If a
 * record does not exist yet for the active user, its section shows a
 * short explanatory message instead of fabricated content.
 *
 * Arabic-only, RTL. All visible text comes from string resources.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            viewModel.onSaveSuccessConsumed()
        }
    }

    if (uiState.isLoading) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (uiState.errorNoActiveUser) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.onboarding_error_no_active_user),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.profile_title),
            style = MaterialTheme.typography.headlineMedium
        )

        // Profile section
        Text(
            text = stringResource(id = R.string.profile_section_profile),
            style = MaterialTheme.typography.titleMedium
        )
        if (!uiState.hasProfile) {
            Text(
                text = stringResource(id = R.string.profile_section_missing),
                style = MaterialTheme.typography.bodySmall
            )
        }

        OutlinedTextField(
            value = uiState.name,
            onValueChange = viewModel::onNameChanged,
            label = { Text(stringResource(id = R.string.onboarding_field_name)) },
            isError = uiState.nameError,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.age,
            onValueChange = viewModel::onAgeChanged,
            label = { Text(stringResource(id = R.string.onboarding_field_age)) },
            isError = uiState.ageError,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth()
        )
        ProfileGenderSelector(selected = uiState.gender, onSelected = viewModel::onGenderChanged)
        OutlinedTextField(
            value = uiState.heightCm,
            onValueChange = viewModel::onHeightChanged,
            label = { Text(stringResource(id = R.string.onboarding_field_height)) },
            isError = uiState.heightError,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.currentWeightKg,
            onValueChange = viewModel::onWeightChanged,
            label = { Text(stringResource(id = R.string.onboarding_field_weight)) },
            isError = uiState.weightError,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            modifier = Modifier.fillMaxWidth()
        )
        ProfileExperienceLevelDropdown(
            selected = uiState.experienceLevel,
            onSelected = viewModel::onExperienceLevelChanged
        )
        ProfileActivityLevelDropdown(
            selected = uiState.activityLevel,
            onSelected = viewModel::onActivityLevelChanged
        )

        HorizontalDivider()

        // Goal section
        Text(
            text = stringResource(id = R.string.profile_section_goal),
            style = MaterialTheme.typography.titleMedium
        )
        if (!uiState.hasGoal) {
            Text(
                text = stringResource(id = R.string.profile_section_missing),
                style = MaterialTheme.typography.bodySmall
            )
        }
        ProfilePrimaryGoalDropdown(
            selected = uiState.primaryGoal,
            onSelected = viewModel::onPrimaryGoalChanged
        )
        if (Goal.isTargetWeightApplicable(uiState.primaryGoal)) {
            OutlinedTextField(
                value = uiState.targetWeightKg,
                onValueChange = viewModel::onTargetWeightChanged,
                label = { Text(stringResource(id = R.string.onboarding_field_target_weight)) },
                isError = uiState.targetWeightError,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
        ProfileSecondaryGoalsSelector(
            primaryGoal = uiState.primaryGoal,
            selected = uiState.secondaryGoals,
            onToggle = viewModel::onSecondaryGoalToggled
        )

        HorizontalDivider()

        // Preferences section
        Text(
            text = stringResource(id = R.string.profile_section_preferences),
            style = MaterialTheme.typography.titleMedium
        )
        if (!uiState.hasPreferences) {
            Text(
                text = stringResource(id = R.string.profile_section_missing),
                style = MaterialTheme.typography.bodySmall
            )
        }
        ProfileWorkoutLocationDropdown(
            selected = uiState.workoutLocation,
            onSelected = viewModel::onWorkoutLocationChanged
        )
        OutlinedTextField(
            value = uiState.preferredMealCount,
            onValueChange = viewModel::onMealCountChanged,
            label = { Text(stringResource(id = R.string.onboarding_field_meal_count)) },
            isError = uiState.mealCountError,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth()
        )
        ProfileBudgetLevelDropdown(
            selected = uiState.budgetLevel,
            onSelected = viewModel::onBudgetLevelChanged
        )
        ProfileUnitSystemDropdown(
            selected = uiState.unitSystem,
            onSelected = viewModel::onUnitSystemChanged
        )

        Button(
            onClick = viewModel::onSaveClicked,
            enabled = !uiState.isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isSaving) {
                CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
            }
            Text(text = stringResource(id = R.string.profile_save_changes))
        }
    }
}

@Composable
private fun ProfileGenderSelector(selected: Gender?, onSelected: (Gender?) -> Unit) {
    Column {
        Text(
            text = stringResource(id = R.string.onboarding_field_gender),
            style = MaterialTheme.typography.labelLarge
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 4.dp)
        ) {
            FilterChip(
                selected = selected == Gender.MALE,
                onClick = { onSelected(Gender.MALE) },
                label = { Text(stringResource(id = R.string.onboarding_gender_male)) }
            )
            FilterChip(
                selected = selected == Gender.FEMALE,
                onClick = { onSelected(Gender.FEMALE) },
                label = { Text(stringResource(id = R.string.onboarding_gender_female)) }
            )
            FilterChip(
                selected = selected == null,
                onClick = { onSelected(null) },
                label = { Text(stringResource(id = R.string.onboarding_gender_unspecified)) }
            )
        }
    }
}

private fun labelForExperienceLevel(level: ExperienceLevel): Int = when (level) {
    ExperienceLevel.BEGINNER -> R.string.experience_level_beginner
    ExperienceLevel.INTERMEDIATE -> R.string.experience_level_intermediate
    ExperienceLevel.ADVANCED -> R.string.experience_level_advanced
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileExperienceLevelDropdown(
    selected: ExperienceLevel,
    onSelected: (ExperienceLevel) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = stringResource(id = labelForExperienceLevel(selected)),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(id = R.string.onboarding_field_experience_level)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            ExperienceLevel.entries.forEach { level ->
                DropdownMenuItem(
                    text = { Text(stringResource(id = labelForExperienceLevel(level))) },
                    onClick = { onSelected(level); expanded = false }
                )
            }
        }
    }
}

private fun labelForActivityLevel(level: ActivityLevel): Int = when (level) {
    ActivityLevel.SEDENTARY -> R.string.activity_level_sedentary
    ActivityLevel.LIGHTLY_ACTIVE -> R.string.activity_level_lightly_active
    ActivityLevel.MODERATELY_ACTIVE -> R.string.activity_level_moderately_active
    ActivityLevel.VERY_ACTIVE -> R.string.activity_level_very_active
    ActivityLevel.EXTRA_ACTIVE -> R.string.activity_level_extra_active
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileActivityLevelDropdown(
    selected: ActivityLevel,
    onSelected: (ActivityLevel) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = stringResource(id = labelForActivityLevel(selected)),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(id = R.string.onboarding_field_activity_level)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            ActivityLevel.entries.forEach { level ->
                DropdownMenuItem(
                    text = { Text(stringResource(id = labelForActivityLevel(level))) },
                    onClick = { onSelected(level); expanded = false }
                )
            }
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
private fun ProfilePrimaryGoalDropdown(
    selected: PrimaryGoal,
    onSelected: (PrimaryGoal) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = stringResource(id = labelForPrimaryGoal(selected)),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(id = R.string.onboarding_field_primary_goal)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            PrimaryGoal.entries.forEach { goal ->
                DropdownMenuItem(
                    text = { Text(stringResource(id = labelForPrimaryGoal(goal))) },
                    onClick = { onSelected(goal); expanded = false }
                )
            }
        }
    }
}

@Composable
private fun ProfileSecondaryGoalsSelector(
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
            PrimaryGoal.entries.filter { it != primaryGoal }.forEach { goal ->
                FilterChip(
                    selected = goal in selected,
                    onClick = { onToggle(goal) },
                    label = { Text(stringResource(id = labelForPrimaryGoal(goal))) }
                )
            }
        }
    }
}

private fun labelForWorkoutLocation(location: WorkoutLocation): Int = when (location) {
    WorkoutLocation.HOME -> R.string.workout_location_home
    WorkoutLocation.GYM -> R.string.workout_location_gym
    WorkoutLocation.OUTDOOR -> R.string.workout_location_outdoor
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileWorkoutLocationDropdown(
    selected: WorkoutLocation,
    onSelected: (WorkoutLocation) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = stringResource(id = labelForWorkoutLocation(selected)),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(id = R.string.onboarding_field_workout_location)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            WorkoutLocation.entries.forEach { location ->
                DropdownMenuItem(
                    text = { Text(stringResource(id = labelForWorkoutLocation(location))) },
                    onClick = { onSelected(location); expanded = false }
                )
            }
        }
    }
}

private fun labelForBudgetLevel(level: BudgetLevel): Int = when (level) {
    BudgetLevel.LOW -> R.string.budget_level_low
    BudgetLevel.MEDIUM -> R.string.budget_level_medium
    BudgetLevel.HIGH -> R.string.budget_level_high
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileBudgetLevelDropdown(
    selected: BudgetLevel,
    onSelected: (BudgetLevel) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = stringResource(id = labelForBudgetLevel(selected)),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(id = R.string.onboarding_field_budget_level)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            BudgetLevel.entries.forEach { level ->
                DropdownMenuItem(
                    text = { Text(stringResource(id = labelForBudgetLevel(level))) },
                    onClick = { onSelected(level); expanded = false }
                )
            }
        }
    }
}

private fun labelForUnitSystem(unit: UnitSystem): Int = when (unit) {
    UnitSystem.METRIC -> R.string.unit_system_metric
    UnitSystem.IMPERIAL -> R.string.unit_system_imperial
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileUnitSystemDropdown(
    selected: UnitSystem,
    onSelected: (UnitSystem) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = stringResource(id = labelForUnitSystem(selected)),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(id = R.string.onboarding_field_unit_system)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            UnitSystem.entries.forEach { unit ->
                DropdownMenuItem(
                    text = { Text(stringResource(id = labelForUnitSystem(unit))) },
                    onClick = { onSelected(unit); expanded = false }
                )
            }
        }
    }
}

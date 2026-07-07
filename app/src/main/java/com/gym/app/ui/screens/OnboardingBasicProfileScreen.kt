package com.gym.app.ui.screens

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DropdownMenuBox
import androidx.compose.material3.DropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenu
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
import com.gym.app.domain.model.ActivityLevel
import com.gym.app.domain.model.ExperienceLevel
import com.gym.app.domain.model.Gender
import com.gym.app.navigation.Routes

/**
 * PHASE 02B — BASIC PROFILE INPUT
 *
 * First real onboarding screen. Collects only fields that already exist
 * on [com.gym.app.domain.model.UserProfile] (name, age, gender, height,
 * current weight, experience level, activity level). All UI state lives
 * in [OnboardingBasicProfileViewModel]; this composable only renders it
 * and forwards input events — no persistence logic here.
 *
 * Arabic-only, RTL (inherited from [com.gym.app.ui.theme.GymTheme]). All
 * visible text comes from string resources.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingBasicProfileScreen(
    navController: NavHostController,
    viewModel: OnboardingBasicProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.saveComplete) {
        if (uiState.saveComplete) {
            navController.navigate(Routes.Onboarding.NextPlaceholder.route) {
                popUpTo(Routes.Onboarding.BasicProfile.route) { inclusive = true }
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
            text = stringResource(id = R.string.onboarding_basic_profile_title),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = stringResource(id = R.string.onboarding_basic_profile_subtitle),
            style = MaterialTheme.typography.bodyMedium
        )

        OutlinedTextField(
            value = uiState.name,
            onValueChange = viewModel::onNameChanged,
            label = { Text(stringResource(id = R.string.onboarding_field_name)) },
            isError = uiState.nameError,
            supportingText = {
                if (uiState.nameError) {
                    Text(stringResource(id = R.string.onboarding_field_name_error))
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.age,
            onValueChange = viewModel::onAgeChanged,
            label = { Text(stringResource(id = R.string.onboarding_field_age)) },
            isError = uiState.ageError,
            supportingText = {
                if (uiState.ageError) {
                    Text(
                        stringResource(
                            id = R.string.onboarding_field_age_error,
                            com.gym.app.domain.model.UserProfile.MIN_AGE,
                            com.gym.app.domain.model.UserProfile.MAX_AGE
                        )
                    )
                }
            },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth()
        )

        GenderSelector(
            selected = uiState.gender,
            onSelected = viewModel::onGenderChanged
        )

        OutlinedTextField(
            value = uiState.heightCm,
            onValueChange = viewModel::onHeightChanged,
            label = { Text(stringResource(id = R.string.onboarding_field_height)) },
            isError = uiState.heightError,
            supportingText = {
                if (uiState.heightError) {
                    Text(stringResource(id = R.string.onboarding_field_height_error))
                }
            },
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
            supportingText = {
                if (uiState.weightError) {
                    Text(stringResource(id = R.string.onboarding_field_weight_error))
                }
            },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            modifier = Modifier.fillMaxWidth()
        )

        ExperienceLevelDropdown(
            selected = uiState.experienceLevel,
            onSelected = viewModel::onExperienceLevelChanged
        )

        ActivityLevelDropdown(
            selected = uiState.activityLevel,
            onSelected = viewModel::onActivityLevelChanged
        )

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
private fun GenderSelector(
    selected: Gender?,
    onSelected: (Gender?) -> Unit
) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExperienceLevelDropdown(
    selected: ExperienceLevel,
    onSelected: (ExperienceLevel) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val labelFor: (ExperienceLevel) -> Int = {
        when (it) {
            ExperienceLevel.BEGINNER -> R.string.experience_level_beginner
            ExperienceLevel.INTERMEDIATE -> R.string.experience_level_intermediate
            ExperienceLevel.ADVANCED -> R.string.experience_level_advanced
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
            label = { Text(stringResource(id = R.string.onboarding_field_experience_level)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            ExperienceLevel.entries.forEach { level ->
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
private fun ActivityLevelDropdown(
    selected: ActivityLevel,
    onSelected: (ActivityLevel) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val labelFor: (ActivityLevel) -> Int = {
        when (it) {
            ActivityLevel.SEDENTARY -> R.string.activity_level_sedentary
            ActivityLevel.LIGHTLY_ACTIVE -> R.string.activity_level_lightly_active
            ActivityLevel.MODERATELY_ACTIVE -> R.string.activity_level_moderately_active
            ActivityLevel.VERY_ACTIVE -> R.string.activity_level_very_active
            ActivityLevel.EXTRA_ACTIVE -> R.string.activity_level_extra_active
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
            label = { Text(stringResource(id = R.string.onboarding_field_activity_level)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            ActivityLevel.entries.forEach { level ->
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

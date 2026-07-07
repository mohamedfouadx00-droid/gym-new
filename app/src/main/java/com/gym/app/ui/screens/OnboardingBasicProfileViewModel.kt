package com.gym.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.app.domain.appstate.AppStateRepository
import com.gym.app.domain.model.ActivityLevel
import com.gym.app.domain.model.ExperienceLevel
import com.gym.app.domain.model.Gender
import com.gym.app.domain.model.UserProfile
import com.gym.app.domain.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * PHASE 02B — BASIC PROFILE INPUT
 *
 * Raw, editable form state for the basic profile onboarding screen. Kept
 * entirely separate from [UserProfile] (the persisted domain model): this
 * is text/UI-facing state (so a partially typed age or height doesn't
 * have to be a valid [Int]/[Double] yet), plus per-field validation
 * errors and a save-in-progress flag.
 *
 * Only fields that already exist on [UserProfile] are collected here:
 * name, age, gender, height, current weight, experience level, and
 * activity level. No duplicate or invented fields.
 */
data class OnboardingBasicProfileUiState(
    val name: String = "",
    val age: String = "",
    val gender: Gender? = null,
    val heightCm: String = "",
    val currentWeightKg: String = "",
    val experienceLevel: ExperienceLevel = ExperienceLevel.BEGINNER,
    val activityLevel: ActivityLevel = ActivityLevel.SEDENTARY,
    val nameError: Boolean = false,
    val ageError: Boolean = false,
    val heightError: Boolean = false,
    val weightError: Boolean = false,
    val isSaving: Boolean = false,
    val saveComplete: Boolean = false
)

/**
 * PHASE 02B — BASIC PROFILE INPUT
 *
 * Owns the basic profile form state and its persistence, kept separate
 * from [OnboardingBasicProfileScreen] (UI state vs. persistence logic).
 *
 * On valid save:
 * - Generates a fresh, non-hardcoded `userId` (a random [UUID]) — this
 *   screen only ever runs for a brand-new local profile, so there is no
 *   existing `userId` to preserve yet.
 * - Persists the resulting [UserProfile] through the existing
 *   [UserProfileRepository] (Phase 01F) — never touches Room/DAOs
 *   directly.
 * - Stores that same `userId` as `activeUserId` via [AppStateRepository]
 *   (Phase 01G) — never touches DataStore directly.
 * - Deliberately does NOT set `onboardingCompleted = true`; that remains
 *   for a later onboarding step (Goal Setup and beyond) to decide once
 *   the full flow exists.
 */
@HiltViewModel
class OnboardingBasicProfileViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val appStateRepository: AppStateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingBasicProfileUiState())
    val uiState: StateFlow<OnboardingBasicProfileUiState> = _uiState.asStateFlow()

    fun onNameChanged(value: String) {
        _uiState.update { it.copy(name = value, nameError = false) }
    }

    fun onAgeChanged(value: String) {
        _uiState.update { it.copy(age = value, ageError = false) }
    }

    fun onGenderChanged(value: Gender?) {
        _uiState.update { it.copy(gender = value) }
    }

    fun onHeightChanged(value: String) {
        _uiState.update { it.copy(heightCm = value, heightError = false) }
    }

    fun onWeightChanged(value: String) {
        _uiState.update { it.copy(currentWeightKg = value, weightError = false) }
    }

    fun onExperienceLevelChanged(value: ExperienceLevel) {
        _uiState.update { it.copy(experienceLevel = value) }
    }

    fun onActivityLevelChanged(value: ActivityLevel) {
        _uiState.update { it.copy(activityLevel = value) }
    }

    /**
     * Validates the current form state and, if valid, saves a new
     * [UserProfile] and sets it as the active user. Safe to call multiple
     * times; re-entrant saves while one is already in progress are
     * ignored.
     */
    fun onSaveClicked() {
        val current = _uiState.value
        if (current.isSaving) return

        val trimmedName = current.name.trim()
        val age = current.age.trim().toIntOrNull()
        val height = current.heightCm.trim().toDoubleOrNull()
        val weight = current.currentWeightKg.trim().toDoubleOrNull()

        val nameError = trimmedName.isBlank()
        val ageError = age == null || !UserProfile.isValidAge(age)
        val heightError = height == null || !UserProfile.isValidHeight(height)
        val weightError = weight == null || !UserProfile.isValidWeight(weight)

        if (nameError || ageError || heightError || weightError) {
            _uiState.update {
                it.copy(
                    nameError = nameError,
                    ageError = ageError,
                    heightError = heightError,
                    weightError = weightError
                )
            }
            return
        }

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            // Non-hardcoded, freshly generated identifier for this new
            // local profile.
            val userId = UUID.randomUUID().toString()

            val profile = UserProfile(
                userId = userId,
                name = trimmedName,
                age = age!!,
                gender = current.gender,
                heightCm = height!!,
                currentWeightKg = weight!!,
                experienceLevel = current.experienceLevel,
                activityLevel = current.activityLevel
            )

            userProfileRepository.save(profile)
            appStateRepository.setActiveUserId(userId)
            // onboardingCompleted intentionally left untouched here; it is
            // not set to true until a future onboarding step completes
            // the whole flow.

            _uiState.update { it.copy(isSaving = false, saveComplete = true) }
        }
    }
}

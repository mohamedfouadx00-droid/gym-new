package com.gym.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.app.domain.appstate.AppStateRepository
import com.gym.app.domain.model.Goal
import com.gym.app.domain.model.PrimaryGoal
import com.gym.app.domain.repository.GoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * PHASE 02C — GOAL SETUP
 *
 * Raw, editable form state for the goal setup onboarding screen. Only
 * fields that already exist on [Goal] are collected: primary goal,
 * optional target weight, optional target date, and secondary goals.
 */
data class OnboardingGoalSetupUiState(
    val primaryGoal: PrimaryGoal = PrimaryGoal.GENERAL_FITNESS,
    val targetWeightKg: String = "",
    val targetWeightError: Boolean = false,
    val secondaryGoals: Set<PrimaryGoal> = emptySet(),
    val isSaving: Boolean = false,
    val saveComplete: Boolean = false,
    val errorNoActiveUser: Boolean = false
)

/**
 * PHASE 02C — GOAL SETUP
 *
 * Owns the goal setup form state and its persistence. Reads the current
 * `activeUserId` from [AppStateRepository] (never hardcoded) and persists
 * the resulting [Goal] through [GoalRepository] only — no direct DAO
 * access.
 */
@HiltViewModel
class OnboardingGoalSetupViewModel @Inject constructor(
    private val goalRepository: GoalRepository,
    private val appStateRepository: AppStateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingGoalSetupUiState())
    val uiState: StateFlow<OnboardingGoalSetupUiState> = _uiState.asStateFlow()

    fun onPrimaryGoalChanged(value: PrimaryGoal) {
        _uiState.update {
            it.copy(
                primaryGoal = value,
                // A goal cannot be both primary and secondary at once.
                secondaryGoals = it.secondaryGoals - value,
                targetWeightError = false
            )
        }
    }

    fun onTargetWeightChanged(value: String) {
        _uiState.update { it.copy(targetWeightKg = value, targetWeightError = false) }
    }

    fun onSecondaryGoalToggled(value: PrimaryGoal) {
        _uiState.update { state ->
            if (value == state.primaryGoal) return@update state
            val updated = if (value in state.secondaryGoals) {
                state.secondaryGoals - value
            } else {
                state.secondaryGoals + value
            }
            state.copy(secondaryGoals = updated)
        }
    }

    /**
     * Validates the current form state and, if valid, saves a new [Goal]
     * for the current `activeUserId`. Safe to call multiple times;
     * re-entrant saves while one is already in progress are ignored.
     */
    fun onSaveClicked() {
        val current = _uiState.value
        if (current.isSaving) return

        val isTargetWeightApplicable = Goal.isTargetWeightApplicable(current.primaryGoal)
        val targetWeight = if (current.targetWeightKg.isBlank()) {
            null
        } else {
            current.targetWeightKg.trim().toDoubleOrNull()
        }

        val targetWeightError = isTargetWeightApplicable &&
            current.targetWeightKg.isNotBlank() &&
            (targetWeight == null || !Goal.isValidTargetWeight(targetWeight))

        if (targetWeightError) {
            _uiState.update { it.copy(targetWeightError = true) }
            return
        }

        _uiState.update { it.copy(isSaving = true, errorNoActiveUser = false) }

        viewModelScope.launch {
            val activeUserId = appStateRepository.appState.first().activeUserId

            if (activeUserId == null) {
                _uiState.update { it.copy(isSaving = false, errorNoActiveUser = true) }
                return@launch
            }

            val today = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis())

            val goal = Goal(
                goalId = UUID.randomUUID().toString(),
                userId = activeUserId,
                primaryGoal = current.primaryGoal,
                targetWeightKg = if (isTargetWeightApplicable) targetWeight else null,
                goalStartDate = today,
                targetDate = null,
                secondaryGoals = current.secondaryGoals.toList()
            )

            goalRepository.save(goal)

            _uiState.update { it.copy(isSaving = false, saveComplete = true) }
        }
    }
}

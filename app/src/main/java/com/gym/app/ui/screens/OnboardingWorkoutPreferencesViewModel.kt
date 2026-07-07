package com.gym.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.app.domain.appstate.AppStateRepository
import com.gym.app.domain.model.BudgetLevel
import com.gym.app.domain.model.TimeOfDay
import com.gym.app.domain.model.UserPreferences
import com.gym.app.domain.model.Weekday
import com.gym.app.domain.model.WorkoutLocation
import com.gym.app.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * PHASE 02D — WORKOUT PREFERENCES
 *
 * Raw, editable form state for the workout-related subset of
 * [UserPreferences]: workout location, preferred workout days, rest days,
 * and preferred workout time. Only fields that already exist on
 * [UserPreferences] are collected here.
 */
data class OnboardingWorkoutPreferencesUiState(
    val workoutLocation: WorkoutLocation = WorkoutLocation.HOME,
    val preferredWorkoutDays: Set<Weekday> = emptySet(),
    val restDays: Set<Weekday> = emptySet(),
    val preferredWorkoutHour: String = "",
    val preferredWorkoutMinute: String = "",
    val timeError: Boolean = false,
    val daysOverlapError: Boolean = false,
    val isSaving: Boolean = false,
    val saveComplete: Boolean = false,
    val errorNoActiveUser: Boolean = false
)

/**
 * PHASE 02D — WORKOUT PREFERENCES
 *
 * Owns the workout preferences form state and its persistence. Loads any
 * existing [UserPreferences] row for the current `activeUserId` first (so
 * fields outside this screen's scope, such as lifestyle fields saved by a
 * later step, are preserved) and only overwrites the workout-related
 * fields on save.
 */
@HiltViewModel
class OnboardingWorkoutPreferencesViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val appStateRepository: AppStateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingWorkoutPreferencesUiState())
    val uiState: StateFlow<OnboardingWorkoutPreferencesUiState> = _uiState.asStateFlow()

    private var activeUserId: String? = null
    private var existingPreferences: UserPreferences? = null

    init {
        viewModelScope.launch {
            val userId = appStateRepository.appState.first().activeUserId
            activeUserId = userId
            if (userId == null) {
                _uiState.update { it.copy(errorNoActiveUser = true) }
                return@launch
            }
            val existing = userPreferencesRepository.getByUserId(userId)
            existingPreferences = existing
            if (existing != null) {
                _uiState.update {
                    it.copy(
                        workoutLocation = existing.workoutLocation,
                        preferredWorkoutDays = existing.preferredWorkoutDays,
                        restDays = existing.restDays,
                        preferredWorkoutHour = existing.preferredWorkoutTime?.hour?.toString() ?: "",
                        preferredWorkoutMinute = existing.preferredWorkoutTime?.minute?.toString() ?: ""
                    )
                }
            }
        }
    }

    fun onWorkoutLocationChanged(value: WorkoutLocation) {
        _uiState.update { it.copy(workoutLocation = value) }
    }

    fun onWorkoutDayToggled(value: Weekday) {
        _uiState.update { state ->
            val updatedDays = if (value in state.preferredWorkoutDays) {
                state.preferredWorkoutDays - value
            } else {
                state.preferredWorkoutDays + value
            }
            state.copy(
                preferredWorkoutDays = updatedDays,
                restDays = state.restDays - value,
                daysOverlapError = false
            )
        }
    }

    fun onRestDayToggled(value: Weekday) {
        _uiState.update { state ->
            val updatedRest = if (value in state.restDays) {
                state.restDays - value
            } else {
                state.restDays + value
            }
            state.copy(
                restDays = updatedRest,
                preferredWorkoutDays = state.preferredWorkoutDays - value,
                daysOverlapError = false
            )
        }
    }

    fun onPreferredHourChanged(value: String) {
        _uiState.update { it.copy(preferredWorkoutHour = value, timeError = false) }
    }

    fun onPreferredMinuteChanged(value: String) {
        _uiState.update { it.copy(preferredWorkoutMinute = value, timeError = false) }
    }

    /**
     * Validates the current form state and, if valid, saves the
     * workout-related fields onto [UserPreferences] for the current
     * `activeUserId`, preserving any other, unrelated fields already
     * saved on that row.
     */
    fun onSaveClicked() {
        val current = _uiState.value
        if (current.isSaving) return

        val userId = activeUserId
        if (userId == null) {
            _uiState.update { it.copy(errorNoActiveUser = true) }
            return
        }

        val overlap = current.preferredWorkoutDays.intersect(current.restDays)
        if (overlap.isNotEmpty()) {
            _uiState.update { it.copy(daysOverlapError = true) }
            return
        }

        val preferredTime: TimeOfDay? = if (
            current.preferredWorkoutHour.isBlank() && current.preferredWorkoutMinute.isBlank()
        ) {
            null
        } else {
            val hour = current.preferredWorkoutHour.trim().toIntOrNull()
            val minute = current.preferredWorkoutMinute.trim().toIntOrNull()
            if (hour == null || minute == null) {
                _uiState.update { it.copy(timeError = true) }
                return
            }
            TimeOfDay.ofOrNull(hour, minute) ?: run {
                _uiState.update { it.copy(timeError = true) }
                return
            }
        }

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            val base = existingPreferences ?: UserPreferences(
                userId = userId,
                workoutLocation = current.workoutLocation,
                preferredWorkoutTime = null,
                preferredMealCount = 3,
                sleepTime = null,
                wakeTime = null,
                budgetLevel = BudgetLevel.MEDIUM,
                unitSystem = com.gym.app.domain.model.UnitSystem.METRIC
            )

            val updated = base.copy(
                userId = userId,
                workoutLocation = current.workoutLocation,
                preferredWorkoutDays = current.preferredWorkoutDays,
                restDays = current.restDays,
                preferredWorkoutTime = preferredTime
            )

            userPreferencesRepository.save(updated)

            _uiState.update { it.copy(isSaving = false, saveComplete = true) }
        }
    }
}

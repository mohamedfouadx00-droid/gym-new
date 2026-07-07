package com.gym.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.app.domain.appstate.AppStateRepository
import com.gym.app.domain.model.BudgetLevel
import com.gym.app.domain.model.ReminderPreferences
import com.gym.app.domain.model.TimeOfDay
import com.gym.app.domain.model.UnitSystem
import com.gym.app.domain.model.UserPreferences
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
 * PHASE 02E — LIFESTYLE PREFERENCES
 *
 * Raw, editable form state for the lifestyle-related subset of
 * [UserPreferences]: meal count, sleep/wake time, budget level, unit
 * system, and reminder preferences. Only fields that already exist on
 * [UserPreferences] are collected here.
 */
data class OnboardingLifestylePreferencesUiState(
    val preferredMealCount: String = "3",
    val mealCountError: Boolean = false,
    val sleepHour: String = "",
    val sleepMinute: String = "",
    val wakeHour: String = "",
    val wakeMinute: String = "",
    val timeError: Boolean = false,
    val budgetLevel: BudgetLevel = BudgetLevel.MEDIUM,
    val unitSystem: UnitSystem = UnitSystem.METRIC,
    val reminderPreferences: ReminderPreferences = ReminderPreferences(),
    val isSaving: Boolean = false,
    val saveComplete: Boolean = false,
    val errorNoActiveUser: Boolean = false
)

/**
 * PHASE 02E — LIFESTYLE PREFERENCES
 *
 * Owns the lifestyle preferences form state and its persistence. Loads any
 * existing [UserPreferences] row for the current `activeUserId` first —
 * preserving the workout-related fields already saved by Phase 02D — and
 * only overwrites the lifestyle-related fields on save.
 */
@HiltViewModel
class OnboardingLifestylePreferencesViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val appStateRepository: AppStateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingLifestylePreferencesUiState())
    val uiState: StateFlow<OnboardingLifestylePreferencesUiState> = _uiState.asStateFlow()

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
                        preferredMealCount = existing.preferredMealCount.toString(),
                        sleepHour = existing.sleepTime?.hour?.toString() ?: "",
                        sleepMinute = existing.sleepTime?.minute?.toString() ?: "",
                        wakeHour = existing.wakeTime?.hour?.toString() ?: "",
                        wakeMinute = existing.wakeTime?.minute?.toString() ?: "",
                        budgetLevel = existing.budgetLevel,
                        unitSystem = existing.unitSystem,
                        reminderPreferences = existing.reminderPreferences
                    )
                }
            }
        }
    }

    fun onMealCountChanged(value: String) {
        _uiState.update { it.copy(preferredMealCount = value, mealCountError = false) }
    }

    fun onSleepHourChanged(value: String) {
        _uiState.update { it.copy(sleepHour = value, timeError = false) }
    }

    fun onSleepMinuteChanged(value: String) {
        _uiState.update { it.copy(sleepMinute = value, timeError = false) }
    }

    fun onWakeHourChanged(value: String) {
        _uiState.update { it.copy(wakeHour = value, timeError = false) }
    }

    fun onWakeMinuteChanged(value: String) {
        _uiState.update { it.copy(wakeMinute = value, timeError = false) }
    }

    fun onBudgetLevelChanged(value: BudgetLevel) {
        _uiState.update { it.copy(budgetLevel = value) }
    }

    fun onUnitSystemChanged(value: UnitSystem) {
        _uiState.update { it.copy(unitSystem = value) }
    }

    fun onWorkoutRemindersToggled(value: Boolean) {
        _uiState.update { it.copy(reminderPreferences = it.reminderPreferences.copy(workoutReminders = value)) }
    }

    fun onMealRemindersToggled(value: Boolean) {
        _uiState.update { it.copy(reminderPreferences = it.reminderPreferences.copy(mealReminders = value)) }
    }

    fun onSleepRemindersToggled(value: Boolean) {
        _uiState.update { it.copy(reminderPreferences = it.reminderPreferences.copy(sleepReminders = value)) }
    }

    fun onHydrationRemindersToggled(value: Boolean) {
        _uiState.update { it.copy(reminderPreferences = it.reminderPreferences.copy(hydrationReminders = value)) }
    }

    /**
     * Validates the current form state and, if valid, saves the
     * lifestyle-related fields onto [UserPreferences] for the current
     * `activeUserId`, preserving the workout-related fields already saved
     * by the previous onboarding step.
     */
    fun onSaveClicked() {
        val current = _uiState.value
        if (current.isSaving) return

        val userId = activeUserId
        if (userId == null) {
            _uiState.update { it.copy(errorNoActiveUser = true) }
            return
        }

        val mealCount = current.preferredMealCount.trim().toIntOrNull()
        val mealCountError = mealCount == null || !UserPreferences.isValidMealCount(mealCount)
        if (mealCountError) {
            _uiState.update { it.copy(mealCountError = true) }
            return
        }

        val sleepProvided = current.sleepHour.isNotBlank() || current.sleepMinute.isNotBlank()
        val wakeProvided = current.wakeHour.isNotBlank() || current.wakeMinute.isNotBlank()

        val sleepTime = parseOptionalTime(current.sleepHour, current.sleepMinute)
        val wakeTime = parseOptionalTime(current.wakeHour, current.wakeMinute)

        if ((sleepProvided && sleepTime == null) || (wakeProvided && wakeTime == null)) {
            _uiState.update { it.copy(timeError = true) }
            return
        }

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            val base = existingPreferences ?: UserPreferences(
                userId = userId,
                workoutLocation = WorkoutLocation.HOME,
                preferredWorkoutTime = null,
                preferredMealCount = mealCount!!,
                sleepTime = null,
                wakeTime = null,
                budgetLevel = current.budgetLevel,
                unitSystem = current.unitSystem
            )

            val updated = base.copy(
                userId = userId,
                preferredMealCount = mealCount!!,
                sleepTime = sleepTime,
                wakeTime = wakeTime,
                budgetLevel = current.budgetLevel,
                unitSystem = current.unitSystem,
                reminderPreferences = current.reminderPreferences
            )

            userPreferencesRepository.save(updated)

            _uiState.update { it.copy(isSaving = false, saveComplete = true) }
        }
    }

    private fun parseOptionalTime(hourText: String, minuteText: String): TimeOfDay? {
        if (hourText.isBlank() && minuteText.isBlank()) return null
        val hour = hourText.trim().toIntOrNull() ?: return null
        val minute = minuteText.trim().toIntOrNull() ?: return null
        return TimeOfDay.ofOrNull(hour, minute)
    }
}

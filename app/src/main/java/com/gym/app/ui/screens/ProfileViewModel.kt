package com.gym.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.app.domain.appstate.AppStateRepository
import com.gym.app.domain.model.ActivityLevel
import com.gym.app.domain.model.BudgetLevel
import com.gym.app.domain.model.ExperienceLevel
import com.gym.app.domain.model.Gender
import com.gym.app.domain.model.Goal
import com.gym.app.domain.model.PrimaryGoal
import com.gym.app.domain.model.UnitSystem
import com.gym.app.domain.model.UserPreferences
import com.gym.app.domain.model.UserProfile
import com.gym.app.domain.model.WorkoutLocation
import com.gym.app.domain.repository.GoalRepository
import com.gym.app.domain.repository.UserPreferencesRepository
import com.gym.app.domain.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * PHASE 02G — PROFILE SCREEN
 *
 * Editable, text/UI-facing form state for the profile screen, mirroring
 * the currently saved [UserProfile], [Goal], and [UserPreferences] for
 * the active user. Kept separate from the domain models themselves for
 * the same reason as the onboarding screens: partially edited input isn't
 * always a valid domain value yet.
 */
data class ProfileUiState(
    val isLoading: Boolean = true,
    val userId: String? = null,

    // Profile fields
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

    // Goal fields
    val goalId: String? = null,
    val goalStartDate: Long = 0L,
    val primaryGoal: PrimaryGoal = PrimaryGoal.GENERAL_FITNESS,
    val targetWeightKg: String = "",
    val targetWeightError: Boolean = false,
    val secondaryGoals: Set<PrimaryGoal> = emptySet(),

    // Preferences fields
    val workoutLocation: WorkoutLocation = WorkoutLocation.HOME,
    val preferredMealCount: String = "3",
    val mealCountError: Boolean = false,
    val budgetLevel: BudgetLevel = BudgetLevel.MEDIUM,
    val unitSystem: UnitSystem = UnitSystem.METRIC,

    val hasProfile: Boolean = false,
    val hasGoal: Boolean = false,
    val hasPreferences: Boolean = false,

    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorNoActiveUser: Boolean = false
)

/**
 * PHASE 02G — PROFILE SCREEN
 *
 * Loads the active user's saved [UserProfile], [Goal], and
 * [UserPreferences] through their respective repositories only (never
 * direct DAO access) and allows editing/saving supported values back
 * through the same repositories.
 *
 * Only fields already defined on the domain models are editable here —
 * no invented fields, no fake data. If a record does not exist yet for
 * the active user (e.g. no goal saved), that section is simply left
 * empty/unavailable rather than fabricated.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val goalRepository: GoalRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val appStateRepository: AppStateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var existingPreferences: UserPreferences? = null

    init {
        loadProfile()
    }

    fun loadProfile() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val activeUserId = appStateRepository.appState.first().activeUserId

            if (activeUserId == null) {
                _uiState.update { it.copy(isLoading = false, errorNoActiveUser = true) }
                return@launch
            }

            val profile = userProfileRepository.getByUserId(activeUserId)
            val goals = goalRepository.getByUserId(activeUserId)
            val preferences = userPreferencesRepository.getByUserId(activeUserId)
            existingPreferences = preferences

            val goal = goals.firstOrNull()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    userId = activeUserId,
                    errorNoActiveUser = false,
                    hasProfile = profile != null,
                    hasGoal = goal != null,
                    hasPreferences = preferences != null,
                    name = profile?.name ?: "",
                    age = profile?.age?.toString() ?: "",
                    gender = profile?.gender,
                    heightCm = profile?.heightCm?.toString() ?: "",
                    currentWeightKg = profile?.currentWeightKg?.toString() ?: "",
                    experienceLevel = profile?.experienceLevel ?: ExperienceLevel.BEGINNER,
                    activityLevel = profile?.activityLevel ?: ActivityLevel.SEDENTARY,
                    goalId = goal?.goalId,
                    goalStartDate = goal?.goalStartDate ?: 0L,
                    primaryGoal = goal?.primaryGoal ?: PrimaryGoal.GENERAL_FITNESS,
                    targetWeightKg = goal?.targetWeightKg?.toString() ?: "",
                    secondaryGoals = goal?.secondaryGoals?.toSet() ?: emptySet(),
                    workoutLocation = preferences?.workoutLocation ?: WorkoutLocation.HOME,
                    preferredMealCount = preferences?.preferredMealCount?.toString() ?: "3",
                    budgetLevel = preferences?.budgetLevel ?: BudgetLevel.MEDIUM,
                    unitSystem = preferences?.unitSystem ?: UnitSystem.METRIC
                )
            }
        }
    }

    // Profile field edits
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

    // Goal field edits
    fun onPrimaryGoalChanged(value: PrimaryGoal) {
        _uiState.update {
            it.copy(
                primaryGoal = value,
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

    // Preferences field edits
    fun onWorkoutLocationChanged(value: WorkoutLocation) {
        _uiState.update { it.copy(workoutLocation = value) }
    }

    fun onMealCountChanged(value: String) {
        _uiState.update { it.copy(preferredMealCount = value, mealCountError = false) }
    }

    fun onBudgetLevelChanged(value: BudgetLevel) {
        _uiState.update { it.copy(budgetLevel = value) }
    }

    fun onUnitSystemChanged(value: UnitSystem) {
        _uiState.update { it.copy(unitSystem = value) }
    }

    /**
     * Validates and saves all edited sections through their respective
     * repositories. Only overwrites fields this screen actually edits —
     * workout-schedule/lifestyle fields on [UserPreferences] not shown
     * here (e.g. sleep/wake time, reminders) are preserved from the
     * existing saved row.
     */
    fun onSaveClicked() {
        val current = _uiState.value
        if (current.isSaving) return

        val userId = current.userId
        if (userId == null) {
            _uiState.update { it.copy(errorNoActiveUser = true) }
            return
        }

        val trimmedName = current.name.trim()
        val age = current.age.trim().toIntOrNull()
        val height = current.heightCm.trim().toDoubleOrNull()
        val weight = current.currentWeightKg.trim().toDoubleOrNull()

        val nameError = trimmedName.isBlank()
        val ageError = age == null || !UserProfile.isValidAge(age)
        val heightError = height == null || !UserProfile.isValidHeight(height)
        val weightError = weight == null || !UserProfile.isValidWeight(weight)

        val isTargetWeightApplicable = Goal.isTargetWeightApplicable(current.primaryGoal)
        val targetWeight = if (current.targetWeightKg.isBlank()) {
            null
        } else {
            current.targetWeightKg.trim().toDoubleOrNull()
        }
        val targetWeightError = isTargetWeightApplicable &&
            current.targetWeightKg.isNotBlank() &&
            (targetWeight == null || !Goal.isValidTargetWeight(targetWeight))

        val mealCount = current.preferredMealCount.trim().toIntOrNull()
        val mealCountError = mealCount == null || !UserPreferences.isValidMealCount(mealCount)

        if (nameError || ageError || heightError || weightError || targetWeightError || mealCountError) {
            _uiState.update {
                it.copy(
                    nameError = nameError,
                    ageError = ageError,
                    heightError = heightError,
                    weightError = weightError,
                    targetWeightError = targetWeightError,
                    mealCountError = mealCountError
                )
            }
            return
        }

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
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

            val today = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis())
            val goal = Goal(
                goalId = current.goalId ?: java.util.UUID.randomUUID().toString(),
                userId = userId,
                primaryGoal = current.primaryGoal,
                targetWeightKg = if (isTargetWeightApplicable) targetWeight else null,
                goalStartDate = if (current.goalId != null) current.goalStartDate else today,
                targetDate = null,
                secondaryGoals = current.secondaryGoals.toList()
            )
            goalRepository.save(goal)

            val basePreferences = existingPreferences ?: UserPreferences(
                userId = userId,
                workoutLocation = current.workoutLocation,
                preferredWorkoutTime = null,
                preferredMealCount = mealCount!!,
                sleepTime = null,
                wakeTime = null,
                budgetLevel = current.budgetLevel,
                unitSystem = current.unitSystem
            )
            val updatedPreferences = basePreferences.copy(
                userId = userId,
                workoutLocation = current.workoutLocation,
                preferredMealCount = mealCount!!,
                budgetLevel = current.budgetLevel,
                unitSystem = current.unitSystem
            )
            userPreferencesRepository.save(updatedPreferences)
            existingPreferences = updatedPreferences

            _uiState.update {
                it.copy(
                    isSaving = false,
                    saveSuccess = true,
                    goalId = goal.goalId,
                    goalStartDate = goal.goalStartDate,
                    hasProfile = true,
                    hasGoal = true,
                    hasPreferences = true
                )
            }
        }
    }

    fun onSaveSuccessConsumed() {
        _uiState.update { it.copy(saveSuccess = false) }
    }
}

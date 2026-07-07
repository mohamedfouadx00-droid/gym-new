package com.gym.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.app.domain.appstate.AppStateRepository
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
 * PHASE 02F — COMPLETE ONBOARDING
 *
 * Result of the final onboarding validation, driving what
 * [OnboardingCompleteScreen] shows/does next.
 */
sealed class OnboardingCompleteResult {
    /** Still checking; nothing decided yet. */
    data object Checking : OnboardingCompleteResult()

    /** All required records exist; onboarding was marked complete. */
    data object Success : OnboardingCompleteResult()

    /**
     * The active user (or one of the required records) is missing.
     * Onboarding is deliberately NOT marked complete — no fake success.
     */
    data object Incomplete : OnboardingCompleteResult()
}

data class OnboardingCompleteUiState(
    val result: OnboardingCompleteResult = OnboardingCompleteResult.Checking
)

/**
 * PHASE 02F — COMPLETE ONBOARDING
 *
 * Before marking onboarding as complete, verifies that the current
 * `activeUserId` (read from [AppStateRepository], never hardcoded) has a
 * saved [com.gym.app.domain.model.UserProfile], a saved
 * [com.gym.app.domain.model.Goal], and saved
 * [com.gym.app.domain.model.UserPreferences] — all fetched through their
 * respective repositories only, never via direct DAO access.
 *
 * Only if all three exist does this set `onboardingCompleted = true`
 * (keeping `activeUserId` untouched). If anything is missing, onboarding
 * is left incomplete — no fake success is ever reported.
 */
@HiltViewModel
class OnboardingCompleteViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val goalRepository: GoalRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val appStateRepository: AppStateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingCompleteUiState())
    val uiState: StateFlow<OnboardingCompleteUiState> = _uiState.asStateFlow()

    init {
        verifyAndComplete()
    }

    fun verifyAndComplete() {
        _uiState.update { it.copy(result = OnboardingCompleteResult.Checking) }

        viewModelScope.launch {
            val activeUserId = appStateRepository.appState.first().activeUserId

            if (activeUserId == null) {
                _uiState.update { it.copy(result = OnboardingCompleteResult.Incomplete) }
                return@launch
            }

            val profile = userProfileRepository.getByUserId(activeUserId)
            val goals = goalRepository.getByUserId(activeUserId)
            val preferences = userPreferencesRepository.getByUserId(activeUserId)

            val isValid = profile != null && goals.isNotEmpty() && preferences != null

            if (!isValid) {
                _uiState.update { it.copy(result = OnboardingCompleteResult.Incomplete) }
                return@launch
            }

            // activeUserId is intentionally left untouched.
            appStateRepository.setOnboardingCompleted(true)

            _uiState.update { it.copy(result = OnboardingCompleteResult.Success) }
        }
    }
}

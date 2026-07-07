package com.gym.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.app.domain.appstate.AppStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * PHASE 01H — APP START LOGIC
 *
 * The one-time startup routing decision, derived from [AppStateRepository]
 * (Phase 01G), kept separate from UI rendering ([StartScreen] only reacts
 * to [destination] — it contains no decision logic itself).
 *
 * Exactly three possible outcomes, matching the three startup cases:
 *
 * - [StartDestination.Onboarding] — `onboardingCompleted == false`, or
 *   `onboardingCompleted == true` but `activeUserId == null` (safe
 *   fallback: onboarding was marked complete but there is no active user
 *   to show Home for).
 * - [StartDestination.Home] — `onboardingCompleted == true` and
 *   `activeUserId != null`.
 * - [StartDestination.Undetermined] — initial value only, before the
 *   first [com.gym.app.domain.appstate.AppState] emission has been read.
 *   [StartScreen] must not navigate while in this state.
 */
sealed class StartDestination {
    data object Undetermined : StartDestination()
    data object Onboarding : StartDestination()
    data object Home : StartDestination()
}

/**
 * Reads [AppStateRepository.appState] once (the current state is enough to
 * make a one-time startup routing decision; this is not a screen that
 * needs to keep reacting to later app-state changes) on a coroutine, never
 * blocking the main thread, and exposes the routing result as a
 * [StateFlow] for [StartScreen] to observe and act on.
 */
@HiltViewModel
class StartViewModel @Inject constructor(
    private val appStateRepository: AppStateRepository
) : ViewModel() {

    private val _destination = MutableStateFlow<StartDestination>(StartDestination.Undetermined)
    val destination: StateFlow<StartDestination> = _destination.asStateFlow()

    init {
        viewModelScope.launch {
            val appState = appStateRepository.appState.first()

            _destination.value = if (appState.onboardingCompleted && appState.activeUserId != null) {
                // Case 2: onboarding done and an active user exists -> Home.
                StartDestination.Home
            } else {
                // Case 1 (onboarding not completed) and Case 3
                // (onboardingCompleted == true but activeUserId == null,
                // safely falling back to onboarding) both resolve here.
                StartDestination.Onboarding
            }
        }
    }
}

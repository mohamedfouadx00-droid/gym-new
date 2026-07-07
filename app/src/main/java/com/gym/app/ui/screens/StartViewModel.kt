package com.gym.app.ui.screens

import androidx.lifecycle.ViewModel
import com.gym.app.core.di.AppInfoProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * PHASE 01C — DEPENDENCY INJECTION FOUNDATION
 *
 * Immutable UI state exposed by [StartViewModel]. Contains only technical
 * diagnostic information, never real feature or user data.
 */
data class StartUiState(
    val isDependencyInjectionReady: Boolean = false,
    val applicationName: String = ""
)

/**
 * A minimal, purely technical ViewModel whose only job is to prove that
 * Hilt ViewModel injection works: [AppInfoProvider] is received through
 * the constructor, and Hilt is responsible for supplying it.
 *
 * This ViewModel contains no real feature logic. It is temporarily wired
 * to [StartScreen] only, to keep the verification surface minimal.
 */
@HiltViewModel
class StartViewModel @Inject constructor(
    appInfoProvider: AppInfoProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        StartUiState(
            isDependencyInjectionReady = true,
            applicationName = appInfoProvider.getApplicationName()
        )
    )
    val uiState: StateFlow<StartUiState> = _uiState.asStateFlow()
}

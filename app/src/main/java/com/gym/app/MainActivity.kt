package com.gym.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.gym.app.navigation.AppNavHost
import com.gym.app.ui.theme.GymTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * PHASE 01B — NAVIGATION FOUNDATION
 * PHASE 01C — DEPENDENCY INJECTION FOUNDATION
 *
 * MainActivity is intentionally a thin entry point only. It applies the
 * app theme (which forces RTL layout direction, since the app is
 * Arabic-only) and hosts the Compose navigation graph ([AppNavHost]).
 *
 * [@AndroidEntryPoint] makes this Activity part of the Hilt dependency
 * graph so any Composable hosted here (via hiltViewModel()) can obtain
 * Hilt-injected ViewModels. This annotation alone adds no feature logic —
 * MainActivity still contains no business rules or data models.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GymTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost()
                }
            }
        }
    }
}

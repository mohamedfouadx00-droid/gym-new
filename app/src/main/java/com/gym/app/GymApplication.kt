package com.gym.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * PHASE 01C — DEPENDENCY INJECTION FOUNDATION
 *
 * Root [Application] class for Hilt. [@HiltAndroidApp] triggers Hilt's code
 * generation and creates the top-level (SingletonComponent) dependency
 * container that every injected Activity/ViewModel in the app ultimately
 * depends on.
 *
 * This class intentionally contains no feature logic, no eager
 * initialization, and no business rules — only the annotation Hilt requires.
 */
@HiltAndroidApp
class GymApplication : Application()

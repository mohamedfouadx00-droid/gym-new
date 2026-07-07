package com.gym.app.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * PHASE 01G — DATASTORE FOUNDATION
 *
 * Provides the single Preferences [DataStore] instance used for simple,
 * application-level state (see [com.gym.app.domain.appstate.AppState]) to
 * the Hilt dependency graph, so
 * [com.gym.app.data.appstate.AppStateRepositoryImpl] can inject it
 * directly.
 *
 * A [dagger.Module] with [Provides] is used here (rather than constructor
 * injection) for the same reason as [DatabaseModule]: the [DataStore]
 * instance is not built through a plain constructor this codebase can
 * annotate with [javax.inject.Inject] — it is built through the
 * [preferencesDataStore] property delegate, which requires an
 * application [Context].
 *
 * This DataStore is intentionally separate from Room (Phase 01E/01F): it
 * never stores [com.gym.app.domain.model.UserProfile],
 * [com.gym.app.domain.model.Goal], or
 * [com.gym.app.domain.model.UserPreferences] data, only the small,
 * user-independent app state defined in
 * [com.gym.app.domain.appstate.AppState].
 */
private const val APP_STATE_PREFERENCES_NAME = "app_state"

private val Context.appStateDataStore: DataStore<Preferences> by preferencesDataStore(
    name = APP_STATE_PREFERENCES_NAME
)

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideAppStateDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.appStateDataStore
}

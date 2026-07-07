package com.gym.app.core.di

import com.gym.app.data.appstate.AppStateRepositoryImpl
import com.gym.app.domain.appstate.AppStateRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * PHASE 01G — DATASTORE FOUNDATION
 *
 * Binds [AppStateRepository] to its DataStore-backed implementation, so
 * ViewModels (and future use cases/app-start logic) can depend on the
 * [com.gym.app.domain.appstate] contract only, never on
 * [AppStateRepositoryImpl] directly.
 *
 * Mirrors [RepositoryModule] from Phase 01F: an abstract class with
 * [Binds] is used because [AppStateRepositoryImpl] already has an
 * `@Inject`-annotated constructor that takes the [androidx.datastore.core
 * .DataStore] provided by [DataStoreModule].
 *
 * Kept as a separate module (rather than added to [RepositoryModule])
 * because [AppStateRepository] is deliberately not part of the Room-backed
 * repository family from Phase 01F — see
 * [com.gym.app.domain.appstate.AppState] for why.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppStateRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAppStateRepository(
        impl: AppStateRepositoryImpl
    ): AppStateRepository
}

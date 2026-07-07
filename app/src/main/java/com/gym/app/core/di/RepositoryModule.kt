package com.gym.app.core.di

import com.gym.app.data.repository.GoalRepositoryImpl
import com.gym.app.data.repository.UserPreferencesRepositoryImpl
import com.gym.app.data.repository.UserProfileRepositoryImpl
import com.gym.app.domain.repository.GoalRepository
import com.gym.app.domain.repository.UserPreferencesRepository
import com.gym.app.domain.repository.UserProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * PHASE 01F — USER REPOSITORIES
 *
 * Binds each domain repository interface to its default implementation,
 * so ViewModels (and future use cases) can depend on the
 * [com.gym.app.domain.repository] contracts only, never on
 * [com.gym.app.data.repository] implementation classes directly.
 *
 * An abstract class with [Binds] is used here (rather than [dagger.Provides])
 * because each implementation already has an `@Inject`-annotated
 * constructor that takes its DAO (see [DatabaseModule] for how those DAOs
 * themselves reach the graph) — [Binds] is the minimal, recommended way to
 * wire an existing constructor-injected implementation to its interface.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserProfileRepository(
        impl: UserProfileRepositoryImpl
    ): UserProfileRepository

    @Binds
    @Singleton
    abstract fun bindGoalRepository(
        impl: GoalRepositoryImpl
    ): GoalRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        impl: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository
}

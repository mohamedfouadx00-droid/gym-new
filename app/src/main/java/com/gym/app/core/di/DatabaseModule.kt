package com.gym.app.core.di

import android.content.Context
import androidx.room.Room
import com.gym.app.data.local.AppDatabase
import com.gym.app.data.local.dao.GoalDao
import com.gym.app.data.local.dao.UserPreferencesDao
import com.gym.app.data.local.dao.UserProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * PHASE 01E — ROOM DATABASE FOUNDATION
 *
 * Provides the single [AppDatabase] instance and its DAOs to the Hilt
 * dependency graph, so a future repository layer (Phase 01F — User
 * Repositories, per NEXT_TASK.md/PROJECT_HANDOFF.md) can inject them
 * directly instead of constructing the database itself.
 *
 * A [dagger.Module] with [Provides] is used here (rather than
 * constructor injection) because [AppDatabase] and Room's generated DAO
 * implementations are not classes this codebase can annotate with
 * [javax.inject.Inject] directly — [AppDatabase] must be built through
 * [Room.databaseBuilder], and the DAOs come from the abstract database
 * instance, not their own constructors.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()

    @Provides
    fun provideUserProfileDao(appDatabase: AppDatabase): UserProfileDao =
        appDatabase.userProfileDao()

    @Provides
    fun provideGoalDao(appDatabase: AppDatabase): GoalDao =
        appDatabase.goalDao()

    @Provides
    fun provideUserPreferencesDao(appDatabase: AppDatabase): UserPreferencesDao =
        appDatabase.userPreferencesDao()
}

package com.gym.app.data.appstate

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.gym.app.domain.appstate.AppState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import java.io.File

/**
 * PHASE 01G — DATASTORE FOUNDATION
 *
 * Verifies [AppStateRepositoryImpl] correctly reads/writes [AppState]
 * through a real Preferences [DataStore] — backed by a temp file rather
 * than [com.gym.app.core.di.DataStoreModule]'s Android-context-backed
 * instance, since these are plain JVM unit tests (no Android
 * instrumentation, matching the project's existing test strategy from
 * Phase 01E/01F — see [com.gym.app.data.local.fake.FakeUserProfileDao]
 * for the same "avoid needing a real platform dependency" approach).
 *
 * A fresh temp file per test guarantees no state leaks between tests.
 */
class AppStateRepositoryImplTest {

    private lateinit var tempFile: File
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var repository: AppStateRepositoryImpl

    @Before
    fun setUp() {
        tempFile = File.createTempFile("app_state_test", ".preferences_pb")
        dataStore = PreferenceDataStoreFactory.create(
            scope = kotlinx.coroutines.CoroutineScope(Dispatchers.Unconfined),
            produceFile = { tempFile }
        )
        repository = AppStateRepositoryImpl(dataStore)
    }

    @After
    fun tearDown() {
        tempFile.delete()
    }

    @Test
    fun `appState defaults to onboarding not completed and no active user`() = runTest {
        val state = repository.appState.first()

        assertFalse(state.onboardingCompleted)
        assertNull(state.activeUserId)
    }

    @Test
    fun `setOnboardingCompleted true is reflected in appState`() = runTest {
        repository.setOnboardingCompleted(true)

        assertTrue(repository.appState.first().onboardingCompleted)
    }

    @Test
    fun `setOnboardingCompleted can be toggled back to false`() = runTest {
        repository.setOnboardingCompleted(true)
        repository.setOnboardingCompleted(false)

        assertFalse(repository.appState.first().onboardingCompleted)
    }

    @Test
    fun `setActiveUserId stores the given user id`() = runTest {
        repository.setActiveUserId("user-1")

        assertEquals("user-1", repository.appState.first().activeUserId)
    }

    @Test
    fun `setActiveUserId with null clears a previously stored user id`() = runTest {
        repository.setActiveUserId("user-1")
        repository.setActiveUserId(null)

        assertNull(repository.appState.first().activeUserId)
    }

    @Test
    fun `onboardingCompleted and activeUserId are independent`() = runTest {
        repository.setOnboardingCompleted(true)
        repository.setActiveUserId("user-42")

        val state = repository.appState.first()

        assertEquals(AppState(onboardingCompleted = true, activeUserId = "user-42"), state)
    }
}

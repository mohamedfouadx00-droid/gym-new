package com.gym.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

/**
 * PHASE 01D — CORE USER MODELS
 *
 * Unit tests for [TimeOfDay]'s validation rules.
 */
class TimeOfDayTest {

    @Test
    fun `valid time is constructed successfully`() {
        val time = TimeOfDay(hour = 6, minute = 30)
        assertEquals(6, time.hour)
        assertEquals(30, time.minute)
    }

    @Test
    fun `boundary values are accepted`() {
        TimeOfDay(hour = 0, minute = 0)
        TimeOfDay(hour = 23, minute = 59)
    }

    @Test
    fun `hour out of range is rejected`() {
        assertThrows(IllegalArgumentException::class.java) { TimeOfDay(hour = -1, minute = 0) }
        assertThrows(IllegalArgumentException::class.java) { TimeOfDay(hour = 24, minute = 0) }
    }

    @Test
    fun `minute out of range is rejected`() {
        assertThrows(IllegalArgumentException::class.java) { TimeOfDay(hour = 10, minute = -1) }
        assertThrows(IllegalArgumentException::class.java) { TimeOfDay(hour = 10, minute = 60) }
    }

    @Test
    fun `ofOrNull returns instance for valid input`() {
        assertNotNull(TimeOfDay.ofOrNull(12, 0))
    }

    @Test
    fun `ofOrNull returns null for invalid input`() {
        assertNull(TimeOfDay.ofOrNull(25, 0))
        assertNull(TimeOfDay.ofOrNull(10, 61))
    }
}

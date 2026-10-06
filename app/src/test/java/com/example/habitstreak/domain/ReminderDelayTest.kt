package com.example.habitstreak.domain

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Duration
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

class ReminderDelayTest {
    private val zone = ZoneId.of("Asia/Kolkata") // no daylight saving, so results are predictable

    private fun at(hour: Int, minute: Int) = ZonedDateTime.of(2025, 3, 10, hour, minute, 0, 0, zone)

    @Test
    fun timeLaterToday_waitsUntilToday() {
        assertEquals(Duration.ofMinutes(90), delayUntilNext(LocalTime.of(9, 0), at(7, 30)))
    }

    @Test
    fun timeAlreadyPassedToday_waitsUntilTomorrow() {
        assertEquals(Duration.ofHours(23), delayUntilNext(LocalTime.of(9, 0), at(10, 0)))
    }

    @Test
    fun timeExactlyNow_waitsAFullDay() {
        assertEquals(Duration.ofDays(1), delayUntilNext(LocalTime.of(9, 0), at(9, 0)))
    }

    @Test
    fun twoMinutesAhead_waitsTwoMinutes() {
        assertEquals(Duration.ofMinutes(2), delayUntilNext(LocalTime.of(14, 32), at(14, 30)))
    }

    @Test
    fun midnightReminderLateAtNight_waitsUntilNextMidnight() {
        assertEquals(Duration.ofMinutes(30), delayUntilNext(LocalTime.MIDNIGHT, at(23, 30)))
    }
}

package com.example.habitstreak.domain

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class StreakTest {
    private val today = LocalDate.of(2025, 3, 10)

    @Test
    fun emptyList_isZero() {
        assertEquals(0, calculateStreak(emptyList(), today))
    }

    @Test
    fun onlyToday_isOne() {
        assertEquals(1, calculateStreak(listOf(today), today))
    }

    @Test
    fun todayMissingButYesterdayDone_continuesFromYesterday() {
        val dates = listOf(today.minusDays(1), today.minusDays(2), today.minusDays(3))
        assertEquals(3, calculateStreak(dates, today))
    }

    @Test
    fun todayDoneAndPreviousDays_countsThemAll() {
        val dates = listOf(today, today.minusDays(1), today.minusDays(2))
        assertEquals(3, calculateStreak(dates, today))
    }

    @Test
    fun gapBreaksTheStreak_onlyRecentRunCounts() {
        val dates = listOf(today, today.minusDays(1), today.minusDays(3), today.minusDays(4))
        assertEquals(2, calculateStreak(dates, today))
    }

    @Test
    fun lastDoneTwoDaysAgo_isZero() {
        assertEquals(0, calculateStreak(listOf(today.minusDays(2)), today))
    }

    @Test
    fun duplicateDates_countOnce() {
        val dates = listOf(today, today, today.minusDays(1), today.minusDays(1))
        assertEquals(2, calculateStreak(dates, today))
    }

    @Test
    fun unsortedDates_giveSameResult() {
        val dates = listOf(today.minusDays(2), today, today.minusDays(1))
        assertEquals(3, calculateStreak(dates, today))
    }

    @Test
    fun midnightBoundary_streakSurvivesUntilEndOfNextDay() {
        val doneOnMarch9 = listOf(LocalDate.of(2025, 3, 9))
        // Just before midnight on the 9th: today is done
        assertEquals(1, calculateStreak(doneOnMarch9, LocalDate.of(2025, 3, 9)))
        // Just after midnight: the 10th has started but isn't done yet, streak still alive
        assertEquals(1, calculateStreak(doneOnMarch9, LocalDate.of(2025, 3, 10)))
        // A full day later with no completion: the streak is lost
        assertEquals(0, calculateStreak(doneOnMarch9, LocalDate.of(2025, 3, 11)))
    }

    @Test
    fun streakCountsAcrossMonthAndYearBoundaries() {
        val dates = listOf(LocalDate.of(2024, 12, 31), LocalDate.of(2025, 1, 1))
        assertEquals(2, calculateStreak(dates, LocalDate.of(2025, 1, 1)))
    }
}

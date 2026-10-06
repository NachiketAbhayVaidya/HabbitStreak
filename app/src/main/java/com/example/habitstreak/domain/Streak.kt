package com.example.habitstreak.domain

import java.time.LocalDate

// Counts consecutive done days ending today, or ending yesterday if today isn't done yet
// (the day isn't over, so the streak is still alive). A missed day before that gives 0.
// `today` is a parameter instead of LocalDate.now() so tests can control the date.
fun calculateStreak(completionDates: Collection<LocalDate>, today: LocalDate): Int {
    // A set removes duplicate dates and makes each "was this day done?" lookup instant
    val doneDays = completionDates.toSet()
    val lastCountedDay = if (today in doneDays) today else today.minusDays(1)
    return generateSequence(lastCountedDay) { it.minusDays(1) }
        .takeWhile { it in doneDays }
        .count()
}

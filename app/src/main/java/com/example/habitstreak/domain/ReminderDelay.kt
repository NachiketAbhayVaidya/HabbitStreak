package com.example.habitstreak.domain

import java.time.Duration
import java.time.LocalTime
import java.time.ZonedDateTime

// How long to wait until the next time the clock shows `time`: today if it is still ahead,
// otherwise tomorrow. ZonedDateTime (not LocalDateTime) so daylight-saving shifts are handled.
// `now` is a parameter so tests can control it.
fun delayUntilNext(time: LocalTime, now: ZonedDateTime): Duration {
    val today = now.with(time)
    val next = if (today.isAfter(now)) today else today.plusDays(1)
    return Duration.between(now, next)
}

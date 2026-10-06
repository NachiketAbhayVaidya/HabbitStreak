package com.example.habitstreak.data

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

// SQLite only stores numbers and text, so each java.time type is saved as a plain Long or Int.
// Epoch day = days since 1970-01-01: a date with no time zone, so the stored value never shifts.
class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate): Long = date.toEpochDay()

    @TypeConverter
    fun toLocalDate(epochDay: Long): LocalDate = LocalDate.ofEpochDay(epochDay)

    @TypeConverter
    fun fromLocalTime(time: LocalTime): Int = time.toSecondOfDay()

    @TypeConverter
    fun toLocalTime(secondOfDay: Int): LocalTime = LocalTime.ofSecondOfDay(secondOfDay.toLong())

    @TypeConverter
    fun fromInstant(instant: Instant): Long = instant.toEpochMilli()

    @TypeConverter
    fun toInstant(epochMillis: Long): Instant = Instant.ofEpochMilli(epochMillis)
}

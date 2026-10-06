package com.example.habitstreak.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

// exportSchema = false: we don't keep schema JSON files, so migrations aren't verified by Room's test tooling
@Database(entities = [Habit::class, Completion::class, Quote::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class HabitStreakDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun completionDao(): CompletionDao
    abstract fun quoteDao(): QuoteDao
}

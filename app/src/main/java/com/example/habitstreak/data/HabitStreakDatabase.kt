package com.example.habitstreak.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

// exportSchema = false: we don't keep schema JSON files, since there are no migrations to test yet
@Database(entities = [Habit::class, Completion::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class HabitStreakDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun completionDao(): CompletionDao
}

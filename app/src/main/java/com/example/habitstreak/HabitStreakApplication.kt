package com.example.habitstreak

import android.app.Application
import androidx.room.Room
import com.example.habitstreak.data.HabitRepository
import com.example.habitstreak.data.HabitStreakDatabase

// Created once per app process, so the whole app shares one database and one repository.
// Phase 7 (Hilt) will replace this hand-written wiring.
class HabitStreakApplication : Application() {
    private val database by lazy {
        Room.databaseBuilder(this, HabitStreakDatabase::class.java, "habitstreak.db").build()
    }

    val repository by lazy { HabitRepository(database.habitDao(), database.completionDao()) }
}

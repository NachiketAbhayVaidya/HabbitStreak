package com.example.habitstreak

import android.app.Application
import androidx.room.Room
import com.example.habitstreak.data.HabitRepository
import com.example.habitstreak.data.HabitStreakDatabase
import com.example.habitstreak.data.MIGRATION_1_2
import com.example.habitstreak.data.QuoteApi
import com.example.habitstreak.data.QuoteRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Created once per app process, so the whole app shares one database and one repository.
// Phase 7 (Hilt) will replace this hand-written wiring.
class HabitStreakApplication : Application() {
    private val database by lazy {
        Room.databaseBuilder(this, HabitStreakDatabase::class.java, "habitstreak.db")
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    private val quoteApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://zenquotes.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QuoteApi::class.java)
    }

    val repository by lazy { HabitRepository(database.habitDao(), database.completionDao()) }

    val quoteRepository by lazy { QuoteRepository(database.quoteDao(), quoteApi) }
}

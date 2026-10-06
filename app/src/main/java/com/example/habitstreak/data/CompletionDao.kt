package com.example.habitstreak.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface CompletionDao {
    // All completions at once: the streak function needs every date of every habit
    @Query("SELECT * FROM completions")
    fun observeAll(): Flow<List<Completion>>

    // IGNORE: tapping "done" twice is harmless instead of crashing on the primary key
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(completion: Completion)

    // Used to un-tick today's checkbox
    @Query("DELETE FROM completions WHERE habitId = :habitId AND date = :date")
    suspend fun delete(habitId: Long, date: LocalDate)
}

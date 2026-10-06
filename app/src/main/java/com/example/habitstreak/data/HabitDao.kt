package com.example.habitstreak.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    // Returning a Flow makes Room re-emit the list whenever the table changes
    @Query("SELECT * FROM habits ORDER BY createdAt")
    fun observeAll(): Flow<List<Habit>>

    @Insert
    suspend fun insert(habit: Habit): Long

    @Update
    suspend fun update(habit: Habit)

    @Delete
    suspend fun delete(habit: Habit)
}

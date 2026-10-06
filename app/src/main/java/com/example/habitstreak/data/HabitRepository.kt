package com.example.habitstreak.data

import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

// The only class the ViewModels talk to; they never see the DAOs or Room
class HabitRepository(
    private val habitDao: HabitDao,
    private val completionDao: CompletionDao
) {
    val habits: Flow<List<Habit>> = habitDao.observeAll()
    val completions: Flow<List<Completion>> = completionDao.observeAll()

    suspend fun getHabit(id: Long): Habit? = habitDao.getById(id)

    suspend fun addHabit(name: String, reminderTime: LocalTime) {
        habitDao.insert(Habit(name = name, reminderTime = reminderTime, createdAt = Instant.now()))
    }

    suspend fun updateHabit(habit: Habit) = habitDao.update(habit)

    suspend fun deleteHabit(habit: Habit) = habitDao.delete(habit)

    suspend fun setDone(habitId: Long, date: LocalDate, done: Boolean) {
        if (done) completionDao.insert(Completion(habitId, date)) else completionDao.delete(habitId, date)
    }
}

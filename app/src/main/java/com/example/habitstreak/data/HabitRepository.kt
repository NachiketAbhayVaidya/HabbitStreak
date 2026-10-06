package com.example.habitstreak.data

import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

// The only class the ViewModels talk to; they never see the DAOs or Room
@Singleton
class HabitRepository @Inject constructor(
    private val habitDao: HabitDao,
    private val completionDao: CompletionDao,
    private val reminderScheduler: ReminderScheduler
) {
    val habits: Flow<List<Habit>> = habitDao.observeAll()
    val completions: Flow<List<Completion>> = completionDao.observeAll()

    suspend fun getHabit(id: Long): Habit? = habitDao.getById(id)

    // Scheduling lives here, next to the database writes, so no screen can forget to do it
    suspend fun addHabit(name: String, reminderTime: LocalTime) {
        val habit = Habit(name = name, reminderTime = reminderTime, createdAt = Instant.now())
        val id = habitDao.insert(habit) // the database generates the id, so schedule with the copy that has it
        reminderScheduler.schedule(habit.copy(id = id))
    }

    suspend fun updateHabit(habit: Habit) {
        habitDao.update(habit)
        reminderScheduler.schedule(habit)
    }

    suspend fun deleteHabit(habit: Habit) {
        habitDao.delete(habit)
        reminderScheduler.cancel(habit.id)
    }

    suspend fun isDone(habitId: Long, date: LocalDate): Boolean = completionDao.isCompleted(habitId, date)

    suspend fun setDone(habitId: Long, date: LocalDate, done: Boolean) {
        if (done) completionDao.insert(Completion(habitId, date)) else completionDao.delete(habitId, date)
    }
}

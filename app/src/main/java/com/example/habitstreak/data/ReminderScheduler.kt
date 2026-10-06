package com.example.habitstreak.data

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.habitstreak.domain.delayUntilNext
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderScheduler @Inject constructor(private val workManager: WorkManager) {

    // One uniquely named job per habit, repeating every 24h. The initial delay makes the first
    // run land on the chosen time, and the daily repeat keeps it at that time.
    fun schedule(habit: Habit) {
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delayUntilNext(habit.reminderTime, ZonedDateTime.now()))
            .setInputData(workDataOf(KEY_HABIT_ID to habit.id))
            .build()
        // UPDATE replaces the existing job's settings, so editing the time moves the reminder
        workManager.enqueueUniquePeriodicWork(workName(habit.id), ExistingPeriodicWorkPolicy.UPDATE, request)
    }

    fun cancel(habitId: Long) {
        workManager.cancelUniqueWork(workName(habitId))
    }

    private fun workName(habitId: Long) = "reminder_$habitId"
}

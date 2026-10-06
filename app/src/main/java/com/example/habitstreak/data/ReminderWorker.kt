package com.example.habitstreak.data

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.habitstreak.MainActivity
import com.example.habitstreak.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate

private const val TAG = "ReminderWorker"
const val KEY_HABIT_ID = "habitId"

// Runs in the background at the reminder time. WorkManager (not a plain alarm) keeps the schedule
// across reboots and app restarts. Android may delay it a few minutes to save battery.
// @HiltWorker + @AssistedInject: Hilt supplies the repository, WorkManager supplies the two @Assisted ones
@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: HabitRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val habitId = inputData.getLong(KEY_HABIT_ID, -1L)

        val habit = repository.getHabit(habitId)
        if (habit == null) {
            Log.d(TAG, "Habit $habitId no longer exists, nothing to remind")
            return Result.success()
        }
        if (repository.isDone(habitId, LocalDate.now())) {
            Log.d(TAG, "'${habit.name}' already done today, skipping reminder")
            return Result.success()
        }
        if (!canPostNotifications(applicationContext)) {
            Log.d(TAG, "Notification permission missing, skipping reminder for '${habit.name}'")
            return Result.success()
        }
        showNotification(habit)
        return Result.success() // success just means "this run is finished"; the daily repeat continues
    }

    private fun showNotification(habit: Habit) {
        val openApp = PendingIntent.getActivity(
            applicationContext,
            0,
            Intent(applicationContext, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE // required on Android 12+ unless the intent must be modified
        )
        val notification = NotificationCompat.Builder(applicationContext, REMINDER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(habit.name)
            .setContentText("Time for today's habit. Keep your streak going!")
            .setContentIntent(openApp)
            .setAutoCancel(true)
            .build()
        // Using the habit id as the notification id gives each habit its own notification
        NotificationManagerCompat.from(applicationContext).notify(habit.id.toInt(), notification)
        Log.d(TAG, "Showed reminder for '${habit.name}'")
    }
}

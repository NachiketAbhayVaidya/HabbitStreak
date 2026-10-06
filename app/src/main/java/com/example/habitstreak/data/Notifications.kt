package com.example.habitstreak.data

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

const val REMINDER_CHANNEL_ID = "habit_reminders"

// Android 8+ requires every notification to belong to a channel (the user controls them in Settings).
// Creating a channel that already exists does nothing, so calling this on every start is safe.
fun createReminderChannel(context: Context) {
    val channel = NotificationChannel(
        REMINDER_CHANNEL_ID,
        "Habit reminders",
        NotificationManager.IMPORTANCE_DEFAULT
    )
    context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
}

// Showing notifications needs a runtime permission only from Android 13 (API 33)
fun canPostNotifications(context: Context): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
        PackageManager.PERMISSION_GRANTED

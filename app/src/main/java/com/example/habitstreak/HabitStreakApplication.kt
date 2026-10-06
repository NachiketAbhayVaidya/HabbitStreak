package com.example.habitstreak

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.habitstreak.data.createReminderChannel
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

// @HiltAndroidApp creates the dependency container that every injected class is built from
@HiltAndroidApp
class HabitStreakApplication : Application(), Configuration.Provider {

    // WorkManager normally creates workers itself and can't pass them constructor arguments.
    // Hilt's factory builds them instead, so ReminderWorker can receive its repository.
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

    override fun onCreate() {
        super.onCreate()
        createReminderChannel(this)
    }
}

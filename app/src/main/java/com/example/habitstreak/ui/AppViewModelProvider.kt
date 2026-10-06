package com.example.habitstreak.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.createSavedStateHandle
import com.example.habitstreak.HabitStreakApplication

// ViewModels with constructor parameters need a factory telling Android how to build them
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer { HabitListViewModel(application().repository, application().quoteRepository) }
        initializer { HabitEditorViewModel(application().repository, createSavedStateHandle()) }
    }
}

private fun CreationExtras.application(): HabitStreakApplication =
    this[APPLICATION_KEY] as HabitStreakApplication

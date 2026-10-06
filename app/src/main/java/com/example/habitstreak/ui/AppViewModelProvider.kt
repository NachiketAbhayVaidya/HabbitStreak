package com.example.habitstreak.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.createSavedStateHandle
import com.example.habitstreak.HabitStreakApplication
import com.example.habitstreak.data.HabitRepository

// ViewModels with constructor parameters need a factory telling Android how to build them
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer { HabitListViewModel(repository()) }
        initializer { HabitEditorViewModel(repository(), createSavedStateHandle()) }
    }
}

private fun CreationExtras.repository(): HabitRepository =
    (this[APPLICATION_KEY] as HabitStreakApplication).repository

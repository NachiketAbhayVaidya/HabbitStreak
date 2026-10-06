package com.example.habitstreak.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitstreak.data.Habit
import com.example.habitstreak.data.HabitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime

private val DEFAULT_REMINDER_TIME = LocalTime.of(9, 0)

data class EditorUiState(
    val name: String = "",
    val reminderTime: LocalTime = DEFAULT_REMINDER_TIME,
    val isLoading: Boolean = false,
    val isFinished: Boolean = false // tells the screen to navigate back
) {
    val canSave = name.isNotBlank()
}

class HabitEditorViewModel(
    private val repository: HabitRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    // Navigation puts the route argument into the SavedStateHandle
    private val habitId: Long = checkNotNull(savedStateHandle[HABIT_ID_ARG])
    val isEditing = habitId != NO_HABIT_ID

    // The habit being edited, kept so saving can preserve its id and createdAt
    private var editedHabit: Habit? = null

    private val _state = MutableStateFlow(EditorUiState(isLoading = isEditing))
    val state: StateFlow<EditorUiState> = _state.asStateFlow()

    init {
        if (isEditing) loadHabit()
    }

    private fun loadHabit() {
        viewModelScope.launch {
            val habit = repository.getHabit(habitId)
            if (habit == null) {
                _state.update { it.copy(isLoading = false, isFinished = true) } // deleted meanwhile
                return@launch
            }
            editedHabit = habit
            _state.update { it.copy(name = habit.name, reminderTime = habit.reminderTime, isLoading = false) }
        }
    }

    fun onNameChange(name: String) = _state.update { it.copy(name = name) }

    fun onReminderTimeChange(time: LocalTime) = _state.update { it.copy(reminderTime = time) }

    fun onSave() {
        val current = _state.value
        if (!current.canSave) return
        viewModelScope.launch {
            val name = current.name.trim()
            val existing = editedHabit
            if (existing == null) {
                repository.addHabit(name, current.reminderTime)
            } else {
                repository.updateHabit(existing.copy(name = name, reminderTime = current.reminderTime))
            }
            _state.update { it.copy(isFinished = true) }
        }
    }
}

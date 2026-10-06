package com.example.habitstreak.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitstreak.data.Habit
import com.example.habitstreak.data.HabitRepository
import com.example.habitstreak.data.Quote
import com.example.habitstreak.data.QuoteRepository
import com.example.habitstreak.domain.calculateStreak
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject

// One row on the home screen
data class HabitItem(val habit: Habit, val streak: Int, val doneToday: Boolean)

// A sealed type forces the UI to handle every case: loading, failure, or data (possibly empty)
sealed interface HabitListState {
    data object Loading : HabitListState
    data object Error : HabitListState
    data class Success(val habits: List<HabitItem>) : HabitListState
}

@HiltViewModel
class HabitListViewModel @Inject constructor(
    private val repository: HabitRepository,
    private val quoteRepository: QuoteRepository
) : ViewModel() {
    // combine re-runs whenever habits OR completions change, so ticking a box updates the streak at once
    val state: StateFlow<HabitListState> = combine(repository.habits, repository.completions) { habits, completions ->
        val today = LocalDate.now()
        val datesByHabit = completions.groupBy({ it.habitId }, { it.date })
        val items = habits.map { habit ->
            val dates = datesByHabit[habit.id].orEmpty()
            HabitItem(habit, calculateStreak(dates, today), today in dates)
        }
        HabitListState.Success(items) as HabitListState
    }
        .catch { emit(HabitListState.Error) }
        // WhileSubscribed(5000) keeps the data flowing through a rotation but stops 5s after the UI is gone
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HabitListState.Loading)

    // The screen shows whatever is cached, so it also appears offline; null until one exists
    val quote: StateFlow<Quote?> = quoteRepository.quote
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    // True when the refresh failed; the screen only shows it if there is no cached quote to fall back on
    private val _quoteRefreshFailed = MutableStateFlow(false)
    val quoteRefreshFailed: StateFlow<Boolean> = _quoteRefreshFailed.asStateFlow()

    init {
        viewModelScope.launch { _quoteRefreshFailed.value = !quoteRepository.refreshIfStale() }
    }

    // The habit awaiting delete confirmation; null means no dialog is showing
    private val _habitToDelete = MutableStateFlow<Habit?>(null)
    val habitToDelete: StateFlow<Habit?> = _habitToDelete.asStateFlow()

    fun onDoneToggled(habit: Habit, done: Boolean) {
        viewModelScope.launch { repository.setDone(habit.id, LocalDate.now(), done) }
    }

    fun onDeleteRequested(habit: Habit) = _habitToDelete.update { habit }

    fun onDeleteDismissed() = _habitToDelete.update { null }

    fun onDeleteConfirmed() {
        val habit = _habitToDelete.value ?: return
        _habitToDelete.update { null }
        viewModelScope.launch { repository.deleteHabit(habit) }
    }
}

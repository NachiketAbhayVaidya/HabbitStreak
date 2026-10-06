package com.example.habitstreak.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitstreak.data.Habit
import com.example.habitstreak.data.Quote
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private val reminderFormat = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitListScreen(
    onAddHabit: () -> Unit,
    onEditHabit: (Long) -> Unit,
    viewModel: HabitListViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    // Lifecycle-aware: stops collecting while the app is in the background
    val state by viewModel.state.collectAsStateWithLifecycle()
    val habitToDelete by viewModel.habitToDelete.collectAsStateWithLifecycle()
    val quote by viewModel.quote.collectAsStateWithLifecycle()
    val quoteRefreshFailed by viewModel.quoteRefreshFailed.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("HabitStreak") }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddHabit,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add habit") }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            QuoteOfTheDay(quote = quote, refreshFailed = quoteRefreshFailed)
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (val current = state) {
                    HabitListState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                    HabitListState.Error -> CenteredMessage("Something went wrong loading your habits.")
                    is HabitListState.Success ->
                        if (current.habits.isEmpty()) {
                            CenteredMessage("No habits yet. Tap \"Add habit\" to start your first streak.")
                        } else {
                            HabitList(
                                items = current.habits,
                                onDoneToggled = viewModel::onDoneToggled,
                                onEdit = onEditHabit,
                                onDelete = viewModel::onDeleteRequested
                            )
                        }
                }
            }
        }
    }

    habitToDelete?.let { habit ->
        DeleteConfirmationDialog(
            habitName = habit.name,
            onConfirm = viewModel::onDeleteConfirmed,
            onDismiss = viewModel::onDeleteDismissed
        )
    }
}

// A cached quote always wins, so a failed refresh only matters when there is nothing to show
@Composable
private fun QuoteOfTheDay(quote: Quote?, refreshFailed: Boolean) {
    val message = when {
        quote != null -> "“${quote.text}” — ${quote.author}"
        refreshFailed -> "Today's quote isn't available. Check your connection."
        else -> return // still loading: show nothing instead of a flash of placeholder
    }
    Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        fontStyle = FontStyle.Italic,
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    )
}

@Composable
private fun HabitList(
    items: List<HabitItem>,
    onDoneToggled: (Habit, Boolean) -> Unit,
    onEdit: (Long) -> Unit,
    onDelete: (Habit) -> Unit
) {
    // key = id lets Compose keep each row's identity when the list changes
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(items, key = { it.habit.id }) { item ->
            HabitRow(item, onDoneToggled, onEdit, onDelete)
            HorizontalDivider()
        }
    }
}

@Composable
private fun HabitRow(
    item: HabitItem,
    onDoneToggled: (Habit, Boolean) -> Unit,
    onEdit: (Long) -> Unit,
    onDelete: (Habit) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit(item.habit.id) }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = item.doneToday, onCheckedChange = { onDoneToggled(item.habit, it) })
        Column(modifier = Modifier.weight(1f)) {
            Text(item.habit.name, style = MaterialTheme.typography.titleMedium)
            Text(
                text = "${item.streak}-day streak · reminder ${item.habit.reminderTime.format(reminderFormat)}",
                style = MaterialTheme.typography.bodySmall
            )
        }
        IconButton(onClick = { onDelete(item.habit) }) {
            Icon(Icons.Default.Delete, contentDescription = "Delete ${item.habit.name}")
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(habitName: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete habit?") },
        text = { Text("\"$habitName\" and its streak history will be removed.") },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Delete") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun CenteredMessage(message: String) {
    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Text(message, textAlign = TextAlign.Center)
    }
}

package com.example.habitstreak.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

private const val LIST_ROUTE = "habits"
private const val EDITOR_ROUTE = "editor"

const val HABIT_ID_ARG = "habitId"
const val NO_HABIT_ID = -1L // the editor's argument when creating a new habit

@Composable
fun HabitStreakApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = LIST_ROUTE) {
        composable(LIST_ROUTE) {
            HabitListScreen(
                onAddHabit = { navController.navigate(EDITOR_ROUTE) },
                onEditHabit = { habitId -> navController.navigate("$EDITOR_ROUTE?$HABIT_ID_ARG=$habitId") }
            )
        }
        // The optional argument means "editor" alone opens a blank form and "editor?habitId=3" edits habit 3
        composable(
            route = "$EDITOR_ROUTE?$HABIT_ID_ARG={$HABIT_ID_ARG}",
            arguments = listOf(
                navArgument(HABIT_ID_ARG) {
                    type = NavType.LongType
                    defaultValue = NO_HABIT_ID
                }
            )
        ) {
            HabitEditorScreen(
                onFinished = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
    }
}

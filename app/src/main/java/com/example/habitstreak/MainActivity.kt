package com.example.habitstreak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.habitstreak.ui.HabitStreakApp
import com.example.habitstreak.ui.theme.HabitStreakTheme
import dagger.hilt.android.AndroidEntryPoint

// Hilt can only inject into an Activity marked with @AndroidEntryPoint
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HabitStreakTheme {
                HabitStreakApp()
            }
        }
    }
}

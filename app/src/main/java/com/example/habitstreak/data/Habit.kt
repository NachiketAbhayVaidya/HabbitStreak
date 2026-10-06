package com.example.habitstreak.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalTime

@Entity(tableName = "habits")
data class Habit(
    // 0 means "not inserted yet": Room then generates the real id
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val reminderTime: LocalTime,
    val createdAt: Instant
)

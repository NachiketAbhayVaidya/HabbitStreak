package com.example.habitstreak.data

import androidx.room.Entity
import androidx.room.ForeignKey
import java.time.LocalDate

// One row means "this habit was done on this date". The composite primary key makes a second
// row for the same habit and day impossible, so duplicates can't happen at the database level.
@Entity(
    tableName = "completions",
    primaryKeys = ["habitId", "date"],
    foreignKeys = [
        ForeignKey(
            entity = Habit::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE // deleting a habit also deletes its completions
        )
    ]
)
data class Completion(
    val habitId: Long,
    val date: LocalDate
)

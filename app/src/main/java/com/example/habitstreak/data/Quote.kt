package com.example.habitstreak.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

const val QUOTE_ROW_ID = 1L

// The cache holds a single row: the id never changes, so saving a new quote replaces the old one.
// `date` is the day it was fetched, used to decide whether the cache is still fresh.
@Entity(tableName = "quotes")
data class Quote(
    @PrimaryKey val id: Long = QUOTE_ROW_ID,
    val text: String,
    val author: String,
    val date: LocalDate
)

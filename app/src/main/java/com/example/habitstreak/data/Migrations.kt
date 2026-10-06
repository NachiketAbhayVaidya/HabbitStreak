package com.example.habitstreak.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// Version 1 -> 2: adds the quote cache. A migration keeps the user's existing habits and
// completions; without one, Room would crash on startup or force us to wipe the database.
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `quotes` " +
                "(`id` INTEGER NOT NULL, `text` TEXT NOT NULL, `author` TEXT NOT NULL, " +
                "`date` INTEGER NOT NULL, PRIMARY KEY(`id`))"
        )
    }
}

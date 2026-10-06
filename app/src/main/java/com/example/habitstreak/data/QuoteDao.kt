package com.example.habitstreak.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuoteDao {
    // Emits null while nothing is cached yet
    @Query("SELECT * FROM quotes WHERE id = $QUOTE_ROW_ID")
    fun observe(): Flow<Quote?>

    @Query("SELECT * FROM quotes WHERE id = $QUOTE_ROW_ID")
    suspend fun get(): Quote?

    // REPLACE overwrites the existing row with the same id
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(quote: Quote)
}

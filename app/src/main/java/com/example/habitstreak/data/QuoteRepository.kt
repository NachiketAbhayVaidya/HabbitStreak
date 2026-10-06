package com.example.habitstreak.data

import com.google.gson.JsonParseException
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import java.io.IOException
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

// What zenquotes.io puts in the author field when it answers "too many requests" with HTTP 200
private const val RATE_LIMIT_AUTHOR = "zenquotes.io"

@Singleton
class QuoteRepository @Inject constructor(
    private val quoteDao: QuoteDao,
    private val quoteApi: QuoteApi
) {
    // The UI only ever reads the cache, so it works offline and a fetch updates it automatically
    val quote: Flow<Quote?> = quoteDao.observe()

    // Fetches only when the cached quote isn't from today (the API limits request rates).
    // Returns whether a quote for today is available; failures are expected, so they aren't thrown.
    suspend fun refreshIfStale(today: LocalDate = LocalDate.now()): Boolean {
        if (quoteDao.get()?.date == today) return true
        return try {
            val response = quoteApi.getToday().firstOrNull()
            val text = response?.q
            val author = response?.a
            if (text.isNullOrBlank() || author.isNullOrBlank() || author == RATE_LIMIT_AUTHOR) {
                false
            } else {
                quoteDao.save(Quote(text = text, author = author, date = today))
                true
            }
        } catch (e: IOException) { // no network, timeout, DNS failure
            false
        } catch (e: HttpException) { // server answered with 4xx/5xx
            false
        } catch (e: JsonParseException) { // body wasn't the JSON we expected
            false
        }
    }
}

package com.example.habitstreak.data

import retrofit2.http.GET

// Gson fills these fields by reflection and ignores nulls in the type system, so they are
// declared nullable: a missing field in the JSON then can't sneak a null into a non-null String.
data class QuoteResponse(val q: String?, val a: String?)

interface QuoteApi {
    // The endpoint returns a JSON array with a single quote
    @GET("api/today")
    suspend fun getToday(): List<QuoteResponse>
}

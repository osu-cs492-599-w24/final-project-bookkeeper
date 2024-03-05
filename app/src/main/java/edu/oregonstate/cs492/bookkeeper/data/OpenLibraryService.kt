package edu.oregonstate.cs492.bookkeeper.data

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenLibraryService {
    @GET("search.json")
    fun getBooks(
        @Query("q") query: String,
        @Query("fields") fields: String? = "title,author_name,cover_i,ratings_average,ratings_count",
        @Query("sort") sort: String? = null,
        @Query("lang") lang: String? = null,
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = null,
        @Query("page") page: Int? = null
    ): Call<String>

        companion object {
            private const val BASE_URL: String = "https://openlibrary.org/"

            fun create(): OpenLibraryService {
                return Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build()
                    .create(OpenLibraryService::class.java)
            }
        }
}
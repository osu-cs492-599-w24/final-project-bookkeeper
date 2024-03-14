package edu.oregonstate.cs492.bookkeeper.data

import retrofit2.Response
import retrofit2.Retrofit
import com.squareup.moshi.Moshi
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenLibraryService {
    @GET("search.json")
    suspend fun getBooks(
        @Query("q") query: String,
        @Query("fields") fields: String? = "title,author_name,cover_i,ratings_average,ratings_count,id_amazon,number_of_pages_median",
        @Query("sort") sort: String? = null,
        @Query("lang") lang: String? = null,
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = null,
        @Query("page") page: Int? = null
    ): Response<BookSearch>

    companion object {
        private const val BASE_URL: String = "https://openlibrary.org/"

        private val moshi = Moshi.Builder()
            .add(OpenLibraryBookJsonAdapter())
            .build()

        fun create(): OpenLibraryService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(OpenLibraryService::class.java)
        }
    }
}
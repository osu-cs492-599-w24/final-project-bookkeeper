package edu.oregonstate.cs492.bookkeeper.data

import android.util.Log
import java.io.Serializable
import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.ToJson

@JsonClass(generateAdapter = true)
data class Book(
    val title: String,
    val author: String? = null,
    val coverURL: String? = null,
    val rating: Float? = null,
    val ratingCount: Int? = null
)

@JsonClass(generateAdapter = true)
data class BookJson(
    val title: String,
    val author_name: List<String>? = null,
    val cover_i: String? = null,
    val ratings_average: Float? = null,
    val ratings_count: Int? = null
)

class OpenLibraryBookJsonAdapter {
    @FromJson
    fun bookFromJson(book: BookJson): Book {
        Log.d("Book", "book: $book")
        return Book(
            title = book.title,
            author = book.author_name?.let{book.author_name[0]},
            coverURL = book.cover_i?.let{"https://covers.openlibrary.org/b/id/${book.cover_i}-M.jpg"},
            rating = book.ratings_average,
            ratingCount = book.ratings_count
        )
    }

    @ToJson
    fun bookToJson(book: Book): String {
        throw UnsupportedOperationException("encoding Book to JSON is not supported")
    }
}
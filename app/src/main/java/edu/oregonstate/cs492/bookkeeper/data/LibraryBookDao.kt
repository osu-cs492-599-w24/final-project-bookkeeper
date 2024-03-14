package edu.oregonstate.cs492.bookkeeper.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryBookDao {
    @Insert (onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(book: LibraryBook)

    @Query("DELETE FROM library WHERE title = :title AND author = :author")
    suspend fun delete(title: String, author: String)

    @Query("SELECT * FROM library")
    fun getAllBooks() : Flow<List<LibraryBook>>

    @Query("SELECT * FROM library WHERE title = :title AND author = :author")
    fun getBook(title: String, author: String) : Flow<LibraryBook>

    @Query("SELECT * FROM library WHERE title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%'")
    fun getBookByTitleOrAuthor(query: String) : Flow<List<LibraryBook>>
}
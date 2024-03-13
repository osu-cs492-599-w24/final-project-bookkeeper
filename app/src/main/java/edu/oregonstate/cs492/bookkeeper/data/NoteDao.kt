package edu.oregonstate.cs492.bookkeeper.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("SELECT * FROM notes WHERE bookTitle = :title AND bookAuthor = :author")
    fun getNotesByBook(title: String, author: String) : Flow<List<Note>>

    @Query("SELECT * FROM notes")
    fun getAllNotes() : Flow<List<Note>>
}
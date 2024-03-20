package edu.oregonstate.cs492.bookkeeper.data

class NotesRepository (
    private val dao: NoteDao
) {
    suspend fun insertNote(note: Note) = dao.insert(note)

    suspend fun deleteNote(note: Note) = dao.delete(note)

    fun getNotesByBook(title: String, author: String) = dao.getNotesByBook(title, author)

    fun getAllNotes() = dao.getAllNotes()
}
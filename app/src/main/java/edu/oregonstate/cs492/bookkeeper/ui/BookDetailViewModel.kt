package edu.oregonstate.cs492.bookkeeper.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import edu.oregonstate.cs492.bookkeeper.data.LibraryRepository
import edu.oregonstate.cs492.bookkeeper.data.Note
import edu.oregonstate.cs492.bookkeeper.data.NotesRepository
import kotlinx.coroutines.launch

class BookDetailViewModel(
    private val libraryRepository: LibraryRepository,
    private val notesRepository: NotesRepository
) : ViewModel() {

    fun getBookDetails(title: String, author: String) =
        libraryRepository.getBook(title, author).asLiveData()

    fun getNotesByBook(title: String, author: String) =
        notesRepository.getNotesByBook(title, author).asLiveData()

    fun insertNote(note: Note) = viewModelScope.launch {
        notesRepository.insertNote(note)
    }

    fun deleteNote(note: Note) = viewModelScope.launch {
        notesRepository.deleteNote(note)
    }
}

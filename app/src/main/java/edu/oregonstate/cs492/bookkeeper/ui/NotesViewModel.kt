package edu.oregonstate.cs492.bookkeeper.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import edu.oregonstate.cs492.bookkeeper.data.AppDatabase
import edu.oregonstate.cs492.bookkeeper.data.Note
import edu.oregonstate.cs492.bookkeeper.data.NotesRepository
import kotlinx.coroutines.launch
class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NotesRepository(
        AppDatabase.getInstance(application).noteDao()
    )

    fun addNote(note: Note) {
        viewModelScope.launch {
            repository.insertNote(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun getNotesByBook(title: String, author: String) =
        repository.getNotesByBook(title, author).asLiveData()

    fun getAllNotes() = repository.getAllNotes().asLiveData()
}
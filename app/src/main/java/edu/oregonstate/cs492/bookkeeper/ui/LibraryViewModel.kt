package edu.oregonstate.cs492.bookkeeper.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import edu.oregonstate.cs492.bookkeeper.data.AppDatabase
import edu.oregonstate.cs492.bookkeeper.data.LibraryBook
import edu.oregonstate.cs492.bookkeeper.data.LibraryRepository
import kotlinx.coroutines.launch
class LibraryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = LibraryRepository(
        AppDatabase.getInstance(application).libraryBookDao()
    )

    val libraryBooks = repository.getAllBooks().asLiveData()

    fun addBook(book: LibraryBook) {
        viewModelScope.launch {
            repository.insertBook(book)
        }
    }

    fun removeBook(title: String, author: String) {
        viewModelScope.launch {
            repository.deleteBook(title, author)
        }
    }

    fun getBook(title: String, author: String) =
        repository.getBook(title, author).asLiveData()

    fun getBookByTitleOrAuthor(query: String) =
        repository.getBookByTitleOrAuthor(query).asLiveData()
}
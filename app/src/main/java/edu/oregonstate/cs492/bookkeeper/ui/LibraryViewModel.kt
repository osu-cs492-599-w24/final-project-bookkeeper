package edu.oregonstate.cs492.bookkeeper.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import edu.oregonstate.cs492.bookkeeper.data.AppDatabase
import edu.oregonstate.cs492.bookkeeper.data.BookSearch
import edu.oregonstate.cs492.bookkeeper.data.LibraryBook
import edu.oregonstate.cs492.bookkeeper.data.LibraryRepository
import edu.oregonstate.cs492.bookkeeper.util.LoadingStatus
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

    fun removeAllBooks(){
        viewModelScope.launch{
            repository.deleteAllBooks()
        }
    }

    fun getBook(title: String, author: String) =
        repository.getBook(title, author).asLiveData()

    fun getBookByTitleOrAuthor(query: String) =
        repository.getBookByTitleOrAuthor(query).asLiveData()
}
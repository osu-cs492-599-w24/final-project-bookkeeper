package edu.oregonstate.cs492.bookkeeper.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.oregonstate.cs492.bookkeeper.data.BookSearch
import edu.oregonstate.cs492.bookkeeper.data.BookSearchRepository
import edu.oregonstate.cs492.bookkeeper.data.OpenLibraryService
import edu.oregonstate.cs492.bookkeeper.util.LoadingStatus
import kotlinx.coroutines.launch

class BookSearchViewModel : ViewModel() {
    private val repository = BookSearchRepository(OpenLibraryService.create())

    private val _searchResults = MutableLiveData<BookSearch?>(null)
    val searchResults: LiveData<BookSearch?> = _searchResults

    private val _loadingStatus = MutableLiveData<LoadingStatus>(LoadingStatus.SUCCESS)
    val loadingStatus: LiveData<LoadingStatus> = _loadingStatus

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    fun loadSearchResults(query: String) {
        viewModelScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING
            val result = repository.loadBookSearch(query)
            _searchResults.value = result.getOrNull()
            _error.value = result.exceptionOrNull()?.message
            _loadingStatus.value = when (result.isSuccess) {
                true -> LoadingStatus.SUCCESS
                false -> LoadingStatus.ERROR
            }
        }
    }
}
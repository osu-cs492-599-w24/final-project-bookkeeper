package edu.oregonstate.cs492.bookkeeper.ui

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import edu.oregonstate.cs492.bookkeeper.R
import edu.oregonstate.cs492.bookkeeper.data.BookSearch
import edu.oregonstate.cs492.bookkeeper.data.OpenLibraryService
import androidx.fragment.app.viewModels


class BrowseBooksFragment : Fragment(R.layout.fragment_browse_books) {
    private val tag = "BrowseBooksFragment"
    private val viewModel: BookSearchViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchBoxET: EditText = view.findViewById(R.id.et_search_box)
        val searchBtn: Button = view.findViewById(R.id.btn_search)

        viewModel.searchResults.observe(viewLifecycleOwner) {
            searchResults -> filterSearchResults(searchResults)
        }

        viewModel.loadingStatus.observe(viewLifecycleOwner) {
            loadingStatus -> Log.d(tag, "Loading status: $loadingStatus")
        }

        viewModel.error.observe(viewLifecycleOwner) {
            error -> Log.d(tag, "Error: $error")
        }

        searchBtn.setOnClickListener {
            val query = searchBoxET.text.toString()
            if (!TextUtils.isEmpty(query)) {
                viewModel.loadSearchResults(query)
            }
        }
    }


    private fun filterSearchResults(searchResults: BookSearch?) {
        Log.d(tag, "Moshi adapted books: ")
        searchResults?.books?.forEachIndexed {index, book ->
            Log.d(tag, "Book ${index + 1}: $book")
        }

        //remove any books that don't have an author or don't have a cover
        searchResults?.books?.removeIf {book ->
            book.author.isNullOrEmpty() || book.coverURL.isNullOrEmpty()
        }

        Log.d(tag, "Filtered books: ")
        searchResults?.books?.forEachIndexed {index, book ->
            Log.d(tag, "Book ${index + 1}: $book")
        }

    }


}

package edu.oregonstate.cs492.bookkeeper.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.oregonstate.cs492.bookkeeper.R
import edu.oregonstate.cs492.bookkeeper.data.BookSearch


class BrowseBooksFragment : Fragment(R.layout.fragment_browse_books) {
    private val tag = "BrowseBooksFragment"
    private val viewModel: BookSearchViewModel by viewModels()

    private lateinit var browseBooksAdapter: BrowseBooksAdapter
    private lateinit var booksRecyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        booksRecyclerView = view.findViewById(R.id.books_recycler_view)
        browseBooksAdapter = BrowseBooksAdapter()  // Initialize your adapter here
        booksRecyclerView.adapter = browseBooksAdapter  // Set the adapter
        booksRecyclerView.layoutManager = LinearLayoutManager(context)  // Set the layout manager

        viewModel.searchResults.observe(viewLifecycleOwner) { searchResults ->
            filterSearchResults(searchResults)
            // TODO update the adapter with filtered data
            // forecastAdapter.books = searchResults?.books ?: listOf()
            // forecastAdapter.notifyDataSetChanged()
        }

        viewModel.loadingStatus.observe(viewLifecycleOwner) {
            loadingStatus -> Log.d(tag, "Loading status: $loadingStatus")
        }

        viewModel.error.observe(viewLifecycleOwner) {
            error -> Log.d(tag, "Error: $error")
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

package edu.oregonstate.cs492.bookkeeper.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.oregonstate.cs492.bookkeeper.R
import edu.oregonstate.cs492.bookkeeper.data.Book
import edu.oregonstate.cs492.bookkeeper.data.BookSearch
import edu.oregonstate.cs492.bookkeeper.data.LibraryBook


class BrowseBooksFragment : Fragment(R.layout.fragment_browse_books) {
    private val tag = "BrowseBooksFragment"
    private val viewModel: BookSearchViewModel by viewModels()
    private val libraryViewModel: LibraryViewModel by viewModels()
    private lateinit var libraryBooks: List<LibraryBook>

    private val browseBooksAdapter = BrowseBooksAdapter(::onBookClick, ::setButtonText)
    private lateinit var booksRecyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        booksRecyclerView = view.findViewById(R.id.books_recycler_view)
        booksRecyclerView.adapter = browseBooksAdapter  // Set the adapter
        booksRecyclerView.layoutManager = LinearLayoutManager(context)  // Set the layout manager

        //observe library books in order to see if a searched book can be added or removed
        libraryViewModel.libraryBooks.observe(viewLifecycleOwner) { books ->
            libraryBooks = books
        }

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

    // set a searched book's button to add/remove based on whether or not it's in the library
    private fun setButtonText(book: Book, button: Button) {
        book.author?.let {
            libraryViewModel.getBook(book.title, book.author).observe(viewLifecycleOwner) {
                book ->
                when (book) {
                    null -> button.text = "Add"
                    else -> button.text = "Remove"
                }
            }
        }
    }

    private fun onBookClick(book: Book) {
        book.author?.let {
            // check if a matching book is in the library
            val testBook = libraryBooks.any { libraryBook ->
                libraryBook.title == book.title && libraryBook.author == book.author
            }

            // either add or remove the book based on it being in the library
            if (testBook) {
                libraryViewModel.removeBook(book.title, book.author)
            } else {
                libraryViewModel.addBook(
                    LibraryBook(
                        book.title,
                        book.author,
                        book.coverURL?: "",
                        book.rating,
                        book.ratingCount,
                        book.amazonLink,
                        book.pageCount ?: 0
                    )
                )
            }
        }
    }

}

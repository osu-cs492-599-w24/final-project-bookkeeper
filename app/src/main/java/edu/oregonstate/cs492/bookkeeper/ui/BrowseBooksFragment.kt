package edu.oregonstate.cs492.bookkeeper.ui

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import com.google.android.material.snackbar.Snackbar
import edu.oregonstate.cs492.bookkeeper.R
import edu.oregonstate.cs492.bookkeeper.data.Book
import edu.oregonstate.cs492.bookkeeper.data.BookSearch
import edu.oregonstate.cs492.bookkeeper.data.LibraryBook
import edu.oregonstate.cs492.bookkeeper.util.LoadingStatus


class BrowseBooksFragment : Fragment(R.layout.fragment_browse_books) {
    private val tag = "BrowseBooksFragment"
    private val viewModel: BookSearchViewModel by viewModels()
    private val libraryViewModel: LibraryViewModel by viewModels()
    private lateinit var libraryBooks: List<LibraryBook>

    private val browseBooksAdapter = BrowseBooksAdapter(emptyList(), ::onBookClick, ::setButtonText)
    private lateinit var booksRecyclerView: RecyclerView
    private lateinit var searchBar: SearchBar
    private lateinit var searchView: SearchView
    private lateinit var loadingIndicator: CircularProgressIndicator


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        booksRecyclerView = view.findViewById(R.id.books_recycler_view)
        booksRecyclerView.adapter = browseBooksAdapter
        booksRecyclerView.layoutManager = LinearLayoutManager(context)

        searchBar = view.findViewById(R.id.search_bar)
        searchView = view.findViewById(R.id.search_view)

        searchView
            .editText
            .setOnEditorActionListener { _, _, _ ->
                searchBar.setText(searchView.text)
                searchView.hide()
                viewModel.loadSearchResults(searchView.text.toString())
                true
            }

        loadingIndicator = view.findViewById(R.id.loading_indicator)


        //observe library books in order to see if a searched book can be added or removed
        libraryViewModel.libraryBooks.observe(viewLifecycleOwner) { books ->
            libraryBooks = books
        }

        viewModel.searchResults.observe(viewLifecycleOwner) { searchResults ->
            filterSearchResults(searchResults)
            browseBooksAdapter.updateBookList(searchResults?.books)
        }

        viewModel.loadingStatus.observe(viewLifecycleOwner) { loadingStatus ->
            Log.d(tag, "Loading status: $loadingStatus")

            if (loadingStatus == LoadingStatus.LOADING) {
                booksRecyclerView.visibility = View.INVISIBLE
                loadingIndicator.visibility = View.VISIBLE
            } else {
                booksRecyclerView.visibility = View.VISIBLE
                loadingIndicator.visibility = View.INVISIBLE
            }


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
    private fun setButtonText(book: Book, button: MaterialButton) {
        book.author?.let {
            libraryViewModel.getBook(book.title, book.author).observe(viewLifecycleOwner) {
                book ->
                when (book) {
                    null -> button.setIconResource(R.drawable.add_book)
                    else -> button.setIconResource(R.drawable.remove_book)
                }
            }
        }
    }

    private fun onBookClick(book: Book) {
        var snackbarMessage: String

        book.author?.let {
            // check if a matching book is in the library
            val testBook = libraryBooks.any { libraryBook ->
                libraryBook.title == book.title && libraryBook.author == book.author
            }

            // either add or remove the book based on it being in the library
            if (testBook) {
                libraryViewModel.removeBook(book.title, book.author)
                snackbarMessage = "removed from library"

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
                snackbarMessage = "added to library"
            }

            Snackbar.make(
                requireView(),
                Html.fromHtml("<i>${book.title}</i> $snackbarMessage"),
                Snackbar.LENGTH_LONG
            ).show()
        }
    }
}

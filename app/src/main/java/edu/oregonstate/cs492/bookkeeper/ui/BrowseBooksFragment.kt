package edu.oregonstate.cs492.bookkeeper.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
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

    private val browseBooksAdapter = BrowseBooksAdapter(emptyList(), ::onBookClick, ::setButtonText, ::onCartClick)
    private lateinit var booksRecyclerView: RecyclerView
    private val recentSearchAdapter = RecentSearchAdapter(emptyList(), ::onRecentSearchClick)

    private lateinit var searchBar: SearchBar
    private lateinit var searchView: SearchView
    private lateinit var loadingIndicator: CircularProgressIndicator



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        booksRecyclerView = view.findViewById(R.id.books_recycler_view)
        booksRecyclerView.adapter = browseBooksAdapter
        booksRecyclerView.layoutManager = LinearLayoutManager(context)

        val recentSearchRecyclerView = view.findViewById<RecyclerView>(R.id.recent_search_recycler_view)
        recentSearchRecyclerView.adapter = recentSearchAdapter
        recentSearchRecyclerView.layoutManager = LinearLayoutManager(context)

        searchBar = view.findViewById(R.id.search_bar)
        searchView = view.findViewById(R.id.search_view)
        val bottomNav: BottomNavigationView? = activity?.findViewById(R.id.bottom_nav)

        // Hide bottom navbar after clicking on searchbar
        searchView.addTransitionListener { _, _, newState ->
            if (newState == SearchView.TransitionState.SHOWING) {
                bottomNav?.visibility = View.GONE
            }
             /* This can be included if we want the navbar to appear before loading finishes.
             The issue is that it jumps down from above the keyboard, to the bottom of the screen
             Felt more jarring than only reappearing after loading.

             else if (newState == SearchView.TransitionState.HIDDEN) {
                bottomNav?.visibility = View.VISIBLE
            }
             */
        }

        searchView
            .editText
            .setOnEditorActionListener { _, _, _ ->
                val searchQuery = searchView.text.toString()
                searchBar.setText(searchQuery)
                searchView.hide()
                viewModel.loadSearchResults(searchQuery)
                addRecentSearch(searchQuery)
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
                bottomNav?.visibility = View.VISIBLE
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

    private fun onCartClick(book: Book) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(book.amazonLink)))
        } catch (e: Exception) {
            Snackbar.make(
                requireView(),
                "Error opening link in browser",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun onRecentSearchClick(recentSearch: String) {
        searchBar.setText(recentSearch)
        searchView.hide()
        viewModel.loadSearchResults(recentSearch)
        addRecentSearch(recentSearch)
    }

    private fun addRecentSearch(searchQuery: String) {
        val currentRecentSearches = recentSearchAdapter.getRecentSearches().toMutableList()

        // Remove the search query if it already exists in the list
        currentRecentSearches.remove(searchQuery)

        // Add the new search query to the beginning of the list
        val updatedRecentSearches = mutableListOf(searchQuery)
        updatedRecentSearches.addAll(currentRecentSearches)


        val maxRecentSearches = 10
        val limitedRecentSearches = updatedRecentSearches.take(maxRecentSearches)

        recentSearchAdapter.updateRecentSearches(limitedRecentSearches)
    }
}

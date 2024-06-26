package edu.oregonstate.cs492.bookkeeper.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.GridLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.oregonstate.cs492.bookkeeper.R
import edu.oregonstate.cs492.bookkeeper.data.LibraryBook
import edu.oregonstate.cs492.bookkeeper.data.Note
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView


class LibraryFragment : Fragment(R.layout.fragment_library) {
    private val tag: String = "LibraryFragment"
    private val viewModel: LibraryViewModel by viewModels()
    private val notesViewModel: NotesViewModel by viewModels()

    private lateinit var books: List<LibraryBook>
    private var notes: MutableList<Note> = mutableListOf()

    private val libraryAdapter = LibraryAdapter(emptyList(), ::onLibraryBookClick)
    private val librarySearchAdapter = LibrarySearchAdapter(emptyList(), ::onRecentSearchClick)
    private  lateinit var libraryRecyclerView: RecyclerView
    private lateinit var searchBar: SearchBar
    private lateinit var searchView: SearchView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        libraryRecyclerView = view.findViewById(R.id.library_recycler_view)
        libraryRecyclerView.adapter = libraryAdapter
        libraryRecyclerView.layoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)

        val recentSearchRecyclerView = view.findViewById<RecyclerView>(R.id.recent_search_library_recycler_view)
        recentSearchRecyclerView.adapter = librarySearchAdapter
        recentSearchRecyclerView.layoutManager = LinearLayoutManager(context)

        searchBar = view.findViewById(R.id.search_library_bar)
        searchView = view.findViewById(R.id.search_library_view)
        val appBar: MaterialToolbar? = activity?.findViewById(R.id.top_app_bar)
        val bottomNav: BottomNavigationView? = activity?.findViewById(R.id.bottom_nav)



        searchView.addTransitionListener { _, _, newState ->
            if (newState == SearchView.TransitionState.SHOWING) {
                appBar?.visibility = View.GONE
                bottomNav?.visibility = View.GONE
            } else if (newState == SearchView.TransitionState.HIDDEN) {
                appBar?.visibility = View.VISIBLE
                bottomNav?.visibility = View.VISIBLE
            }
        }

        searchView.editText.setOnEditorActionListener { _, _, _ ->
            val searchQuery = searchView.text.toString()
            searchBar.setText(searchQuery)
            searchView.hide()
            Log.d(tag, "Lib Query: $searchQuery")
            viewModel.getBookByTitleOrAuthor(searchQuery).observe(viewLifecycleOwner) {
                books ->
                libraryAdapter.updateLibraryList(books)
                Log.d(tag, "books: $books")
            }
            addRecentSearch(searchQuery)
            true
        }

        viewModel.libraryBooks.observe(viewLifecycleOwner) {books ->
            //Log.d(tag, "\nBooks in library:\n")
//            books.forEach { book ->
//                Log.d(tag, "Book: $book")
//            }
            libraryAdapter.updateLibraryList(books)
        }

        //deleteDB()
        //createTestingData()
        //testDbQueries()
    }

    private fun onLibraryBookClick(book: LibraryBook){
        Log.d(tag, "Book clicked: ${book.title}")
    }

    private fun onRecentSearchClick(recentSearch: String) {
        searchBar.setText(recentSearch)
        searchView.hide()
        viewModel.getBookByTitleOrAuthor(recentSearch)
        addRecentSearch(recentSearch)
    }

    private fun addRecentSearch(searchQuery: String){
        val currentRecentSearches = librarySearchAdapter.getRecentSearches().toMutableList()

        currentRecentSearches.remove(searchQuery)

        val updatedRecentSearches = mutableListOf(searchQuery)
        updatedRecentSearches.addAll(currentRecentSearches)

        val maxRecentSearches = 10
        val limitedRecentSearches = updatedRecentSearches.take(maxRecentSearches)

        librarySearchAdapter.updateRecentSearches(limitedRecentSearches)
    }

    private fun deleteDB() {
        viewModel.removeAllBooks()
    }

    private fun createTestingData() {
        books = listOf(
            LibraryBook(
                title = "Book 1",
                author = "Author 1",
                coverURL = "https://covers.openlibrary.org/b/id/12818862-M.jpg",
                rating = 4.5f,
                ratingCount = 100,
                amazonLink = "https://www.amazon.com/book1"
            ),
            LibraryBook(
                title = "Book 2",
                author = "Author 2",
                coverURL = "https://covers.openlibrary.org/b/id/12818862-M.jpg",
                amazonLink = "https://www.amazon.com/book2"
            ),
            LibraryBook(
                title = "Book 3",
                author = "Author 3",
                coverURL = "https://covers.openlibrary.org/b/id/12818862-M.jpg"
            )
        )

        books.forEach { book ->
            viewModel.addBook(book)
        }

        books.forEach { book ->
            repeat(2) {
                notes.add(
                    Note(
                        title = "Note ${it + 1} for ${book.title}",
                        bookTitle = book.title,
                        bookAuthor = book.author,
                        category = "Category ${it + 1}",
                        content = "This is a test note for ${book.title}."
                    )
                )
            }
        }

        notes.forEach{ note ->
            notesViewModel.addNote(note)
        }
    }

    private fun testDbQueries() {
        viewModel.getBookByTitleOrAuthor("1").observe(viewLifecycleOwner) {books ->
            Log.d(tag, "\nBook query testing:\n")
            books.forEach {book ->
                Log.d(tag, "Testing query for book like '1': $book")
            }
        }

        notesViewModel.getNotesByBook("Book 2", "Author 2").observe(viewLifecycleOwner) { notes ->
            Log.d(tag, "\nNote query testing: \n")
            notes.forEach {note ->
                Log.d(tag, "Testing notes for Book 2: $note")
            }
        }

        notesViewModel.getAllNotes().observe(viewLifecycleOwner) {notes ->
            Log.d(tag, "\nTesting all notes: \n")
            notes.forEach {note ->
                Log.d(tag, "note: $note")
            }
        }
    }
}
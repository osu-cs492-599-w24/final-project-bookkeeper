package edu.oregonstate.cs492.bookkeeper.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import edu.oregonstate.cs492.bookkeeper.R
import edu.oregonstate.cs492.bookkeeper.data.LibraryBook
import edu.oregonstate.cs492.bookkeeper.data.Note

class LibraryFragment : Fragment(R.layout.fragment_library) {
    private val tag: String = "LibraryFragment"
    private val viewModel: LibraryViewModel by viewModels()
    private val notesViewModel: NotesViewModel by viewModels()

    private lateinit var books: List<LibraryBook>
    private var notes: MutableList<Note> = mutableListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.libraryBooks.observe(viewLifecycleOwner) {books ->
            Log.d(tag, "\nBooks in library:\n")
            books.forEach { book ->
                Log.d(tag, "Book: $book")
            }
        }

        createTestingData()
        testDbQueries()
    }

    private fun createTestingData() {
        books = listOf(
            LibraryBook(
                title = "Book 1",
                author = "Author 1",
                coverURL = "https://example.com/book1_cover.jpg",
                rating = 4.5f,
                ratingCount = 100,
                amazonLink = "https://www.amazon.com/book1"
            ),
            LibraryBook(
                title = "Book 2",
                author = "Author 2",
                coverURL = "https://example.com/book2_cover.jpg",
                amazonLink = "https://www.amazon.com/book2"
            ),
            LibraryBook(
                title = "Book 3",
                author = "Author 3",
                coverURL = "https://example.com/book3_cover.jpg"
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
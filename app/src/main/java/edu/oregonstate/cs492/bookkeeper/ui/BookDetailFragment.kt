package edu.oregonstate.cs492.bookkeeper.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import edu.oregonstate.cs492.bookkeeper.data.LibraryBook
import edu.oregonstate.cs492.bookkeeper.databinding.FragmentBookDetailBinding
import androidx.fragment.app.viewModels
import edu.oregonstate.cs492.bookkeeper.R
import edu.oregonstate.cs492.bookkeeper.data.AppDatabase
import edu.oregonstate.cs492.bookkeeper.data.LibraryRepository
import edu.oregonstate.cs492.bookkeeper.data.NotesRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import edu.oregonstate.cs492.bookkeeper.data.Note

class BookDetailFragment : Fragment() {

    private var _binding: FragmentBookDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BookDetailViewModel by viewModels {
        val database = AppDatabase.getInstance(requireContext())
        val libraryRepository = LibraryRepository(database.libraryBookDao())
        val notesRepository = NotesRepository(database.noteDao())
        BookDetailViewModelFactory(libraryRepository, notesRepository)
    }

    private val notesViewModel: NotesViewModel by viewModels()
    private val notesAdapter = BookDetailAdapter(emptyList(), ::showNoteDialog)
    private lateinit var notesRecyclerView: RecyclerView
    private lateinit var book: LibraryBook

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBookDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // notes recycler view
        notesRecyclerView = view.findViewById(R.id.notesRecyclerView)
        notesRecyclerView.adapter = notesAdapter
        notesRecyclerView.layoutManager = LinearLayoutManager(context)

        // Retrieve the LibraryBook object from the fragment's arguments.
        book = arguments?.getSerializable("bookDetails") as LibraryBook

        book.let {
            // Update UI elements with the book details.
            binding.bookTitleText.text = it.title
            binding.bookAuthorText.text = it.author
            binding.pagesReadButton.text = getString(R.string.pages_read_button_text, it.pagesRead, it.pageCount)
            // Now set the rating and summary
            binding.bookRatingBar.rating = it.rating ?: 0.0f // Assuming 'rating' is a Float, replace with default if null
            //binding.bookSummaryText.text = it.summary ?: getString(R.string.default_summary) // Assuming 'summary' is a String

            Glide.with(this)
                .load(it.coverURL)
                .placeholder(R.drawable.baseline_book_24)  // Replace 'default_cover' with your actual placeholder image resource
                .error(R.drawable.baseline_book_24)        // Replace 'default_cover' with your actual error image resource
                .into(binding.bookCoverImage)

            // Handle 'Add Note' button click
            binding.addNoteButton.setOnClickListener {
                showNoteDialog(null)
            }
        }

        // pages read button listener
        binding.pagesReadButton.setOnClickListener {
            showPagesDialog()
        }

        binding.deleteBookButton.setOnClickListener {
            showDeleteBookConfirmationDialog()
        }

        // observe notes viewModel
        notesViewModel.getNotesByBook(book.title, book.author)
            .observe(viewLifecycleOwner) { notes ->
                notesAdapter.updateNotesList(notes)
            }

        // observe changes in book (such as pages read/count)
        viewModel.getBookDetails(book.title, book.author).observe(viewLifecycleOwner) { updatedBook ->
            book = updatedBook
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Clear the binding when the view is destroyed.
    }


    private fun showNoteDialog(note: Note?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_note, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val noteTitleET = dialogView.findViewById<EditText>(R.id.note_title)
        val noteContentET = dialogView.findViewById<EditText>(R.id.note_content)

        // if a note is clicked on, populate title and content
        if (note != null) {
            noteTitleET.setText(note.title)
            noteContentET.setText(note.content)
        }

        val btnCancel = dialogView.findViewById<Button>(R.id.cancel_note)
        val btnSave = dialogView.findViewById<Button>(R.id.save_note)
        val btnDelete = dialogView.findViewById<Button>(R.id.delete_note)

        btnCancel.setOnClickListener { dialog.dismiss() }

        // remove delete note button if there is no note, otherwise set click listener
        if (note == null) {
            btnDelete.visibility = View.GONE
        } else {
            btnDelete.setOnClickListener {
                showDeleteNoteConfirmationDialog(note, dialog)
            }
        }

        btnSave.setOnClickListener {
            // save the note
            val noteTitle = noteTitleET.text.toString()
            val noteContent = noteContentET.text.toString()

            // create note and add to db. using a placeholder category for now as we don't have time to implement this feature
            // TODO: implement note categories (place, people, plot, etc.)
            notesViewModel.addNote(
                Note(
                    noteTitle,
                    book.title,
                    book.author,
                    getString(R.string.note_category_placeholder),
                    noteContent
                )
            )

            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showPagesDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_pages, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        // update ets
        val totalPagesET = dialogView.findViewById<EditText>(R.id.total_pages_et)
        totalPagesET.hint = book.pageCount.toString()
        val pagesReadET = dialogView.findViewById<EditText>(R.id.pages_read_et)
        pagesReadET.hint = book.pagesRead.toString()

        // set button click listeners
        val btnCancel = dialogView.findViewById<Button>(R.id.cancel_pages)
        val btnSave = dialogView.findViewById<Button>(R.id.save_pages)

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnSave.setOnClickListener {
            // save the pages
            var totalPages = totalPagesET.text.toString()
            var pagesRead = pagesReadET.text.toString()

            // only update the button and db if there were changes
            if (pagesRead.toIntOrNull() != book.pagesRead ||
                totalPages.toIntOrNull() != book.pageCount) {


                //TODO: fix db logic so we don't need so many checks/conversions
                if (totalPages.isEmpty())
                    totalPages = book.pageCount.toString()

                if (pagesRead.isEmpty())
                    pagesRead = book.pagesRead.toString()

                // set text for pages read button
                binding.pagesReadButton.text = getString(
                    R.string.pages_read_button_text,
                    pagesRead.toInt(),
                    totalPages.toInt()
                )

                // update db
                viewModel.updatePages(
                    book.title,
                    book.author,
                    pagesRead.toInt(),
                    totalPages.toInt()
                )
            }

            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDeleteBookConfirmationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_confirm_delete_book, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val cancelButton = dialogView.findViewById<Button>(R.id.cancel_delete_book)
        val confirmButton = dialogView.findViewById<Button>(R.id.confirm_delete_book)

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        confirmButton.setOnClickListener {
            //delete book from db (notes cascade delete automatically)
            viewModel.removeBook(
                book.title,
                book.author
            )

            // navigate back to the library (clear backwards nav first so you can't go back
            // to the book details page of the deleted book)
            findNavController().popBackStack(R.id.library_fragment, true)

            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDeleteNoteConfirmationDialog(note: Note, noteDialog: AlertDialog) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_confirm_delete_book, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val cancelButton = dialogView.findViewById<Button>(R.id.cancel_delete_book)
        val confirmButton = dialogView.findViewById<Button>(R.id.confirm_delete_book)

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        // change text to work for deleting notes too
        // TODO: fix naming conventions to make confirmation dialog generic
        val headerText = dialogView.findViewById<TextView>(R.id.delete_book_header)
        val bodyText = dialogView.findViewById<TextView>(R.id.delete_book_message)
        headerText.text = getString(R.string.delete_note_header_text)
        bodyText.text = getString(R.string.delete_note_dialog_message)

        confirmButton.setOnClickListener {
            //delete note from db
            notesViewModel.deleteNote(note)

            noteDialog.dismiss()
            dialog.dismiss()
        }

        dialog.show()
    }
}

class BookDetailViewModelFactory(
    private val libraryRepository: LibraryRepository,
    private val notesRepository: NotesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookDetailViewModel(libraryRepository, notesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


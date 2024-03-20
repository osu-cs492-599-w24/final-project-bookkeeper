package edu.oregonstate.cs492.bookkeeper.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class BookDetailFragment : Fragment() {

    private var _binding: FragmentBookDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BookDetailViewModel by viewModels {
        val database = AppDatabase.getInstance(requireContext())
        val libraryRepository = LibraryRepository(database.libraryBookDao())
        val notesRepository = NotesRepository(database.noteDao())
        BookDetailViewModelFactory(libraryRepository, notesRepository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBookDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the LibraryBook object from the fragment's arguments.
        val book = arguments?.getSerializable("bookDetails") as? LibraryBook

        book?.let {
            // Update UI elements with the book details.
            binding.bookTitleText.text = it.title
            binding.bookAuthorText.text = it.author
            binding.pagesReadText.text = getString(R.string.pages_read, it.pagesRead, it.pageCount)

            Glide.with(this)
                .load(it.coverURL)
                .placeholder(R.drawable.baseline_book_24)  // Replace 'default_cover' with your actual placeholder image resource
                .error(R.drawable.baseline_book_24)        // Replace 'default_cover' with your actual error image resource
                .into(binding.bookCoverImage)

            // Handle 'Add Note' button click
            binding.addNoteButton.setOnClickListener {
                // Here, add logic to show the note adding interface or fragment
                // For example, you could navigate to another fragment to add a note:
                // findNavController().navigate(R.id.action_bookDetailFragment_to_addNoteFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Clear the binding when the view is destroyed.
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


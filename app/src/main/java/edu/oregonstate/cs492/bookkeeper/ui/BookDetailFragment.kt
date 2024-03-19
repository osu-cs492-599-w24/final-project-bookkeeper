package edu.oregonstate.cs492.bookkeeper.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import edu.oregonstate.cs492.bookkeeper.data.LibraryBook
import edu.oregonstate.cs492.bookkeeper.databinding.FragmentBookDetailBinding // Make sure this matches the name of your layout file
import androidx.fragment.app.viewModels
import edu.oregonstate.cs492.bookkeeper.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import edu.oregonstate.cs492.bookkeeper.data.LibraryRepository
import edu.oregonstate.cs492.bookkeeper.data.NotesRepository
import edu.oregonstate.cs492.bookkeeper.data.AppDatabase


//class BookDetailFragment : Fragment() {
//
//    private var _binding: FragmentBookDetailBinding? = null
//    private val binding get() = _binding!!
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
//        _binding = FragmentBookDetailBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        val book = arguments?.getSerializable("bookDetails") as? LibraryBook
//
//        book?.let {
//            binding.bookTitleText.text = it.title
//            binding.bookAuthorText.text = it.author
//            // Populate other views in your layout similarly
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null // This is to avoid memory leaks
//    }
//}

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

        val bookTitle = arguments?.getString("bookTitle")
        val bookAuthor = arguments?.getString("bookAuthor")

        bookTitle?.let { title ->
            bookAuthor?.let { author ->
                viewModel.getBookDetails(title, author).observe(viewLifecycleOwner) { book ->
                    binding.bookTitleText.text = book?.title ?: "Unknown Title"
                    binding.bookAuthorText.text = book?.author ?: "Unknown Author"
                    // Update additional views for book details here if necessary
                }

                viewModel.getNotesByBook(title, author).observe(viewLifecycleOwner) { notes ->
                    // Implement notes display logic here, e.g., update a RecyclerView
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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


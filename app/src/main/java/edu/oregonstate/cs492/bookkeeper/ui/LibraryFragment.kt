package edu.oregonstate.cs492.bookkeeper.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import edu.oregonstate.cs492.bookkeeper.R

class LibraryFragment : Fragment(R.layout.fragment_library) {
    private val tag: String = "LibraryFragment"
    private val viewModel: LibraryViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.libraryBooks.observe(viewLifecycleOwner) {books ->
            Log.d(tag, "Books in Library: $books")
        }
    }
}
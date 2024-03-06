package edu.oregonstate.cs492.bookkeeper.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import edu.oregonstate.cs492.bookkeeper.R

class HomePageFragment : Fragment(R.layout.fragment_home) {
    private val tag: String = "HomePageFragment"
    private lateinit var libraryBtn: Button
    private lateinit var browseBtn: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        libraryBtn = view.findViewById(R.id.btn_your_library)
        browseBtn = view.findViewById(R.id.btn_browse_books)

        libraryBtn.setOnClickListener{
            val directions = HomePageFragmentDirections.navigateToLibrary()
            findNavController().navigate(directions)
        }

        browseBtn.setOnClickListener{
            val directions = HomePageFragmentDirections.navigateToBrowseBooks()
            findNavController().navigate(directions)
        }
    }
}
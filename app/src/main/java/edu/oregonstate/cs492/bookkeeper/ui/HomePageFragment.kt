package edu.oregonstate.cs492.bookkeeper.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
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
            Log.d(tag, "View Library Button Clicked!")
        }

        browseBtn.setOnClickListener{
            Log.d(tag, "Browse Button Clicked!")
        }
    }
}
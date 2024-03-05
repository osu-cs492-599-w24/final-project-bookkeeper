package edu.oregonstate.cs492.bookkeeper.ui

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.squareup.moshi.Moshi
import edu.oregonstate.cs492.bookkeeper.R
import edu.oregonstate.cs492.bookkeeper.data.BookJsonAdapter
import edu.oregonstate.cs492.bookkeeper.data.BookSearch
import edu.oregonstate.cs492.bookkeeper.data.OpenLibraryBookJsonAdapter
import edu.oregonstate.cs492.bookkeeper.data.OpenLibraryService
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response

class BrowseBooksFragment : Fragment(R.layout.fragment_browse_books) {
    private val tag = "BrowseBooksFragment"
    private val libraryService = OpenLibraryService.create()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchBoxET: EditText = view.findViewById(R.id.et_search_box)
        val searchBtn: Button = view.findViewById(R.id.btn_search)

        searchBtn.setOnClickListener {
            val query = searchBoxET.text.toString()
            if (!TextUtils.isEmpty(query)) {
                searchLibrary(query)
            }
        }
    }

    private fun searchLibrary(query: String) {
        libraryService.getBooks(query).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d(tag, "Status code: ${response.code()}")
                Log.d(tag, "Library call body: ${response.body()}")

                if (response.isSuccessful) {
                    val moshi = Moshi.Builder()
                        .add(OpenLibraryBookJsonAdapter())
                        .build()

                    val jsonAdapter = moshi.adapter(BookSearch::class.java)
                    val searchResults = response.body()?.let { jsonAdapter.fromJson(it) }

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
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d(tag, "Error making API call: ${t.message}")
            }
        })
    }
}

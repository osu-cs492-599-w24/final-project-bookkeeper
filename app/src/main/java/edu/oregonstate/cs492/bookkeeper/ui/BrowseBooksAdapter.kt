package edu.oregonstate.cs492.bookkeeper.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.oregonstate.cs492.bookkeeper.R
import edu.oregonstate.cs492.bookkeeper.data.Book

class ForecastAdapter() : RecyclerView.Adapter<ForecastAdapter.ViewHolder>() {
    // TODO update to actual data not hardcoded
    var books = listOf(
        Book(
            title = "Book Title 1",
            author = "Author Name 1",
            coverURL = "https://covers.openlibrary.org/b/id/1-M.jpg",

        ),
        Book(
            title = "Book Title 2",
            author = "Author Name 2",
            coverURL = "https://covers.openlibrary.org/b/id/2-M.jpg",
        ),
        Book(
            title = "Book Title 3",
            author = "Author Name 3",
            coverURL = "https://covers.openlibrary.org/b/id/3-M.jpg",
        ),
        Book(
            title = "Book Title 4",
            author = "Author Name 4",
            coverURL = "https://covers.openlibrary.org/b/id/4-M.jpg",
        ),
        Book(
            title = "Book Title 5",
            author = "Author Name 5",
            coverURL = "https://covers.openlibrary.org/b/id/5-M.jpg",
        ),
        Book(
            title = "Book Title 6",
            author = "Author Name 6",
            coverURL = "https://covers.openlibrary.org/b/id/5-M.jpg",
        )

    )


    override fun getItemCount() = books.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.browse_book_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(books[position])
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val coverIV : ImageView = itemView.findViewById(R.id.book_cover)
        private val titleTV = itemView.findViewById<TextView>(R.id.book_title)
        private val authorTV = itemView.findViewById<TextView>(R.id.book_author)
        private val ratingTV = itemView.findViewById<TextView>(R.id.book_rating)

        private lateinit var currentBook: Book

        fun bind(forecastPeriod: Book) {
            currentBook = forecastPeriod

            val ctx = itemView.context

            // TODO Load the image, properly handle rating
            titleTV.text = currentBook.title
            authorTV.text = currentBook.author
            // ratingTV.text = currentBook.rating.toString()
    }
}


}
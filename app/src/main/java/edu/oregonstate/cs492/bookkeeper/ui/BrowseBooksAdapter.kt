package edu.oregonstate.cs492.bookkeeper.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.oregonstate.cs492.bookkeeper.R
import edu.oregonstate.cs492.bookkeeper.data.Book

class BrowseBooksAdapter(
    private var books: List<Book>,
    private val onBookClick: (Book) -> Unit,
    private val setButtonText: (Book, Button) -> Unit
) : RecyclerView.Adapter<BrowseBooksAdapter.ViewHolder>() {

    fun updateBookList(newBookList: List<Book>?){
        notifyItemRangeRemoved(0, books.size)
        books = newBookList ?: listOf()
        notifyItemRangeInserted(0, books.size)
    }

    override fun getItemCount() = books.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.browse_book_list_item, parent, false)
        return ViewHolder(view, onBookClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(books[position], setButtonText)
    }

    class ViewHolder(
        itemView: View,
        onClick: (Book) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val coverIV : ImageView = itemView.findViewById(R.id.book_cover)
        private val titleTV = itemView.findViewById<TextView>(R.id.book_title)
        private val authorTV = itemView.findViewById<TextView>(R.id.book_author)
        private val ratingTV = itemView.findViewById<TextView>(R.id.book_rating)
        private val button = itemView.findViewById<Button>(R.id.book_button)

        private lateinit var currentBook: Book

        init {
            button.setOnClickListener{
                onClick(currentBook)
            }
        }

        fun bind(book: Book, setButtonText:(Book, Button) -> Unit) {
            currentBook = book

            val ctx = itemView.context

            // TODO Load the image, properly handle rating
            titleTV.text = currentBook.title
            authorTV.text = currentBook.author
            ratingTV.text = String.format("%.2f", currentBook.rating)

            Glide.with(ctx)
                .load(currentBook.coverURL)
                .fitCenter()
                .into(coverIV)

            setButtonText(currentBook, button)
    }
}


}
package edu.oregonstate.cs492.bookkeeper.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.oregonstate.cs492.bookkeeper.R
import edu.oregonstate.cs492.bookkeeper.data.Book

class BrowseBooksAdapter(private var books: List<Book>) : RecyclerView.Adapter<BrowseBooksAdapter.ViewHolder>() {

    fun updateBookList(newBookList: List<Book>?){

        notifyItemRangeRemoved(0, books.size)
        books = newBookList ?: listOf()
        notifyItemRangeInserted(0, books.size)
    }

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

        fun bind(book: Book) {
            currentBook = book
            val ctx = itemView.context

            titleTV.text = currentBook.title
            authorTV.text = currentBook.author
            ratingTV.text = String.format("%.2f", currentBook.rating)

            Glide.with(ctx)
                .load(currentBook.coverURL)
                .fitCenter()
                .into(coverIV)
    }
}


}
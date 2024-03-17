package edu.oregonstate.cs492.bookkeeper.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.oregonstate.cs492.bookkeeper.R
import edu.oregonstate.cs492.bookkeeper.data.LibraryBook

class LibraryAdapter (
    private var library: List<LibraryBook>,
    private val onLibraryBookClick: (LibraryBook) -> Unit
) : RecyclerView.Adapter<LibraryAdapter.ViewHolder>() {

    fun updateLibraryList(newLibraryList: List<LibraryBook>?){
        notifyItemRangeRemoved(0, library.size)
        library = newLibraryList ?: listOf()
        notifyItemRangeRemoved(0, library.size)
    }

    override fun getItemCount() = library.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.library_grid_item, parent, false)
        return ViewHolder(view, onLibraryBookClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(library[position])
    }

    class ViewHolder(
        itemView: View,
        val onLibraryBookClick: (LibraryBook) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val tag = "LibraryAdapter"
        private val coverIV : ImageView = itemView.findViewById(R.id.library_cover)
        private val percentTV = itemView.findViewById<TextView>(R.id.library_percent)

        private lateinit var currentBook: LibraryBook

        init {
            itemView.setOnClickListener {
                currentBook?.let(onLibraryBookClick)
            }
        }
        fun bind(book: LibraryBook) {
            currentBook = book

            val ctx = itemView.context
            var percent = .0
            if(currentBook.pagesRead != 0){
                percent = currentBook.pagesRead.toDouble() / currentBook.pageCount
            }
            Log.d(tag, "Percent: $percent (pR: ${currentBook.pagesRead} / pC: ${currentBook.pageCount})")

            percentTV.text = String.format("%.0f", percent*100).plus("%")

            Glide.with(ctx)
                .load(currentBook.coverURL)
                .fitCenter()
                .into(coverIV)
        }

    }



}


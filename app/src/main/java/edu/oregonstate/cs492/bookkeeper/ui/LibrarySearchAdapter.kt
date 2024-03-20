package edu.oregonstate.cs492.bookkeeper.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import edu.oregonstate.cs492.bookkeeper.R

class LibrarySearchAdapter(
    private var recentSearches: List<String>,
    private val onSearchClick: (String) -> Unit
) : RecyclerView.Adapter<LibrarySearchAdapter.LibrarySearchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int
    ): LibrarySearchAdapter.LibrarySearchViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recent_search_library_item, parent, false)
        return LibrarySearchViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: LibrarySearchAdapter.LibrarySearchViewHolder,
        position: Int
    ) {
        val recentSearch = recentSearches[position]
        holder.bind(recentSearch)
    }

    override fun getItemCount(): Int = recentSearches.size

    fun getRecentSearches(): List<String> = recentSearches

    fun updateRecentSearches(newRecentSearches: List<String>) {
        recentSearches = newRecentSearches.toList()
        notifyDataSetChanged()
    }

    inner class LibrarySearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recentSearchTextView: TextView = itemView.findViewById(R.id.text_recent_search_library)

        fun bind(recentSearch: String){
            recentSearchTextView.text = recentSearch
            itemView.setOnClickListener {
                onSearchClick(recentSearch)
            }
        }
    }
}

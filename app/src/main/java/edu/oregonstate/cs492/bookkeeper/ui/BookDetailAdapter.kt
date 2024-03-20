package edu.oregonstate.cs492.bookkeeper.ui

import edu.oregonstate.cs492.bookkeeper.data.Note
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.oregonstate.cs492.bookkeeper.R

class BookDetailAdapter(
    private var notes: List<Note>,
    private val onNoteClick: (Note) -> Unit
) : RecyclerView.Adapter<BookDetailAdapter.ViewHolder>() {
    fun updateNotesList(newNotesList: List<Note>?) {
        notifyItemRangeRemoved(0, notes.size)
        notes = newNotesList ?: listOf()
        notifyItemRangeInserted(0, notes.size)
    }

    override fun getItemCount() = notes.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.book_note_item, parent, false)
        return ViewHolder(view, onNoteClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(notes[position])
    }

    class ViewHolder(
        itemView: View,
        onNoteClick: (Note) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private lateinit var currentNote: Note
        private val noteTitleTV = itemView.findViewById<TextView>(R.id.book_note)

        init {
            itemView.setOnClickListener{
                onNoteClick(currentNote)
            }
        }
        fun bind(note: Note) {
            currentNote = note
            noteTitleTV.text = currentNote.title
        }
    }
}
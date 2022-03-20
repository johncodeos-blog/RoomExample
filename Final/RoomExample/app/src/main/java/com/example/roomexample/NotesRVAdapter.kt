package com.example.roomexample

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.roomexample.databinding.NotesRowBinding

class NotesRVAdapter : ListAdapter<Note, NotesRVAdapter.NoteHolder>(DiffCallback()) {

    class NoteHolder(var viewBinding: NotesRowBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    private lateinit var listener: RecyclerClickListener
    fun setItemListener(listener: RecyclerClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        val binding =
            NotesRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val note = NoteHolder(binding)

        note.viewBinding.noteDelete.setOnClickListener {
            listener.onItemRemoveClick(note.adapterPosition)
        }

        note.viewBinding.note.setOnClickListener {
            listener.onItemClick(note.adapterPosition)
        }
        return note
    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        val currentItem = getItem(position)
        holder.viewBinding.noteText.text = currentItem.noteText
    }

    class DiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note) =
            oldItem.dateAdded == newItem.dateAdded

        override fun areContentsTheSame(oldItem: Note, newItem: Note) =
            oldItem == newItem

    }
}
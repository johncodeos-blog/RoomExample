package com.example.roomexample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class NotesActivity : AppCompatActivity() {

    private lateinit var adapter: NotesRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        setRecyclerView()
    }


    private fun setRecyclerView() {
        val notesRecyclerview = findViewById<RecyclerView>(R.id.notes_recyclerview)
        notesRecyclerview.layoutManager = LinearLayoutManager(this)
        notesRecyclerview.setHasFixedSize(true)
        adapter = NotesRVAdapter()
        adapter.setItemListener(object : RecyclerClickListener {

            // Tap the 'X' to delete the note.
            override fun onItemRemoveClick(position: Int) {
                // Get the list of notes
                val notes = adapter.currentList.toMutableList()
                notes.removeAt(position)
                // Update RecyclerView
                adapter.submitList(notes)
            }

            // Tap the note to edit.
            override fun onItemClick(position: Int) {
                // Get the list of notes
                val notes = adapter.currentList.toMutableList()
                val intent = Intent(this@NotesActivity, AddNoteActivity::class.java)
                intent.putExtra("note_date_added", notes[position].dateAdded)
                intent.putExtra("note_text", notes[position].noteText)
                editNoteResultLauncher.launch(intent)
            }
        })
        notesRecyclerview.adapter = adapter
    }

    private val newNoteResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Get the list of notes
                val notes = adapter.currentList.toMutableList()
                // Get the new note from the AddNoteActivity
                val noteDateAdded = Date()
                val noteText = result.data?.getStringExtra("note_text")
                // Add the new note at the top of the list
                val newNote = Note(noteDateAdded, noteText ?: "")
                notes.add(newNote)
                // Update RecyclerView
                adapter.submitList(notes)
            }
        }

    private val editNoteResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Get the list of notes
                val notes = adapter.currentList.toMutableList()
                // Get the edited note from the AddNoteActivity
                val noteDateAdded = result.data?.getSerializableExtra("note_date_added") as Date
                val editedNoteText = result.data?.getStringExtra("note_text")
                // Find the note in the list with the same date and replace the note text with the edited
                for (note in notes) {
                    if (note.dateAdded == noteDateAdded) {
                        note.noteText = editedNoteText ?: ""
                    }
                }
                // Update RecyclerView
                adapter.submitList(notes)
                adapter.notifyDataSetChanged()
            }
        }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // The '+' menu button
        if (item.itemId == R.id.add_note_menu_item) {
            // Open AddNoteActivity
            val intent = Intent(this, AddNoteActivity::class.java)
            newNoteResultLauncher.launch(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_notes, menu)
        return true
    }
}

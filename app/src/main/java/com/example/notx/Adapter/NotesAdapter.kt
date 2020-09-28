package com.example.notx.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.notx.Fragments.SettingsFragment
import com.example.notx.Model.Note
import com.example.notx.Model.User
import com.example.notx.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class NotesAdapter
    (
    private val mContext: Context,
    private val mNotes: List<Note>,
) : RecyclerView.Adapter<NotesAdapter.ViewHolder>()
{
    private var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = LayoutInflater.from(mContext).inflate(R.layout.notes_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mNotes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val note = mNotes[position]

        holder.heading.text = note.chapter

        publisherInfo(note.publisher, holder.publisher)

        holder.publisher.setOnClickListener {
            val editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()

            editor.putString("postId", note.publisher)

            editor.apply()

            (mContext as FragmentActivity).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, SettingsFragment()).commit()
        }

        holder.heading.setOnClickListener{

            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(note.pdfUrl))
            mContext.startActivity(browserIntent)
        }


    }


    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var publisher: TextView
        var heading: TextView

        init {
            publisher = itemView.findViewById(R.id.publisher_name_note_item)
            heading = itemView.findViewById(R.id.subject_name_note_item)
        }
    }

    private fun publisherInfo(publisherID: String, publisher: TextView)
    {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherID)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val user = p0.getValue<User>(User::class.java)

                    publisher.text = user!!.getFullname()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

}
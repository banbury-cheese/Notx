package com.example.notx.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notx.Adapter.NotesAdapter
import com.example.notx.Model.Note
import com.example.notx.Model.User
import com.example.notx.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_notes_detail.view.*

class NotesDetailFragment : Fragment() {

    private var noteAdapter: NotesAdapter? = null
    private var noteList: MutableList<Note>? = null
    private lateinit var schoolCodeNote: String
    private var subject: Int = 0
    private lateinit var subjectString: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_notes_detail, container, false)


        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null)
        {
            this.subject = pref.getInt("subject", 0)
        }

        when(subject){
            R.drawable.chemistry_tile -> subjectString = "Chemistry"
            R.drawable.ped_tile -> subjectString = "PEd"
            R.drawable.biology_tile -> subjectString = "Biology"
            R.drawable.hindi_tile -> subjectString = "Hindi"
            R.drawable.history_tile -> subjectString = "History"
            R.drawable.physics_tile -> subjectString = "Physics"
            R.drawable.maths_tile -> subjectString = "Maths"
            R.drawable.computer_tile -> subjectString = "Computer"
            R.drawable.geography_tile -> subjectString = "Geography"
            R.drawable.language_tile -> subjectString = "Language"
            R.drawable.literature_tile -> subjectString = "Literature"
            R.drawable.others_tile -> subjectString = "Others"
            0 -> subjectString = "0"
        }


        var recyclerView: RecyclerView? = view.findViewById(R.id.recycler_view_notes)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView?.layoutManager = linearLayoutManager

        noteList = ArrayList()
        noteAdapter = context?.let { NotesAdapter(it, noteList as ArrayList<Note>) }
        recyclerView?.adapter = noteAdapter

        getSchoolCodeForPost()
        getClassForPost()

        return view
    }


    private fun retrieveNotes(UserClass: String) {
        val noteRef = FirebaseDatabase.getInstance().reference.child("Notes").child(UserClass).child(subjectString)

        noteRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                noteList?.clear()

                for (snapshot in p0.children) {
                    val note = snapshot.getValue(Note::class.java)

                    if (note!!.schoolCode == schoolCodeNote) {
                        noteList!!.add(note)
                    }

                    noteAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun getSchoolCodeForPost(){
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().currentUser!!.uid)
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val user = p0.getValue(User::class.java)
                    schoolCodeNote = user!!.getSchoolCode().toString()
                    }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun getClassForPost(){
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().currentUser!!.uid)
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    var classU=""
                    val user = p0.getValue(User::class.java)
                    classU = user!!.getclassSection().toString()
                    if (classU.length>=4) {
                        classU = classU.substring(0, 2)
                        Log.i("NewErr",classU+"on Data")
                        assign(classU)
                    }
                    else{
                        classU = classU.substring(0,1)
                        assign(classU)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun assign(classUser: String){
        retrieveNotes(classUser)
        Log.i("NewErr","assign"+classUser)
    }

}
package com.example.notx.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notx.Activities.AddBookActivity
import com.example.notx.Adapter.BookAdapter
import com.example.notx.Model.Book
import com.example.notx.Model.User
import com.example.notx.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_books.view.*

class BooksFragment : Fragment() {

    private var bookAdapter: BookAdapter ? = null
    private var bookList: MutableList<Book>? = null
    private lateinit var schoolCodeBook: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_books, container, false)

        getSchoolCodeForPost()

        view.fab_books.setOnClickListener {
            startActivity(Intent(context, AddBookActivity::class.java))
        }

        var recyclerView: RecyclerView? = null

        recyclerView = view.findViewById(R.id.recycler_view_books)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager

        bookList = ArrayList()
        bookAdapter = context?.let { BookAdapter(it, bookList as ArrayList<Book>) }
        recyclerView.adapter = bookAdapter

        retrieveBooks()

        return view
    }


    private fun retrieveBooks() {
        val bookRef = FirebaseDatabase.getInstance().reference.child("Books")

        bookRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot) {
                bookList?.clear()

                for (snapshot in p0.children)
                {
                    val book = snapshot.getValue(Book::class.java)

                    if (book!!.schoolCode == schoolCodeBook && (System.currentTimeMillis() - book.startTime.toLong() <= 604800000 ) )
                    {
                        bookList!!.add(book)
                    }

                    bookAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun getSchoolCodeForPost(){
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().currentUser!!.uid)
        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val user = p0.getValue(User::class.java)
                    schoolCodeBook = user!!.getSchoolCode().toString()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}
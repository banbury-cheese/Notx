package com.example.notx.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notx.Activities.AddPostActivity
import com.example.notx.Activities.MainActivity
import com.example.notx.Adapter.PostAdapter
import com.example.notx.Model.Post
import com.example.notx.Model.User
import com.example.notx.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.activity_add_post.view.*
import kotlinx.android.synthetic.main.fragment_connect.*
import kotlinx.android.synthetic.main.fragment_connect.view.*

class ConnectFragment : Fragment() {

    private var postAdapter: PostAdapter? = null
    private var postList: MutableList<Post>? = null
    private lateinit var schoolCodeUserCheck: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_connect, container, false)

        view.fab_connect.setOnClickListener {
            startActivity(Intent(context, AddPostActivity::class.java))
        }

        getSchoolCodeForPost()

        var recyclerView: RecyclerView? = null

        recyclerView = view.findViewById(R.id.recycler_view_connect)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager

        postList = ArrayList()
        postAdapter = context?.let { PostAdapter(it, postList as ArrayList<Post>) }
        recyclerView.adapter = postAdapter

        retrievePosts()

        return view
    }


    private fun retrievePosts() {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postsRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot) {
                postList?.clear()

                for (snapshot in p0.children)
                {
                    val post = snapshot.getValue(Post::class.java)

                    if (post!!.getSchoolCode() == schoolCodeUserCheck)
                    {
                        postList!!.add(post)
                    }

                    postAdapter!!.notifyDataSetChanged()
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
                    schoolCodeUserCheck = user!!.getSchoolCode().toString()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}



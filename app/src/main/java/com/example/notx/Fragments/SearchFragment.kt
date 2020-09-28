package com.example.notx.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notx.Adapter.UserAdapter
import com.example.notx.Model.User
import com.example.notx.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_search.view.*
import kotlinx.android.synthetic.main.fragment_settings.view.*

class SearchFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var userAdapter: UserAdapter? = null
    private var mUser: MutableList<User>? = null
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var currentSchoolCode: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        getCurrentUserSchoolCode()

        recyclerView = view.findViewById(R.id.recycler_view_search_fragment)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        mUser = ArrayList()
        userAdapter = context?.let { UserAdapter(it,mUser as ArrayList<User>,true) }
        recyclerView?.adapter = userAdapter

        view.profile_search_edit_text.addTextChangedListener(object: TextWatcher
        {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (view.profile_search_edit_text.text.toString() == "") {
                }
                else {
                    recyclerView?.visibility = View.VISIBLE
                    searchUser(p0.toString().toLowerCase())
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        return view
    }

    private fun searchUser(input: String) {
        val query = FirebaseDatabase.getInstance().getReference()
            .child("Users")
            .orderByChild("fullname")
            .startAt(input)
            .endAt(input + "\uf8ff")

        query.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mUser?.clear()

                for (snap in dataSnapshot.children){
                    val users = snap.getValue(User::class.java)

                    if (users != null && (users.getSchoolCode()==currentSchoolCode)){
                        mUser?.add(users)
                        userAdapter?.notifyDataSetChanged()
                    }
                }

                userAdapter?.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    private fun getCurrentUserSchoolCode()
    {

        val usersRef = FirebaseDatabase.getInstance().getReference()
            .child("Users")
            .child(firebaseUser.uid)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val user = p0.getValue<User>(User::class.java)

                    currentSchoolCode = user!!.getSchoolCode().toString()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }


}
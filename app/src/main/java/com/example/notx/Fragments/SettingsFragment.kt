package com.example.notx.Fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notx.Activities.AccountSettingsActivity
import com.example.notx.Adapter.MyImagesAdapter
import com.example.notx.Model.Post
import com.example.notx.Model.User
import com.example.notx.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import java.util.*
import kotlin.collections.ArrayList


class SettingsFragment : Fragment() {

    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser

    var postList: List<Post>? = null
    var myImagesAdapter: MyImagesAdapter? = null

    var myImagesAdapterSavedImg: MyImagesAdapter? = null
    var postListSaved: List<Post>? = null
    var mySavesImg: List<String>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null)
        {
            this.profileId = pref.getString("profileId", "none").toString()
        }


        if (profileId == firebaseUser.uid)
        {
            view.edit_profile.text = "Edit Profile"
        }
        else if (profileId != firebaseUser.uid)
        {
            view.edit_profile.text = "Call Him"
        }

        //recycler View for Uploaded Images
        var recyclerViewUploadImages: RecyclerView
        recyclerViewUploadImages = view.findViewById(R.id.recycler_view_upload_pic)
        recyclerViewUploadImages.setHasFixedSize(true)
        val linearLayoutManager: LinearLayoutManager = GridLayoutManager(context, 3)
        recyclerViewUploadImages.layoutManager = linearLayoutManager

        postList = ArrayList()
        myImagesAdapter = context?.let { MyImagesAdapter(it, postList as ArrayList<Post>) }
        recyclerViewUploadImages.adapter = myImagesAdapter




        //recycler View for Saved Images
        var recyclerViewSavedImages: RecyclerView
        recyclerViewSavedImages = view.findViewById(R.id.recycler_view_saved_pic)
        recyclerViewSavedImages.setHasFixedSize(true)
        val linearLayoutManager2: LinearLayoutManager = GridLayoutManager(context, 3)
        recyclerViewSavedImages.layoutManager = linearLayoutManager2

        postListSaved = ArrayList()
        myImagesAdapterSavedImg = context?.let { MyImagesAdapter(it, postListSaved as ArrayList<Post>) }
        recyclerViewSavedImages.adapter = myImagesAdapterSavedImg

        recyclerViewSavedImages.visibility = View.GONE
        recyclerViewUploadImages.visibility = View.VISIBLE

        var uploadedImgesBtn: ImageButton
        uploadedImgesBtn = view.findViewById(R.id.images_grid_view_btn)
        uploadedImgesBtn.setOnClickListener {
            recyclerViewSavedImages.visibility = View.GONE
            recyclerViewUploadImages.visibility = View.VISIBLE
        }

        var savedImgesBtn: ImageButton
        savedImgesBtn = view.findViewById(R.id.images_save_btn)
        savedImgesBtn.setOnClickListener {
            recyclerViewSavedImages.visibility = View.VISIBLE
            recyclerViewUploadImages.visibility = View.GONE
        }


        view.edit_profile.setOnClickListener {
            val getButtonText = view.edit_profile.text.toString()

            when
            {
                getButtonText == "Edit Profile" -> startActivity(Intent(context,
                    AccountSettingsActivity::class.java))

                getButtonText == "Call Him" -> {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.setData(Uri.parse("tel:${call_icon_profile.tag.toString()}"))
                    startActivity(intent)
                }
            }

        }

        view.email_icon_profile.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO,
                Uri.parse("mailto:${email_icon_profile.tag.toString()}"))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Wanted to contact you regarding...")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi there")
            startActivity(Intent.createChooser(emailIntent, "Send Mail"))
        }

        view.call_icon_profile.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.setData(Uri.parse("tel:${call_icon_profile.tag.toString()}"))
            startActivity(intent)
        }

        view.whatsapp_icon_profile.setOnClickListener{
            val smsNumber = whatsapp_icon_profile.tag.toString() // E164 format without '+' sign

            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.type = "text/plain"
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Hi ${full_name_profile?.text}")
            sendIntent.putExtra("jid",
                "$smsNumber@s.whatsapp.net") //phone number without "+" prefix

            sendIntent.setPackage("com.whatsapp")
            if (sendIntent.resolveActivity(activity!!.packageManager) != null) {
                startActivity(sendIntent)
            }
        }

        userInfo()
        myPhotos()
        mySaves()

        return view
    }

    private fun myPhotos()
    {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postsRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    (postList as ArrayList<Post>).clear()

                    for (snapshot in p0.children)
                    {
                        val post = snapshot.getValue(Post::class.java)!!
                        if (post.getPublisher().equals(profileId))
                        {
                            (postList as ArrayList<Post>).add(post)
                        }
                        Collections.reverse(postList)
                        myImagesAdapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }


    private fun userInfo()
    {

        val usersRef = FirebaseDatabase.getInstance().getReference()
            .child("Users")
            .child(profileId)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val user = p0.getValue<User>(User::class.java)

                    Picasso.get()
                        .load(user!!.getImage())
                        .placeholder(R.drawable.profile)
                        .into(view?.profile_image_profile)
                    view?.full_name_profile?.text = user!!.getFullname()
                    view?.bio_profile_frag?.text = user!!.getBio()
                    view?.class_profile?.text = user!!.getclassSection()
                    view?.call_icon_profile?.tag = "+"+user!!.getPhoneNo()
                    view?.email_icon_profile?.tag = user!!.getEmail()
                    view?.whatsapp_icon_profile?.tag = user!!.getPhoneNo()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun mySaves()
    {
        mySavesImg = ArrayList()

        val savedRef = FirebaseDatabase.getInstance()
            .reference
            .child("Saves").child(firebaseUser.uid)

        savedRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    for (snapshot in dataSnapshot.children)
                    {
                        (mySavesImg as ArrayList<String>).add(snapshot.key!!)
                    }
                    readSavedImagesData()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun readSavedImagesData()
    {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postsRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    (postListSaved as ArrayList<Post>).clear()

                    for (snapshot in dataSnapshot.children)
                    {
                        val post = snapshot.getValue(Post::class.java)

                        for (key in mySavesImg!!)
                        {
                            if (post!!.getPostid() == key)
                            {
                                (postListSaved as ArrayList<Post>).add(post!!)
                            }
                        }
                    }
                    myImagesAdapterSavedImg!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }





    override fun onStop() {
        super.onStop()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

}
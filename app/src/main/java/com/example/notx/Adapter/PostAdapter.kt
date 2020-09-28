package com.example.notx.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.notx.Activities.CommentsActivity
import com.example.notx.Fragments.SettingsFragment
import com.example.notx.Model.Post
import com.example.notx.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.example.notx.Model.User
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PostAdapter
    (private val mContext: Context,
     private val mPost: List<Post>) : RecyclerView.Adapter<PostAdapter.ViewHolder>()
{
    private var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = LayoutInflater.from(mContext).inflate(R.layout.posts_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val post = mPost[position]

        Picasso.get().load(post.getPostimage()).into(holder.postImage)

        if (post.getDescription().equals(""))
        {
            holder.description.visibility = View.GONE
        }
        else
        {
            holder.description.visibility = View.VISIBLE
            holder.description.setText(post.getDescription())
        }

        if (post.getHeading().equals(""))
        {
            holder.heading.visibility = View.GONE
        }
        else
        {
            holder.heading.visibility = View.VISIBLE
            holder.heading.setText(post.getHeading())
        }

        if (post.getPostimage().equals(""))
        {
            holder.postImage.visibility = View.GONE
        }
        else
        {
            holder.postImage.visibility = View.VISIBLE
        }

        publisherInfo(holder.profileImage, holder.fullName,post.getPublisher())
        isLikes(post.getPostid(), holder.likeButton)
        numberOfLikes(holder.likes, post.getPostid())
        checkSavedStatus(post.getPostid(), holder.saveButton)
        getTotalComments(holder.comments, post.getPostid())

        holder.fullName.setOnClickListener {
            val editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()

            editor.putString("profileId", post.getPublisher())

            editor.apply()

            (mContext as FragmentActivity).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, SettingsFragment()).commit()
        }

        holder.profileImage.setOnClickListener {
            val editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()

            editor.putString("profileId", post.getPublisher())

            editor.apply()

            (mContext as FragmentActivity).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, SettingsFragment()).commit()
        }


        holder.likeButton.setOnClickListener {
            if (holder.likeButton.tag == "Like")
            {
                FirebaseDatabase.getInstance().reference
                    .child("Likes")
                    .child(post.getPostid())
                    .child(firebaseUser!!.uid)
                    .setValue(true)

            }
            else
            {
                FirebaseDatabase.getInstance().reference
                    .child("Likes")
                    .child(post.getPostid())
                    .child(firebaseUser!!.uid)
                    .removeValue()

                numberOfLikes(holder.likes, post.getPostid())

            }
        }

        holder.commentButton.setOnClickListener {
            val intentComment = Intent(mContext, CommentsActivity::class.java)
            intentComment.putExtra("postId", post.getPostid())
            intentComment.putExtra("publisherId", post.getPublisher())
            mContext.startActivity(intentComment)
        }

        holder.comments.setOnClickListener {
            val intentComment = Intent(mContext, CommentsActivity::class.java)
            intentComment.putExtra("postId", post.getPostid())
            intentComment.putExtra("publisherId", post.getPublisher())
            mContext.startActivity(intentComment)
        }

        holder.saveButton.setOnClickListener {
            if (holder.saveButton.tag == "Save")
            {
                FirebaseDatabase.getInstance().reference
                    .child("Saves")
                    .child(firebaseUser!!.uid)
                    .child(post.getPostid())
                    .setValue(true)
            }
            else
            {
                FirebaseDatabase.getInstance().reference
                    .child("Saves")
                    .child(firebaseUser!!.uid)
                    .child(post.getPostid())
                    .removeValue()
            }
        }


    }


    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var profileImage: CircleImageView
        var postImage: ImageView
        var likeButton: ImageView
        var commentButton: ImageView
        var saveButton: ImageView
        var fullName: TextView
        var likes: TextView
        var heading: TextView
        var description: TextView
        var comments: TextView

        init {
            profileImage = itemView.findViewById(R.id.user_profile_image_post)
            postImage = itemView.findViewById(R.id.post_image_home)
            likeButton = itemView.findViewById(R.id.post_image_like_btn)
            commentButton = itemView.findViewById(R.id.post_image_comment_btn)
            saveButton = itemView.findViewById(R.id.post_save_comment_btn)
            fullName = itemView.findViewById(R.id.full_name_post)
            likes = itemView.findViewById(R.id.likes)
            heading = itemView.findViewById(R.id.heading)
            description = itemView.findViewById(R.id.description)
            comments = itemView.findViewById(R.id.comments)

        }
    }




    private fun publisherInfo(profileImage: CircleImageView, fullName: TextView, publisherID: String)
    {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherID)

        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val user = p0.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profileImage)
                    fullName.text = user!!.getFullname()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun getTotalComments(comments: TextView, postid: String)
    {
        val commentsRef = FirebaseDatabase.getInstance().reference
            .child("Comments").child(postid)

        commentsRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists())
                {
                    if (p0.childrenCount.toString() != "1")
                        comments.text = "view all " + p0.childrenCount.toString() + " comments"
                    else
                        comments.text = "view " + p0.childrenCount.toString() + " comment"

                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }



    private fun isLikes(postid: String, likeButton: ImageView)
    {
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val LikesRef = FirebaseDatabase.getInstance().reference
            .child("Likes").child(postid)

        LikesRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.child(firebaseUser!!.uid).exists())
                {
                    likeButton.setImageResource(R.drawable.heart_filled_icon)
                    likeButton.tag = "Liked"
                }
                else
                {
                    likeButton.setImageResource(R.drawable.heart_not_filled_icon)
                    likeButton.tag = "Like"
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }


    private fun numberOfLikes(likes: TextView, postid: String)
    {
        val LikesRef = FirebaseDatabase.getInstance().reference
            .child("Likes").child(postid)

        LikesRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists())
                {
                    if (p0.childrenCount.toString() != "1")
                        likes.text = p0.childrenCount.toString() + " likes"
                    else
                        likes.text = p0.childrenCount.toString() + " like"

                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    private fun checkSavedStatus(postid: String, imageView: ImageView)
    {
        val savesRef = FirebaseDatabase.getInstance().reference
            .child("Saves")
            .child(firebaseUser!!.uid)

        savesRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.child(postid).exists())
                {
                    imageView.setImageResource(R.drawable.save_icon_filled)
                    imageView.tag = "Saved"
                }
                else
                {
                    imageView.setImageResource(R.drawable.save_icon_not_filled)
                    imageView.tag = "Save"
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }



}
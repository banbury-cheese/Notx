package com.example.notx.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.notx.Fragments.SettingsFragment
import com.example.notx.Model.Book
import com.example.notx.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso

class BookAdapter
    (private val mContext: Context,
     private val mBook: List<Book>) : RecyclerView.Adapter<BookAdapter.ViewHolder>()
{
    private var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = LayoutInflater.from(mContext).inflate(R.layout.book_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mBook.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val book = mBook[position]

        Picasso.get().load(book.bookImage).into(holder.bookImage)
        holder.price.text = book.price
        holder.quality.text = book.quality
        holder.heading.text = book.heading
        holder.description.text = book.description

        holder.contactSeller.setOnClickListener {
            val editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()

            editor.putString("profileId", book.publisher)

            editor.apply()

            (mContext as FragmentActivity).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, SettingsFragment()).commit()
        }


    }


    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var bookImage: ImageView
        var heading: TextView
        var description: TextView
        var price: TextView
        var quality: TextView
        var contactSeller: Button

        init {
            bookImage = itemView.findViewById(R.id.book_item_image)
            heading = itemView.findViewById(R.id.book_name_book_item)
            description = itemView.findViewById(R.id.book_description_book_item)
            price = itemView.findViewById(R.id.book_price_book_item)
            quality = itemView.findViewById(R.id.book_quality_book_item)
            contactSeller = itemView.findViewById(R.id.view_profile_book_item)
        }
    }
}
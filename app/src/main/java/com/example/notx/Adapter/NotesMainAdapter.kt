package com.example.notx.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.notx.Adapter.NotesMainAdapter.SliderViewHolder
import com.example.notx.Fragments.NotesDetailFragment
import com.example.notx.Fragments.NotesFragment
import com.example.notx.Fragments.SettingsFragment
import com.example.notx.Model.Book
import com.example.notx.Model.SliderNote
import com.example.notx.R

class NotesMainAdapter(private val mContext: Context, private val sliderNotes: ArrayList<SliderNote>, private val viewPager2: ViewPager2) : RecyclerView.Adapter<SliderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        return SliderViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.slide_item_container, parent, false))
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {

        holder.imageView.setImageResource(sliderNotes[position].image)

        if (position == sliderNotes.size -2){
            viewPager2.post(sliderRunnable)
        }

        holder.imageView.setOnClickListener{

                val editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                editor.putInt("subject", sliderNotes[position].image)
                editor.apply()
                (mContext as FragmentActivity).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, NotesDetailFragment()).commit()
        }

    }

    override fun getItemCount(): Int {
        return sliderNotes.size
    }


    inner class SliderViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var imageView: ImageView

        init {
            imageView = itemView.findViewById(R.id.imageSlide)
        }
    }

    private val sliderRunnable: Runnable = object : Runnable {
        override fun run() {
            sliderNotes.addAll(sliderNotes)
            notifyDataSetChanged()
        }
    }
}
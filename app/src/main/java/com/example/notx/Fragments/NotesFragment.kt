package com.example.notx.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.notx.Activities.AddBookActivity
import com.example.notx.Activities.AddNotes
import com.example.notx.Adapter.BookAdapter
import com.example.notx.Adapter.NotesAdapter
import com.example.notx.Adapter.NotesMainAdapter
import com.example.notx.Model.Book
import com.example.notx.Model.Note
import com.example.notx.Model.SliderNote
import com.example.notx.R
import kotlinx.android.synthetic.main.fragment_books.view.*
import kotlinx.android.synthetic.main.fragment_notes.view.*
import javax.security.auth.Subject


public class NotesFragment : Fragment() {

    private lateinit var viewPager2: ViewPager2
    val handler = Handler()
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notes, container, false)

        mContext = context!!

        view.fab_notes.setOnClickListener {
            startActivity(Intent(context, AddNotes::class.java))
        }


        viewPager2 = view.findViewById(R.id.viewPagerNotes)

        var sliderItems: MutableList<SliderNote>?
        sliderItems = ArrayList()
        sliderItems.add(SliderNote(R.drawable.chemistry_tile))
        sliderItems.add(SliderNote(R.drawable.ped_tile))
        sliderItems.add(SliderNote(R.drawable.biology_tile))
        sliderItems.add(SliderNote(R.drawable.hindi_tile))
        sliderItems.add(SliderNote(R.drawable.history_tile))
        sliderItems.add(SliderNote(R.drawable.physics_tile))
        sliderItems.add(SliderNote(R.drawable.maths_tile))
        sliderItems.add(SliderNote(R.drawable.computer_tile))
        sliderItems.add(SliderNote(R.drawable.geography_tile))
        sliderItems.add(SliderNote(R.drawable.language_tile))
        sliderItems.add(SliderNote(R.drawable.literature_tile))
        sliderItems.add(SliderNote(R.drawable.others_tile))


        viewPager2.adapter = NotesMainAdapter(mContext,sliderItems, viewPager2)

        viewPager2.clipToPadding = false
        viewPager2.clipChildren = false
        viewPager2.offscreenPageLimit = 3
        viewPager2.getChildAt(0).overScrollMode =  RecyclerView.OVER_SCROLL_NEVER

        var compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - Math.abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }

        viewPager2.setPageTransformer(compositePageTransformer)

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler.removeCallbacks(sliderRunnable)
                handler.postDelayed(sliderRunnable,3000)
            }
        })

        return  view
    }

    private val sliderRunnable: Runnable = object : Runnable {
        override fun run() {
            viewPager2.setCurrentItem(viewPager2.currentItem+1)
        }
    }


}
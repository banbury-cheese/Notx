package com.example.notx.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.notx.Fragments.*
import com.example.notx.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_settings.*

class MainActivity : AppCompatActivity() {

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_connect -> {
                moveToFragment(ConnectFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_search -> {
                moveToFragment(SearchFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_books -> {
                moveToFragment(BooksFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_notes -> {
                moveToFragment(NotesFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_settings -> {
                val navView: BottomNavigationView = findViewById(R.id.nav_view)
                navView.tag = "Settings Clicked"
                moveToFragment(SettingsFragment())
                return@OnNavigationItemSelectedListener true
            }
        }

        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)


        moveToFragment(ConnectFragment())
    }

    private fun moveToFragment(fragment: Fragment)
    {
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.fragment_container, fragment)
        fragmentTrans.commit()
    }
}
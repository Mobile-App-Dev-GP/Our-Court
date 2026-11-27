package com.example.ourcourt

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        bottomNav = findViewById(R.id.bottom_navigation)

        if (savedInstanceState == null) {
            loadFragment(DiscoverFragment())
        }

        bottomNav.setOnItemSelectedListener { item ->
            supportFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(DiscoverFragment())
                    true
                }
                R.id.nav_community -> {
                    loadFragment(PartnersFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }

        supportFragmentManager.addOnBackStackChangedListener {
            val currentFrag = supportFragmentManager.findFragmentById(R.id.fragment_container)
            if (currentFrag is ProfileFragment || currentFrag is SettingsFragment) {
                bottomNav.menu.findItem(R.id.nav_profile).isChecked = true
            } else if (currentFrag is DiscoverFragment || currentFrag is CourtDetailsFragment) {
                bottomNav.menu.findItem(R.id.nav_home).isChecked = true
            } else if (currentFrag is PartnersFragment) {
                bottomNav.menu.findItem(R.id.nav_community).isChecked = true
            }
        }
    }

    fun selectTab(itemId: Int) {
        if (::bottomNav.isInitialized) {
            bottomNav.selectedItemId = itemId
        }
    }

    fun openHitList() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HitListFragment())
            .addToBackStack("HitList")
            .commit()
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}

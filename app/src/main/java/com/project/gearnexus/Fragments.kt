package com.project.gearnexus


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.gearnexus.databinding.ActivityFragmentsBinding
import com.project.gearnexus.fragments.AddNewFragment
import com.project.gearnexus.fragments.HomeFragment
import com.project.gearnexus.fragments.ProfileFragment
import com.project.gearnexus.fragments.SearchFragment
import com.project.gearnexus.fragments.ChatsFragment

class Fragments : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var binding: ActivityFragmentsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFragmentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomNavigationView = findViewById(R.id.main_bottom_menu)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottom_home -> {
                    supportFragmentManager.beginTransaction().replace(R.id.main, HomeFragment()).commit()
                    true
                }
                R.id.bottom_search -> {
                    supportFragmentManager.beginTransaction().replace(R.id.main, SearchFragment()).commit()
                    true
                }
                R.id.bottom_add_new -> {
                    supportFragmentManager.beginTransaction().replace(R.id.main, AddNewFragment()).commit()
                    true
                }
                R.id.bottom_chat -> {
                    supportFragmentManager.beginTransaction().replace(R.id.main, ChatsFragment()).commit()
                    true
                }
                R.id.bottom_profile -> {
                    supportFragmentManager.beginTransaction().replace(R.id.main, ProfileFragment()).commit()
                    true
                }
                else -> false
            }
        }

        // Set the default fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.main, HomeFragment()).commit()
        }
    }
}

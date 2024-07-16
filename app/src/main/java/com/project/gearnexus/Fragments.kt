package com.project.gearnexus

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.gearnexus.databinding.ActivityFragmentsBinding
import com.project.gearnexus.fragments.*

class Fragments : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var binding: ActivityFragmentsBinding

    private val homeFragment = HomeFragment()
    private val searchFragment = SearchFragment()
    private val addNewFragment = AddNewFragment()
    private val chatsFragment = ChatsFragment()
    private val profileFragment = ProfileFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFragmentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = intent.getStringExtra("userId")
        val name = intent.getStringExtra("name")
        val email = intent.getStringExtra("email")
        val number = intent.getStringExtra("number")
        val password = intent.getStringExtra("password")
        val profileImageUrl = intent.getStringExtra("profileImageUrl")

        // Create a bundle to pass data to fragments
        val bundle = Bundle().apply {
            putString("userId", userId)
            putString("name", name)
            putString("email", email)
            putString("number", number)
            putString("password", password)
            putString("profileImageUrl", profileImageUrl)
        }

        // Set arguments to fragments
        homeFragment.arguments = bundle
        addNewFragment.arguments = bundle
        profileFragment.arguments = bundle

        bottomNavigationView = findViewById(R.id.main_bottom_menu)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottom_home -> {
                    showFragment(homeFragment)
                    true
                }
                R.id.bottom_search -> {
                    showFragment(searchFragment)
                    true
                }
                R.id.bottom_add_new -> {
                    showFragment(addNewFragment)
                    true
                }
                R.id.bottom_chat -> {
                    showFragment(chatsFragment)
                    true
                }
                R.id.bottom_profile -> {
                    showFragment(profileFragment)
                    true
                }
                else -> false
            }
        }

        // Set the default fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.main, homeFragment, "HomeFragment")
                .commit()
        }
    }

    private fun showFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()

        // Hide all fragments
        fragmentManager.fragments.forEach { transaction.hide(it) }

        if (fragment.isAdded) {
            // Show existing fragment
            transaction.show(fragment)
        } else {
            // Add new fragment
            transaction.add(R.id.main, fragment)
        }

        transaction.commit()
    }
}

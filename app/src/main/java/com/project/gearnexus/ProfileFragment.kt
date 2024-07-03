package com.project.gearnexus.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.project.gearnexus.Login_Page
import com.project.gearnexus.ProfileEdit
import com.project.gearnexus.R

class ProfileFragment : Fragment() {

    private lateinit var profileImage: ImageView
    private lateinit var profileName: TextView
    private lateinit var profileEmail: TextView
    private lateinit var profileNumber: TextView
    private lateinit var profilePassword: TextView
    private lateinit var editProfile: Button
    private lateinit var logoutButton: Button

    private lateinit var reference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as? AppCompatActivity)?.supportActionBar?.title = "User Profile"

        val root = inflater.inflate(R.layout.fragment_profile, container, false)

        profileImage = root.findViewById(R.id.profileImageView)
        profileName = root.findViewById(R.id.profileName)
        profileEmail = root.findViewById(R.id.profileEmail)
        profileNumber = root.findViewById(R.id.profileNumber)
        profilePassword = root.findViewById(R.id.profilePassword)
        editProfile = root.findViewById(R.id.editButton)
        logoutButton = root.findViewById(R.id.logoutButton)

        reference = FirebaseDatabase.getInstance().getReference("users")
         val storageReference = FirebaseStorage.getInstance().reference.child("profile_images")

        // Retrieve data from arguments
        arguments?.let {
            val userId = it.getString("userId")
            val name = it.getString("name")
            val email = it.getString("email")
            val number = it.getString("number")
            val password = it.getString("password")
            val profileImageUrl = it.getString("profileImageUrl")

            Log.d("ProfileFragment", "Received data: userId=$userId, name=$name, email=$email, number=$number, password=$password, profileImageUrl=$profileImageUrl")

            // Display data
            profileName.text = name
            profileEmail.text = email
            profileNumber.text = number
            profilePassword.text = password

            if (!profileImageUrl.isNullOrEmpty()) {
                Glide.with(this).load(profileImageUrl).into(profileImage)
            }

            editProfile.setOnClickListener {
                passUserData(userId ?: "")
            }

            logoutButton.setOnClickListener {
                showLogoutConfirmationDialog()
            }
        }

        return root
    }

    private fun passUserData(userId: String) {
        reference.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nameFromDB = snapshot.child("name").getValue(String::class.java)
                val emailFromDB = snapshot.child("email").getValue(String::class.java)
                val numberFromDB = snapshot.child("number").getValue(String::class.java)
                val passwordFromDB = snapshot.child("password").getValue(String::class.java)
                val profileImageUrlFromDB = snapshot.child("profileImageUrl").getValue(String::class.java)

                Log.d("ProfileFragment", "Database data: name=$nameFromDB, email=$emailFromDB, number=$numberFromDB, password=$passwordFromDB, profileImageUrl=$profileImageUrlFromDB")

                val intent = Intent(activity, ProfileEdit::class.java).apply {
                    putExtra("name", nameFromDB)
                    putExtra("email", emailFromDB)
                    putExtra("number", numberFromDB)
                    putExtra("password", passwordFromDB)
                    putExtra("profileImageUrl", profileImageUrlFromDB)
                }
                startActivity(intent)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                val intent = Intent(activity, Login_Page::class.java)
                startActivity(intent)
                activity?.finish()
            }
            .setNegativeButton("No", null)
            .show()
    }
}

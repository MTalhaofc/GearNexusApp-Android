package com.project.gearnexus.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.project.gearnexus.ProfileEdit
import com.project.gearnexus.R

class ProfileFragment : Fragment() {

    private lateinit var profileImage: ImageView
    private lateinit var profileName: TextView
    private lateinit var profileEmail: TextView
    private lateinit var profileNumber: TextView
    private lateinit var profilePassword: TextView
    private lateinit var titleName: TextView
    private lateinit var titleNumber: TextView
    private lateinit var editProfile: Button
    private lateinit var logoutButton: Button

    private lateinit var reference: DatabaseReference
    private lateinit var auth: FirebaseAuth

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
        auth = FirebaseAuth.getInstance()

        showAllUserData()

        editProfile.setOnClickListener {
            val userNumber = profileNumber.text.toString().trim()
            passUserData(userNumber)
        }

        logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        return root
    }

    private fun showAllUserData() {
        val intent = activity?.intent
        val nameUser = intent?.getStringExtra("name")
        val emailUser = intent?.getStringExtra("email")
        val numberUser = intent?.getStringExtra("number")
        val passwordUser = intent?.getStringExtra("password")
        val profileImageUrl = intent?.getStringExtra("profileImageUrl")


        profileName.text = nameUser
        profileEmail.text = emailUser
        profileNumber.text = numberUser
        profilePassword.text = passwordUser

        if (!profileImageUrl.isNullOrEmpty()) {
            Glide.with(this).load(profileImageUrl).into(profileImage)
        }
    }

    private fun passUserData(userNumber: String) {
        val checkUserDatabase = reference.orderByChild("number").equalTo(userNumber)

        checkUserDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val nameFromDB = snapshot.child(userNumber).child("name").getValue(String::class.java)
                    val emailFromDB = snapshot.child(userNumber).child("email").getValue(String::class.java)
                    val numberFromDB = snapshot.child(userNumber).child("number").getValue(String::class.java)
                    val passwordFromDB = snapshot.child(userNumber).child("password").getValue(String::class.java)
                    val profileImageUrlFromDB = snapshot.child(userNumber).child("profileImageUrl").getValue(String::class.java)

                    val intent = Intent(activity, ProfileEdit::class.java).apply {
                        putExtra("name", nameFromDB)
                        putExtra("email", emailFromDB)
                        putExtra("number", numberFromDB)
                        putExtra("password", passwordFromDB)
                        putExtra("profileImageUrl", profileImageUrlFromDB)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(activity, "User not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showLogoutConfirmationDialog() {
        // Your existing code to show a logout confirmation dialog
    }
}

package com.project.gearnexus.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.project.gearnexus.Contactuspage
import com.project.gearnexus.Login_Page
import com.project.gearnexus.ProfileEdit
import com.project.gearnexus.R
import com.project.gearnexus.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var reference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup action bar
        (activity as? AppCompatActivity)?.supportActionBar?.hide()

        reference = FirebaseDatabase.getInstance().getReference("users")
        storageReference = FirebaseStorage.getInstance().reference.child("profile_images")


        // Retrieve data from arguments
        arguments?.let {
            val userId = it.getString("userId")
            val name = it.getString("name")
            val email = it.getString("email")
            val number = it.getString("number")
            val password = it.getString("password")
            val profileImageUrl = it.getString("profileImageUrl")

            Log.d(
                "ProfileFragment",
                "Received data: userId=$userId, name=$name, email=$email, number=$number, password=$password, profileImageUrl=$profileImageUrl"
            )

            // Display data
            binding.apply {
                profileName.text = name
                profileEmail.text = email
                profileNumber.text = number
                profilePassword.text = password

                if (!profileImageUrl.isNullOrEmpty()) {
                    Glide.with(this@ProfileFragment)
                        .load(profileImageUrl)
                        .apply(RequestOptions.circleCropTransform())
                        .into(profileImageView)
                }

                editButton.setOnClickListener {
                    passUserData(userId ?: "")
                }

                logoutButton.setOnClickListener {
                    showLogoutConfirmationDialog()
                }

                btnContactUs.setOnClickListener {
                    val intent = Intent(activity, Contactuspage::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun passUserData(userId: String) {
        reference.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nameFromDB = snapshot.child("name").getValue(String::class.java)
                val emailFromDB = snapshot.child("email").getValue(String::class.java)
                val numberFromDB = snapshot.child("number").getValue(String::class.java)
                val passwordFromDB = snapshot.child("password").getValue(String::class.java)
                val profileImageUrlFromDB =
                    snapshot.child("profileImageUrl").getValue(String::class.java)

                Log.d(
                    "ProfileFragment",
                    "Database data: name=$nameFromDB, email=$emailFromDB, number=$numberFromDB, password=$passwordFromDB, profileImageUrl=$profileImageUrlFromDB"
                )

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

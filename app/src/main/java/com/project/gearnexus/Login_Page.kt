package com.project.gearnexus

import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.project.gearnexus.databinding.ActivityLoginPageBinding

class Login_Page : AppCompatActivity() {

    private lateinit var binding: ActivityLoginPageBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        binding = ActivityLoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("users")

        binding.btnLogin.setOnClickListener {
            if (!validateUsername() || !validatePassword()) {
                // Do nothing if validation fails
            } else {
                checkUser()
            }
        }

        binding.tvSignUp.setOnClickListener {
            val intent = Intent(this, Signup_Page::class.java)
            startActivity(intent)
        }
    }

    private fun validateUsername(): Boolean {
        val value = binding.etUserEmail.text.toString()
        return if (value.isEmpty()) {
            binding.etUserEmail.error = "Email cannot be empty"
            false
        } else {
            binding.etUserEmail.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        val value = binding.etUserPass.text.toString()
        return if (value.isEmpty()) {
            binding.etUserPass.error = "Password cannot be empty"
            false
        } else {
            binding.etUserPass.error = null
            true
        }
    }

    private fun checkUser() {
        val userEmail = binding.etUserEmail.text.toString().trim()
        val userPassword = binding.etUserPass.text.toString().trim()

        val checkUserDatabase = reference.orderByChild("email").equalTo(userEmail)

        checkUserDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    binding.etUserEmail.error = null
                    val userSnapshot = snapshot.children.first()

                    val passwordFromDB = userSnapshot.child("password").getValue(String::class.java)

                    if (passwordFromDB == userPassword) {
                        binding.etUserEmail.error = null

                        val userId = userSnapshot.key
                        val nameFromDB = userSnapshot.child("name").getValue(String::class.java)
                        val emailFromDB = userSnapshot.child("email").getValue(String::class.java)
                        val numberFromDB = userSnapshot.child("number").getValue(String::class.java)
                        val profileImageUrl = userSnapshot.child("profileImageUrl").getValue(String::class.java)

                        // Start Fragments activity with user data
                        val intent = Intent(this@Login_Page, Fragments::class.java)
                        intent.putExtra("userId", userId)
                        intent.putExtra("name", nameFromDB)
                        intent.putExtra("email", emailFromDB)
                        intent.putExtra("number", numberFromDB)
                        intent.putExtra("password", passwordFromDB)
                        intent.putExtra("profileImageUrl", profileImageUrl)
                        startActivity(intent)

                        binding.etUserEmail.text.clear()
                        binding.etUserPass.text.clear()
                    } else {
                        binding.etUserEmail.error = "Invalid Credentials"
                        binding.etUserPass.requestFocus()
                    }
                } else {
                    binding.etUserEmail.error = "User does not exist"
                    binding.etUserPass.requestFocus()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Login_Page, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}

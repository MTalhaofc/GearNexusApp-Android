package com.project.gearnexus

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

import com.project.gearnexus.databinding.ActivitySignupPageBinding

class Signup_Page : AppCompatActivity() {

    private lateinit var binding: ActivitySignupPageBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("users") // Reference to your "users" node

        binding.btnSignup.setOnClickListener {
            val name = binding.etUserName.text.toString().trim()
            val email = binding.etUserEmail.text.toString().trim()
            val number = binding.etUserNumber.text.toString().trim()
            val password = binding.etUserPass.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && number.isNotEmpty() && password.isNotEmpty()) {
                // Create a helper class instance
                val helperClass = HelperClass(name, email, number, password)

                // Set value in the database under the child node "users"
                reference.child(number).setValue(helperClass)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "You have signed up successfully!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, Login_Page::class.java)
                            startActivity(intent)
                            binding.etUserName.text.clear()
                            binding.etUserEmail.text.clear()
                            binding.etUserNumber.text.clear()
                            binding.etUserPass.text.clear()

                            finish() // Optional: Finish the activity after signup
                        } else {
                            Toast.makeText(this, "Failed to sign up. Please try again later.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvSignIn.setOnClickListener {
            val intent = Intent(this, Login_Page::class.java)
            startActivity(intent)
        }
    }
}

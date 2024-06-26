package com.project.gearnexus

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.project.gearnexus.databinding.ActivityProfileEditBinding

class ProfileEdit : AppCompatActivity() {

    private lateinit var binding: ActivityProfileEditBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    private var nameUser: String = ""
    private var emailUser: String = ""
    private var numberUser: String = ""
    private var passwordUser: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        reference = database.getReference("users")

        // Assuming intent extras are passed from previous activity
        nameUser = intent.getStringExtra("name") ?: ""
        emailUser = intent.getStringExtra("email") ?: ""
        numberUser = intent.getStringExtra("number") ?: ""
        passwordUser = intent.getStringExtra("password") ?: ""

        binding.editName.setText(nameUser)
        binding.editEmail.setText(emailUser)
        binding.editUsername.setText(numberUser)
        binding.editPassword.setText(passwordUser)

        binding.saveButton.setOnClickListener {
            if (isNameChanged() || isPasswordChanged() || isEmailChanged()) {
                saveChanges()
            } else {
                Toast.makeText(this, "No Changes Found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isNameChanged(): Boolean {
        val newName = binding.editName.text.toString().trim()
        return newName != nameUser
    }

    private fun isEmailChanged(): Boolean {
        val newEmail = binding.editEmail.text.toString().trim()
        return newEmail != emailUser
    }

    private fun isPasswordChanged(): Boolean {
        val newPassword = binding.editPassword.text.toString().trim()
        return newPassword != passwordUser
    }

    private fun saveChanges() {
        val newName = binding.editName.text.toString().trim()
        val newEmail = binding.editEmail.text.toString().trim()
        val newPassword = binding.editPassword.text.toString().trim()

        // Update user data in Firebase
        reference.child(numberUser).apply {
            child("name").setValue(newName)
            child("email").setValue(newEmail)
            child("password").setValue(newPassword)
        }

        // Display toast message
        Toast.makeText(this, "Changes saved, Changes will be applied after logging out", Toast.LENGTH_LONG).show()

        // Return to previous activity (ProfileFragment's hosting activity)
        val intent = Intent()
        intent.putExtra("updatedName", newName)
        intent.putExtra("updatedEmail", newEmail)
        intent.putExtra("updatedPassword", newPassword)
        setResult(RESULT_OK, intent)
        finish()
    }
}

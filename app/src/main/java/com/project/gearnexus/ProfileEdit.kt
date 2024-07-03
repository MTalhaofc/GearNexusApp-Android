package com.project.gearnexus

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.project.gearnexus.databinding.ActivityProfileEditBinding

class ProfileEdit : AppCompatActivity() {

    private lateinit var binding: ActivityProfileEditBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var storageReference: StorageReference

    private var nameUser: String = ""
    private var emailUser: String = ""
    private var numberUser: String = ""
    private var passwordUser: String = ""
    private var profileImageUrl: String? = null
    private var selectedImageUri: Uri? = null

    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                Glide.with(this).load(uri).into(binding.profileImage)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        reference = database.getReference("users")
        storageReference = FirebaseStorage.getInstance().reference.child("profile_images")

        // Receiving intent extras
        nameUser = intent.getStringExtra("name") ?: ""
        emailUser = intent.getStringExtra("email") ?: ""
        numberUser = intent.getStringExtra("number") ?: ""
        passwordUser = intent.getStringExtra("password") ?: ""
        profileImageUrl = intent.getStringExtra("profileImageUrl")

        // Set previous details
        binding.editName.setText(nameUser)
        binding.editEmail.setText(emailUser)
        binding.editnumber.setText(numberUser)
        binding.editPassword.setText(passwordUser)

        // Load the profile image if it exists
        if (!profileImageUrl.isNullOrEmpty()) {
            Glide.with(this).load(profileImageUrl).into(binding.profileImage)
        }

        binding.changeImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            getContent.launch(intent)
        }

        binding.saveButton.setOnClickListener {
            if (isNameChanged() || isPasswordChanged() || isEmailChanged() || selectedImageUri != null) {
                saveChanges()
            } else {
                Toast.makeText(this, "No Changes Found", Toast.LENGTH_SHORT).show()
            }
        }

        binding.backButton.setOnClickListener {
            finish()
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

        if (selectedImageUri != null) {
            val imageRef = storageReference.child("$numberUser.jpg")
            imageRef.putFile(selectedImageUri!!).addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    updateUserData(newName, newEmail, newPassword, uri.toString())
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
        } else {
            updateUserData(newName, newEmail, newPassword, profileImageUrl)
        }
    }

    private fun updateUserData(name: String, email: String, password: String, imageUrl: String?) {
        val updates = mutableMapOf<String, Any>(
            "name" to name,
            "email" to email,
            "password" to password
        )
        if (imageUrl != null) {
            updates["profileImageUrl"] = imageUrl
        }

        reference.orderByChild("number").equalTo(numberUser).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    userSnapshot.ref.updateChildren(updates).addOnCompleteListener {
                        Toast.makeText(this@ProfileEdit, "Changes saved. Changes will be applied after logging out.", Toast.LENGTH_LONG).show()
                        val intent = Intent().apply {
                            putExtra("updatedName", name)
                            putExtra("updatedEmail", email)
                            putExtra("updatedPassword", password)
                            putExtra("updatedProfileImageUrl", imageUrl)
                        }
                        setResult(RESULT_OK, intent)
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this@ProfileEdit, "Failed to save changes", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProfileEdit, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}

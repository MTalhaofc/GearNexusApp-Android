package com.project.gearnexus

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.project.gearnexus.databinding.ActivitySignupPageBinding

class Signup_Page : AppCompatActivity() {

    private lateinit var binding: ActivitySignupPageBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var storageReference: StorageReference
    private var selectedImageUri: Uri? = null

    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                Glide.with(this).load(uri).into(binding.uploadedImage)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        binding = ActivitySignupPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        database = FirebaseDatabase.getInstance()
        storageReference = FirebaseStorage.getInstance().reference.child("profile_images")

        binding.imageUploadButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            getContent.launch(intent)
        }

        binding.btnSignup.setOnClickListener {
            val name = binding.etUserName.text.toString().trim()
            val email = binding.etUserEmail.text.toString().trim()
            val number = binding.etUserNumber.text.toString().trim()
            val password = binding.etUserPass.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && number.isNotEmpty() && password.isNotEmpty() && selectedImageUri != null) {
                val userId = database.reference.child("users").push().key!! // Generate a unique user ID

                uploadImageAndSaveUserData(userId, name, email, number, password)
            } else {
                Toast.makeText(this, "Please fill in all fields and upload an image", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvSignIn.setOnClickListener {
            val intent = Intent(this, Login_Page::class.java)
            startActivity(intent)
        }
    }

    private fun uploadImageAndSaveUserData(userId: String, name: String, email: String, number: String, password: String) {
        val imageRef = storageReference.child("$userId.jpg")
        imageRef.putFile(selectedImageUri!!)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val profileImageUrl = uri.toString()
                    val helperClass = HelperClass(userId, name, email, number, password, profileImageUrl)

                    database.getReference("users").child(userId).setValue(helperClass)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "You have signed up successfully!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, Login_Page::class.java)
                                startActivity(intent)
                                binding.etUserName.text.clear()
                                binding.etUserEmail.text.clear()
                                binding.etUserNumber.text.clear()
                                binding.etUserPass.text.clear()
                                binding.uploadedImage.setImageResource(0)

                                finish()
                            } else {
                                Toast.makeText(this, "Failed to sign up. Please try again later.", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
    }
}

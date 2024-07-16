package com.project.gearnexus.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.project.gearnexus.databinding.FragmentAddNewBinding
import com.project.gearnexus.models.Post

class AddNewFragment : Fragment() {

    private lateinit var binding: FragmentAddNewBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: FirebaseStorage
    private lateinit var reference: DatabaseReference

    private var selectedImageUri: Uri? = null
    private var currentUserEmail: String? = null
    private var currentUserName: String? = null
    private var currentUserProfileImageUrl: String? = null
    private var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddNewBinding.inflate(inflater, container, false)
        val root = binding.root

        // Initialize Firebase references
        databaseReference = FirebaseDatabase.getInstance().getReference("posts")


        storageReference = FirebaseStorage.getInstance()
        reference = FirebaseDatabase.getInstance().getReference("users")
        val storageReference = FirebaseStorage.getInstance().reference.child("profile_images")


        // Fetch userId from arguments
        arguments?.let {
            userId = it.getString("userId")
        }

        if (userId != null) {
            Toast.makeText(context, "Fill in Complete Details and select an image", Toast.LENGTH_SHORT).show()
        } else {
            Log.d("AddNewFragment", "No userId received")
            Toast.makeText(context, "No userId received", Toast.LENGTH_SHORT).show()
        }

        // Fetch current user's email and profile image URL from Realtime Database
        fetchCurrentUserDetails()

        binding.imageUploadButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

        binding.saveButton.setOnClickListener {
            // Check if user details have been fetched
            if (currentUserName != null && currentUserEmail != null) {
                uploadPost()
            } else {
                Toast.makeText(context, "User details not loaded yet. Please wait.", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

    private fun fetchCurrentUserDetails() {
        // Fetch current user's email and profile image URL from Realtime Database
        userId?.let {
            reference.child(it).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    currentUserName = snapshot.child("name").getValue(String::class.java)
                    currentUserEmail = snapshot.child("email").getValue(String::class.java)
                    currentUserProfileImageUrl = snapshot.child("profileImageUrl").getValue(String::class.java)

                    Log.d("AddNewFragment", "Database data: name=$currentUserName, email=$currentUserEmail, profileImageUrl=$currentUserProfileImageUrl")
                    Toast.makeText(context, "User : $currentUserName, Email : $currentUserEmail", Toast.LENGTH_LONG).show()

                    // Enable the save button now that user details are fetched
                    binding.saveButton.isEnabled = true
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("AddNewFragment", "Failed to retrieve user data: ${error.message}")
                    Toast.makeText(context, "Failed to retrieve user data: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            binding.uploadedImage.setImageURI(selectedImageUri)
        }
    }

    private fun uploadPost() {
        val name = binding.nameInput.text.toString().trim()
        val price = binding.priceInput.text.toString().trim()
        val details = binding.detailsInput.text.toString().trim()
        val location = binding.locationInput.text.toString().trim()
        val contactNumber = binding.contactNumberInput.text.toString().trim()

        if (selectedImageUri != null && name.isNotEmpty() && price.isNotEmpty() && details.isNotEmpty() && location.isNotEmpty() && contactNumber.isNotEmpty()) {
            val postId = databaseReference.push().key ?: return
            val imageRef = storageReference.reference.child("images/$postId")

            imageRef.putFile(selectedImageUri!!)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        val post = Post(
                            postId = postId,
                            email = currentUserEmail,
                            imageUrl = imageUrl,
                            name = name,
                            price = price,
                            details = details,
                            location = location,
                            username = currentUserName,
                            contactNumber = contactNumber,
                            postuserId = userId,
                            timestamp = System.currentTimeMillis(),
                            userprofileimageurl = currentUserProfileImageUrl
                        )

                        databaseReference.child(postId).setValue(post)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "Post uploaded successfully", Toast.LENGTH_SHORT).show()
                                    Toast.makeText(context, "User Posted: $currentUserName User Email: : $currentUserEmail", Toast.LENGTH_SHORT).show()
                                    parentFragmentManager.popBackStack()
                                    binding.nameInput.text.clear()
                                    binding.priceInput.text.clear()
                                    binding.detailsInput.text.clear()
                                    binding.locationInput.text.clear()
                                    binding.contactNumberInput.text.clear()
                                    binding.uploadedImage.setImageResource(0)
                                } else {
                                    Toast.makeText(context, "Failed to upload post", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
        }
    }
}
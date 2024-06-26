package com.project.gearnexus.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.project.gearnexus.databinding.FragmentAddNewBinding
import com.project.gearnexus.models.Post

class AddNewFragment : Fragment() {

    private lateinit var binding: FragmentAddNewBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private var selectedImageUri: Uri? = null
    private var currentUserEmail: String? = null // Store current user's email

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddNewBinding.inflate(inflater, container, false)
        val root = binding.root

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("posts")
        storageReference = FirebaseStorage.getInstance()

        // Fetch current user's email from Realtime Database
        fetchCurrentUserEmail()

        binding.imageUploadButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

        binding.saveButton.setOnClickListener {
            uploadPost()
        }

        return root
    }

    private fun fetchCurrentUserEmail() {
        val currentUserUid = auth.currentUser?.uid
        if (currentUserUid != null) {
            val userReference = FirebaseDatabase.getInstance().getReference("users").child(currentUserUid)
            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Fetch user's email from the snapshot
                    currentUserEmail = snapshot.child("email").getValue(String::class.java)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Failed to retrieve user data: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(context, "Fill In The Complete Details", Toast.LENGTH_SHORT).show()
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
                        val post = Post(postId, currentUserEmail, imageUrl, name, price, details, location, contactNumber) // Use currentUserEmail instead of auth.currentUser?.email
                        databaseReference.child(postId).setValue(post)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "Post uploaded successfully", Toast.LENGTH_SHORT).show()
                                    // Navigate back to the HomeFragment
                                    parentFragmentManager.popBackStack()
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

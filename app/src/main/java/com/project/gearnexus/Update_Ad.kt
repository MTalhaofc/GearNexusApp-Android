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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.project.gearnexus.databinding.ActivityUpdateAdBinding
import com.project.gearnexus.models.Post

class Update_Ad : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateAdBinding
    private var postId: String? = null
    private var selectedImageUri: Uri? = null
    private lateinit var storageReference: StorageReference

    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                Glide.with(this).load(uri).into(binding.postImage)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUpdateAdBinding.inflate(layoutInflater)
        setContentView(binding.root)


        storageReference = FirebaseStorage.getInstance().reference.child("images")

        postId = intent.getStringExtra("postId")

        loadPostDetails()

        binding.changeImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            getContent.launch(intent)
        }

        binding.updateButton.setOnClickListener {
            updatePost()
        }
    }

    private fun loadPostDetails() {
        if (postId != null) {
            val databaseReference = FirebaseDatabase.getInstance().getReference("posts").child(postId!!)
            databaseReference.get().addOnSuccessListener { snapshot ->
                val post = snapshot.getValue(Post::class.java)
                if (post != null) {
                    binding.editName.setText(post.name)
                    binding.editPrice.setText(post.price)
                    binding.editDetails.setText(post.details)
                    binding.editlocation.setText(post.location) // Update location EditText
                    binding.editcontactnumber.setText(post.contactNumber) // Update contact number EditText
                    Glide.with(this).load(post.imageUrl).into(binding.postImage)
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to load post details", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updatePost() {
        val name = binding.editName.text.toString().trim()
        val price = binding.editPrice.text.toString().trim()
        val details = binding.editDetails.text.toString().trim()
        val location = binding.editlocation.text.toString().trim() // Read location
        val contactNumber = binding.editcontactnumber.text.toString().trim() // Read contact number

        if (name.isNotEmpty() && price.isNotEmpty() && details.isNotEmpty() && location.isNotEmpty() && contactNumber.isNotEmpty()) {
            if (selectedImageUri != null) {
                uploadImageAndUpdatePost(title.toString(), price, details, location, contactNumber)
            } else {
                val databaseReference = FirebaseDatabase.getInstance().getReference("posts").child(postId!!)
                databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val imageUrl = snapshot.child("imageUrl").getValue(String::class.java)
                        val updatedPost = imageUrl?.let {
                            Post(
                                postId!!,
                                null.toString(), it,
                                title.toString(), price, details, location, contactNumber)
                        }
                        databaseReference.setValue(updatedPost).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this@Update_Ad, "Post updated successfully", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                Toast.makeText(this@Update_Ad, "Failed to update post", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@Update_Ad, "Failed to retrieve current image URL", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImageAndUpdatePost(title: String, price: String, details: String, location: String, contactNumber: String) {
        val imageRef = storageReference.child("$postId.jpg")
        imageRef.putFile(selectedImageUri!!)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    val updatedPost = postId?.let { it1 ->
                        Post(
                            it1,
                            null.toString(), imageUrl, title, price, details, location, contactNumber)
                    }
                    val databaseReference = FirebaseDatabase.getInstance().getReference("posts").child(postId!!)
                    databaseReference.setValue(updatedPost).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Post updated successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this, "Failed to update post", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
    }
}
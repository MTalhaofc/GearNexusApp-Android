package com.project.gearnexus

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.gearnexus.models.Post

class View_Ad : AppCompatActivity() {

    private lateinit var postImage: ImageView
    private lateinit var postTitle: TextView
    private lateinit var postPrice: TextView
    private lateinit var postLocation: TextView
    private lateinit var postContactNumber: TextView
    private lateinit var postDetails: TextView
    private lateinit var fabClose: FloatingActionButton

    private lateinit var databaseReference: DatabaseReference
    private lateinit var postId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_ad)

        postImage = findViewById(R.id.viewPostImage)
        postTitle = findViewById(R.id.viewPostTitle)
        postPrice = findViewById(R.id.viewPostPrice)
        postLocation = findViewById(R.id.viewPostLocation)
        postContactNumber = findViewById(R.id.viewPostContactNumber)
        postDetails = findViewById(R.id.viewPostDetails)
        fabClose = findViewById(R.id.fabClose)

        postId = intent.getStringExtra("postId") ?: return

        databaseReference = FirebaseDatabase.getInstance().getReference("posts").child(postId)
        loadPostDetails()

        fabClose.setOnClickListener {
            finish()  // Go back to the previous screen
        }
    }

    private fun loadPostDetails() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val post = snapshot.getValue(Post::class.java)
                post?.let {
                    postTitle.text = it.name
                    postPrice.text = it.price
                    postLocation.text = it.location
                    postContactNumber.text = it.contactNumber
                    postDetails.text = it.details
                    Glide.with(this@View_Ad).load(it.imageUrl).into(postImage)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}